package net.jonp.armi.base;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.rmi.NotBoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Superclass for objects which can be represented by the command/response
 * language (i.e. commands and responses).
 */
public abstract class AbstractLanguageObject
{
    private static final Logger LOG = Logger.getLogger(AbstractLanguageObject.class);

    /** The label on this communication, or <code>null</code>. */
    protected String _label;

    /**
     * Construct a new AbstractLanguageObject.
     * 
     * @param label The label, or <code>null</code>. The label <code>*</code> is
     *            used for the response when a command has a syntax error and
     *            cannot be parsed, and should not be used otherwise.
     */
    protected AbstractLanguageObject(final String label)
    {
        _label = label;
    }

    /**
     * Get the label.
     * 
     * @return The label.
     */
    public String getLabel()
    {
        return _label;
    }

    /**
     * Set the label.
     * 
     * @param label The new label.
     */
    public void setLabel(final String label)
    {
        _label = label;
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
        return makeArgument(new ArrayList<Object>(), arg, registry);
    }

    /**
     * Convert a generic object into a command/response language description.
     * 
     * @param indexedValues Values that have already been serialized, so we can
     *            compute back references. Must include one entry for each call
     *            to this function, in order (depth-first).
     * @param arg The object. All non-static, non-transient, non-final, public
     *            fields will be serialized, recursing into other objects as
     *            necessary. No getters/setters will be called.
     * @param registry The Class Registry to use when building the argument.
     * @return A string serialization of an object, which is a legal statement
     *         in the command/response language.
     * @throws NotBoundException If the object's class is not a command language
     *             primitive, and is not in the {@link #registry}.
     */
    protected String makeArgument(final List<Object> indexedValues, final Object arg, final ClassRegistry registry)
        throws NotBoundException
    {
        final StringBuilder buf = new StringBuilder();

        if (null == arg) {
            // Special case: It is more space efficient (and clear) to write
            // 'null' instead of a back-reference, so look for that first

            // Also, we cannot guarantee that non-serializable classes will
            // restore correctly
            // For example, Throwable contains an ArrayList wrapped by
            // Collections.unmodifiableList(); this construct is not
            // serializable, and would be difficult to coerce without very
            // special-purpose code

            buf.append("null");
            indexedValues.add(null);
        }
        else if (backIndex(indexedValues, arg, buf)) {
            indexedValues.add(arg);
        }
        else {
            indexedValues.add(arg);

            if (arg instanceof Byte) {
                buf.append(arg.toString()).append("Y");
            }
            else if (arg instanceof Float) {
                buf.append(arg.toString()).append("F");
            }
            else if (arg instanceof Long) {
                buf.append(arg.toString()).append("L");
            }
            else if (arg instanceof Short) {
                buf.append(arg.toString()).append("T");
            }
            else if (arg instanceof Number) {
                buf.append(arg.toString());
            }
            else if (arg instanceof CharSequence) {
                String s = arg.toString();
                s = s.replaceAll("\\\\", "\\\\\\\\"); // Replace \ with \\
                s = s.replaceAll("\"", "\\\\\\\"");

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

                    buf.append(makeArgument(indexedValues, element, registry));
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

                    buf.append(makeArgument(indexedValues, element, registry));
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

                    buf.append(makeArgument(indexedValues, entry.getKey(), registry));
                    buf.append(" = ");
                    buf.append(makeArgument(indexedValues, entry.getValue(), registry));
                }
                buf.append("]");
            }
            else {
                buf.append(registry.reverseLookup(arg.getClass())).append(" (");
                boolean first = true;

                Class<?> clazz = arg.getClass();
                final List<Field> fields = new ArrayList<Field>();
                while (clazz != null) {
                    final Field[] fieldArray = clazz.getDeclaredFields();
                    AccessibleObject.setAccessible(fieldArray, true);

                    Collections.addAll(fields, fieldArray);
                    clazz = clazz.getSuperclass();
                }

                Collections.sort(fields, new Comparator<Field>() {
                    @Override
                    public int compare(final Field lhs, final Field rhs)
                    {
                        final int c = lhs.getDeclaringClass().getName().compareTo(rhs.getDeclaringClass().getName());
                        if (c != 0) {
                            return c;
                        }

                        return lhs.getName().compareTo(rhs.getName());
                    }
                });

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
                            buf.append(field.getDeclaringClass().getName()).append(".").append(field.getName());
                            buf.append(" = ").append(makeArgument(indexedValues, field.get(arg), registry));
                        }
                        catch (final IllegalAccessException iae) {
                            // Should not happen, since we disabled access
                            // checking
                            throw new IllegalStateException("Field " + field.getName() + " of " + arg.getClass().getName() +
                                                            " is not accessible: " + iae.getMessage(), iae);
                        }
                    }
                }
                buf.append(")");
            }
        }

        final String s = buf.toString();
        LOG.debug("Converted '" + arg + "' to '" + s + "'");
        return s;
    }

    private boolean backIndex(final List<Object> indexedValues, final Object arg, final StringBuilder buf)
    {
        // XXX: Cannot use indexOf() because we need to search for reference
        // equality, not object equality

        boolean found = false;
        for (int idx = 0; idx < indexedValues.size(); idx++) {
            if (arg == indexedValues.get(idx)) {
                buf.append("ref " + idx);
                found = true;
                break;
            }
        }

        return found;
    }
}
