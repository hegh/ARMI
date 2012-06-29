package net.jonp.armi.base.response;

import java.rmi.NotBoundException;

import net.jonp.armi.base.ClassRegistry;

/**
 * Represents an Error response.
 */
public class ErrorResponse
    extends Response
{
    private final Throwable _exception;

    /**
     * Construct a new Error response.
     * 
     * @param label The label, or <code>null</code>.
     * @param exception The exception represented by this response.
     */
    public ErrorResponse(final String label, final Throwable exception)
    {
        super(label);

        _exception = exception;
    }

    /**
     * Get the exception of this error.
     * 
     * @return The exception.
     */
    public Throwable getException()
    {
        return _exception;
    }

    @Override
    public String toString()
    {
        if (null == getException()) {
            return String.format("%s", getException());
        }
        else {
            return String.format("%s[%s]", getException().getClass().getSimpleName(), getException().getMessage());
        }
    }

    @Override
    public String toStatement(final ClassRegistry registry)
        throws NotBoundException
    {
        final StringBuilder buf = new StringBuilder();

        buf.append("error ");

        if (null != getLabel()) {
            buf.append("label \"").append(getLabel()).append("\" ");
        }

        buf.append("(").append(makeArgument(getException(), registry)).append(")");

        return buf.toString();
    }
}
