package net.jonp.armi.comm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import net.jonp.armi.base.ClassRegistry;
import net.jonp.armi.base.command.Command;
import net.jonp.armi.base.command.HelpCommand;
import net.jonp.armi.base.response.Response;
import net.jonp.armi.base.response.ValueResponse;

import org.junit.Test;

public class CommunicatorPipeTest
{
    @Test
    public void testCommunicatorPipeClassRegistry()
    {
        final ClassRegistry registry = new DefaultClassRegistry();
        final CommunicatorPipe pipe = new CommunicatorPipe(registry);

        assertEquals(registry, pipe.getClassRegistry());
        assertEquals(CommunicatorPipe.TIMEOUT, pipe.getTimeout());
    }

    @Test
    public void testCommunicatorPipeClassRegistryLong()
    {
        final ClassRegistry registry = new DefaultClassRegistry();
        final CommunicatorPipe pipe = new CommunicatorPipe(registry, 1);

        assertEquals(registry, pipe.getClassRegistry());
        assertEquals(1, pipe.getTimeout());

        try {
            new CommunicatorPipe(registry, -1);
            fail("Expected an IllegalArgumentException for using a negative timeout");
        }
        catch (final IllegalArgumentException iae) {
            // This was expected
        }
    }

    @Test
    public void testSendCommand()
        throws IOException
    {
        final ClassRegistry registry = new DefaultClassRegistry();
        final CommunicatorPipe pipe = new CommunicatorPipe(registry);
        final Command command = new HelpCommand();

        pipe.sendCommand(command);

        assertEquals(command, pipe.readNextCommand());
    }

    @Test
    public void testSendResponse()
        throws IOException
    {
        final ClassRegistry registry = new DefaultClassRegistry();
        final CommunicatorPipe pipe = new CommunicatorPipe(registry);
        final Response response = new ValueResponse(null, null);

        pipe.sendResponse(response);

        assertEquals(response, pipe.readNextResponse());
    }

    @Test
    public void testIsClosed()
    {
        final CommunicatorPipe pipe = new CommunicatorPipe(null);

        assertEquals(false, pipe.isClosed());

        pipe.close();

        assertEquals(true, pipe.isClosed());
    }

    @Test
    public void testClose()
        throws IOException
    {
        final ClassRegistry registry = new DefaultClassRegistry();
        final CommunicatorPipe pipe = new CommunicatorPipe(registry);

        final Command command = new HelpCommand();
        final Response response = new ValueResponse(null, null);

        pipe.sendCommand(command);
        pipe.sendResponse(response);

        pipe.close();

        assertEquals(true, pipe.isClosed());

        try {
            pipe.sendCommand(command);
            fail("Expected IOException due to closed communicator");
        }
        catch (final IOException ioe) {
            // This was expected
        }

        try {
            pipe.sendResponse(response);
            fail("Expected IOException due to closed communicator");
        }
        catch (final IOException ioe) {
            // This was expected
        }

        assertEquals(null, pipe.readNextCommand());
        assertEquals(null, pipe.readNextResponse());
    }
}
