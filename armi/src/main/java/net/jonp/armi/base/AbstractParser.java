package net.jonp.armi.base;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectStreamClass;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.NotBoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.jonp.armi.ARMILexer;
import net.jonp.armi.ARMIParser;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.tree.CommonTree;

import com.google.common.io.LineReader;

/**
 * Superclass for command/response parsers.
 */
public abstract class AbstractParser
{
    protected final ARMIParser parser;
    protected final ClassRegistry registry;
    protected final LineReader in;

    /**
     * Each value that we deserialize goes in here, including back-references
     * and nulls.
     */
    protected final List<Object> indexedValues = new ArrayList<Object>();

    /**
     * Construct a new AbstractParser.
     * 
     * @param _in The stream from which to read command/response language
     *            constructs.
     * @param _registry The class registry for looking up instance classes from
     *            command language names.
     * @throws IOException If there was a problem initializing the parsing
     *             framework from the stream.
     */
    protected AbstractParser(final InputStream _in, final ClassRegistry _registry)
        throws IOException
    {
        in = new LineReader(new InputStreamReader(_in));
        parser = new ARMIParser(null);
        registry = _registry;
    }

    /**
     * Get the {@link ClassRegistry} used by this parser.
     * 
     * @return The class registry.
     */
    public ClassRegistry getClassRegistry()
    {
        return registry;
    }

    /**
     * Sets up the parser to read the next message.
     * 
     * @throws IOException If there was a problem.
     */
    protected void parserSetup()
        throws IOException
    {
        String line;
        do {
            line = in.readLine();
        } while (line != null && line.trim().isEmpty());

        if (line == null) {
            throw new EOFException();
        }

        final CharStream charStream = new ANTLRStringStream(line);
        final ARMILexer lexer = new ARMILexer(charStream);
        final CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        parser.setTokenStream(tokenStream);
        indexedValues.clear();
    }

    /**
     * Parse the tree from a label.
     * 
     * @param ast The tree.
     * @return The label.
     * @throws SyntaxException If there was a problem parsing the tree.
     */
    protected String label(final CommonTree ast)
        throws SyntaxException
    {
        if (ast.getType() != ARMIParser.LABEL) {
            throw new SyntaxException("Not a LABEL: " + ast.getType());
        }

        if (ast.getChildCount() != 1) {
            throw new SyntaxException("LABEL childCount != 1: " + ast.getChildCount());
        }

        return str((CommonTree)ast.getChild(0));
    }

    /**
     * Parse the tree from an identifier.
     * 
     * @param ast The tree.
     * @return The path of the identifier (packages, objects, ...).
     * @throws SyntaxException If there was a problem parsing the tree.
     */
    protected String[] ident(final CommonTree ast)
        throws SyntaxException
    {
        final String[] path = new String[ast.getChildCount()];
        int i = 0;
        for (final Object childAST : ast.getChildren()) {
            final CommonTree child = (CommonTree)childAST;
            path[i++] = child.getText();
        }

        return path;
    }

    /**
     * Parse the tree of a val (str, num, bool, obj, ...).
     * 
     * @param ast The tree.
     * @return The value.
     * @throws SyntaxException If there was a problem parsing the tree.
     */
    protected Object val(final CommonTree ast)
        throws SyntaxException
    {
        final Object obj;

        // If the handler function adds to indexedValues, we don't want to do
        // the same
        boolean indexed = false;
        switch (ast.getType()) {
            case ARMIParser.STR:
                obj = str(ast);
                break;
            case ARMIParser.NUM:
                obj = num(ast);
                break;
            case ARMIParser.BOOL:
                obj = bool(ast);
                break;
            case ARMIParser.ARRAY:
                indexed = true;
                obj = array(ast);
                break;
            case ARMIParser.COLLECTION:
                indexed = true;
                obj = collection(ast);
                break;
            case ARMIParser.MAP:
                indexed = true;
                obj = map(ast);
                break;
            case ARMIParser.OBJ:
                indexed = true;
                obj = obj(ast);
                break;
            case ARMIParser.REF:
                obj = indexedValues.get(ref(ast));
                break;
            case ARMIParser.NIL:
                obj = null;
                break;
            default:
                throw new SyntaxException("Unknown VAL type: " + ast.getType());
        }

        if (!indexed) {
            indexedValues.add(obj);
        }

        return obj;
    }

