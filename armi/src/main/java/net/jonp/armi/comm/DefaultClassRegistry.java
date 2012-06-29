package net.jonp.armi.comm;

import java.rmi.NotBoundException;

import net.jonp.armi.base.ClassRegistry;

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
    private final ClassLoader _classLoader;

    /**
     * Construct a new {@link DefaultClassRegistry} that will fall back onto the
     * system classloader ({@link ClassLoader#getSystemClassLoader()}).
     */
    public DefaultClassRegistry()
    {
        // FIXME: This sometimes comes out as NULL
        this(ClassLoader.getSystemClassLoader());
    }

    /**
     * Construct a new {@link DefaultClassRegistry} that will fall back onto the
     * given {@link ClassLoader}.
     * 
     * @param classLoader The {@link ClassLoader} onto which this registry will
     *            fall back. If <code>null</code>, this registry will not fall
     *            back onto a {@link ClassLoader}, and will throw a
     *            {@link NotBoundException} when performing a
     *            {@link #lookup(String)} or {@link #reverseLookup(Class)} for a
     *            class that is not registered.
     */
    public DefaultClassRegistry(final ClassLoader classLoader)
    {
        _classLoader = classLoader;
    }

    /**
     * Get the {@link ClassLoader} onto which this registry will fall back when
     * it cannot locate a class.
     * 
     * @return The associated {@link ClassLoader}. May be <code>null</code>.
     */
    public ClassLoader getClassLoader()
    {
        return _classLoader;
    }

    /**
     * Clear all entries from this registry.
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
     * @return The class, or <code>null</code> if not bound. Does not fall back
     *         onto a {@link ClassLoader}.
     */
    public Class<?> get(final String name)
    {
        return map.get(name);
    }

    /**
     * Get the name for a given mapped class.
     * 
     * @param clazz The class.
     * @return The name, or <code>null</code> if not bound. Does not fall back
     *         onto a {@link ClassLoader}.
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
     * @return True if the name is present in this registry; false if not. Does
     *         not fall back onto a {@link ClassLoader}.
     */
    public boolean contains(final String name)
    {
        return map.containsKey(name);
    }

    /**
     * Check whether this registry contains a mapping for the given class.
     * 
     * @param clazz The class.
     * @return True if the class is present in this registry; false if not. Does
     *         not fall back onto a {@link ClassLoader}.
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
     *         registered with this registry, and then the associated
     *         {@link ClassLoader}. This allows for unregistered classes to be
     *         used, as long as they can be found by the {@link ClassLoader}.
     *         Never <code>null</code>.
     * @throws NotBoundException If the object binding is not present in this
     *             registry, and is unknown to the associated
     *             {@link ClassLoader}.
     */
    @Override
    public Class<?> lookup(final String name)
        throws NotBoundException
    {
        Class<?> clazz = map.get(name);

        if (null == clazz && null != getClassLoader()) {
            // It may be available from the associated class loader
            try {
                clazz = Class.forName(name, true, getClassLoader());
            }
            catch (final ClassNotFoundException cnfe) {
                // No, but ignore the exception; we will throw our own below
            }
        }

        if (null == clazz) {
            throw new NotBoundException(name);
        }

        return clazz;
    }

    /**
     * Look up the name for the given class is this registry.
     * 
     * @param clazz The class whose name to look up.
     * @return The name associated with that class, first checking the classes
     *         registered with this registry, and then the associated
     *         {@link ClassLoader}.
     * @throws NotBoundException If the object binding is not present in this
     *             registry, and is unknown to the associated
     *             {@link ClassLoader}.
     */
    @Override
    public String reverseLookup(final Class<?> clazz)
        throws NotBoundException
    {
        String name = map.inverse().get(clazz);

        if (null == name && null != getClassLoader()) {
            // Not found? See if it is available from the class loader before
            // throwing an exception
            try {
                Class.forName(clazz.getName(), false, getClassLoader());
                name = clazz.getName();
            }
            catch (final ClassNotFoundException cnfe) {
                // Not available
                // Ignore the exception; we will throw our own below
            }
        }

        if (null == name) {
            throw new NotBoundException(clazz.getName());
        }

        return name;
    }
}
