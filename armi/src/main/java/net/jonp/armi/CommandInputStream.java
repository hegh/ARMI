package net.jonp.armi;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Deque;
import java.util.LinkedList;

import org.apache.log4j.Logger;

/**
 * A stream with minor knowledge of the command/response language. Detects the
 * end of each command and prevents reading any further.
 */
public class CommandInputStream
    extends FilterInputStream
{
    private static final Logger LOG = Logger.getLogger(CommandInputStream.class);

    private boolean eof = false;
    private final Deque<Integer> stack = new LinkedList<Integer>();
    private boolean esc = false;

    private final int[] help = new int[] {
        'h', 'e', 'l', 'p'
    };
    private int hpos = 0;

    public CommandInputStream(final InputStream in)
    {
        super(in);
    }

    @Override
    public synchronized int read()
        throws IOException
    {
        if (eof) {
            return -1;
        }

        final int c = super.read();

        if (c == -1) {
            return -1;
        }

        // Out of all the commands and responses, the help command is the only
        // one that doesn't end with ')'
        if (hpos != -1 && !Character.isWhitespace((char)c)) {
            if (help[hpos] == c) {
                hpos++;

                if (hpos >= help.length) {
                    eof = true;
                }
            }
            else {
                hpos = -1;
            }
        }

        if (esc) {
            esc = false;
        }
        else {
            switch (c) {
                case '(':
                case '[':
                    stack.addLast(c);
                    break;
                case ']':
                case ')':
                    if (stack.isEmpty()) {
                        LOG.warn("No matching open to " + (char)c);
                        eof = true;
                    }
                    else {
                        final int open = stack.removeLast();
                        if ((open == '(' && c != ')') || (open == '[' && c != ']')) {
                            LOG.warn("No matching open to " + (char)c);
                            eof = true;
                        }
                    }

                    if (c == ')' && stack.isEmpty()) {
                        eof = true;
                    }

                    break;
                case '\\':
                    esc = true;
                    break;
                case '"':
                    if (!stack.isEmpty() && stack.peekLast() == '"') {
                        stack.remove();
                    }
            }
        }

        return c;
    }

    @Override
    public synchronized int read(final byte[] buf, final int off, final int len)
        throws IOException
    {
        int pos = 0;
        while (!eof && pos < len) {
            final int c = read();
            if (c == -1) {
                break;
            }

            buf[off + pos] = (byte)c;
            pos++;
        }

        if (pos == 0) {
            return -1;
        }

        return pos;
    }

    public synchronized void resetStream()
    {
        eof = false;
        stack.clear();
        esc = false;
        hpos = 0;
    }

    @Override
    public synchronized void close()
    {
        eof = true;
    }

    public void realClose()
        throws IOException
    {
        super.close();
    }
}
