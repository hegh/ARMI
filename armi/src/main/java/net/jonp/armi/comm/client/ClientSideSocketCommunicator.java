package net.jonp.armi.comm.client;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.rmi.NotBoundException;

import net.jonp.armi.base.ClassRegistry;
import net.jonp.armi.base.SyntaxException;
import net.jonp.armi.base.command.Command;
import net.jonp.armi.base.response.Response;
import net.jonp.armi.base.response.ResponseParser;

/**
 * A {@link ClientSideCommunicator} that works over a {@link Socket}.
 */
public class ClientSideSocketCommunicator
    implements ClientSideCommunicator
{
    private final Socket _sock;
    private final ResponseParser _parser;
    private final OutputStream _responseStream;

    /**
     * Construct a new SocketCommunicator.
     * 
     * @param sock The socket to use for low-level communications.
     * @param registry The class registry.
     * @throws IOException If there was a problem setting up communications.
     */
    public ClientSideSocketCommunicator(final Socket sock, final ClassRegistry registry)
        throws IOException
    {
        _sock = sock;
        _responseStream = _sock.getOutputStream();
        _parser = new ResponseParser(_sock.getInputStream(), registry);
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
    public Response readNextResponse()
        throws IOException, SyntaxException
    {
        return _parser.readNextResponse();
    }

    @Override
    public void sendCommand(final Command command)
        throws IOException, NotBoundException
    {
        _responseStream.write(command.toStatement(getClassRegistry()).getBytes());
        _responseStream.write('\n');
        _responseStream.flush();
    }

    @Override
    public boolean isClosed()
    {
        return _sock.isClosed();
    }

    @Override
    public String getClientSideName()
    {
        return _sock.getInetAddress().getHostName();
    }
}
