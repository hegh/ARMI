package net.jonp.armi.example.chat.server;

import java.io.IOException;
import java.rmi.NotBoundException;

import net.jonp.armi.base.SyntaxException;
import net.jonp.armi.base.command.CallCommand;
import net.jonp.armi.base.command.Command;
import net.jonp.armi.base.command.HelpCommand;
import net.jonp.armi.base.command.LangAPI;
import net.jonp.armi.base.response.ErrorResponse;
import net.jonp.armi.base.response.Response;
import net.jonp.armi.comm.CommandExecutor;
import net.jonp.armi.comm.DefaultClassRegistry;
import net.jonp.armi.comm.ServerSideCommunicator;
import net.jonp.armi.comm.SimpleRegistry;

import org.apache.log4j.Logger;

/**
 * A superclass providing the infrastructure necessary to communicate with and
 * run commands for a chatter.
 */
public abstract class AbstractChatHandler
    extends Thread
{
    private static final Logger LOG = Logger.getLogger(AbstractChatHandler.class);

    protected final ServerSideCommunicator client;
    protected final DefaultClassRegistry classRegistry;

    protected final SimpleRegistry<Object> apiRegistry;
    protected final CommandExecutor executor;

    private boolean connected = true;
    private Command currentCommand;

    protected AbstractChatHandler(final ServerSideCommunicator _client)
    {
        client = _client;
        classRegistry = (DefaultClassRegistry)client.getClassRegistry();

        apiRegistry = new SimpleRegistry<Object>();
        apiRegistry.put("lang", new LangAPI(apiRegistry));

        executor = new CommandExecutor(apiRegistry);
    }

    /**
     * Get the socket used for communication with this client.
     * 
     * @return This client's socket.
     */
    public ServerSideCommunicator getClientCommunicator()
    {
        return client;
    }

    /**
     * Get the currently executing {@link Command}.
     * 
     * @return The currently executing {@link Command}.
     */
    public Command getCurrentCommand()
    {
        return currentCommand;
    }

    /**
     * Get the current name of the player represented by this handler.
     * 
     * @return The name of the player.
     */
    public abstract String getChatterName();

    /**
     * Send a response to the client connected to this handler.
     * 
     * @param response The {@link Response} to send.
     */
    public void sendResponse(final Response response)
    {
        try {
            client.sendResponse(response);
        }
        catch (final IOException ioe) {
            LOG.error("Error sending response to client, disconnecting", ioe);
            disconnect();
        }
        catch (final NotBoundException nbe) {
            LOG.error("Unrecognized object in response, escalating", nbe);
            throw new RuntimeException(nbe);
        }
    }

    @Override
    public void run()
    {
        while (connected && isRunning()) {
            currentCommand = null;

            try {
                currentCommand = client.readNextCommand();

                // FIXME: This returns EOF if the client sends a blank line
                if (currentCommand == null) {
                    LOG.warn("EOF from client, disconnecting");
                    disconnect();
                }
            }
            catch (final SyntaxException se) {
                LOG.warn("Erroneous command received from " + this, se);
                sendResponse(new ErrorResponse("*", se));
            }
            catch (final IOException ioe) {
                LOG.error("Error reading command, disconnecting client", ioe);
                disconnect();
            }

            // If we already have a response, do not overwrite it
            if (connected && currentCommand != null) {
                LOG.debug("Message received from " + getChatterName() + ": " + currentCommand.toString());
                if (currentCommand instanceof HelpCommand) {
                    LOG.debug("Sending response for HelpCommand");
                    sendResponse(((HelpCommand)currentCommand).getDefaultResponse());
                }
                else if (currentCommand instanceof CallCommand) {
                    LOG.debug("Executing CallCommand " + currentCommand.toString());
                    sendResponse(executor.executeCommand((CallCommand)currentCommand));
                }
                else {
                    LOG.error("Unrecognized command type '" + currentCommand.getClass().getName() + "', escalating");
                    throw new RuntimeException("Not a recognized command type: " + currentCommand.getClass().getName());
                }
            }
        }
    }

    /**
     * Check whether this handler should continue running.
     * 
     * @return True to continue running; false to stop.
     */
    protected abstract boolean isRunning();

    /**
     * Implemented by clients to do any disconnection cleanup. Default
     * implementation is a no-op.
     */
    protected void subDisconnect()
    {
        // Nothing to do
    }

    /** Disconnect from the client and broadcast the chatter left message. */
    private void disconnect()
    {
        connected = false;

        subDisconnect();

        try {
            client.close();
        }
        catch (final IOException ioe) {
            LOG.warn("Error closing client", ioe);
        }
    }
}
