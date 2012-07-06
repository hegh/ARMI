package net.jonp.armi.comm;

import java.io.IOException;
import java.rmi.NotBoundException;

import net.jonp.armi.base.ClassRegistry;
import net.jonp.armi.base.SyntaxException;
import net.jonp.armi.base.command.Command;
import net.jonp.armi.base.response.Response;

/**
 * Provides an interface through which the server may receive commands from, and
 * send responses to, the client, regardless of the communication medium.
 */
public interface ServerSideCommunicator
{
    /**
     * Get the {@link ClassRegistry} used by this {@link ServerSideCommunicator}
     * .
     * 
     * @return The class registry.
     */
    public ClassRegistry getClassRegistry();

    /**
     * Get the next command from the client, blocking until one arrives or an
     * exception is thrown.
     * 
     * @return The next command from this client, or <code>null</code> at EOF.
     * @throws IOException If there was a problem reading the command.
     * @throws SyntaxException If there was a problem parsing the command.
     */
    public Command readNextCommand()
        throws IOException, SyntaxException;

    /**
     * Send a response to the client.
     * 
     * @param response The response to send.
     * @throws IOException If there was a problem writing the response.
     * @throws NotBoundException If thrown by
     *             {@link Response#toStatement(net.jonp.armi.base.ClassRegistry)}
     *             .
     */
    public void sendResponse(Response response)
        throws IOException, NotBoundException;

    /**
     * Test whether this {@link ServerSideCommunicator} is closed.
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
     * Get a descriptive name for this server-side communicator.
     * 
     * @return A name for the server-side communicator.
     */
    public String getServerSideName();
}
