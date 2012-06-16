package net.jonp.armi.response;

import java.rmi.NotBoundException;

import net.jonp.armi.ClassRegistry;
import net.jonp.armi.Conversion;

/**
 * Represents a value returned by a response.
 */
public class ValueResponse
    extends Response
{
    private final Object value;

    /**
     * Construct a new Value.
     * 
     * @param _label The label, or <code>null</code>.
     * @param _value The value.
     */
    public ValueResponse(final String _label, final Object _value)
    {
        super(_label);

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
        return Conversion.describe(getValue());
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.jonp.armi.AbstractLanguageObject#toStatement()
     */
    @Override
    public String toStatement(final ClassRegistry registry)
        throws NotBoundException
    {
        final StringBuilder buf = new StringBuilder();

        buf.append("response ");

        if (null != getLabel()) {
            buf.append("label \"").append(getLabel()).append("\" ");
        }

        buf.append("(").append(makeArgument(getValue(), registry)).append(")");

        return buf.toString();
    }
}
