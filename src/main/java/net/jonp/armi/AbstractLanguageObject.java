package net.jonp.armi;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.rmi.NotBoundException;
import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Superclass for objects which can be represented by the command/response
 * language (i.e. commands and responses).
 */
public abstract class AbstractLanguageObject
{
    private static final Logger LOG = Logger.getLogger(Utils.thisClassName());

    /** The label on this communication, or <code>null</code>. */
    protected final String label;

    /**
     * Construct a new AbstractLanguageObject.
     * 
     * @param _label The label, or <code>null</code>. The label <code>*</code>
     *            is used for the response when a command has a syntax error and
     *            cannot be parsed, and should not be used otherwise.
     */
    protected AbstractLanguageObject(final String _label)
    {
        label = _label;
    }

    /**
     * Get the label.
     * 
     * @return The label.
     */
    public String getLabel()
    {
        return label;
    }

    /**
     * Convert this object into a legal statement in the command/response
     * language.
     * 
     * @param registry The ClassRegistry to use when building the statement.
     * @return The statement.
     * @throws NotBoundException If, during compilation, an object's class is
     *             not a command language primitive, and is not in the
     *             {@link #registry} is encountered.
     */
    public abstract String toStatement(ClassRegistry registry)
        throws NotBoundException;

    // FUTURE: Have makeArgument() call getters/setters

    /**
     * Convert a generic object into a command/response language description.
     * 
     * @param arg The object. All non-static, non-transient, non-final, public
     *            fields will be serialized, recursing into other objects as
     *            necessary. No getters/setters will be called.
     * @param registry The Class Registry to use when building the argument.
     * @return A string serialization of an object, which is a legal statement
     *         in the command/response language.
     * @throws NotBoundException If the object's class is not a command language
     *             primitive, and is not in the {@link #registry}.
     */
    protected String makeArgument(final Object arg, final ClassRegistry registry)
        throws NotBoundException
    {
        final StringBuilder buf = new StringBuilder();
        if (null == arg) {
            buf.append("null");
        }
        else if (arg instanceof Byte) {
            buf.append(arg.toString()).append("B");
        }
        else if (arg instanceof Float) {
            buf.append(arg.toString()).append("F");
        }
        else if (arg instanceof Long) {
            buf.append(arg.toString()).append("L");
        }
        else if (arg instanceof Short) {
            buf.append(arg.toString()).append("S");
        }
        else if (arg instanceof Number) {
            buf.append(arg.toString());
        }
        else if (arg instanceof CharSequence) {
            String s = arg.toString();
            s = s.replaceAll("\\\\", "\\\\\\\\"); // Replace \ with \\
            s = s.replaceAll("\"", "\\\"");

            buf.append("\"").append(s).append("\"");
        }
        else if (arg instanceof Boolean) {
            buf.append(arg.toString());
        }
        else if (arg.getClass().isArray()) {
            // FUTURE: Once primitive array deserialization is implemented,
            // remove this check
            if (arg.getClass().getComponentType().isPrimitive()) {
                throw new IllegalArgumentException("Arrays of primitive types are not supported");
            }

            buf.append("array(");
            buf.append(arg.getClass().getComponentType().getName());
            buf.append(") [");
            final Object[] elements = (Object[])arg;
            boolean first = true;
            for (final Object element : elements) {
                if (first) {
                    first = false;
                }
                else {
                    buf.append(", ");
                }

                buf.append(makeArgument(element, registry));
            }
            buf.append("]");
        }
        else if (arg instanceof Collection) {
            buf.append("collection(");
            buf.append(arg.getClass().getName());
            buf.append(") [");
            boolean first = true;
            for (final Object element : (Collection<?>)arg) {
                if (first) {
                    first = false;
                }
                else {
                    buf.append(", ");
                }

                buf.append(makeArgument(element, registry));
            }
            buf.append("]");
        }
        else if (arg instanceof Map) {
            buf.append("map(");
            buf.append(arg.getClass().getName());
            buf.append(") [");
            boolean first = true;
            for (final Map.Entry<?, ?> entry : ((Map<?, ?>)arg).entrySet()) {
                if (first) {
                    first = false;
                }
                else {
                    buf.append(", ");
                }

                buf.append(makeArgument(entry.getKey(), registry));
                buf.append(" = ");
                buf.append(makeArgument(entry.getValue(), registry));
            }
            buf.append("]");
        }
        else {
            buf.append(registry.reverseLookup(arg.getClass())).append(" (");
            final Field[] fields = arg.getClass().getFields();
            AccessibleObject.setAccessible(fields, true);

            boolean first = true;
            for (final Field field : fields) {
                if (((field.getModifiers() & Modifier.TRANSIENT) != Modifier.TRANSIENT) &&
                    ((field.getModifiers() & Modifier.STATIC) != Modifier.STATIC)) {

                    if (first) {
                        first = false;
                    }
                    else {
                        buf.append(", ");
                    }

                    try {
                        buf.append(field.getName()).append(" = ").append(makeArgument(field.get(arg), registry));
                    }
                    catch (final IllegalAccessException iae) {
                        // Should not happen, since we disabled access checking
                        throw new IllegalStateException("Field " + field.getName() + " of " + arg.getClass().getName() +
                                                        " is not accessible: " + iae.getMessage(), iae);
                    }
                }
            }
            buf.append(")");
        }

        final String s = buf.toString();
        LOG.debug("Converted '" + arg + "' to '" + s + "'");
        return s;
    }
}