    /**
     * Parse the tree from a str.
     * 
     * @param ast The tree.
     * @return The string.
     * @throws SyntaxException If there was a problem parsing the tree.
     */
    protected String str(final CommonTree ast)
        throws SyntaxException
    {
        if (ast.getType() != ARMIParser.STR) {
            throw new SyntaxException("Not a STR: " + ast.getType());
        }

        if (ast.getChildCount() != 1) {
            throw new SyntaxException("STR childCount != 1: " + ast.getChildCount());
        }

        return ast.getChild(0).getText();
    }

    /**
     * Parse the tree from a num.
     * 
     * @param ast The tree.
     * @return The number.
     * @throws SyntaxException If there was a problem parsing the tree.
     */
    protected Number num(final CommonTree ast)
        throws SyntaxException
    {
        if (ast.getType() != ARMIParser.NUM) {
            throw new SyntaxException("Not a NUM: " + ast.getType());
        }

        if (ast.getChildCount() != 2 && ast.getChildCount() != 4) {
            throw new SyntaxException("NUM childCount != 2 or 4: " + ast.getChildCount());
        }

        final CommonTree type = (CommonTree)ast.getChild(0);
        switch (type.getType()) {
            case ARMIParser.BYTE:
                return Byte.valueOf(ast.getChild(1).getText());
            case ARMIParser.FLOAT:
                return Float.valueOf(ast.getChild(1).getText() + "." + ast.getChild(3).getText());
            case ARMIParser.LONG:
                return Long.valueOf(ast.getChild(1).getText());
            case ARMIParser.SHORT:
                return Short.valueOf(ast.getChild(1).getText());
            case ARMIParser.NUMDEFAULT:
                if (ast.getChildCount() == 2) {
                    return Integer.valueOf(ast.getChild(1).getText());
                }
                else {
                    return Double.valueOf(ast.getChild(1).getText() + "." + ast.getChild(3).getText());
                }
            default:
                throw new IllegalStateException("Unexpected numeric type: " + type.getType());
        }
    }

    /**
     * Parse the tree from a bool.
     * 
     * @param ast The tree.
     * @return The boolean.
     * @throws SyntaxException If there was a problem parsing the tree.
     */
    protected Boolean bool(final CommonTree ast)
        throws SyntaxException
    {
        if (ast.getType() != ARMIParser.BOOL) {
            throw new SyntaxException("Not a BOOL: " + ast.getType());
        }

        if (ast.getChildCount() != 1) {
            throw new SyntaxException("BOOL childCount != 1: " + ast.getChildCount());
        }

        return Boolean.valueOf(ast.getChild(0).getText());
    }

    /**
     * Parse the tree from an array.
     * 
     * @param ast The tree.
     * @return The elements of the array.
     * @throws SyntaxException If there was a problem parsing the tree.
     */
    protected Object[] array(final CommonTree ast)
        throws SyntaxException
    {
        if (ast.getType() != ARMIParser.ARRAY) {
            throw new SyntaxException("Not an ARRAY: " + ast.getType());
        }

        if (ast.getChildCount() != 2) {
            throw new SyntaxException("ARRAY childCount != 2: " + ast.getChildCount());
        }

        final String[] ident = ident((CommonTree)ast.getChild(0));
        final String className = Conversion.arrayToString(ident, ".");
        final Class<?> clazz = findClass(className);

        // FUTURE: Check for primitive types and built appropriate arrays
        final Object[] objects = elements(clazz, true, (CommonTree)ast.getChild(1));

        return objects;
    }

    /**
     * Parse the tree from a collection.
     * 
     * @param ast The tree.
     * @return A collection of the specified type with the specified elements.
     * @throws SyntaxException If there was a problem parsing the tree.
     */
    protected Collection<?> collection(final CommonTree ast)
        throws SyntaxException
    {
        if (ast.getType() != ARMIParser.COLLECTION) {
            throw new SyntaxException("Not a COLLECTION: " + ast.getType());
        }

        if (ast.getChildCount() != 2) {
            throw new SyntaxException("COLLECTION childCount != 2: " + ast.getChildCount());
        }

        final String[] ident = ident((CommonTree)ast.getChild(0));
        final String className = Conversion.arrayToString(ident, ".");
        final Class<?> clazz = findClass(className);
        final Collection<Object> collection = Utils.cast(newObject(clazz));
        indexedValues.add(collection);

        final Object[] elements = elements(Object.class, false, (CommonTree)ast.getChild(1));

        Collections.addAll(collection, elements);

        return collection;
    }

