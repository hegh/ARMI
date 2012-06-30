package net.jonp.armi.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Verifies that circular references work properly, both straight-up and in
 * arrays, collections, and maps.
 */
public class CircularReferenceTestClass
    extends TestClass
    implements Cloneable
{
    public static final String NAME = "CircularTestObject";

    public static final String COMMAND = "CircularTestObject (" +
                                         //
                                         CircularReferenceTestClass.class.getName() +
                                         ".circularfield = ref 0, " +
                                         //
                                         CircularReferenceTestClass.class.getName() + ".circulararray = array(" +
                                         CircularReferenceTestClass.class.getName() +
                                         ") [ref 0], " +
                                         //
                                         CircularReferenceTestClass.class.getName() +
                                         ".circularcollection = collection(java.util.ArrayList) [ref 0], " +
                                         //
                                         CircularReferenceTestClass.class.getName() +
                                         ".circularmap = map(java.util.HashMap) [ref 0 = ref 0])";

    // We cannot do much with toString() because it would cause a
    // StackOverflowError, so we don't bother
    public static final String STRING = "CircularTestClass()";

    public CircularReferenceTestClass circularfield;
    public CircularReferenceTestClass[] circulararray;
    public List<CircularReferenceTestClass> circularcollection;
    public Map<CircularReferenceTestClass, CircularReferenceTestClass> circularmap;

    public CircularReferenceTestClass()
    {
        this(false);
    }

    public CircularReferenceTestClass(final boolean initialize)
    {
        if (initialize) {
            circularfield = this;
            circulararray = new CircularReferenceTestClass[] {
                this
            };

            circularcollection = new ArrayList<CircularReferenceTestClass>();
            circularcollection.add(this);

            circularmap = new HashMap<CircularReferenceTestClass, CircularReferenceTestClass>();
            circularmap.put(this, this);
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
        // We don't actually have any data that can be hashed
        return 0;
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (null == obj) {
            return false;
        }
        else if (obj instanceof CircularReferenceTestClass) {
            final CircularReferenceTestClass rhs = (CircularReferenceTestClass)obj;

            // Cannot use Arrays.equals or similar because they would throw a
            // StackOverflowError
            return (circularfield == rhs.circularfield && //
                    arraysAreEqual(circulararray, rhs.circulararray) && //
                    listsAreEqual(circularcollection, rhs.circularcollection) && //
            mapsAreEqual(circularmap, rhs.circularmap));
        }
        else {
            return false;
        }
    }

    @Override
    public CircularReferenceTestClass clone()
    {
        return (CircularReferenceTestClass)super.clone();
    }

    @Override
    public String toString()
    {
        return String.format("%s()", getClass().getSimpleName());
    }

    private boolean arraysAreEqual(final CircularReferenceTestClass[] lhs, final CircularReferenceTestClass[] rhs)
    {
        if (null == lhs && null == rhs) {
            return true;
        }
        else if (null == lhs || null == rhs) {
            return false;
        }
        else if (lhs.length != rhs.length) {
            return false;
        }
        else {
            for (int i = 0; i < lhs.length; i++) {
                if (lhs[i] != rhs[i]) {
                    return false;
                }
            }

            return true;
        }
    }

    private boolean listsAreEqual(final List<CircularReferenceTestClass> lhs, final List<CircularReferenceTestClass> rhs)
    {
        if (null == lhs && null == rhs) {
            return true;
        }
        else if (null == lhs || null == rhs) {
            return false;
        }
        else {
            return arraysAreEqual(lhs.toArray(new CircularReferenceTestClass[lhs.size()]),
                                  rhs.toArray(new CircularReferenceTestClass[rhs.size()]));
        }
    }

    private boolean mapsAreEqual(final Map<CircularReferenceTestClass, CircularReferenceTestClass> lhs,
                                 final Map<CircularReferenceTestClass, CircularReferenceTestClass> rhs)
    {
        if (null == lhs && null == rhs) {
            return true;
        }
        else if (null == lhs || null == rhs) {
            return false;
        }
        else if (lhs.size() != rhs.size()) {
            return false;
        }
        else {
            for (final Map.Entry<CircularReferenceTestClass, CircularReferenceTestClass> entry : lhs.entrySet()) {
                if (rhs.get(entry.getKey()) != entry.getValue()) {
                    return false;
                }
            }

            return true;
        }
    }
}
