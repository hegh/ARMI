package net.jonp.armi;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.rmi.NotBoundException;
import java.util.HashMap;
import java.util.Map;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.tree.CommonTree;

/**
 * Superclass for command/response parsers.
 */
public abstract class AbstractParser
{
    protected final ARMIParser parser;
    protected final ClassRegistry registry;

    /**
     * Construct a new AbstractParser.
     * 
     * @param in The stream from which to read command/response language
     *            constructs.
     * @param _registry The class registry for looking up instance classes from
     *            command language names.
     * @throws IOException If there was a problem initializing the parsing
     *             framework from the stream.
     */
    protected AbstractParser(final InputStream in, final ClassRegistry _registry)
        throws IOException
    {
        // FIXME: ANTLRInputStream, which is descended from ANTLRStringStream,
        // vacuums the input stream and provides data to ANTLR from the
        // resulting buffer; this is not compatible with on-line command parsing
        // Need to provide commands one-by-one to ANTLR, which means either
        // writing a new ANTLR CharStream, or writing something like
        // MUXInputStream which is more flexible, as it needs to pre-parse the
        // command stream
        final ANTLRInputStream charStream = new ANTLRInputStream(in);
        final ARMILexer lexer = new ARMILexer(charStream);
        final CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        parser = new ARMIParser(tokenStream);
        registry = _registry;
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
     * Parse the tree of a val (str, num, bool, or obj).
     * 
     * @param ast The tree.
     * @return The value.
     * @throws SyntaxException If there was a problem parsing the tree.
     */
    protected Object val(final CommonTree ast)
        throws SyntaxException
    {
        switch (ast.getType()) {
            case ARMIParser.STR:
                return str(ast);
            case ARMIParser.NUM:
                return num(ast);
            case ARMIParser.BOOL:
                return bool(ast);
            case ARMIParser.ARRAY:
                return array(ast);
            case ARMIParser.OBJ:
                return obj(ast);
            case ARMIParser.NIL:
                return null;
            default:
                throw new SyntaxException("Unknown VAL type: " + ast.getType());
        }
    }

    /**
     * Parse the tree from a strings.
     * 
     * @param ast The tree.
     * @return The string array.
     * @throws SyntaxException If there was a problem parsing the tree.
     */
    protected String[] strings(final CommonTree ast)
        throws SyntaxException
    {
        if (ast.getType() != ARMIParser.STRINGS) {
            throw new SyntaxException("Not a STRINGS: " + ast.getType());
        }

        final String[] values = new String[ast.getChildCount()];
        if (values.length > 0) {
            // Otherwise, ast.getChildren() may be null
            int i = 0;
            for (final Object childAST : ast.getChildren()) {
                final CommonTree child = (CommonTree)childAST;
                values[i++] = str(child);
            }
        }

        return values;
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

        if (ast.getChildCount() == 1) {
            return Long.valueOf(ast.getChild(0).getText());
        }
        else if (ast.getChildCount() == 3) {
            return Double.valueOf(ast.getChild(0).getText() + "." + ast.getChild(2).getText());
        }
        else {
            throw new SyntaxException("NUM childCount != 1 or 3: " + ast.getChildCount());
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

        if (ast.getChildCount() != 1) {
            throw new SyntaxException("ARRAY childCount != 1: " + ast.getChildCount());
        }

        return elements((CommonTree)ast.getChild(0));
    }

    /**
     * Parse the tree from an elements.
     * 
     * @param ast The tree.
     * @return The elements.
     * @throws SyntaxException If there was a problem parsing the tree.
     */
    protected Object[] elements(final CommonTree ast)
        throws SyntaxException
    {
        if (ast.getType() != ARMIParser.ELEMENTS) {
            throw new SyntaxException("Not an ELEMENTS: " + ast.getType());
        }

        final Object[] elements = new Object[ast.getChildCount()];
        int i = 0;
        for (final Object childAST : ast.getChildren()) {
            final CommonTree child = (CommonTree)childAST;
            elements[i++] = val(child);
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
        final Map<String, Object> fields = fields((CommonTree)ast.getChild(1));

        return buildObject(Conversion.arrayToString(ident, "."), fields);
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

        final String name = ast.getChild(0).getText();
        final Object val = val((CommonTree)ast.getChild(1));

        fields.put(name, val);
    }

    /**
     * Build an object, given a class name and map of field values.
     * 
     * @param className The name of the class to construct.
     * @param fields A map of field names onto field values.
     * @return The object.
     * @throws SyntaxException If there was a problem constructing the object,
     *             setting its fields, or calling {@link Initializable#init()},
     *             if it applies.
     */
    protected Object buildObject(final String className, final Map<String, Object> fields)
        throws SyntaxException
    {
        final Class<?> clazz;

        try {
            clazz = registry.lookup(className);
        }
        catch (final NotBoundException nbe) {
            throw new SyntaxException("Unable to locate referenced class " + className + ": " + nbe.getMessage(), nbe);
        }

        final Object instance;
        try {
            instance = clazz.newInstance();
        }
        catch (final InstantiationException ie) {
            throw new SyntaxException("Cannot instantiate referenced class " + className + ": " + ie.getMessage(), ie);
        }
        catch (final IllegalAccessException iae) {
            throw new SyntaxException("Cannot access reference class constructor " + className + ": " + iae.getMessage(), iae);
        }

        // FUTURE: Look for set methods and use them rather than fields

        String fieldName = null;
        try {
            for (final Map.Entry<String, Object> entry : fields.entrySet()) {
                fieldName = entry.getKey();
                final Field field = clazz.getField(fieldName);

                // Do some quick conversions (long to int, short, or byte;
                // double to float)
                Object value = entry.getValue();
                if (value instanceof Long) {
                    final Long l = (Long)value;

                    if (field.getType().isInstance(Integer.class) || field.getType().equals(int.class)) {
                        value = Integer.valueOf(l.intValue());
                    }
                    else if (field.getType().isInstance(Short.class) || field.getType().equals(short.class)) {
                        value = Short.valueOf(l.shortValue());
                    }
                    else if (field.getType().isInstance(Byte.class) || field.getType().equals(byte.class)) {
                        value = Byte.valueOf(l.byteValue());
                    }
                }
                else if (value instanceof Double) {
                    final Double d = (Double)value;

                    if (field.getType().isInstance(Float.class) || field.getType().equals(float.class)) {
                        value = Float.valueOf(d.floatValue());
                    }
                }

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
}
