package net.jonp.armi;

import java.rmi.NotBoundException;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * A default implementation of a ClassRegistry backed by a {@link HashBiMap} and
 * the Runtime class loader. This follows the same synchronization semantics as
 * {@link HashBiMap}.
 */
public class DefaultClassRegistry
    implements ClassRegistry
{
    private final BiMap<String, Class<?>> map = HashBiMap.create();

    /**
     * Construct a new DefaultClassRegistry.
     */
    public DefaultClassRegistry()
    {
        // Nothing to do
    }

    /**
     * Clear all entries from this class registry.
     */
    public void clear()
    {
        map.clear();
    }

    /**
     * Put a name/class pair into this registry.
     * 
     * @param name The name.
     * @param clazz The class.
     * @throws IllegalArgumentException If the given mapping is already present
     *             in this registry.
     */
    public void put(final String name, final Class<?> clazz)
        throws IllegalArgumentException
    {
        map.put(name, clazz);
    }

    /**
     * Get the mapped class for a given name.
     * 
     * @param name The name.
     * @return The class, or <code>null</code> if not bound.
     */
    public Class<?> get(final String name)
    {
        return map.get(name);
    }

    /**
     * Get the name for a given mapped class.
     * 
     * @param clazz The class.
     * @return The name, or <code>null</code> if not bound.
     */
    public String get(final Class<?> clazz)
    {
        return map.inverse().get(clazz);
    }

    /**
     * Remove a mapping from this registry.
     * 
     * @param name The name to remove.
     */
    public void remove(final String name)
    {
        map.remove(name);
    }

    /**
     * Remove a mapping from this registry.
     * 
     * @param clazz The class to remove.
     */
    public void remove(final Class<?> clazz)
    {
        map.inverse().remove(clazz);
    }

    /**
     * Check whether this registry contains a mapping for the given name.
     * 
     * @param name The name.
     * @return True if the name is present in this registry; false if not.
     */
    public boolean contains(final String name)
    {
        return map.containsKey(name);
    }

    /**
     * Check whether this registry contains a mapping for the given class.
     * 
     * @param clazz The class.
     * @return True if the class is present in this registry; false if not.
     */
    public boolean contains(final Class<?> clazz)
    {
        return map.inverse().containsKey(clazz);
    }

    /**
     * Look up the given class name in this registry.
     * 
     * @param name The name of the class to look up.
     * @return The class associated with that name, first checking the classes
     *         registered with this registry, and then the Runtime class loader.
     *         This allows for unregistered classes to be used, as long as they
     *         come from the standard library.
     */
    @Override
    public Class<?> lookup(final String name)
        throws NotBoundException
    {
        Class<?> clazz = map.get(name);

        if (null == clazz) {
            // It may be a standard class
            try {
                clazz = Class.forName(name, true, Runtime.class.getClassLoader());
            }
            catch (final ClassNotFoundException cnfe) {
                // Nope, not a standard class; ignore the exception
            }
        }

        if (null == clazz) {
            throw new NotBoundException(name);
        }

        return clazz;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.jonp.armi.ClassRegistry#reverseLookup(java.lang.Class)
     */
    @Override
    public String reverseLookup(final Class<?> clazz)
        throws NotBoundException
    {
        String name = map.inverse().get(clazz);

        if (null == name) {
            // Not found? See if it is available in the standard library before
            // throwing an exception
            try {
                Class.forName(clazz.getName(), false, Runtime.class.getClassLoader());

                // Yup, it's a standard class, so use that name
                name = clazz.getName();
            }
            catch (final ClassNotFoundException cnfe) {
                // Nope, not a standard class; ignore the exception
            }
        }

        if (null == name) {
            throw new NotBoundException(clazz.getName());
        }

        return name;
    }
}
