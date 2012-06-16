package net.jonp.armi;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;

/**
 * A registry of method names that are accessible through the command/response
 * language. Similar to {@link java.rmi.registry.Registry}, but generic.
 * 
 * @param <E> The type of object that can be bound by this registry.
 */
public interface Registry<E>
{
    // Try to keep this compatible with java.rmi.registry.Registry, in case we
    // decide to switch

    /**
     * Bind the given name to the given object. Methods of this object will
     * become available to remote calls by the command/response language.
     * 
     * @param name The name to bind to the object.
     * @param object The object.
     * @throws AlreadyBoundException If the name is already bound to an object.
     */
    public void bind(String name, E object)
        throws AlreadyBoundException;

    /**
     * List the bound names in this registry.
     * 
     * @return The bound names in this registry.
     */
    public String[] list();

    /**
     * Look up the object assigned to the given name.
     * 
     * @param name The name.
     * @return The object.
     * @throws NotBoundException If the name is not currently bound to an
     *             object.
     */
    public E lookup(String name)
        throws NotBoundException;

    /**
     * Bind the given name to a new object. The name may or may not already be
     * bound to an object.
     * 
     * @param name The name.
     * @param object The object.
     */
    public void rebind(String name, E object);

    /**
     * Unbind the given name.
     * 
     * @param name The name to unbind.
     * @throws NotBoundException If the name is not currently bound to any
     *             object.
     */
    public void unbind(String name)
        throws NotBoundException;
}
