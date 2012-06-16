package net.jonp.armi.comm;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.util.HashMap;

import net.jonp.armi.base.Registry;

/**
 * A simple, generic {@link Registry} implementation backed by a {@link HashMap}
 * . Uses the same synchronization semantics as {@link HashMap}.
 */
public class SimpleRegistry<T>
    extends HashMap<String, T>
    implements Registry<T>
{
    @Override
    public void bind(final String name, final T object)
        throws AlreadyBoundException
    {
        if (containsKey(name)) {
            throw new AlreadyBoundException(name);
        }

        put(name, object);
    }

    @Override
    public String[] list()
    {
        return keySet().toArray(new String[size()]);
    }

    @Override
    public T lookup(final String name)
        throws NotBoundException
    {
        if (!containsKey(name)) {
            throw new NotBoundException(name);
        }

        return get(name);
    }

    @Override
    public void rebind(final String name, final T object)
    {
        put(name, object);
    }

    @Override
    public void unbind(final String name)
        throws NotBoundException
    {
        if (!containsKey(name)) {
            throw new NotBoundException(name);
        }

        remove(name);
    }
}
