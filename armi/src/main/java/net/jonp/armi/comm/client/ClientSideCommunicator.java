package net.jonp.armi.comm.client;

import java.io.IOException;
import java.rmi.NotBoundException;

import net.jonp.armi.base.ClassRegistry;
import net.jonp.armi.base.SyntaxException;
import net.jonp.armi.base.command.Command;
import net.jonp.armi.base.response.Response;

/**
 * Provides an interface through which the client send commands to, and receive
 * responses from, the server, regardless of the communication medium.
 */
public interface ClientSideCommunicator
{
    /**
     * Get the {@link ClassRegistry} used by this {@link ClientSideCommunicator}
     * .
     * 
     * @return The class registry.
     */
    public ClassRegistry getClassRegistry();

    /**
     * Send a command to the server.
     * 
     * @param command The command to send.
     * @throws IOException If there was a problem writing the command.
     * @throws NotBoundException If thrown by
     *             {@link Command#toStatement(net.jonp.armi.base.ClassRegistry)}
     *             .
     */
    public void sendCommand(Command command)
        throws IOException, NotBoundException;

    /**
     * Get the next response from the server, blocking until one arrives or an
     * exception is thrown.
     * 
     * @return The next response from the server, or <code>null</code> at EOF.
     * @throws IOException If there was a problem reading the response.
     * @throws SyntaxException If there was a problem parsing the response.
     */
    public Response readNextResponse()
        throws IOException, SyntaxException;

    /**
     * Test whether this {@link ClientSideCommunicator} is closed.
     * 
     * @return True if closed, false if open.
     */
    public boolean isClosed();

    /**
     * Close this communicator.
     * 
     * @throws IOException If there was a problem.
     */
    public void close()
        throws IOException;

    /**
     * Get a descriptive name for this client-side communicator.
     * 
     * @return A name for this client-side communicator.
     */
    public String getClientSideName();
}
