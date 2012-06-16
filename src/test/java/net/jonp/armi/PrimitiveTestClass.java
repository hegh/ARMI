package net.jonp.armi;


/**
 * Used to test serialization with primitive fields.
 */
public class PrimitiveTestClass
    extends TestClass
    implements Cloneable
{
    public static final String NAME = "PrimitiveTestObject";

    public static final String COMMAND = "PrimitiveTestObject (" + //
                                         "booleanfield = true," + //
                                         " bytefield = -5Y," + //
                                         " doublefield = 3.141592653589793," + //
                                         " floatfield = -3.1415927F," + //
                                         " intfield = 1048576," + //
                                         " longfield = 8589934592L," + //
                                         " shortfield = 325T," + //
                                         " stringfield = \"\\\"string\\\\value\\\"\")";

    public static final String STRING =
        "PrimitiveTestClass(true, -5, 325, 1048576, 8589934592, -3.141593, 3.141593, \"string\\value\")";

    public boolean booleanfield = true;
    public byte bytefield = -5;
    public short shortfield = 325;
    public int intfield = 1048576;
    public long longfield = 8589934592L;
    public float floatfield = -3.1415927f;
    public double doublefield = 3.141592653589793;
    public String stringfield = "\"string\\value\"";

    public PrimitiveTestClass()
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
        return (Boolean.valueOf(booleanfield).hashCode() ^ //
                Byte.valueOf(bytefield).hashCode() ^ //
                Short.valueOf(shortfield).hashCode() ^ //
                Integer.valueOf(intfield).hashCode() ^ //
                Long.valueOf(longfield).hashCode() ^ //
                Float.valueOf(floatfield).hashCode() ^ //
                Double.valueOf(doublefield).hashCode() ^ //
        (stringfield == null ? 0 : stringfield.hashCode()));
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (null == obj) {
            return false;
        }
        else if (obj instanceof PrimitiveTestClass) {
            final PrimitiveTestClass rhs = (PrimitiveTestClass)obj;
            return (booleanfield == rhs.booleanfield && //
                    bytefield == rhs.bytefield && //
                    shortfield == rhs.shortfield && //
                    intfield == rhs.intfield && //
                    longfield == rhs.longfield && //
                    floatfield == rhs.floatfield && //
                    doublefield == rhs.doublefield && (stringfield == null ? rhs.stringfield == null : stringfield
                .equals(rhs.stringfield)));
        }
        else {
            return false;
        }
    }

    @Override
    public PrimitiveTestClass clone()
    {
        return (PrimitiveTestClass)super.clone();
    }

    @Override
    public String toString()
    {
        return String.format("%s(%b, %d, %d, %d, %d, %f, %f, %s)", getClass().getSimpleName(), booleanfield, bytefield, shortfield,
                             intfield, longfield, floatfield, doublefield, stringfield);
    }
}
