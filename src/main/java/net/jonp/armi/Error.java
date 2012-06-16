package net.jonp.armi;

/**
 * Represents an Error response.
 */
public class Error
    extends Response
{
    private final String exception;
    private final String message;

    /**
     * Construct a new Error response.
     * 
     * @param _label The label, or <code>null</code>.
     * @param _exception The class name of the exception that raised this error.
     *            The exception must have a constructor matching
     *            {@link Exception#Exception(String)}. May not be
     *            <code>null</code>.
     * @param _message The error message. May not be <code>null</code>.
     * @throws NullPointerException If <code>_path</code> or
     *             <code>_message</code> is <code>null</code>.
     */
    public Error(final String _label, final String _exception, final String _message)
    {
        super(_label);

        if (_exception == null) {
            throw new NullPointerException("_exception");
        }

        if (_message == null) {
            throw new NullPointerException("_message");
        }

        exception = _exception;
        message = _message;
    }

    /**
     * Get the class name of the exception that raised this error.
     * 
     * @return The class name of the exception.
     */
    public String getException()
    {
        return exception;
    }

    /**
     * Get the message associated with this error.
     * 
     * @return This error's message, or <code>null</code>.
     */
    public String getMessage()
    {
        return message;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return String.format("%s[%s]", exception, message);
    }

    /**
     * A wrapper around {@link #toStatement(ClassRegistry)} that passes
     * <code>null</code> for the {@link ClassRegistry} argument.
     * 
     * @return The statement form of this Error.
     */
    public String toStatement()
    {
        return toStatement(null);
    }

    /**
     * Convert this Error into a legal statement in the command/response
     * language.
     * 
     * @param registry Ignored.
     * @return The statement.
     */
    @Override
    public String toStatement(final ClassRegistry registry)
    {
        final StringBuilder buf = new StringBuilder();

        buf.append("error ");

        if (label != null) {
            buf.append("label \"").append(label).append("\" ");
        }

        buf.append(exception).append(" ");
        buf.append("(\"").append(message).append("\")");

        return buf.toString();
    }
}
