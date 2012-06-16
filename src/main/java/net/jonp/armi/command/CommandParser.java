package net.jonp.armi.command;

import java.io.IOException;
import java.io.InputStream;

import net.jonp.armi.ARMIParser;
import net.jonp.armi.AbstractParser;
import net.jonp.armi.ClassRegistry;
import net.jonp.armi.Conversion;
import net.jonp.armi.SyntaxException;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTree;

/**
 * Parses commands into {@link CallCommand} objects.
 */
public class CommandParser
    extends AbstractParser
{
    /**
     * Construct a new CommandParser.
     * 
     * @param in The stream from which to read commands.
     * @param _registry The class registry for looking up instance classes from
     *            command language names.
     * @throws IOException If there was a problem initializing the command
     *             parsing framework with the stream.
     */
    public CommandParser(final InputStream in, final ClassRegistry _registry)
        throws IOException
    {
        super(in, _registry);
    }

    /**
     * Read the next command from this parser.
     * 
     * @return The command that was read.
     * @throws SyntaxException If there was a problem parsing the command.
     */
    public Command readNextCommand()
        throws SyntaxException
    {
        final ARMIParser.command_return cr;
        try {
            cr = parser.command();
        }
        catch (final RecognitionException re) {
            throw new SyntaxException("Error parsing command: " + re.getMessage(), re);
        }

        final CommonTree ast = (CommonTree)cr.getTree();
        return command(ast);
    }

    /**
     * Parse the tree from a command.
     * 
     * @param ast The tree.
     * @return The Command object.
     * @throws SyntaxException If there was a problem parsing the tree.
     */
    private Command command(final CommonTree ast)
        throws SyntaxException
    {
        switch (ast.getType()) {
            case ARMIParser.CALL: {
                String label = null;
                String[] path = null;
                Object[] args = null;

                for (final Object childAST : ast.getChildren()) {
                    final CommonTree child = (CommonTree)childAST;
                    switch (child.getType()) {
                        case ARMIParser.LABEL:
                            label = label(child);
                            break;
                        case ARMIParser.IDENT:
                            path = ident(child);
                            break;
                        case ARMIParser.ARGS:
                            args = args(child);
                            break;
                        default:
                            throw new SyntaxException("Illegal child of CALL: " + child.getType());
                    }
                }

                return new CallCommand(label, Conversion.arrayToString(path, 0, path.length - 1, "."), path[path.length - 1], args);
            }
            case ARMIParser.HELP:
                return new HelpCommand();
            case ARMIParser.LIST:
                return list(ast);
            default:
                throw new SyntaxException("Root of command is not CALL, HELP, or LIST: " + ast.getType());
        }
    }

    /**
     * Parse the tree from a LIST command.
     * 
     * @param ast The tree.
     * @return The List command.
     * @throws SyntaxException If there was a problem parsing the tree.
     */
    private ListCommand list(final CommonTree ast)
        throws SyntaxException
    {
        if (ast.getType() != ARMIParser.LIST) {
            throw new SyntaxException("Not a LIST: " + ast.getType());
        }

        String label = null;
        String object = null;

        for (final Object childAST : ast.getChildren()) {
            final CommonTree child = (CommonTree)childAST;
            switch (child.getType()) {
                case ARMIParser.LABEL:
                    label = label(child);
                    break;
                case ARMIParser.METHODS:
                    object = Conversion.arrayToString(ident((CommonTree)child.getChild(0)), ".");
                    break;
                case ARMIParser.OBJECTS:
                    object = null;
                    break;
                default:
                    throw new SyntaxException("Child of LIST is not LABEL, METHODS, or OBJECTS: " + child.getType());
            }
        }

        return new ListCommand(label, object);
    }

    /**
     * Parse the tree from an args command.
     * 
     * @param ast The tree.
     * @return The arguments.
     * @throws SyntaxException If there was a problem parsing the tree.
     */
    private Object[] args(final CommonTree ast)
        throws SyntaxException
    {
        if (ast.getType() != ARMIParser.ARGS) {
            throw new SyntaxException("Not an ARGS: " + ast.getType());
        }

        final Object[] args = new Object[ast.getChildCount()];
        if (args.length > 0) {
            // Otherwise getChildren() may be null
            int i = 0;
            for (final Object childAST : ast.getChildren()) {
                final CommonTree child = (CommonTree)childAST;
                args[i++] = arg(child);
            }
        }

        return args;
    }

    /**
     * Parse the tree from an arg object.
     * 
     * @param ast The tree.
     * @return The argument.
     * @throws SyntaxException If there was a problem parsing the tree.
     */
    private Object arg(final CommonTree ast)
        throws SyntaxException
    {
        if (ast.getType() != ARMIParser.ARG) {
            throw new SyntaxException("Not an ARG: " + ast.getType());
        }

        if (ast.getChildCount() != 1) {
            throw new SyntaxException("ARG childCount != 1: " + ast.getChildCount());
        }

        return val((CommonTree)ast.getChild(0));
    }
}
