package net.jonp.armi.comm;

/**
 * Thrown when trying to reflectively call a method on an object, but the named
 * method cannot be found.
 */
public class MethodNotFoundException
    extends Exception
{
    public MethodNotFoundException(final String message)
    {
        super(message);
    }

    public MethodNotFoundException(final String message, final Throwable cause)
    {
        super(message, cause);
    }
}
