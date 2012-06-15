package net.jonp.armi;

import java.io.IOException;
import java.io.InputStream;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTree;

/**
 * Parses commands into {@link Command} objects.
 */
public class CommandParser
    extends AbstractParser
{
    /**
     * Construct a new CommandParser.
     * 
     * @param in The stream from which to read commands.
     * @throws IOException If there was a problem initializing the command
     *             parsing framework with the stream.
     */
    public CommandParser(final InputStream in)
        throws IOException
    {
        super(in);
    }

    /**
     * Read the next command from this parser.
     * 
     * @return The command that was read.
     * @throws CommandException If there was a problem parsing the command.
     */
    public Command readNextCommand()
        throws CommandException
    {
        ARMIParser.command_return cr;
        try {
            cr = parser.command();
        }
        catch (final RecognitionException re) {
            throw new CommandException("Error parsing command: " + re.getMessage(), re);
        }

        final CommonTree ast = (CommonTree)cr.getTree();
        return command(ast);
    }

    /**
     * Parse the tree from a command.
     * 
     * @param ast The tree.
     * @return The Command object.
     * @throws CommandException If there was a problem parsing the tree.
     */
    private Command command(final CommonTree ast)
        throws CommandException
    {
        if (ast.getType() != ARMIParser.CALL) {
            throw new CommandException("Root of command is not CALL: " + ast.getType());
        }

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
                    throw new CommandException("Illegal child of CALL: " + child.getType());
            }
        }

        return new Command(label, path, args);
    }

    /**
     * Parse the tree from an args command.
     * 
     * @param ast The tree.
     * @return The arguments.
     * @throws CommandException If there was a problem parsing the tree.
     */
    private Object[] args(final CommonTree ast)
        throws CommandException
    {
        if (ast.getType() != ARMIParser.ARGS) {
            throw new CommandException("Not an ARGS: " + ast.getType());
        }

        final Object[] args = new Object[ast.getChildCount()];
        int i = 0;
        for (final Object childAST : ast.getChildren()) {
            final CommonTree child = (CommonTree)childAST;
            args[i++] = arg(child);
        }

        return args;
    }

    /**
     * Parse the tree from an arg object.
     * 
     * @param ast The tree.
     * @return The argument.
     * @throws CommandException If there was a problem parsing the tree.
     */
    private Object arg(final CommonTree ast)
        throws CommandException
    {
        if (ast.getType() != ARMIParser.ARG) {
            throw new CommandException("Not an ARG: " + ast.getType());
        }

        if (ast.getChildCount() != 1) {
            throw new CommandException("ARG childCount != 1: " + ast.getChildCount());
        }

        return val((CommonTree)ast.getChild(0));
    }
}
