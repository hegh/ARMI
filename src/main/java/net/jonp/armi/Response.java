package net.jonp.armi;

/**
 * Represents a response to a {@link Command}.
 */
public abstract class Response
    extends AbstractLanguageObject
{
    /**
     * Represents a response to a {@link Command}.
     * 
     * @param _label The label on the response, or <code>null</code>.
     */
    public Response(final String _label)
    {
        super(_label);
    }
}
