package net.jonp.armi.base;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import net.jonp.armi.base.SyntaxException;
import net.jonp.armi.base.Utils;
import net.jonp.armi.comm.CommandInputStream;

import org.junit.Test;

/**
 * Tests {@link CommandInputStream).
 */
public class CommandInputStreamTest
{
    /**
     * Test method for {@link net.jonp.armi.comm.CommandInputStream#read()}.
     * 
     * @throws IOException If there was a problem.
     * @throws SyntaxException If there was a problem.
     */
    @Test
    public void testRead()
        throws IOException
    {
        final CommandInputStream in =
            new CommandInputStream(new ByteArrayInputStream("call help.help () help call x.y ()".getBytes()));

        final byte[] c1 = Utils.readFully(in);
        in.resetStream();
        final byte[] c2 = Utils.readFully(in);
        in.resetStream();
        final byte[] c3 = Utils.readFully(in);
        in.resetStream();
        final int eof = in.read();

        assertEquals("call help.help ()", new String(c1));
        assertEquals(" help", new String(c2));
        assertEquals(" call x.y ()", new String(c3));
        assertEquals(-1, eof);
    }
}
