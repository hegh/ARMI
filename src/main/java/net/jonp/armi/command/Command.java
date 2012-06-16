package net.jonp.armi.command;

import net.jonp.armi.AbstractLanguageObject;


/**
 * Represents a command, such as a Call or a List.
 */
public abstract class Command
    extends AbstractLanguageObject
{
    /**
     * Construct a new Command.
     * 
     * @param _label The label, or <code>null</code>.
     */
    public Command(final String _label)
    {
        super(_label);
    }
}
