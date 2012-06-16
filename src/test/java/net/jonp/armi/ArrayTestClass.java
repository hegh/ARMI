package net.jonp.armi;

import java.util.Arrays;

/**
 * Used to test serialization with array fields.
 */
public class ArrayTestClass
    extends TestClass
    implements Cloneable
{
    public static final String NAME = "ArrayTestObject";

    public static final String COMMAND = "ArrayTestObject (" + //
                                         "intarray = array(java.lang.Integer) [3, 5, 7]," + //
                                         " nestedarray = array(java.lang.Object) [" + //
                                         "\"String1\"," + //
                                         " 2," + //
                                         " 3.4," + //
                                         " array(java.lang.Object) [" + //
                                         "\"Sub1\"," + //
                                         " array(java.lang.String) [" + //
                                         "\"SubSub1\"," + //
                                         " null," + //
                                         " \"SubSub2\"]," + //
                                         " \"Sub2\"]," + //
                                         " true]," + //
                                         " nullarray = null," + //
                                         " stringarray = array(java.lang.String) [\"s1\", \"s2\", \"s3\"])";

    public static final String STRING =
        "ArrayTestClass([3, 5, 7], [s1, s2, s3], null, [String1, 2, 3.4, [Sub1, [SubSub1, null, SubSub2], Sub2], true])";

    public Integer[] intarray = new Integer[] {
        3, 5, 7
    };

    public String[] stringarray = new String[] {
        "s1", "s2", "s3"
    };

    public final Short[] nullarray = null;

    public Object[] nestedarray = new Object[] {
        "String1", 2, 3.4, new Object[] {
            "Sub1", new String[] {
                "SubSub1", null, "SubSub2"
            }, "Sub2"
        }, true
    };

    public ArrayTestClass()
    {
        // Nothing to do
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
        return (intarray.length ^ stringarray.length ^ nestedarray.length);
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (null == obj) {
            return false;
        }
        else if (obj instanceof ArrayTestClass) {
            final ArrayTestClass rhs = (ArrayTestClass)obj;
            return (Arrays.equals(intarray, rhs.intarray) && //
                    Arrays.equals(stringarray, rhs.stringarray) && //
            Arrays.deepEquals(nestedarray, rhs.nestedarray));
        }
        else {
            return false;
        }
    }

    @Override
    public ArrayTestClass clone()
    {
        return (ArrayTestClass)super.clone();
    }

    @Override
    public String toString()
    {
        return String.format("%s(%s, %s, %s, %s)", getClass().getSimpleName(), Conversion.describe(intarray),
                             Conversion.describe(stringarray), Conversion.describe(nullarray), Conversion.describe(nestedarray));
    }
}
