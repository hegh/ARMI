package net.jonp.armi;

/**
 * Represents an Error response.
 */
public class Error
    extends Response
{
    private final String message;
    private final String[] path;

    /**
     * Construct a new Error response.
     * 
     * @param _label The label, or <code>null</code>.
     * @param _path The path to the exception that raised this error. The
     *            exception must have a constructor matching
     *            {@link Exception#Exception(String)}. May not be
     *            <code>null</code>.
     * @param _message The error message. May not be <code>null</code>.
     * @throws NullPointerException If <code>_path</code> or
     *             <code>_message</code> is <code>null</code>.
     */
    public Error(final String _label, final String[] _path, final String _message)
    {
        super(_label);

        if (_message == null) {
            throw new NullPointerException("_message");
        }

        path = new String[_path.length];
        System.arraycopy(_path, 0, path, 0, path.length);

        message = _message;
    }

    /**
     * Get the package path to the exception that raised this error.
     * 
     * @return The path to the exception.
     */
    public String[] getPath()
    {
        final String[] _path = new String[path.length];
        System.arraycopy(path, 0, _path, 0, path.length);
        return _path;
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
        return String.format("%s", message);
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

        buf.append("error ");

        if (label != null) {
            buf.append("\"").append(label).append("\" ");
        }

        buf.append(Conversion.arrayToString(path, ".")).append(" ");
        buf.append("(\"").append(message).append("\")");

        return buf.toString();
    }
}
