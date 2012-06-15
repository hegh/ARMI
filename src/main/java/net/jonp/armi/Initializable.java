package net.jonp.armi;

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
