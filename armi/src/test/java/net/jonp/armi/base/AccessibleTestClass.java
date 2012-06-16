package net.jonp.armi.base;

import net.jonp.armi.base.Initializable;


/**
 * Used to test object with inaccessible fields.
 */
public class AccessibleTestClass
    extends TestClass
    implements Cloneable, Initializable
{
    public static final String NAME = "AccessTestObject";

    public static final String COMMAND = "AccessTestObject (" + //
                                         "defaultfield = 15," + //
                                         " finalfield = 1," + //
                                         " privatefield = -225," + //
                                         " protectedfield = 84)";

    public static final String STRING = "AccessibleTestClass(1, 84, 15, -225)";

    public static final int FINALFIELD = 1;
    public static final int TRANSIENTFIELD = 21;

    public final int finalfield;
    public transient int transientfield;
    public static int staticfield;

    protected int protectedfield;
    int defaultfield;
    private int privatefield;

    private transient boolean initialized = false;

    public AccessibleTestClass()
    {
        this(false);
    }

    public AccessibleTestClass(final boolean initialize)
    {
        if (initialize) {
            finalfield = FINALFIELD;
            transientfield = TRANSIENTFIELD;
            protectedfield = 84;
            defaultfield = 15;
            privatefield = -225;
        }
        else {
            finalfield = 0;
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

    public int getProtected()
    {
        return protectedfield;
    }

    public void setProtected(final int value)
    {
        protectedfield = value;
    }

    public int getDefault()
    {
        return defaultfield;
    }

    public void setDefault(final int value)
    {
        defaultfield = value;
    }

    public int getPrivate()
    {
        return privatefield;
    }

    public void setPrivate(final int value)
    {
        privatefield = value;
    }

    @Override
    public void init()
    {
        initialized = true;
    }

    @Override
    public int hashCode()
    {
        return (finalfield ^ transientfield ^ protectedfield ^ defaultfield ^ privatefield);
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (null == obj) {
            return false;
        }
        else if (obj instanceof AccessibleTestClass) {
            final AccessibleTestClass rhs = (AccessibleTestClass)obj;
            return (finalfield == rhs.finalfield && //
                    protectedfield == rhs.protectedfield && //
                    defaultfield == rhs.defaultfield && //
            privatefield == rhs.privatefield);
        }
        else {
            return false;
        }
    }

    @Override
    public AccessibleTestClass clone()
    {
        return (AccessibleTestClass)super.clone();
    }

    @Override
    public String toString()
    {
        return String.format("%s(%d, %d, %d, %d)", getClass().getSimpleName(), finalfield, protectedfield, defaultfield,
                             privatefield);
    }

    @Override
    public boolean isValid()
    {
        if (initialized) {
            return (0 == transientfield);
        }
        else {
            return (TRANSIENTFIELD == transientfield);
        }
    }
}
