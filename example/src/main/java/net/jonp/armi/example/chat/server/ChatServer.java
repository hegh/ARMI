package net.jonp.armi.example.chat.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.jonp.armi.base.response.UnsolicitedResponse;
import net.jonp.armi.comm.DefaultClassRegistry;
import net.jonp.armi.comm.server.ServerSideSocketCommunicator;
import net.jonp.armi.example.api.ChatMessage;
import net.jonp.armi.example.chat.Shared;

import org.apache.log4j.Logger;

/**
 * Listens for new chat clients and hands them off to handler threads.
 */
public class ChatServer
    extends Thread
{
    private static final Logger LOG = Logger.getLogger(ChatServer.class);

    private final ServerSocket _lsock;
    private final List<ChatMessage> _chatMessages = new LinkedList<ChatMessage>();
    private final Set<ChatHandler> _clients = new HashSet<ChatHandler>();

    private boolean _running = false;

    /**
     * Construct a new {@ChatServer}.
     * 
     * @param listenAddr The Internet address on which to listen. Use
     *            <code>null</code> to listen on all local interfaces.
     * @param port The port on which to listen.
     * @param backlog The connection backlog.
     * @throws IOException If there was a problem setting up the listening
     *             socket.
     */
    public ChatServer(final InetAddress listenAddr, final int port, final int backlog)
        throws IOException
    {
        super("ChatServer");

        _lsock = new ServerSocket(port, backlog, listenAddr);
        _lsock.setSoTimeout(3000);
    }

    /**
     * Check whether this {@link ChatServer} is running.
     * 
     * @return True if running, false if not.
     */
    public boolean isRunning()
    {
        return _running;
    }

    /**
     * Signal this {@link ChatServer} to shut down.
     */
    public void shutdown()
    {
        _running = false;
    }

    /**
     * Remove a connected client.
     * 
     * @param handler The client.
     */
    public void removeClient(final ChatHandler handler)
    {
        synchronized (_clients) {
            _clients.remove(handler);
        }
    }

    /**
     * Get a copy of the entire collection of {@link ChatHandler}s.
     * 
     * @return A copy of the collection of {@link ChatHandler}s.
     */
    public Collection<ChatHandler> getClients()
    {
        synchronized (_clients) {
            return new ArrayList<ChatHandler>(_clients);
        }
    }

    /**
     * Add a chat message to the history of chat messages.
     * 
     * @param chatMessage The message to add.
     */
    public void addChatMessage(final ChatMessage chatMessage)
    {
        synchronized (_chatMessages) {
            _chatMessages.add(chatMessage);
        }

        final UnsolicitedResponse unsol = new UnsolicitedResponse(Shared.UNSOL_CHAT, chatMessage);
        broadcast(unsol);
    }

    /**
     * Get a copy of the list of chat messages.
     * 
     * @return A copy of the list of chat messages.
     */
    public List<ChatMessage> getChatMessages()
    {
        synchronized (_chatMessages) {
            return new ArrayList<ChatMessage>(_chatMessages);
        }
    }

    /**
     * Broadcast an unsolicited message to all {@link ChatHandler}s.
     * 
     * @param unsol The unsolicited message to broadcast.
     */
    public void broadcast(final UnsolicitedResponse unsol)
    {
        synchronized (_clients) {
            for (final ChatHandler handler : _clients) {
                handler.sendResponse(unsol);
            }
        }
    }

    @Override
    public void run()
    {
        _running = true;

        int nextChatter = 1;

        while (isRunning()) {
            try {
                final Socket sock = _lsock.accept();
                final String name = String.format("Chatter %d", nextChatter++);

                LOG.info("New connection received, starting ChatHandler " + name);

                final ChatHandler client = new ChatHandler(new ServerSideSocketCommunicator(sock, new DefaultClassRegistry()), name, this);
                synchronized (_clients) {
                    _clients.add(client);
                }

                client.start();

                broadcast(new UnsolicitedResponse(Shared.UNSOL_ADDCHATTER, name));
            }
            catch (final SocketTimeoutException ste) {
                // Ignore it
            }
            catch (final IOException ioe) {
                LOG.error(ioe.getMessage(), ioe);
            }
        }

        waitForStop();
    }

    /**
     * Wait until all handlers have stopped.
     */
    private void waitForStop()
    {
        synchronized (_clients) {
            for (final ChatHandler handler : _clients) {
                while (handler.isAlive()) {
                    try {
                        handler.join();
                    }
                    catch (final InterruptedException ie) {
                        LOG.warn("Unexpected thread interruption; ignoring", ie);
                    }
                }
            }
        }
    }
}
