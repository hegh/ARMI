package net.jonp.armi.comm;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import net.jonp.armi.base.SyntaxException;
import net.jonp.armi.base.command.CallCommand;
import net.jonp.armi.base.response.ErrorResponse;
import net.jonp.armi.base.response.Response;
import net.jonp.armi.base.response.UnsolicitedResponse;
import net.jonp.armi.base.response.ValueResponse;

import org.apache.log4j.Logger;

/**
 * Reads responses and dispatches them to the appropriate listener(s).
 */
public class ResponseDispatcher
    extends Thread
{
    private static final Logger LOG = Logger.getLogger(ResponseDispatcher.class);

    /** Default timeout (in milliseconds) when waiting for a response. */
    public static final long TIMEOUT_MS = 30000;

    private final ClientSideCommunicator _comm;
    private final AtomicInteger nextLabel = new AtomicInteger(0);
    private final long _timeout;
    private final Map<String, ResponseListener> responseListeners = new HashMap<String, ResponseListener>();
    private final Map<String, Set<UnsolListener>> unsolListeners = new HashMap<String, Set<UnsolListener>>();
    private final Map<String, Pattern> patternCache = new HashMap<String, Pattern>();

    private final Set<Class<? extends IOException>> suppressedExceptions = new HashSet<Class<? extends IOException>>();


    /**
     * Construct a new {@link ResponseDispatcher} with the default timeout (
     * {@link #TIMEOUT_MS}).
     * 
     * @param comm The {@link ClientSideCommunicator} to use for communications.
     */
    public ResponseDispatcher(final ClientSideCommunicator comm)
    {
        this(comm, TIMEOUT_MS);
    }

    /**
     * Construct a new {@link ResponseDispatcher} with a custom timeout.
     * 
     * @param comm The {@link ClientSideCommunicator} to use for communications.
     * @param timeout The default timeout for {@link #call(CallCommand)}, in
     *            milliseconds.
     */
    public ResponseDispatcher(final ClientSideCommunicator comm, final long timeout)
    {
        super("ResponseDispatcher[" + comm.getClientSideName() + "]");

        _comm = comm;
        _timeout = timeout;
    }

    /**
     * Don't bother logging exceptions of the given type during response
     * reading/parsing.
     * 
     * @param clazz The class of exceptions to suppress.
     */
    public void addSuppressedException(final Class<? extends IOException> clazz)
    {
        synchronized (suppressedExceptions) {
            suppressedExceptions.add(clazz);
        }
    }

    /**
     * Get this {@link ResponseDispatcher}'s {@link ClientSideCommunicator}.
     * 
     * @return The {@link ClientSideCommunicator} used for communications to a
     *         server.
     */
    public ClientSideCommunicator getCommunicator()
    {
        return _comm;
    }

    /**
     * Get the default timeout, in milliseconds, used by
     * {@link #call(CallCommand)}.
     * 
     * @return The default timeout, in milliseconds.
     */
    public long getTimeout()
    {
        return _timeout;
    }

    /**
     * Add a listener for unsolicited messages from the server.
     * 
     * @param pattern A regular expression to match the 'type' field of the
     *            unsolicited messages.
     * @param ul The listener to add. This listener will be notified of any new
     *            unsolicited messages from the server where the type field
     *            matches the given pattern.
     */
    public void addUnsolListener(final String pattern, final UnsolListener ul)
    {
        Set<UnsolListener> listeners;
        synchronized (unsolListeners) {
            listeners = unsolListeners.get(pattern);

            if (null == listeners) {
                listeners = new HashSet<UnsolListener>();
                unsolListeners.put(pattern, listeners);
            }

            listeners.add(ul);

            if (!patternCache.containsKey(pattern)) {
                patternCache.put(pattern, Pattern.compile(pattern));
            }
        }
    }

    /**
     * Remove a listener of unsolicited messages from this
     * {@link ResponseDispatcher}.
     * 
     * @param ul The listener to remove. This listener will be removed from all
     *            patterns for which it was registered.
     */
    public void removeUnsolListener(final UnsolListener ul)
    {
        synchronized (unsolListeners) {
            for (final Iterator<Map.Entry<String, Set<UnsolListener>>> itEntry = unsolListeners.entrySet().iterator(); itEntry
                .hasNext();) {
                final Map.Entry<String, Set<UnsolListener>> entry = itEntry.next();
                entry.getValue().remove(ul);

                if (entry.getValue().isEmpty()) {
                    patternCache.remove(entry.getKey());
                    itEntry.remove();
                }
            }
        }
    }

    /**
     * Remove a listener of unsolicited messages for a single pattern from this
     * {@link ResponseDispatcher}.
     * 
     * @param pattern The regular expression from which to remove the listener.
     * @param ul The listener to remove.
     */
    public void removeUnsolListener(final String pattern, final UnsolListener ul)
    {
        synchronized (unsolListeners) {
            final Set<UnsolListener> v = unsolListeners.get(pattern);
            if (null != v) {
                v.remove(ul);

                if (v.isEmpty()) {
                    patternCache.remove(pattern);
                    unsolListeners.remove(pattern);
                }
            }
        }
    }

    @Override
    public void run()
    {
        while (!getCommunicator().isClosed()) {
            final Response response;
            try {
                response = getCommunicator().readNextResponse();
            }
            catch (final IOException ioe) {
                final boolean logit;
                synchronized (suppressedExceptions) {
                    logit = !suppressedExceptions.contains(ioe.getClass());
                }

                if (logit) {
                    LOG.warn("IOException during response reading/parsing", ioe);
                }

                continue;
            }
            catch (final SyntaxException se) {
                LOG.warn("SyntaxException during response parsing", se);
                continue;
            }

            if (response instanceof ValueResponse || response instanceof ErrorResponse) {
                LOG.info("Received a " + response.getClass().getSimpleName() + " labeled '" + response.getLabel() + "'");

                final String label = response.getLabel();
                final ResponseListener rl;
                synchronized (responseListeners) {
                    rl = responseListeners.get(label);
                }

                if (null != rl) {
                    rl.response(response);
                }
            }
            else if (response instanceof UnsolicitedResponse) {
                final UnsolicitedResponse unsol = (UnsolicitedResponse)response;

                LOG.info("Received an UnsolicitedResponse of type '" + unsol.getType() + "'");
                fireUnsolicitedResponse(unsol);
            }
            else {
                LOG.error("Unrecognized response type: " + response.getClass().getName());
            }
        }
    }

    /**
     * Call a remote method and wait for a response using the timeout configured
     * for this {@link ResponseDispatcher}.
     * 
     * @param cmd The call command to send.
     * @return The object returned in the response.
     * @throws RemoteException If there was an exception on the remote end.
     * @throws IOException If there was an exception sending the command over
     *             the {@link ClientSideCommunicator} associated with this
     *             {@link ResponseDispatcher}.
     * @throws NotBoundException If the {@link ClientSideCommunicator} is unable
     *             to serialize the {@link CallCommand}.
     * @throws TimeoutException If no response is received from the server
     *             before the timeout expires.
     */
    public Object call(final CallCommand cmd)
        throws RemoteException, IOException, NotBoundException, TimeoutException
    {
        return call(cmd, getTimeout());
    }

    /**
     * Call a remote method and wait for a response using a custom timeout.
     * 
     * @param cmd The call command to send.
     * @param timeout The timeout to use, in milliseconds.
     * @return The object returned in the response.
     * @throws RemoteException If there was an exception on the remote end, or
     *             if the remote sent something expected.
     * @throws IOException If there was an exception sending the command over
     *             the {@link ClientSideCommunicator} associated with this
     *             {@link ResponseDispatcher}.
     * @throws NotBoundException If the {@link ClientSideCommunicator} is unable
     *             to serialize the {@link CallCommand}.
     * @throws TimeoutException If no response is received from the server
     *             before the timeout expires.
     */
    public Object call(final CallCommand cmd, final long timeout)
        throws RemoteException, IOException, NotBoundException, TimeoutException
    {
        final String label = String.format("%d", nextLabel.getAndIncrement());
        cmd.setLabel(label);

        final Response[] rx = new Response[1];
        final ResponseListener rl = new ResponseListener() {
            @Override
            public void response(final Response response)
            {
                rx[0] = response;

                synchronized (rx) {
                    rx.notify();
                }
            }
        };

        synchronized (rx) {
            setResponseListener(label, rl);
            getCommunicator().sendCommand(cmd);

            final long limit = System.currentTimeMillis() + timeout;
            while (null == rx[0] && System.currentTimeMillis() < limit) {
                final long remaining = limit - System.currentTimeMillis();
                if (remaining > 0) {
                    try {
                        rx.wait(remaining);
                    }
                    catch (final InterruptedException ie) {
                        // Ignore it
                    }
                }
            }

            delResponseListener(label);
        }

        if (null != rx[0]) {
            if (rx[0] instanceof ValueResponse) {
                return ((ValueResponse)rx[0]).getValue();
            }
            else if (rx[0] instanceof ErrorResponse) {
                final Throwable th = ((ErrorResponse)rx[0]).getException();
                throw new RemoteException("Remote exception: " + th.getMessage(), th);
            }
            else {
                throw new RemoteException("Unrecognized result type: " + rx[0].getClass().getName());
            }
        }
        else {
            throw new TimeoutException("Timeout waiting for response to CallCommand");
        }
    }

    /**
     * Call a remote method and do not wait for a response; any response will be
     * ignored when it arrives.
     * 
     * @param cmd The call command to send.
     * @throws IOException If there was an exception sending the command over
     *             the {@link ClientSideCommunicator} associated with this
     *             {@link ResponseDispatcher}.
     * @throws NotBoundException If the {@link ClientSideCommunicator} is unable
     *             to serialize the {@link CallCommand}.
     */
    public void callNoResponse(final CallCommand cmd)
        throws IOException, NotBoundException
    {
        getCommunicator().sendCommand(cmd);
    }

    /**
     * Set a response listener for value and error responses, by label. Only one
     * listener may be associated with a label, so this will replace any
     * existing listener registered with the label.
     * 
     * @param label The label. A value or error response received with this
     *            label will activate the given response listener.
     * @param rl The response listener to notify when value and error responses
     *            with the given label are received.
     */
    protected void setResponseListener(final String label, final ResponseListener rl)
    {
        synchronized (responseListeners) {
            responseListeners.put(label, rl);
        }
    }

    /**
     * Remove the response listener associated with the given label.
     * 
     * @param label The label.
     */
    protected void delResponseListener(final String label)
    {
        synchronized (responseListeners) {
            responseListeners.remove(label);
        }
    }

    /**
     * Notify each registered {@link UnsolListener} whose registered pattern
     * matches the type of the given response that such a response has been
     * received.
     * 
     * @param ur The unsolicited response that was received.
     */
    protected void fireUnsolicitedResponse(final UnsolicitedResponse ur)
    {
        LOG.debug("Searching for type matches for " + ur.getType());

        synchronized (unsolListeners) {
            for (final Map.Entry<String, Set<UnsolListener>> entry : unsolListeners.entrySet()) {
                final Pattern pattern = patternCache.get(entry.getKey());
                if (pattern.matcher(ur.getType()).matches()) {
                    LOG.debug("Pattern " + pattern + " matches, notifying " + entry.getValue().size() + " listeners");
                    for (final UnsolListener ul : entry.getValue()) {
                        try {
                            ul.unsolReceived(ur.getType(), ur.getValue());
                        }
                        catch (final Throwable th) {
                            LOG.warn("Exception during UnsolListener.unsolReceived()", th);
                        }
                    }
                }
                else {
                    LOG.debug("Pattern " + pattern + " does not match");
                }
            }
        }
    }

    /**
     * Receives a notification when a value or error response is received
     * matching a given registration.
     */
    protected static interface ResponseListener
    {
        /**
         * Called when a response is received matching this listener's
         * registration.
         * 
         * @param response The response that was received.
         */
        public void response(Response response);
    }
}
