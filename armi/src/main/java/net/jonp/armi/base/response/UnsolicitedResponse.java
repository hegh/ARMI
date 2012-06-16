package net.jonp.armi.base.response;

import java.rmi.NotBoundException;

import net.jonp.armi.base.ClassRegistry;

/**
 * Represents an Unsolicited message from the party that usually responds to
 * commands.
 */
public class UnsolicitedResponse
    extends Response
{
    private final String type;
    private final Object value;

    /**
     * Construct a new Unsolicited response. Unsolicited messages are not
     * labeled.
     * 
     * @param _type The type of this unsolicited response.
     * @param _value The value of this unsolicited response.
     */
    public UnsolicitedResponse(final String _type, final Object _value)
    {
        super(null);

        type = _type;
        value = _value;
    }

    /**
     * Get the type of unsolicited response.
     * 
     * @return The type of the response.
     */
    public String getType()
    {
        return type;
    }

    /**
     * Get the value contained in this usnolicited response.
     * 
     * @return The value.
     */
    public Object getValue()
    {
        return value;
    }

    @Override
    public String toString()
    {
        return String.format("%s(%s)", getType(), getValue());
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
        throws NotBoundException
    {
        final StringBuilder buf = new StringBuilder();

        buf.append("unsol (").append(getType()).append(", ").append(makeArgument(getValue(), registry)).append(")");

        return buf.toString();
    }
}
