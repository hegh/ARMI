package net.jonp.armi.base.response;

import net.jonp.armi.base.AbstractLanguageObject;
import net.jonp.armi.base.command.CallCommand;

/**
 * Represents a response to a {@link CallCommand}.
 */
public abstract class Response
    extends AbstractLanguageObject
{
    /**
     * Represents a response to a {@link CallCommand}.
     * 
     * @param _label The label on the response, or <code>null</code>.
     */
    public Response(final String _label)
    {
        super(_label);
    }
}
