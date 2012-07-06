package net.jonp.armi.example.chat.client;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.concurrent.TimeoutException;

import net.jonp.armi.base.command.CallCommand;
import net.jonp.armi.comm.ResponseDispatcher;
import net.jonp.armi.example.api.Chatter;
import net.jonp.armi.example.chat.ChatAPI;

/**
 * The client side of the Chat RMI interface.
 */
public class ClientAPI
    implements ChatAPI
{
    private final ResponseDispatcher _dispatcher;

    public ClientAPI(final ResponseDispatcher dispatcher)
    {
        _dispatcher = dispatcher;
    }

    public ResponseDispatcher getDispatcher()
    {
        return _dispatcher;
    }

    @Override
    public Chatter getChatter()
        throws RemoteException, IOException, NotBoundException, TimeoutException
    {
        return (Chatter)_dispatcher.call(new CallCommand(null, "chat", "getChatter", new Object[0]));
    }

    @Override
    public void setName(final String name)
        throws IOException, NotBoundException
    {
        _dispatcher.callNoResponse(new CallCommand(null, "chat", "setName", new Object[] {
            name
        }));
    }

    @Override
    public void sendMessage(final String message)
        throws IOException, NotBoundException
    {
        _dispatcher.callNoResponse(new CallCommand(null, "chat", "sendMessage", new Object[] {
            message
        }));
    }

    @Override
    public Chatter[] getChatters()
        throws RemoteException, IOException, NotBoundException, TimeoutException
    {
        return (Chatter[])_dispatcher.call(new CallCommand(null, "chat", "getChatters", new Object[0]));
    }
}
