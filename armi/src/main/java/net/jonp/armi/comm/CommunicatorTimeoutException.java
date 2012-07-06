package net.jonp.armi.comm;

import java.io.IOException;

/**
 * Thrown when a communicator times out.
 */
public class CommunicatorTimeoutException
    extends IOException
{
    public CommunicatorTimeoutException()
    {
        // Nothing to do
    }

    public CommunicatorTimeoutException(final String message)
    {
        super(message);
    }

    public CommunicatorTimeoutException(final Throwable cause)
    {
        super(cause);
    }

    public CommunicatorTimeoutException(final String message, final Throwable cause)
    {
        super(message, cause);
    }
}
