package net.jonp.armi.base;

import net.jonp.armi.base.command.CommandParser;

/**
 * Implemented by objects which need an {@link #init()} method called after all
 * fields have been filled in.
 */
public interface Initializable
{
    /**
     * Called after all fields have been filled in by the {@link CommandParser}.
     */
    public void init();
}
