package net.jonp.armi.comm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import net.jonp.armi.base.ClassRegistry;
import net.jonp.armi.base.command.CallCommand;
import net.jonp.armi.base.command.Command;
import net.jonp.armi.base.response.Response;
import net.jonp.armi.base.response.UnsolicitedResponse;
import net.jonp.armi.base.response.ValueResponse;

import org.junit.Test;

public class ResponseDispatcherTest
{
    @Test
    public void testAddUnsolListener()
    {
        final UnsolListener listener = getDummyUnsolListener();
        final UnsolListener listener2 = getDummyUnsolListener();

        final ClassRegistry registry = new DefaultClassRegistry();
        final CommunicatorPipe pipe = new CommunicatorPipe(registry);
        final ResponseDispatcher dispatcher = new ResponseDispatcher(pipe);
        dispatcher.addUnsolListener(".*", listener);
        dispatcher.addUnsolListener("test", listener);
        dispatcher.addUnsolListener("test", listener2);

        final Map<String, Set<UnsolListener>> listeners = dispatcher.getUnsolListeners();
        assertEquals(2, listeners.size());
        assertEquals(1, listeners.get(".*").size());
        assertEquals(listener, listeners.get(".*").iterator().next());
        assertEquals(2, listeners.get("test").size());

        final Set<UnsolListener> testSet = new HashSet<UnsolListener>();
        Collections.addAll(testSet, new UnsolListener[] {
            listener, listener2
        });

        assertTrue(listeners.get("test").containsAll(testSet));
    }

    @Test
    public void testRemoveUnsolListenerUnsolListener()
    {
        final UnsolListener listener = getDummyUnsolListener();

        final ClassRegistry registry = new DefaultClassRegistry();
        final CommunicatorPipe pipe = new CommunicatorPipe(registry);
        final ResponseDispatcher dispatcher = new ResponseDispatcher(pipe);
        dispatcher.addUnsolListener(".*", listener);
        dispatcher.addUnsolListener("test", listener);

        dispatcher.removeUnsolListener(listener);

        final Map<String, Set<UnsolListener>> listeners = dispatcher.getUnsolListeners();
        assertEquals(0, listeners.size());
    }

    @Test
    public void testRemoveUnsolListenerStringUnsolListener()
        throws IOException
    {
        final String responseValue = "Response value";
        final String[] unsolReceived = new String[] {
            null
        };
        final UnsolListener listener = new UnsolListener() {
            @Override
            public void unsolReceived(final String type, final Object value)
            {
                synchronized (unsolReceived) {
                    unsolReceived[0] = (String)value;
                    unsolReceived.notify();
                }
            }
        };

        final ClassRegistry registry = new DefaultClassRegistry();
        final CommunicatorPipe pipe = new CommunicatorPipe(registry);
        final ResponseDispatcher dispatcher = new ResponseDispatcher(pipe);
        dispatcher.addUnsolListener(".*", listener);
        dispatcher.addUnsolListener("test", listener);

        dispatcher.removeUnsolListener("test", listener);

        final Map<String, Set<UnsolListener>> listeners = dispatcher.getUnsolListeners();
        assertEquals(1, listeners.size());
        assertEquals(1, listeners.get(".*").size());
        assertEquals(listener, listeners.get(".*").iterator().next());

        dispatcher.start();
        try {
            pipe.sendResponse(new UnsolicitedResponse("xxx", responseValue));

            // We'll give it 5 seconds
            final long limit = System.currentTimeMillis() + 5000;
            synchronized (unsolReceived) {
                while (limit > System.currentTimeMillis() && null == unsolReceived[0]) {
                    final long remaining = limit - System.currentTimeMillis();

                    if (remaining > 0) {
                        try {
                            unsolReceived.wait(remaining);
                        }
                        catch (final InterruptedException ie) {
                            // Ignore it
                        }
                    }
                }

                assertEquals(responseValue, unsolReceived[0]);
            }
        }
        finally {
            pipe.close();
        }
    }