    /**
     * Parse the tree from a map.
     * 
     * @param ast The tree.
     * @return A map of the specified type with the specified key/value pairs.
     * @throws SyntaxException If there was a problem parsing the tree.
     */
    protected Map<?, ?> map(final CommonTree ast)
        throws SyntaxException
    {
        if (ast.getType() != ARMIParser.MAP) {
            throw new SyntaxException("Not a MAP: " + ast.getType());
        }

        if (ast.getChildCount() != 2) {
            throw new SyntaxException("MAP childCount != 2: " + ast.getChildCount());
        }

        final String[] ident = ident((CommonTree)ast.getChild(0));
        final String className = Conversion.arrayToString(ident, ".");
        final Class<?> clazz = findClass(className);

        final CommonTree mapvals = (CommonTree)ast.getChild(1);
        if (mapvals.getType() != ARMIParser.MAPVALS) {
            throw new SyntaxException("Not a MAPVALS: " + mapvals.getType());
        }

        final Map<Object, Object> map = Utils.cast(newObject(clazz));
        indexedValues.add(map);

        if (mapvals.getChildCount() > 0) {
            // If 0, getChildren() will return null
            for (final Object childAST : mapvals.getChildren()) {
                final CommonTree mapval = (CommonTree)childAST;

                if (mapval.getType() != ARMIParser.MAPVAL) {
                    throw new SyntaxException("Not a MAPVAL: " + mapval.getType());
                }

                if (mapval.getChildCount() != 2) {
                    throw new SyntaxException("MAPVAL childCount != 2: " + mapval.getChildCount());
                }

                final Object key = val((CommonTree)mapval.getChild(0));
                final Object value = val((CommonTree)mapval.getChild(1));
                map.put(key, value);
            }
        }

        return map;
    }

    /**
     * Parse the tree from an elements.
     * 
     * @param clazz The component class for the array to return.
     * @param index True to add the array object to {@link #indexedValues}
     *            before parsing elements, false not to.
     * @param ast The tree.
     * @return The elements.
     * @throws SyntaxException If there was a problem parsing the tree.
     */
    protected Object[] elements(final Class<?> clazz, final boolean index, final CommonTree ast)
        throws SyntaxException
    {
        if (ast.getType() != ARMIParser.ELEMENTS) {
            throw new SyntaxException("Not an ELEMENTS: " + ast.getType());
        }

        final Object[] elements = newArray(clazz, ast.getChildCount());

        if (index) {
            indexedValues.add(elements);
        }

        if (ast.getChildCount() > 0) {
            // When 0, getChildren() returns null
            int i = 0;
            for (final Object childAST : ast.getChildren()) {
                final CommonTree child = (CommonTree)childAST;
                elements[i++] = val(child);
            }
        }

        return elements;
    }

    /**
     * Parse the tree from an obj.
     * 
     * @param ast The tree.
     * @return The object.
     * @throws SyntaxException If there was a problem parsing the tree or
     *             instantiating the object.
     */
    protected Object obj(final CommonTree ast)
        throws SyntaxException
    {
        if (ast.getType() != ARMIParser.OBJ) {
            throw new SyntaxException("Not a OBJ: " + ast.getType());
        }

        if (ast.getChildCount() != 2) {
            throw new SyntaxException("OBJ childCount != 2: " + ast.getChildCount());
        }

        final String[] ident = ident((CommonTree)ast.getChild(0));
        return buildObject(Conversion.arrayToString(ident, "."), (CommonTree)ast.getChild(1));
    }

