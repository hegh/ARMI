package net.jonp.armi.base;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import net.jonp.armi.base.Conversion;


/**
 * Used to test serialization with array fields.
 */
public class MapTestClass
    extends TestClass
    implements Cloneable
{
    public static final String NAME = "MapTestObject";

    public static final String COMMAND = "MapTestObject (" + //
                                         "hashmap = map(java.util.HashMap) [" + //
                                         "\"TestKey\" = \"TestValue\"]," + //
                                         " nestedlinkedhashmap = map(java.util.LinkedHashMap) [" + //
                                         "1 = map(java.util.LinkedHashMap) [" + //
                                         "\"TrueKey\" = true," + //
                                         " \"FalseKey\" = false," + //
                                         " \"NullKey\" = null," + //
                                         " null = null]," + //
                                         " 2 = map(java.util.Hashtable) [" + //
                                         "\"OnlyKey\" = true]]," + //
                                         " nullmap = null," + //
                                         " treemap = map(java.util.TreeMap) [" + //
                                         "\"5 billion\" = 5000000000L," + //
                                         " \"One\" = 1L])";

    public static final String STRING = "MapTestClass({TestKey=TestValue}," + //
                                        " {1={TrueKey=true, FalseKey=false, NullKey=null, null=null}, 2={OnlyKey=true}}," + //
                                        " {5 billion=5000000000, One=1}," + //
                                        " null)";

    // XXX: We need to use sorted or linked maps, or only store one value, so we
    // can guarantee ordering when comparing to expected values

    public Map<String, String> hashmap;

    public Map<Integer, Map<String, Boolean>> nestedlinkedhashmap;

    public Map<String, Long> treemap;

    public final Map<Object, Object> nullmap = null;

    public MapTestClass()
    {
        this(false);
    }

    public MapTestClass(final boolean initialize)
    {
        if (initialize) {
            hashmap = new HashMap<String, String>();
            nestedlinkedhashmap = new LinkedHashMap<Integer, Map<String, Boolean>>();
            treemap = new TreeMap<String, Long>();

            hashmap.put("TestKey", "TestValue");

            final Map<String, Boolean> map1 = new LinkedHashMap<String, Boolean>();
            map1.put("TrueKey", true);
            map1.put("FalseKey", false);
            map1.put("NullKey", null);
            map1.put(null, null);
            nestedlinkedhashmap.put(1, map1);

            final Map<String, Boolean> map2 = new Hashtable<String, Boolean>();
            map2.put("OnlyKey", true);
            nestedlinkedhashmap.put(2, map2);

            treemap = new TreeMap<String, Long>();
            treemap.put("One", 1L);
            treemap.put("5 billion", 5000000000L);
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
        return ((hashmap == null ? 0 : hashmap.size()) ^ //
                (nestedlinkedhashmap == null ? 0 : nestedlinkedhashmap.size()) ^ //
        (treemap == null ? 0 : treemap.size()));
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (null == obj) {
            return false;
        }
        else if (obj instanceof MapTestClass) {
            final MapTestClass rhs = (MapTestClass)obj;
            return ((hashmap == null ? rhs.hashmap == null : hashmap.equals(rhs.hashmap)) && //
                    (nestedlinkedhashmap == null ? rhs.nestedlinkedhashmap == null : nestedlinkedhashmap
                        .equals(rhs.nestedlinkedhashmap)) && //
                    (treemap == null ? rhs.treemap == null : treemap.equals(rhs.treemap)) && //
            (nullmap == null ? rhs.nullmap == null : nullmap.equals(rhs.nullmap)));
        }
        else {
            return false;
        }
    }

    @Override
    public MapTestClass clone()
    {
        return (MapTestClass)super.clone();
    }

    @Override
    public String toString()
    {
        return String.format("%s(%s, %s, %s, %s)", getClass().getSimpleName(), Conversion.describe(hashmap),
                             Conversion.describe(nestedlinkedhashmap), Conversion.describe(treemap), Conversion.describe(nullmap));
    }
}
