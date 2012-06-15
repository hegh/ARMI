package net.jonp.armi;

/**
 * Represents a problem with a command or response.
 */
public class CommandException
    extends Exception
{
    /**
     * Construct a new CommandException.
     * 
     * @param msg The message.
     */
    public CommandException(final String msg)
    {
        super(msg);
    }

    /**
     * Construct a new CommandException.
     * 
     * @param msg The message.
     * @param cause The cause.
     */
    public CommandException(final String msg, final Throwable cause)
    {
        super(msg, cause);
    }
}
