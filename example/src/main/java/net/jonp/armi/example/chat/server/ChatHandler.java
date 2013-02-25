package net.jonp.armi.example.chat.server;

import java.io.IOException;

import net.jonp.armi.base.response.UnsolicitedResponse;
import net.jonp.armi.comm.DefaultClassRegistry;
import net.jonp.armi.comm.server.ServerSideCommunicator;
import net.jonp.armi.example.api.ChatMessage;
import net.jonp.armi.example.api.Chatter;
import net.jonp.armi.example.api.NameChange;
import net.jonp.armi.example.chat.Shared;

import org.apache.log4j.Logger;

/**
 * Handles communications activity for a single connection/chatter.
 */
public class ChatHandler
    extends AbstractChatHandler
{
    static final Logger LOG = Logger.getLogger(ChatHandler.class);

    private final ChatServer _chatServer;
    protected final ServerAPI _chatAPI;

    /**
     * Construct a new {@link ChatHandler}.
     * 
     * @param client The {@link ServerSideCommunicator} through which to
     *            communicate with the client. It must use a
     *            {@link DefaultClassRegistry} for its class registry.
     * @param name The initial name for the chatter represented by this
     *            {@link ChatHandler}.
     * @param chatServer The {@link ChatServer}.
     * 
     * @throws IOException If there was a problem retrieving I/O streams from
     *             the object.
     */
    public ChatHandler(final ServerSideCommunicator client, final String name, final ChatServer chatServer)
        throws IOException
    {
        super(client);

        _chatServer = chatServer;

        classRegistry.clear();
        classRegistry.put(ChatMessage.class.getSimpleName(), ChatMessage.class);
        classRegistry.put(NameChange.class.getSimpleName(), NameChange.class);
        classRegistry.put(Chatter.class.getSimpleName(), Chatter.class);

        _chatAPI = new ServerAPI(this, name);
        apiRegistry.put("chat", _chatAPI);
    }

    /**
     * Get the {@link ChatServer} object.
     * 
     * @return The {@link ChatServer} object.
     */
    public ChatServer getChatServer()
    {
        return _chatServer;
    }

    /**
     * Get the client-accessible API.
     * 
     * @return The client-accessible API.
     */
    public ServerAPI getChatAPI()
    {
        return _chatAPI;
    }

    @Override
    public String getChatterName()
    {
        return _chatAPI.getChatter().name;
    }

    @Override
    public void run()
    {
        // Send the backlog of chat messages in the background
        final ChatServer pregame = _chatServer;
        final Thread th = new Thread() {
            @Override
            public void run()
            {
                for (final ChatMessage msg : pregame.getChatMessages()) {
                    sendResponse(new UnsolicitedResponse(Shared.UNSOL_CHAT, msg));
                }
            }
        };
        th.start();

        super.run();
    }

    @Override
    protected boolean isRunning()
    {
        return _chatServer.isRunning();
    }

    @Override
    protected void subDisconnect()
    {
        _chatServer.removeClient(this);
        _chatServer.broadcast(new UnsolicitedResponse(Shared.UNSOL_DELCHATTER, _chatAPI.getChatter().name));
    }
}
