package net.jonp.armi;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Superclass for objects which can be represented by the command/response
 * language.
 */
public abstract class AbstractLanguageObject
{
    protected final String label;

    /**
     * Construct a new AbstractLanguageObject.
     * 
     * @param _label The label, or <code>null</code>.
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
     * @return The statement.
     */
    public abstract String toStatement();

    // FUTURE: Have makeArgument() call getters/setters

    /**
     * Convert a generic object into a command/response language description.
     * 
     * @param arg The object. All non-static, non-transient, non-final, public
     *            fields will be serialized, recursing into other objects as
     *            necessary. No getters/setters will be called.
     * @return A string serialization of an object, which is a legal statement
     *         in the command/response language.
     */
    protected String makeArgument(final Object arg)
    {
        final StringBuilder buf = new StringBuilder();
        if (arg instanceof Number) {
            buf.append(arg.toString());
        }
        else if (arg instanceof CharSequence) {
            buf.append("\"").append(arg.toString()).append("\"");
        }
        else if (arg instanceof Boolean) {
            buf.append(arg.toString());
        }
        else {
            buf.append(arg.getClass().getName()).append(" (");
            final Field[] fields = arg.getClass().getFields();
            for (final Field field : fields) {
                if (((field.getModifiers() & Modifier.PUBLIC) == Modifier.PUBLIC) &&
                    ((field.getModifiers() & Modifier.FINAL) != Modifier.FINAL) &&
                    ((field.getModifiers() & Modifier.TRANSIENT) != Modifier.TRANSIENT) &&
                    ((field.getModifiers() & Modifier.STATIC) != Modifier.STATIC)) {

                    try {
                        buf.append(makeArgument(field.get(arg)));
                    }
                    catch (final IllegalAccessException iae) {
                        // Should not happen, since we verified it is public
                        throw new IllegalStateException("Public field " + field.getName() + " of " + arg.getClass().getName() +
                                                        " is not accessible: " + iae.getMessage(), iae);
                    }
                }
            }
        }

        return buf.toString();
    }
}