    @Test
    public void testCallCallCommand()
        throws RemoteException, IOException, NotBoundException, TimeoutException
    {
        final String responseValue = "Response value";
        final ClassRegistry registry = new DefaultClassRegistry();
        final CommunicatorPipe pipe = new CommunicatorPipe(registry);
        final ResponseDispatcher dispatcher = new ResponseDispatcher(pipe, 1000);

        final Thread server = new Thread() {
            @Override
            public void run()
            {
                try {
                    final Command command = pipe.readNextCommand();
                    final Response response = new ValueResponse(command.getLabel(), responseValue);
                    pipe.sendResponse(response);
                }
                catch (final IOException ioe) {
                    throw new RuntimeException("Unexpected IOException: " + ioe.getMessage(), ioe);
                }
            }
        };
        server.start();

        dispatcher.start();
        try {
            final CallCommand call = new CallCommand(null, "test", "test", new Object[0]);
            final Object response = dispatcher.call(call);

            assertEquals(responseValue, response);
        }
        finally {
            pipe.close();
        }
    }

    @Test
    public void testCallCallCommandLong()
        throws RemoteException, IOException, NotBoundException
    {
        final String responseValue = "Response value";
        final ClassRegistry registry = new DefaultClassRegistry();
        final CommunicatorPipe pipe = new CommunicatorPipe(registry);
        final ResponseDispatcher dispatcher = new ResponseDispatcher(pipe, 0);

        final boolean[] commandReceived = new boolean[] {
            false
        };

        final Thread server = new Thread() {
            @Override
            public void run()
            {
                try {
                    final Command command = pipe.readNextCommand();
                    synchronized (commandReceived) {
                        commandReceived[0] = true;
                        commandReceived.notify();
                    }
                }
                catch (final IOException ioe) {
                    throw new RuntimeException("Unexpected IOException: " + ioe.getMessage(), ioe);
                }
            }
        };
        server.start();

        dispatcher.start();
        try {
            final CallCommand call = new CallCommand(null, "test", "test", new Object[0]);
            try {
                final Object response = dispatcher.call(call, 10);
                fail("Expected TimeoutException");
            }
            catch (final TimeoutException te) {
                // This was expected
            }

            // Make sure the command was actually sent
            // We'll give it 5 seconds
            final long limit = System.currentTimeMillis() + 5000;
            synchronized (commandReceived) {
                while (limit > System.currentTimeMillis() && !commandReceived[0]) {
                    final long remaining = limit - System.currentTimeMillis();

                    if (remaining > 0) {
                        try {
                            commandReceived.wait(remaining);
                        }
                        catch (final InterruptedException ie) {
                            // Ignore it
                        }
                    }
                }

                assertEquals(true, commandReceived[0]);
            }
        }
        finally {
            pipe.close();
        }
    }

    @Test
    public void testCallNoResponse()
        throws IOException, NotBoundException
    {
        final String responseValue = "Response value";
        final ClassRegistry registry = new DefaultClassRegistry();
        final CommunicatorPipe pipe = new CommunicatorPipe(registry);
        final ResponseDispatcher dispatcher = new ResponseDispatcher(pipe, 0);

        final boolean[] commandReceived = new boolean[] {
            false
        };

        final Thread server = new Thread() {
            @Override
            public void run()
            {
                try {
                    final Command command = pipe.readNextCommand();
                    synchronized (commandReceived) {
                        commandReceived[0] = true;
                        commandReceived.notify();
                    }
                }
                catch (final IOException ioe) {
                    throw new RuntimeException("Unexpected IOException: " + ioe.getMessage(), ioe);
                }
            }
        };
        server.start();

        dispatcher.start();
        try {
            final CallCommand call = new CallCommand(null, "test", "test", new Object[0]);
            dispatcher.callNoResponse(call);

            // We'll give it 5 seconds
            final long limit = System.currentTimeMillis() + 5000;
            synchronized (commandReceived) {
                while (limit > System.currentTimeMillis() && !commandReceived[0]) {
                    final long remaining = limit - System.currentTimeMillis();

                    if (remaining > 0) {
                        try {
                            commandReceived.wait(remaining);
                        }
                        catch (final InterruptedException ie) {
                            // Ignore it
                        }
                    }
                }

                assertEquals(true, commandReceived[0]);
            }
        }
        finally {
            pipe.close();
        }
    }

    private UnsolListener getDummyUnsolListener()
    {
        final UnsolListener listener = new UnsolListener() {
            @Override
            public void unsolReceived(final String type, final Object value)
            {
                // Ignore it
            }
        };

        return listener;
    }
}
