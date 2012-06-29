package net.jonp.armi.base;

/**
 * Superclass for {@link SuperclassTestClass}.
 */
public abstract class SuperclassTestSuperclass
    extends TestClass
{
    private String stringfield;

    public SuperclassTestSuperclass()
    {
        this(false);
    }

    public SuperclassTestSuperclass(final boolean initialize)
    {
        if (initialize) {
            stringfield = "Superclass test string";
        }
    }

    @Override
    public int hashCode()
    {
        return (stringfield == null ? 0 : stringfield.hashCode());
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (null == obj) {
            return false;
        }
        else if (obj instanceof SuperclassTestSuperclass) {
            final SuperclassTestSuperclass rhs = (SuperclassTestSuperclass)obj;
            return ((stringfield == null ? rhs.stringfield == null : stringfield.equals(rhs.stringfield)));
        }
        else {
            return false;
        }
    }

    @Override
    public SuperclassTestSuperclass clone()
    {
        return (SuperclassTestSuperclass)super.clone();
    }

    @Override
    public String toString()
    {
        // Can't use getClass() because it will return the subclass's name
        return String.format("%s(%s)", SuperclassTestSuperclass.class.getSimpleName(), stringfield);
    }
}
