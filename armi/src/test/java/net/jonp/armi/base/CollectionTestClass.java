package net.jonp.armi.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;


/**
 * Used to test serialization with array fields.
 */
public class CollectionTestClass
    extends TestClass
    implements Cloneable
{
    public static final String NAME = "CollectionTestObject";

    public static final String COMMAND = "CollectionTestObject (" +
                                         //
                                         CollectionTestClass.class.getName() + ".intlist =" +
                                         " collection(java.util.LinkedList) [1, ref 2, 2, 3, 5, 8, 13, 21], " +
                                         CollectionTestClass.class.getName() + ".nestedlist = collection(java.util.ArrayList) [" +
                                         "collection(java.util.Vector) [8.0F, 16.0F]," +
                                         " collection(java.util.ArrayList) [2.0F, 32.0F]," +
                                         " collection(java.util.LinkedList) []], " +
                                         //
                                         CollectionTestClass.class.getName() + ".nullcollection = null, " +
                                         //
                                         CollectionTestClass.class.getName() +
                                         ".stringset = collection(java.util.TreeSet) [\"First\", \"Second\", \"Third\"])";

    public static final String STRING =
        "CollectionTestClass([1, 1, 2, 3, 5, 8, 13, 21], [[8.0, 16.0], [2.0, 32.0], []], [First, Second, Third], null)";

    // XXX: Need to use ordered, sorted, or single-element collections so we can
    // guarantee the order for string comparisons

    public List<Integer> intlist;

    public Set<String> stringset;

    public final Collection<Object> nullcollection = null;

    public List<List<Float>> nestedlist;

    public CollectionTestClass()
    {
        this(false);
    }

    public CollectionTestClass(final boolean initialize)
    {
        if (initialize) {
            intlist = new LinkedList<Integer>();
            Collections.addAll(intlist, 1, 1, 2, 3, 5, 8, 13, 21);

            stringset = new TreeSet<String>();
            Collections.addAll(stringset, "Third", "Second", "First");

            nestedlist = new ArrayList<List<Float>>();

            final List<Float> list1 = new Vector<Float>();
            Collections.addAll(list1, 8f, 16f);
            nestedlist.add(list1);

            final List<Float> list2 = new ArrayList<Float>();
            Collections.addAll(list2, 2f, 32f);
            nestedlist.add(list2);

            final List<Float> list3 = new LinkedList<Float>();
            nestedlist.add(list3);
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
        return ((intlist == null ? 0 : intlist.size()) ^ //
                (nestedlist == null ? 0 : nestedlist.size()) ^ //
        (stringset == null ? 0 : stringset.size()));
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (null == obj) {
            return false;
        }
        else if (obj instanceof CollectionTestClass) {
            final CollectionTestClass rhs = (CollectionTestClass)obj;
            return ((intlist == null ? rhs.intlist == null : intlist.equals(rhs.intlist)) && //
                    (nestedlist == null ? rhs.nestedlist == null : nestedlist.equals(rhs.nestedlist)) && //
                    (stringset == null ? rhs.stringset == null : stringset.equals(rhs.stringset)) && //
            (nullcollection == null ? rhs.nullcollection == null : nullcollection.equals(rhs.nullcollection)));
        }
        else {
            return false;
        }
    }

    @Override
    public CollectionTestClass clone()
    {
        return (CollectionTestClass)super.clone();
    }

    @Override
    public String toString()
    {
        return String.format("%s(%s, %s, %s, %s)", getClass().getSimpleName(), Conversion.describe(intlist),
                             Conversion.describe(nestedlist), Conversion.describe(stringset), Conversion.describe(nullcollection));
    }
}
