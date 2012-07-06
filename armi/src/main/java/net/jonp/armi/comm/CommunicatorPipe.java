package net.jonp.armi.comm;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.LinkedList;
import java.util.Queue;

import net.jonp.armi.base.ClassRegistry;
import net.jonp.armi.base.command.Command;
import net.jonp.armi.base.response.Response;

/**
 * Acts as both a {@link ClientSideCommunicator} and a
 * {@link ServerSideCommunicator}.
 */
public class CommunicatorPipe
    implements ClientSideCommunicator, ServerSideCommunicator
{
    public static final long TIMEOUT = 3000;

    protected final ClassRegistry _registry;
    protected final long _timeout;
    protected final Queue<Response> _responses = new LinkedList<Response>();
    protected final Queue<Command> _commands = new LinkedList<Command>();

    protected boolean _closed = false;

    /**
     * Construct a {@link CommunicatorPipe} with the given class registry and
     * the default timeout ({@link #TIMEOUT}).
     * 
     * @param registry The registry.
     */
    public CommunicatorPipe(final ClassRegistry registry)
    {
        this(registry, TIMEOUT);
    }

    /**
     * Construct a {@link CommunicatorPipe} with the given class registry and
     * timeout.
     * 
     * @param registry The class registry to use.
     * @param timeout The timeout to use, in milliseconds. Specify 0 to wait
     *            forever for messages. A {@link CommunicatorTimeoutException}
     *            will be thrown if the timeout expires.
     */
    public CommunicatorPipe(final ClassRegistry registry, final long timeout)
    {
        if (timeout < 0) {
            throw new IllegalArgumentException("Negative timeout is illegal: " + timeout);
        }

        _registry = registry;
        _timeout = timeout;
    }

    /**
     * Get the timeout, in milliseconds, to wait for messages. If 0, will wait
     * forever.
     * 
     * @return The timeout, in milliseconds.
     */
    public long getTimeout()
    {
        return _timeout;
    }

    @Override
    public ClassRegistry getClassRegistry()
    {
        return _registry;
    }

    @Override
    public void sendCommand(final Command command)
        throws IOException
    {
        sendItem(_commands, command, "command");
    }

    @Override
    public void sendResponse(final Response response)
        throws IOException
    {
        sendItem(_responses, response, "response");
    }

    @Override
    public Response readNextResponse()
        throws IOException
    {
        return readNextItem(_responses, "response");
    }

    @Override
    public Command readNextCommand()
        throws IOException
    {
        return readNextItem(_commands, "command");
    }

    @Override
    public boolean isClosed()
    {
        return _closed;
    }

    @Override
    public void close()
    {
        _closed = true;

        synchronized (_commands) {
            _commands.clear();
            _commands.notifyAll();
        }

        synchronized (_responses) {
            _responses.clear();
            _responses.notifyAll();
        }
    }

    @Override
    public String getClientSideName()
    {
        return "ClientPipe";
    }

    @Override
    public String getServerSideName()
    {
        return "ServerPipe";
    }

    /**
     * Read an item from the given queue. Blocks until an item becomes
     * available.
     * 
     * @param queue The queue from which the item will be read.
     * @param type The name of the item type ("command" or "response").
     * @return The next available item from the queue, or <code>null</code> if
     *         the communicator is closed before an item arrives.
     * @throws IOException If there was a problem, such as a timeout or a thread
     *             interruption while waiting.
     */
    protected <T> T readNextItem(final Queue<T> queue, final String type)
        throws IOException
    {
        if (isClosed()) {
            return null;
        }

        synchronized (queue) {
            final boolean forever = (getTimeout() == 0);
            final long limit = System.currentTimeMillis() + getTimeout();
            while (!isClosed() && (forever || limit > System.currentTimeMillis()) && queue.isEmpty()) {
                try {
                    if (forever) {
                        queue.wait();
                    }
                    else {
                        final long remaining = limit - System.currentTimeMillis();
                        if (remaining > 0) {
                            queue.wait(remaining);
                        }
                    }
                }
                catch (final InterruptedException ie) {
                    throw new InterruptedIOException("Interrupted waiting for a " + type);
                }
            }

            final T item = queue.poll();
            if (null == item) {
                if (isClosed()) {
                    return null;
                }
                else {
                    throw new CommunicatorTimeoutException("Timed out waiting for a " + type);
                }
            }

            return item;
        }
    }

    /**
     * Send an item through this communicator by adding it to the given queue.
     * 
     * @param queue The queue to which the item will be added.
     * @param item The item to add to the queue.
     * @param type The name of the item type ("command" or "response").
     * @throws IOException If there was a problem, such as trying to send the
     *             item through a closed communicator.
     */
    protected <T> void sendItem(final Queue<T> queue, final T item, final String type)
        throws IOException
    {
        if (isClosed()) {
            throw new IOException("Cannot send a " + type + " through a closed communicator");
        }

        synchronized (queue) {
            queue.add(item);
            queue.notify();
        }
    }
}
