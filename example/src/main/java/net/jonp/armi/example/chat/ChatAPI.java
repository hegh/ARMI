package net.jonp.armi.example.chat;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.concurrent.TimeoutException;

import net.jonp.armi.base.command.CallCommand;
import net.jonp.armi.comm.ClientSideCommunicator;
import net.jonp.armi.comm.ResponseDispatcher;
import net.jonp.armi.example.api.Chatter;


/**
 * RMI interface for client/server communications.
 */
public interface ChatAPI
{
    /**
     * Get the {@link Chatter} object that represents this client.
     * 
     * @return The {@link Chatter} that represents this client.
     * @throws RemoteException If there was an exception on the remote end.
     * @throws IOException If there was an exception sending the command over
     *             the {@link ClientSideCommunicator} associated with this
     *             {@link ResponseDispatcher}.
     * @throws NotBoundException If the {@link ClientSideCommunicator} is unable
     *             to serialize the {@link CallCommand}.
     * @throws TimeoutException If no response is received from the server
     *             before the timeout expires.
     */
    public Chatter getChatter()
        throws RemoteException, IOException, NotBoundException, TimeoutException;

    /**
     * Set this chatter's name.
     * 
     * @param name The new name.
     * @throws RemoteException If there was an exception on the remote end.
     * @throws IOException If there was an exception sending the command over
     *             the {@link ClientSideCommunicator} associated with this
     *             {@link ResponseDispatcher}.
     * @throws NotBoundException If the {@link ClientSideCommunicator} is unable
     *             to serialize the {@link CallCommand}.
     * @throws TimeoutException If no response is received from the server
     *             before the timeout expires.
     */
    public void setName(final String name)
        throws RemoteException, IOException, NotBoundException, TimeoutException;

    /**
     * Broadcast a chat message to all clients.
     * 
     * @param message The message to broadcast.
     * @throws RemoteException If there was an exception on the remote end.
     * @throws IOException If there was an exception sending the command over
     *             the {@link ClientSideCommunicator} associated with this
     *             {@link ResponseDispatcher}.
     * @throws NotBoundException If the {@link ClientSideCommunicator} is unable
     *             to serialize the {@link CallCommand}.
     * @throws TimeoutException If no response is received from the server
     *             before the timeout expires.
     */
    public void sendMessage(final String message)
        throws RemoteException, IOException, NotBoundException, TimeoutException;

    /**
     * Get an array containing all of the clients currently connected to the
     * server.
     * 
     * @return An array of all clients currently connected to the server.
     * @throws RemoteException If there was an exception on the remote end.
     * @throws IOException If there was an exception sending the command over
     *             the {@link ClientSideCommunicator} associated with this
     *             {@link ResponseDispatcher}.
     * @throws NotBoundException If the {@link ClientSideCommunicator} is unable
     *             to serialize the {@link CallCommand}.
     * @throws TimeoutException If no response is received from the server
     *             before the timeout expires.
     */
    public Chatter[] getChatters()
        throws RemoteException, IOException, NotBoundException, TimeoutException;
}
