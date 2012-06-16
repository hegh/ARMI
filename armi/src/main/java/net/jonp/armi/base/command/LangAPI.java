package net.jonp.armi.base.command;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.rmi.NotBoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.jonp.armi.base.Registry;

/**
 * A default implementation of the <code>lang</code> API. If you use a different
 * API, make sure to use use a different response than
 * {@link HelpCommand#getDefaultResponse()}.
 */
public class LangAPI
{
    private final Registry<?> apiRegistry;

    /**
     * Construct a new LangAPI.
     * 
     * @param _apiRegistry The registry containing available API provider
     *            objects.
     */
    public LangAPI(final Registry<?> _apiRegistry)
    {
        apiRegistry = _apiRegistry;
    }

    /**
     * Get the names of the available objects in the registry.
     * 
     * @return The names of the available objects.
     */
    public Object[] getObjects()
    {
        final List<String> objects = new ArrayList<String>();
        Collections.addAll(objects, apiRegistry.list());
        Collections.sort(objects);
        return objects.toArray();
    }

    /**
     * Get the names of the available methods of the named object. Does not
     * include any methods specified by the {@link Object} class (these are
     * explicitly removed).
     * 
     * @param name The name of the object whose methods to list.
     * @return The names of the methods of the given object.
     * @throws NotBoundException If the name is not currently bound to any API
     *             provider object.
     */
    public Object[] getMethods(final String name)
        throws NotBoundException
    {
        final Set<String> objectMethods = getMethods(Object.class);
        final Set<String> methods = getMethods(apiRegistry.lookup(name).getClass());

        methods.removeAll(objectMethods);

        final Object[] names = methods.toArray();
        Arrays.sort(names);

        return names;
    }

    /**
     * List the API-accessible methods of the given class. This includes all
     * public methods.
     * 
     * @param clazz The class.
     * @return The API-accessible methods of that class.
     */
    private Set<String> getMethods(final Class<?> clazz)
    {
        final Method[] methods = clazz.getMethods();
        final Set<String> names = new HashSet<String>();
        for (final Method method : methods) {
            final int mod = method.getModifiers();
            if ((mod & Modifier.PUBLIC) == Modifier.PUBLIC) {
                names.add(method.getName());
            }
        }

        return names;
    }

    /**
     * Get the parameter types for the given method.
     * 
     * @param name The full name of the object and method.
     * @return The class names of the parameters of the method, in the same
     *         order.
     * @throws NotBoundException If the object name is not currently bound to
     *             any API provider object, or the object has no method with the
     *             given name.
     */
    public Object[] getParameters(final String name)
        throws NotBoundException
    {
        final String objectName = name.substring(0, name.lastIndexOf('.'));
        final String methodName = name.substring(name.lastIndexOf('.') + 1);

        final Object obj = apiRegistry.lookup(objectName);
        final Method[] methods = obj.getClass().getMethods();
        Method method = null;
        for (final Method m : methods) {
            if (methodName.equals(m.getName())) {
                method = m;
                break;
            }
        }

        if (null == method) {
            throw new NotBoundException(objectName + "." + methodName);
        }

        final Class<?>[] types = method.getParameterTypes();
        final List<String> typeNames = new ArrayList<String>(types.length);
        for (final Class<?> type : types) {
            typeNames.add(type.getName());
        }

        Collections.sort(typeNames);

        return typeNames.toArray();
    }
}
