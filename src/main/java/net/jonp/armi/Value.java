package net.jonp.armi;

/**
 * Represents a value returned by a response.
 */
public class Value
    extends Response
{
    private final Object value;

    /**
     * Construct a new Value.
     * 
     * @param _label The label, or <code>null</code>.
     * @param _value The value, may not be <code>null</code>.
     * @throws NullPointerException If <code>_value</code> is <code>null</code>.
     */
    public Value(final String _label, final Object _value)
    {
        super(_label);

        if (_value == null) {
            throw new NullPointerException("_value");
        }

        value = _value;
    }

    /**
     * Get the value of this value.
     * 
     * @return The value.
     */
    public Object getValue()
    {
        return value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return String.format("%s", value.toString());
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.jonp.armi.AbstractLanguageObject#toStatement()
     */
    @Override
    public String toStatement()
    {
        final StringBuilder buf = new StringBuilder();

        buf.append("response ");

        if (label != null) {
            buf.append("\"").append(label).append("\" ");
        }

        buf.append("(").append(makeArgument(value)).append(")");

        return buf.toString();
    }
}