    /**
     * Parse the tree from a ref.
     * 
     * @param ast The tree.
     * @return The object.
     * @throws SyntaxException If there was a problem parsing the tree.
     */
    protected int ref(final CommonTree ast)
        throws SyntaxException
    {
        if (ast.getType() != ARMIParser.REF) {
            throw new SyntaxException("Not a REF: " + ast.getType());
        }

        if (ast.getChildCount() != 1) {
            throw new SyntaxException("REF childCount != 1: " + ast.getChildCount());
        }

        final int idx = Integer.valueOf(ast.getChild(0).getText());
        return idx;
    }

    /**
     * Parse the tree from a fields.
     * 
     * @param ast The tree.
     * @return A map of field names onto field values.
     * @throws SyntaxException If there was a problem parsing the tree.
     */
    protected Map<String, Object> fields(final CommonTree ast)
        throws SyntaxException
    {
        if (ast.getType() != ARMIParser.FIELDS) {
            throw new SyntaxException("Not a FIELDS: " + ast.getType());
        }

        final Map<String, Object> map = new HashMap<String, Object>();
        if (ast.getChildCount() > 0) {
            // Otherwise, getChildren() may be null
            for (final Object childAST : ast.getChildren()) {
                final CommonTree child = (CommonTree)childAST;
                addField(child, map);
            }
        }

        return map;
    }

    /**
     * Parse the tree from an individual field and add it to a map.
     * 
     * @param ast The tree.
     * @param fields The map to which the field will be added.
     * @throws SyntaxException If there was a problem parsing the tree.
     */
    protected void addField(final CommonTree ast, final Map<String, Object> fields)
        throws SyntaxException
    {
        if (ast.getType() != ARMIParser.FIELD) {
            throw new SyntaxException("Not a FIELD: " + ast.getType());
        }

        if (ast.getChildCount() != 2) {
            throw new SyntaxException("FIELD childCount != 2: " + ast.getChildCount());
        }

        final String[] ident = ident((CommonTree)ast.getChild(0));
        final String name = Conversion.arrayToString(ident, ".");
        final Object val = val((CommonTree)ast.getChild(1));

        fields.put(name, val);
    }

    /**
     * Build an object, given a class name and map of field values.
     * 
     * @param className The name of the class to construct.
     * @param ast The tree pointing at the fields for this object.
     * @return The object.
     * @throws SyntaxException If there was a problem constructing the object,
     *             setting its fields, or calling {@link Initializable#init()},
     *             if it applies.
     */
    protected Object buildObject(final String className, final CommonTree ast)
        throws SyntaxException
    {
        final Class<?> clazz = findClass(className);
        final Object instance = newObject(clazz);

        // This needs to be in the list of indexed values BEFORE we can continue
        // reading fields, in case any of the fields is a circular reference
        indexedValues.add(instance);

        final Map<String, Object> fields = fields(ast);

        String fieldName = null;
        try {
            for (final Map.Entry<String, Object> entry : fields.entrySet()) {
                fieldName = entry.getKey();
                final Field field = findField(clazz, fieldName);
                field.setAccessible(true);

                final Object value = entry.getValue();
                field.set(instance, value);
            }
        }
        catch (final NoSuchFieldException nsfe) {
            throw new SyntaxException("No field " + fieldName + " exists on " + className + ": " + nsfe.getMessage(), nsfe);
        }
        catch (final IllegalAccessException iae) {
            throw new SyntaxException("Cannot access field " + fieldName + " on " + className + ": " + iae.getMessage(), iae);
        }
        catch (final IllegalArgumentException iae) {
            throw new SyntaxException(
                                      "Illegal conversion when setting " + fieldName + " on " + className + ": " + iae.getMessage(),
                                      iae);
        }

        if (instance instanceof Initializable) {
            try {
                ((Initializable)instance).init();
            }
            catch (final Throwable th) {
                throw new SyntaxException("Failed to initialize a " + className + ": " + th.getMessage(), th);
            }
        }

        return instance;
    }

    /**
     * Locate a class from the class registry.
     * 
     * @param className The name of the class to locate.
     * @return The class associated with that name.
     * @throws SyntaxException If the class could not be found.
     */
    protected Class<?> findClass(final String className)
        throws SyntaxException
    {
        final Class<?> clazz;
        try {
            clazz = registry.lookup(className);
        }
        catch (final NotBoundException nbe) {
            throw new SyntaxException("Unable to locate referenced class " + className + ": " + nbe.getMessage(), nbe);
        }

        return clazz;
    }

