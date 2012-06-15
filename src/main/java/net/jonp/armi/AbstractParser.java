package net.jonp.armi;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
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

    /**
     * Construct a new AbstractParser.
     * 
     * @param in The stream from which to read command/response language
     *            constructs.
     * @throws IOException If there was a problem initializing the parsing
     *             framework from the stream.
     */
    protected AbstractParser(final InputStream in)
        throws IOException
    {
        final ANTLRInputStream charStream = new ANTLRInputStream(in);
        final ARMILexer lexer = new MSCommandLexer(charStream);
        final CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        parser = new ARMIParser(tokenStream);
    }

    /**
     * Parse the tree from a label.
     * 
     * @param ast The tree.
     * @return The label.
     * @throws CommandException If there was a problem parsing the tree.
     */
    protected String label(final CommonTree ast)
        throws CommandException
    {
        if (ast.getType() != ARMIParser.LABEL) {
            throw new CommandException("Not a LABEL: " + ast.getType());
        }

        if (ast.getChildCount() != 1) {
            throw new CommandException("LABEL childCount != 1: " + ast.getChildCount());
        }

        return str((CommonTree)ast.getChild(0));
    }

    /**
     * Parse the tree from an identifier.
     * 
     * @param ast The tree.
     * @return The path of the identifier (packages, objects, ...).
     * @throws CommandException If there was a problem parsing the tree.
     */
    protected String[] ident(final CommonTree ast)
        throws CommandException
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
     * @throws CommandException If there was a problem parsing the tree.
     */
    protected Object val(final CommonTree ast)
        throws CommandException
    {
        switch (ast.getType()) {
            case ARMIParser.STR:
                return str(ast);
            case ARMIParser.NUM:
                return num(ast);
            case ARMIParser.BOOL:
                return bool(ast);
            case ARMIParser.OBJ:
                return obj(ast);
            default:
                throw new CommandException("Unknown VAL type: " + ast.getType());
        }
    }

    /**
     * Parse the tree from a str.
     * 
     * @param ast The tree.
     * @return The string.
     * @throws CommandException If there was a problem parsing the tree.
     */
    protected String str(final CommonTree ast)
        throws CommandException
    {
        if (ast.getType() != ARMIParser.STR) {
            throw new CommandException("Not a STR: " + ast.getType());
        }

        if (ast.getChildCount() != 1) {
            throw new CommandException("STR childCount != 1: " + ast.getChildCount());
        }

        return ast.getChild(0).getText();
    }

    /**
     * Parse the tree from a num.
     * 
     * @param ast The tree.
     * @return The number.
     * @throws CommandException If there was a problem parsing the tree.
     */
    protected Number num(final CommonTree ast)
        throws CommandException
    {
        if (ast.getType() != ARMIParser.NUM) {
            throw new CommandException("Not a NUM: " + ast.getType());
        }

        if (ast.getChildCount() == 1) {
            return Long.valueOf(ast.getChild(0).getText());
        }
        else if (ast.getChildCount() == 3) {
            return Double.valueOf(ast.getChild(0).getText() + "." + ast.getChild(2).getText());
        }
        else {
            throw new CommandException("NUM childCount != 1 or 3: " + ast.getChildCount());
        }
    }

    /**
     * Parse the tree from a bool.
     * 
     * @param ast The tree.
     * @return The boolean.
     * @throws CommandException If there was a problem parsing the tree.
     */
    protected Boolean bool(final CommonTree ast)
        throws CommandException
    {
        if (ast.getType() != ARMIParser.BOOL) {
            throw new CommandException("Not a BOOL: " + ast.getType());
        }

        if (ast.getChildCount() != 1) {
            throw new CommandException("BOOL childCount != 1: " + ast.getChildCount());
        }

        return Boolean.valueOf(ast.getChild(0).getText());
    }

    /**
     * Parse the tree from an obj.
     * 
     * @param ast The tree.
     * @return The object.
     * @throws CommandException If there was a problem parsing the tree or
     *             instantiating the object.
     */
    protected Object obj(final CommonTree ast)
        throws CommandException
    {
        if (ast.getType() != ARMIParser.OBJ) {
            throw new CommandException("Not a OBJ: " + ast.getType());
        }

        if (ast.getChildCount() != 2) {
            throw new CommandException("OBJ childCount != 2: " + ast.getChildCount());
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
     * @throws CommandException If there was a problem parsing the tree.
     */
    protected Map<String, Object> fields(final CommonTree ast)
        throws CommandException
    {
        if (ast.getType() != ARMIParser.FIELDS) {
            throw new CommandException("Not a FIELDS: " + ast.getType());
        }

        final Map<String, Object> map = new HashMap<String, Object>();
        for (final Object childAST : ast.getChildren()) {
            final CommonTree child = (CommonTree)childAST;
            addField(child, map);
        }

        return map;
    }

    /**
     * Parse the tree from an individual field and add it to a map.
     * 
     * @param ast The tree.
     * @param fields The map to which the field will be added.
     * @throws CommandException If there was a problem parsing the tree.
     */
    protected void addField(final CommonTree ast, final Map<String, Object> fields)
        throws CommandException
    {
        if (ast.getType() != ARMIParser.FIELD) {
            throw new CommandException("Not a FIELD: " + ast.getType());
        }

        if (ast.getChildCount() != 2) {
            throw new CommandException("FIELD childCount != 2: " + ast.getChildCount());
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
     * @throws CommandException If there was a problem constructing the object,
     *             setting its fields, or calling {@link Initializable#init()},
     *             if it applies.
     */
    protected Object buildObject(final String className, final Map<String, Object> fields)
        throws CommandException
    {
        final Class<?> clazz;
        final Object instance;

        try {
            clazz = Class.forName(className);
        }
        catch (final ClassNotFoundException cnfe) {
            throw new CommandException("Unable to locate referenced class " + className + ": " + cnfe.getMessage(), cnfe);
        }

        try {
            instance = clazz.newInstance();
        }
        catch (final InstantiationException ie) {
            throw new CommandException("Cannot instantiate referenced class " + className + ": " + ie.getMessage(), ie);
        }
        catch (final IllegalAccessException iae) {
            throw new CommandException("Cannot access reference class constructor " + className + ": " + iae.getMessage(), iae);
        }

        // FUTURE: Look for set methods and use them rather than fields

        String fieldName = null;
        try {
            for (final Map.Entry<String, Object> entry : fields.entrySet()) {
                fieldName = entry.getKey();
                final Field field = clazz.getField(fieldName);
                field.set(instance, entry.getValue());
            }
        }
        catch (final NoSuchFieldException nsfe) {
            throw new CommandException("No field " + fieldName + " exists on " + className + ": " + nsfe.getMessage(), nsfe);
        }
        catch (final IllegalAccessException iae) {
            throw new CommandException("Cannot access field " + fieldName + " on " + className + ": " + iae.getMessage(), iae);
        }
        catch (final IllegalArgumentException iae) {
            throw new CommandException("Illegal conversion when setting " + fieldName + " on " + className + ": " +
                                       iae.getMessage(), iae);
        }

        if (instance instanceof Initializable) {
            try {
                ((Initializable)instance).init();
            }
            catch (final Throwable th) {
                throw new CommandException("Failed to initialize a " + className + ": " + th.getMessage(), th);
            }
        }

        return instance;
    }
}
