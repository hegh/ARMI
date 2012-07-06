package net.jonp.armi.example.chat.server;

import java.util.Collection;

import net.jonp.armi.base.response.UnsolicitedResponse;
import net.jonp.armi.example.api.ChatMessage;
import net.jonp.armi.example.api.Chatter;
import net.jonp.armi.example.api.NameChange;
import net.jonp.armi.example.chat.ChatAPI;
import net.jonp.armi.example.chat.Shared;

/**
 * Provides necessary server implementations of the chat APIs.
 */
public class ServerAPI
    implements ChatAPI
{
    private final ChatHandler _chatHandler;
    private final Chatter _chatter;

    private final int _nextMessage = 0;

    /**
     * Construct a new {@link ServerAPI}.
     * 
     * @param chatHandler The {@link ChatHandler} that will be executing
     *            commands in this API.
     * @param name The initial name for this chatter.
     */
    public ServerAPI(final ChatHandler chatHandler, final String name)
    {
        _chatHandler = chatHandler;

        _chatter = new Chatter();
        _chatter.name = name;
    }

    @Override
    public Chatter getChatter()
    {
        return _chatter;
    }

    @Override
    public void setName(final String name)
    {
        // Do this first to prevent race conditions; we will roll-back if we see
        // a duplicate
        // This can, however, cause weird results if other chatters request the
        // data before we finish and there was a duplicate...
        final String oldname = _chatter.name;
        _chatter.name = name;

        final Chatter[] chatters = getChatters();
        for (final Chatter chatter : chatters) {
            if (_chatter != chatter && name.equals(chatter.name)) {
                _chatter.name = oldname;
                throw new IllegalArgumentException("Name already in use: " + name);
            }
        }

        _chatHandler.getChatServer().broadcast(new UnsolicitedResponse(Shared.UNSOL_NAMECHANGE, new NameChange(oldname, name)));
    }

    @Override
    public void sendMessage(final String message)
    {
        final ChatMessage msg = new ChatMessage();
        msg.timestamp = System.currentTimeMillis();
        msg.from = _chatter.name;
        msg.message = message;

        _chatHandler.getChatServer().addChatMessage(msg);
    }

    @Override
    public Chatter[] getChatters()
    {
        final Collection<ChatHandler> clients = _chatHandler.getChatServer().getClients();
        final Chatter[] chatters = new Chatter[clients.size()];
        int index = 0;
        for (final ChatHandler client : clients) {
            chatters[index++] = client.getChatAPI().getChatter();
        }

        return chatters;
    }
}