    /**
     * Construct a new instance of a class, using its no-argument constructor.
     * 
     * @param clazz The class of the object to create.
     * @return The new instance of the class.
     * @throws SyntaxException If there is an error instantiating the class,
     *             including the class not having an accessible no-argument
     *             constructor or that constructor throwing an exception.
     */
    protected Object newObject(final Class<?> clazz)
        throws SyntaxException
    {
        final Constructor<?> cons;
        try {
            cons = clazz.getDeclaredConstructor();
        }
        catch (final NoSuchMethodException nsme) {
            // We cannot do this on our own, we need to do a little magic
            return magicallyDeliciousObject(clazz);
        }

        cons.setAccessible(true);
        try {
            return cons.newInstance();
        }
        catch (final IllegalAccessException iae) {
            throw new IllegalStateException("Illegal access on accessible constructor: " + iae.getMessage(), iae);
        }
        catch (final InstantiationException ie) {
            throw new SyntaxException("Unable to instantiate " + clazz.getName() + ": " + ie.getMessage(), ie);
        }
        catch (final InvocationTargetException ite) {
            throw new SyntaxException("Error instantiating " + clazz.getName() + ": " + ite.getCause().getMessage(), ite.getCause());
        }
    }

    /**
     * Force construction of an object, even if the object does not itself have
     * a nullary constructor. Requires that the object is {@link Serializable}.
     * This does some very nasty stuff nearly equivalent to magic.
     * 
     * @param clazz The class to instantiate.
     * @return The instantiated object.
     * @throws SyntaxException If there was a problem instantiating the class.
     */
    protected Object magicallyDeliciousObject(final Class<?> clazz)
        throws SyntaxException
    {
        // XXX: We're about to do something really really bad...

        final ObjectStreamClass osc = ObjectStreamClass.lookupAny(clazz);
        final Class<?> oscClazz = osc.getClass();
        final Method m;
        try {
            m = oscClazz.getDeclaredMethod("newInstance", (Class<?>[])null);
        }
        catch (final NoSuchMethodException nsme) {
            throw new IllegalStateException("Unable to locate ObjectStreamClass.newInstance(): " + nsme.getMessage(), nsme);
        }

        m.setAccessible(true);
        try {
            return m.invoke(osc);
        }
        catch (final IllegalAccessException iae) {
            throw new IllegalStateException("IllegalAccessException on an accessible Method: " + iae.getMessage(), iae);
        }
        catch (final InvocationTargetException ite) {
            throw new SyntaxException("Exception while instantiating " + clazz.getName() + ": " + ite.getCause().getMessage(),
                                      ite.getCause());
        }
    }

    /**
     * Build a new array of the given component type and length.
     * 
     * @param clazz The component class for the new array.
     * @return The converted array.
     */
    protected Object[] newArray(final Class<?> clazz, final int length)
    {
        final Object[] objects = (Object[])Array.newInstance(clazz, length);
        return objects;
    }

    /**
     * Locate the named field on the given class.
     * 
     * @param baseClass The class.
     * @param name The field name. If qualified, finds the appropriate field on
     *            the named superclass; if not qualified, finds the first
     *            matching field going up the class stack.
     * @return The field.
     * @throws NoSuchFieldException If unable to locate the given field.
     */
    protected Field findField(final Class<?> baseClass, final String name)
        throws NoSuchFieldException
    {
        Class<?> clazz = baseClass;
        if (name.indexOf('.') == -1) {
            do {
                try {
                    final Field field = clazz.getDeclaredField(name);
                    return field;
                }
                catch (final NoSuchFieldException nsfe) {
                    // Keep trying
                }
            } while ((clazz = clazz.getSuperclass()) != null);

            throw new NoSuchFieldException("Unable to locate field '" + name + "' on class '" + baseClass.getName() +
                                           "' or any superclass.");
        }
        else {
            final String cname = name.substring(0, name.lastIndexOf('.'));
            final String fname = name.substring(name.lastIndexOf('.') + 1);
            while (null != clazz && !clazz.getName().equals(cname)) {
                clazz = clazz.getSuperclass();
            }

            return clazz.getDeclaredField(fname);
        }
    }
}
