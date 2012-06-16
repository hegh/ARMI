package net.jonp.armi;

import java.util.Arrays;

/**
 * Used to test object serialization.
 */
public class TestClass
    implements Cloneable
{
    public String field1;
    public int field2;
    public double field3;
    public boolean field4;
    public Object[] field5;
    public String[] field6;
    public Integer[] field7;

    public TestClass()
    {
        // Nothing to do
    }

    @Override
    public int hashCode()
    {
        return (null == field1 ? 0 : field1.hashCode()) ^ //
               field2 ^ //
               Double.valueOf(field3).hashCode() ^ //
               Boolean.valueOf(field4).hashCode() ^ //
               (null == field5 ? 0 : field5.length) ^ //
               (null == field6 ? 0 : field6.length) ^ //
               (null == field7 ? 0 : field7.length);
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (null == obj) {
            return false;
        }
        else if (obj instanceof TestClass) {
            final TestClass rhs = (TestClass)obj;
            if (null == field1) {
                if (null != rhs.field1) {
                    return false;
                }
            }
            else if (!field1.equals(rhs.field1)) {
                return false;
            }

            return (field2 == rhs.field2 && //
                    field3 == rhs.field3 && //
                    field4 == rhs.field4 && //
                    Arrays.equals(field5, rhs.field5) && //
                    Arrays.equals(field6, rhs.field6) && //
            Arrays.equals(field7, rhs.field7));
        }
        else {
            return false;
        }
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

    @Override
    public String toString()
    {
        return String.format("TestClass(%s, %d, %f, %b, %s, %s, %s)", field1, field2, field3, field4, Conversion.describe(field5),
                             Conversion.describe(field6), Conversion.describe(field7));
    }
}
