package net.jonp.armi;

/**
 * A test class for testing {@link Initializable}.
 */
public class InitializableTestClass
    extends TestClass
    implements Cloneable, Initializable
{
    public static final String NAME = "InitializableTestObject";

    public static final String COMMAND = "InitializableTestObject ()";

    public static final String STRING = "InitializableTestClass()";

    public transient int initialized = 0;

    @Override
    public void init()
    {
        initialized++;
    }

    @Override
    public String getName()
    {
        return NAME;
    }

    @Override
    public String getCommand()
    {
        return COMMAND;
    }

    @Override
    public String getString()
    {
        return STRING;
    }

    @Override
    public InitializableTestClass clone()
    {
        return (InitializableTestClass)super.clone();
    }

    @Override
    public int hashCode()
    {
        return super.hashCode();
    }

    @Override
    public boolean equals(final Object rhs)
    {
        return (rhs instanceof InitializableTestClass);
    }

    @Override
    public String toString()
    {
        return String.format("%s()", getClass().getSimpleName());
    }
}
