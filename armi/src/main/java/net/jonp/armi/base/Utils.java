package net.jonp.armi.base;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/** Library of general functions. */
public class Utils
{
    private Utils()
    {
        // Prevent instantiation
    }

    /**
     * <b>DANGEROUS</b> Cast the specified object to whatever type is needed.
     * This is very dangerous to use because it will allow casting without any
     * compiler checks; if there is a problem, it can only be caught at runtime.
     * 
     * @param o The object to cast.
     * @return The object, cast to the type <code>T</code>.
     */
    @SuppressWarnings("unchecked")
    public static <T> T cast(final Object o)
    {
        return (T)o;
    }

    /**
     * Turn a normal iterator into a read-only iterator of the same type.
     * 
     * @param <E> The iterator type.
     * @param it The iterator to make read-only.
     * @return A read-only wrapper around the iterator.
     */
    public static <E> Iterator<E> readOnlyIterator(final Iterator<E> it)
    {
        return new Iterator<E>() {
            @Override
            public boolean hasNext()
            {
                return it.hasNext();
            }

            @Override
            public E next()
            {
                return it.next();
            }

            @Override
            public void remove()
            {
                throw new UnsupportedOperationException("Cannot remove elements from a read-only iterator.");
            }
        };
    }

    /** Get the name of the calling class. */
    public static String thisClassName()
    {
        final Throwable th = new Throwable();
        th.fillInStackTrace();
        return th.getStackTrace()[1].getClassName();
    }

    /**
     * Read an InputStream until EOF, and return an array of all the bytes that
     * were read.
     * 
     * @param in The stream to read.
     * @return The bytes that were read from the stream.
     * @throws IOException If there was a problem reading the stream.
     */
    public static byte[] readFully(final InputStream in)
        throws IOException
    {
        int size = 0;
        boolean eof = false;
        byte[] block = new byte[8192];
        final List<byte[]> blocks = new LinkedList<byte[]>();
        while (!eof) {
            final int read = in.read(block);
            if (read == -1) {
                eof = true;
            }
            else {
                if (read == block.length) {
                    blocks.add(block);
                    block = new byte[block.length];
                }
                else {
                    final byte[] shortBlock = new byte[read];
                    System.arraycopy(block, 0, shortBlock, 0, read);
                    blocks.add(shortBlock);
                }

                size += read;
            }
        }

        final byte[] full = new byte[size];
        int offset = 0;
        for (final byte[] piece : blocks) {
            System.arraycopy(piece, 0, full, offset, piece.length);
            offset += piece.length;
        }

        return full;
    }
}
