package net.jonp.armi.command;

import net.jonp.armi.ClassRegistry;


/**
 * Represents a List command, which responds with a list of either available
 * objects, or available methods for a given object.
 */
public class ListCommand
    extends Command
{
    private final String object;

    /**
     * Construct a new List command.
     * 
     * @param _label The label, or <code>null</code>.
     * @param _object The object whose methods to list, or <code>null</code> to
     *            list objects.
     */
    public ListCommand(final String _label, final String _object)
    {
        super(_label);

        object = _object;
    }

    /**
     * Get the name of the object whose methods to list.
     * 
     * @return The object name, or <code>null</code> to list all available
     *         objects.
     */
    public String getObject()
    {
        return object;
    }

    @Override
    public String toString()
    {
        if (null == getObject()) {
            return "list(objects)";
        }
        else {
            return ("list(methods of " + getObject() + ")");
        }
    }

    /**
     * A wrapper around {@link #toStatement(ClassRegistry)} that passes
     * <code>null</code> for the {@link ClassRegistry} argument.
     * 
     * @return The statement form of this List.
     */
    public String toStatement()
    {
        return toStatement(null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * net.jonp.armi.AbstractLanguageObject#toStatement(net.jonp.ms5.
     * command.ClassRegistry)
     */
    @Override
    public String toStatement(final ClassRegistry registry)
    {
        final StringBuilder buf = new StringBuilder();
        buf.append("list ");

        if (null != getLabel()) {
            buf.append("label \"").append(getLabel()).append("\" ");
        }

        if (null == getObject()) {
            buf.append("objects");
        }
        else {
            buf.append("methods ").append(getObject());
        }

        return buf.toString();
    }
}
