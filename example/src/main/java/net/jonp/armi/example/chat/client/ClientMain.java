package net.jonp.armi.example.chat.client;

import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.SwingUtilities;

import net.jonp.armi.example.chat.Shared;

/**
 * Client for the chat program.
 */
public class ClientMain
{
    public static final String SERVER = "localhost";

    private static void usage(final PrintStream out)
    {
        out.println("Usage: ChatClient [OPTION]...");
        out.println(" -s <addr>  Specify the address of the server to connect to");
        out.println("            Default: " + SERVER);
        out.println(" -p <port>  Specify the port to connect to");
        out.println("            Default: " + Shared.PORT);
        out.println(" -h         Display this message and terminate");
    }

    public static void main(final String[] args)
    {
        String server = SERVER;
        int port = Shared.PORT;

        try {
            for (int i = 0; i < args.length; i++) {
                if ("-p".equals(args[i])) {
                    try {
                        port = Integer.parseInt(args[++i]);
                    }
                    catch (final NumberFormatException nfe) {
                        throw new Exception("Not an integer: " + args[i]);
                    }

                    if (port < 1 || port > 65535) {
                        throw new Exception("Port must be in the range [1, 65535]");
                    }
                }
                else if ("-s".equals(args[i])) {
                    server = args[++i];
                }
                else if ("-h".equals(args[i])) {
                    usage(System.out);
                    System.exit(0);
                }
                else {
                    throw new Exception("Unrecognized argument: " + args[i]);
                }
            }
        }
        catch (final ArrayIndexOutOfBoundsException aioobe) {
            System.err.println("Missing argument");
            usage(System.err);
            System.exit(1);
        }
        catch (final Exception e) {
            System.err.println(e.getMessage());
            usage(System.err);
            System.exit(1);
        }

        final ChatClient[] client = new ChatClient[1];
        try {
            final InetAddress serverAddr = InetAddress.getByName(server);
            final int serverPort = port;

            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run()
                {
                    try {
                        client[0] = new ChatClient(serverAddr, serverPort);
                    }
                    catch (final IOException ioe) {
                        throw new RuntimeException(ioe.getMessage(), ioe);
                    }
                }
            });
        }
        catch (final InvocationTargetException ite) {
            System.err.println("Unable to start client: " + ite.getCause().getMessage());
            System.exit(1);
            return;
        }
        catch (final InterruptedException ie) {
            System.err.println("Unexpected thread interruption while initializing client");
            System.exit(1);
            return;
        }
        catch (final UnknownHostException uhe) {
            System.err.println("Unable to resolve server address: " + uhe.getMessage());
            System.exit(1);
            return;
        }

        client[0].setVisible(true);
    }
}
