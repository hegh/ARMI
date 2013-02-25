package net.jonp.armi.comm.server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.rmi.NotBoundException;

import net.jonp.armi.base.ClassRegistry;
import net.jonp.armi.base.SyntaxException;
import net.jonp.armi.base.command.Command;
import net.jonp.armi.base.command.CommandParser;
import net.jonp.armi.base.response.Response;

/**
 * A {@link ServerSideCommunicator} that works over a {@link Socket}.
 */
public class ServerSideSocketCommunicator
    implements ServerSideCommunicator
{
    private final Socket _sock;
    private final CommandParser _parser;
    private final OutputStream _responseStream;

    /**
     * Construct a new SocketCommunicator.
     * 
     * @param sock The socket to use for low-level communications.
     * @param registry The class registry.
     * @throws IOException If there was a problem setting up communications.
     */
    public ServerSideSocketCommunicator(final Socket sock, final ClassRegistry registry)
        throws IOException
    {
        _sock = sock;
        _responseStream = _sock.getOutputStream();
        _parser = new CommandParser(_sock.getInputStream(), registry);
    }

    /**
     * Get the socket used by this SocketCommunicator.
     * 
     * @return The socket.
     */
    public Socket getSocket()
    {
        return _sock;
    }

    @Override
    public void close()
        throws IOException
    {
        _sock.close();
    }

    @Override
    public ClassRegistry getClassRegistry()
    {
        return _parser.getClassRegistry();
    }

    @Override
    public Command readNextCommand()
        throws IOException, SyntaxException
    {
        return _parser.readNextCommand();
    }

    @Override
    public void sendResponse(final Response response)
        throws IOException, NotBoundException
    {
        _responseStream.write(response.toStatement(getClassRegistry()).getBytes());
        _responseStream.write('\n');
        _responseStream.flush();
    }


    @Override
    public boolean isClosed()
    {
        return _sock.isClosed();
    }

    @Override
    public String getServerSideName()
    {
        return _sock.getInetAddress().getHostName();
    }
}
