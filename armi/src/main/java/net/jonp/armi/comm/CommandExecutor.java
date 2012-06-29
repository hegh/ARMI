package net.jonp.armi.comm;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.NotBoundException;

import net.jonp.armi.base.Registry;
import net.jonp.armi.base.command.CallCommand;
import net.jonp.armi.base.response.ErrorResponse;
import net.jonp.armi.base.response.Response;
import net.jonp.armi.base.response.ValueResponse;

import org.apache.log4j.Logger;

/**
 * Executes ARMI commands.
 */
public class CommandExecutor
{
    private static final Logger LOG = Logger.getLogger(CommandExecutor.class);

    private final Registry<? extends Object> _registry;

    // FUTURE: Look into caching object/method name to Method object to avoid
    // reflective lookup
    // Would still need to make sure the object is in the registry, though

    // FUTURE: Look up methods base on names AND arguments, to allow proper
    // implementation of overloading

    /**
     * Construct a new CommandExecutor.
     * 
     * @param registry The registry defining the objects whose methods are
     *            callable. Each class should <b>only</b> include methods which
     *            should be accessible via command, and <b>should not</b>
     *            include multiple methods with the same name.
     */
    public CommandExecutor(final Registry<? extends Object> registry)
    {
        _registry = registry;
    }

    /**
     * Execute a command.
     * 
     * @param command The command to execute.
     * @return The response to the command.
     */
    public Response executeCommand(final CallCommand command)
    {
        Response response;
        try {
            LOG.debug("Looking up API object " + command.getObject());
            final Object object = _registry.lookup(command.getObject());

            LOG.debug("Looking up API method " + command.getObject() + "." + command.getMethod());
            final Method method = findMethod(object, command.getMethod());

            LOG.debug("Calling API method " + command.getObject() + "." + command.getMethod());
            final Object retn = method.invoke(object, command.getArguments());

            response = new ValueResponse(command.getLabel(), retn);
        }
        catch (final IllegalArgumentException iae) {
            LOG.warn("Failed to call " + command.getObject() + "." + command.getMethod(), iae);
            response = new ErrorResponse(command.getLabel(), iae);
        }
        catch (final IllegalAccessException iae) {
            LOG.warn("Failed to call " + command.getObject() + "." + command.getMethod(), iae);
            response = new ErrorResponse(command.getLabel(), iae);
        }
        catch (final InvocationTargetException ite) {
            LOG.warn("Failed to call " + command.getObject() + "." + command.getMethod(), ite);
            response = new ErrorResponse(command.getLabel(), ite);
        }
        catch (final MethodNotFoundException mnfe) {
            LOG.warn("Unable to find method " + command.getObject() + "." + command.getMethod(), mnfe);
            response = new ErrorResponse(command.getLabel(), mnfe);
        }
        catch (final NotBoundException nbe) {
            LOG.warn("Unable to find object " + command.getObject(), nbe);
            response = new ErrorResponse(command.getLabel(), nbe);
        }

        return response;
    }

    /**
     * Locate a method by name.
     * 
     * @param object The object whose method to locate.
     * @param methodName The name of the method.
     * @return A method on the given object with that name. Never
     *         <code>null</code>. If there are multiple methods with the same
     *         name, one will be chosen arbitrarily.
     * @throws MethodNotFoundException If such a method cannot be found.
     */
    private Method findMethod(final Object object, final String methodName)
        throws MethodNotFoundException
    {
        for (final Method method : object.getClass().getMethods()) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }

        throw new MethodNotFoundException(object.getClass().getName() + "." + methodName);
    }
}
