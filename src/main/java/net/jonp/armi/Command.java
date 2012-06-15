package net.jonp.armi;

/**
 * Represents a command, which is a method call with an optional label.
 */
public class Command
    extends AbstractLanguageObject
{
    private final String[] path;
    private final Object[] arguments;

    /**
     * Construct a new Command.
     * 
     * @param _label The label, or <code>null</code>.
     * @param _path The "path" of package and object identifiers, which should
     *            terminate in a method name.
     * @param _arguments The arguments to pass to the method.
     */
    public Command(final String _label, final String[] _path, final Object[] _arguments)
    {
        super(_label);

        path = new String[_path.length];
        System.arraycopy(_path, 0, path, 0, path.length);

        arguments = new Object[_arguments.length];
        System.arraycopy(_arguments, 0, arguments, 0, arguments.length);
    }

    /**
     * Get the path though the packages and objects, not including the final
     * method name.
     * 
     * @return The full path minus the last element (the method name).
     */
    public String[] getPartialPath()
    {
        final String[] _path = new String[path.length - 1];
        System.arraycopy(path, 0, _path, 0, path.length - 1);
        return _path;
    }

    /**
     * Get the full path through the packages and objects, including the final
     * method name.
     * 
     * @return The full path, including the method name.
     */
    public String[] getPath()
    {
        final String[] _path = new String[path.length];
        System.arraycopy(path, 0, _path, 0, path.length);
        return _path;
    }

    /**
     * Get the name of the method, which is the last piece of the path.
     * 
     * @return The name of the method.
     */
    public String getMethodName()
    {
        return path[path.length - 1];
    }

    /**
     * Get the objects to pass to the method.
     * 
     * @return The objects to pass to the method.
     */
    public Object[] getArguments()
    {
        final Object[] _arguments = new Object[arguments.length];
        System.arraycopy(arguments, 0, _arguments, 0, arguments.length);
        return _arguments;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return String.format("%s(%s)", Conversion.arrayToString(path, "."), Conversion.arrayToString(arguments, ", "));
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

        buf.append("call ");

        if (label != null) {
            buf.append("label \"").append(label).append("\" ");
        }

        buf.append(Conversion.arrayToString(arguments, ".")).append(" (");
        for (int i = 0; i < arguments.length; i++) {
            final Object arg = arguments[i];
            buf.append(makeArgument(arg));

            if (i < arguments.length - 1) {
                buf.append(", ");
            }
        }
        buf.append(")");

        return buf.toString();
    }
}
