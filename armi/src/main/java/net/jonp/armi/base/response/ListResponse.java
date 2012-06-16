package net.jonp.armi.base.response;

import net.jonp.armi.base.ClassRegistry;
import net.jonp.armi.base.Conversion;

/**
 * Represents a response to a List command.
 */
public class ListResponse
    extends Response
{
    private final String[] values;

    /**
     * Construct a new ListResponse.
     * 
     * @param _label The label, or <code>null</code>.
     * @param _values The list of values.
     */
    public ListResponse(final String _label, final String[] _values)
    {
        super(_label);

        values = new String[_values.length];
        System.arraycopy(_values, 0, values, 0, values.length);
    }

    /**
     * Get a copy of the list of values.
     * 
     * @return A copy of the list of values.
     */
    public String[] getValues()
    {
        final String[] _values = new String[values.length];
        System.arraycopy(values, 0, _values, 0, _values.length);
        return _values;
    }

    @Override
    public String toString()
    {
        return String.format("list{%s}", Conversion.arrayToString(getValues(), ", "));
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
     * @see net.jonp.armi.AbstractLanguageObject#toStatement(net.jonp.ms5.
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

        buf.append("(");
        boolean first = true;
        for (final String value : getValues()) {
            if (first) {
                first = false;
            }
            else {
                buf.append(", ");
            }

            buf.append("\"").append(value).append("\"");
        }
        buf.append(")");

        return buf.toString();
    }
}
