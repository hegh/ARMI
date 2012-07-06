package net.jonp.armi.example.api;

/**
 * Used to transmit name updates to clients.
 */
public class NameChange
{
    public String oldname;
    public String newname;

    public NameChange()
    {
        // Nothing to do
    }

    public NameChange(final String _oldname, final String _newname)
    {
        oldname = _oldname;
        newname = _newname;
    }
}
