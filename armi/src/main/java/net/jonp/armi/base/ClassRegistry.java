package net.jonp.armi.base;

import java.rmi.NotBoundException;

/**
 * Translates from identifiers received via the command language into the class
 * that should be instantiated when building an object.
 */
public interface ClassRegistry
{
    /**
     * Look up the class which implements the given object on this end of a
     * command language communication.
     * 
     * @param name The name passed via the command language.
     * @return The class to use to instantiate such an object.
     * @throws NotBoundException If no class is associated with the named
     *             object.
     */
    public Class<?> lookup(String name)
        throws NotBoundException;

    /**
     * Look up the name to use when constructing a command or response
     * containing an object of the given class.
     * 
     * @param clazz The class.
     * @return The name to use in the command language.
     * @throws NotBoundException If no name is associated with the class.
     */
    public String reverseLookup(Class<?> clazz)
        throws NotBoundException;
}
