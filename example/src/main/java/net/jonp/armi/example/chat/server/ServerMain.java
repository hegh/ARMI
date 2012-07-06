package net.jonp.armi.example.chat.server;

import java.io.IOException;
import java.io.PrintStream;

import net.jonp.armi.example.chat.Shared;

/**
 * Executes a chat server.
 */
public class ServerMain
{
    private static final int BACKLOG = 3;

    private static void usage(final PrintStream out)
    {
        out.println("Usage: ChatServer [OPTION]...");
        out.println(" -p <port>  Specify the port to listen on");
        out.println("            Default: " + Shared.PORT);
        out.println(" -h         Display this message and terminate");
    }

    public static void main(final String[] args)
    {
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

        final ChatServer server;
        try {
            server = new ChatServer(null, port, BACKLOG);
        }
        catch (final IOException ioe) {
            System.err.println("Unable to start server: " + ioe.getMessage());
            System.exit(1);
            return;
        }

        server.start();
    }
}
