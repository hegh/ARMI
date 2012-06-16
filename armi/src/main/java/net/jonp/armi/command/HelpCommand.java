package net.jonp.armi.command;

import net.jonp.armi.ClassRegistry;
import net.jonp.armi.response.Response;


/**
 * Represents a Help request. The expected response is available as
 * {@link #getDefaultResponse()}.
 */
public class HelpCommand
    extends Command
{
    /**
     * Construct a new HelpCommand. Help commands are not labeled.
     */
    public HelpCommand()
    {
        super(null);
    }

    /**
     * Get the default response (easily human-readable) to a help command.
     * 
     * @return The default response.
     */
    public Response getDefaultResponse()
    {
        return new Response(null) {
            @Override
            public String toStatement(final ClassRegistry registry)
            {
                return "call [label \"<label>\"] <object>.<method> (<value>, ...)\n" + //
                       "help\n" + //
                       "\n" + //
                       "<value> can be:\n" + //
                       "  strings surrounded by \"\"\n" + //
                       "  integers or floats with type postfix\n" + //
                       "    B for Byte, S for Short, L for Long, F for Float\n" + //
                       "    Integer and Double do not have type postfixes\n" + //
                       "  true or false\n" + //
                       "  array(<type>) [<element0>, <element1>, ...]\n" + //
                       "  collection(<type>) [<element0>, <element1>, ...]\n" + //
                       "  map(<type>) [<key0> = <value0>, <key1> = <value1>, ...]\n" + //
                       "  <object name> (<field> = <value>, ...)\n" + //
                       "  null\n" + //
                       "Each command must be on one line and end with a newline.\n" + //
                       "For more information, try 'call lang.getMethods (\"lang\")'\n" + //
                       "and 'call lang.getParameters (\"<object>.<method>\")'";
            }
        };
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "help";
    }

    /**
     * A wrapper around {@link #toStatement(ClassRegistry)} that passes
     * <code>null</code> for the {@link ClassRegistry} argument.
     * 
     * @return The statement form of this Help.
     */
    public String toStatement()
    {
        return toStatement(null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.jonp.armi.AbstractLanguageObject#toStatement(net.jonp.ms5.
     * command.ClassRegistry)
     */
    @Override
    public String toStatement(final ClassRegistry registry)
    {
        return "help";
    }
}
