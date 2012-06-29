package net.jonp.armi.base;

/**
 * 
 */
public class SuperclassTestClass
    extends SuperclassTestSuperclass
{
    public static final String NAME = "SuperclassTestObject";

    public static final String COMMAND = "SuperclassTestObject (" +
                                         //
                                         SuperclassTestClass.class.getName() + ".stringfield = \"Subclass test string\", " +
                                         SuperclassTestSuperclass.class.getName() + ".stringfield = \"Superclass test string\")";

    public static final String STRING =
        "SuperclassTestClass(Subclass test string, [SuperclassTestSuperclass(Superclass test string)])";

    private String stringfield;

    public SuperclassTestClass()
    {
        this(false);
    }

    public SuperclassTestClass(final boolean initialize)
    {
        super(initialize);

        if (initialize) {
            stringfield = "Subclass test string";
        }
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
    public int hashCode()
    {
        return (super.hashCode() ^ (stringfield == null ? 0 : stringfield.hashCode()));
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (null == obj) {
            return false;
        }
        else if (obj instanceof SuperclassTestClass) {
            final SuperclassTestClass rhs = (SuperclassTestClass)obj;
            return (super.equals(rhs) && (stringfield == null ? rhs.stringfield == null : stringfield.equals(rhs.stringfield)));
        }
        else {
            return false;
        }
    }

    @Override
    public SuperclassTestClass clone()
    {
        return (SuperclassTestClass)super.clone();
    }

    @Override
    public String toString()
    {
        return String.format("%s(%s, [%s])", getClass().getSimpleName(), stringfield, super.toString());
    }
}
