package net.jonp.armi;

/**
 * A test class with no fields at all, to verify an empty field list.
 */
public class BlankTestClass
    extends TestClass
    implements Cloneable
{
    public static final String NAME = "BlankTestObject";

    public static final String COMMAND = "BlankTestObject ()";

    public static final String STRING = "BlankTestClass()";

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
    public BlankTestClass clone()
    {
        return (BlankTestClass)super.clone();
    }

    @Override
    public int hashCode()
    {
        return super.hashCode();
    }

    @Override
    public boolean equals(final Object rhs)
    {
        return (rhs instanceof BlankTestClass);
    }

    @Override
    public String toString()
    {
        return String.format("%s()", getClass().getSimpleName());
    }
}
