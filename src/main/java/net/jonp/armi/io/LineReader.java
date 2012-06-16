package net.jonp.armi.io;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;

/**
 * An unbuffered reader that allows reading line-by-line.
 */
public class LineReader
    extends FilterReader
{
    /**
     * Construct a new LineReader.
     * 
     * @param in The reader from which to read the lines.
     */
    public LineReader(final Reader in)
    {
        super(in);
    }

    /**
     * Read until the next newline or EOF.
     * 
     * @return The read string, or <code>null</code> if there was no available
     *         data..
     * @throws IOException If there was a problem.
     */
    public String readLine()
        throws IOException
    {
        final StringBuilder buf = new StringBuilder();
        int c;
        while ((c = read()) != -1) {
            buf.append((char)c);
            if (c == '\n') {
                break;
            }
        }

        if (buf.length() > 0) {
            return buf.toString();
        }
        else {
            return null;
        }
    }
}
