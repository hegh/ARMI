package net.jonp.armi.base;

/**
 * Represents a problem with a command or response.
 */
public class SyntaxException
    extends Exception
{
    /**
     * Construct a new CommandException.
     * 
     * @param msg The message.
     */
    public SyntaxException(final String msg)
    {
        super(msg);
    }

    /**
     * Construct a new CommandException.
     * 
     * @param msg The message.
     * @param cause The cause.
     */
    public SyntaxException(final String msg, final Throwable cause)
    {
        super(msg, cause);
    }
}
