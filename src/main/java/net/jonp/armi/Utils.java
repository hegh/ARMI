package net.jonp.armi;

import java.util.Iterator;

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
}
