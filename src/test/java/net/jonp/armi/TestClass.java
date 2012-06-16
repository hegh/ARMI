package net.jonp.armi;

/**
 * Superclass for test classes.
 */
public abstract class TestClass
    implements Cloneable
{
    protected TestClass()
    {
        // Nothing to do
    }

    public abstract String getName();

    public abstract String getCommand();

    public abstract String getString();

    /**
     * Called after each receipt, to verify that this instance is valid.
     * Override if you need to.
     * 
     * @return True if valid, false if not.
     */
    public boolean isValid()
    {
        return true;
    }

    @Override
    public TestClass clone()
    {
        try {
            return (TestClass)super.clone();
        }
        catch (final CloneNotSupportedException cnse) {
            throw new RuntimeException(cnse);
        }
    }
}
