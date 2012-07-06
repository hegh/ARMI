package net.jonp.armi.base.io;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.Test;

public class LineReaderTest
{
    @Test
    public void testReadLine()
        throws IOException
    {
        final StringBuilder data = new StringBuilder("Line1\nLine2\rContinued\r\n\n");
        final LineReader in = new LineReader(new InputStreamReader(new ByteArrayInputStream(data.toString().getBytes())));

        assertEquals("Line1\n", in.readLine());
        assertEquals("Line2\rContinued\r\n", in.readLine());
        assertEquals("\n", in.readLine());
        assertEquals(null, in.readLine());
    }
}
