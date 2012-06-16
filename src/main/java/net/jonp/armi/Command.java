package net.jonp.armi;

/**
 * Represents a command, which is a method call with an optional label.
 */
public class Command
    extends AbstractLanguageObject
{
    private final String object;
    private final String method;
    private final Object[] arguments;

    /**
     * Construct a new Command.
     * 
     * @param _label The label, or <code>null</code>.
     * @param _object The name of the object whose method is being referenced.
     * @param _method The name of the method.
     * @param _arguments The arguments to pass to the method.
     */
    public Command(final String _label, final String _object, final String _method, final Object[] _arguments)
    {
        super(_label);

        object = _object;
        method = _method;

        arguments = new Object[_arguments.length];
        System.arraycopy(_arguments, 0, arguments, 0, arguments.length);
    }

    /**
     * Get the name of the object.
     * 
     * @return The name of the object (does not include the method name).
     */
    public String getObject()
    {
        return object;
    }

    /**
     * Get the name of the method.
     * 
     * @return The name of the method.
     */
    public String getMethod()
    {
        return method;
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
        return String.format("%s.%s(%s)", object, method, Conversion.arrayToString(arguments, ", "));
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

        buf.append(object).append(".").append(method).append(" (");
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
