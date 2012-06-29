package net.jonp.armi.base;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Superclass for tests.
 */
@RunWith(Parameterized.class)
public abstract class TestCase
{
    @Parameters
    public static Collection<Object[]> getTestObjects()
    {
        final List<Object[]> params = new LinkedList<Object[]>();

        // 0
        params.add(new Object[] {
            new BlankTestClass()
        });

        // 1
        params.add(new Object[] {
            new InitializableTestClass()
        });

        // 2
        params.add(new Object[] {
            new PrimitiveTestClass(true)
        });

        // 3
        params.add(new Object[] {
            new AccessibleTestClass(true)
        });

        // 4
        params.add(new Object[] {
            new ArrayTestClass(true)
        });

        // 5
        params.add(new Object[] {
            new CollectionTestClass(true)
        });

        // 6
        params.add(new Object[] {
            new MapTestClass(true)
        });

        // 7
        params.add(new Object[] {
            new SuperclassTestClass(true)
        });

        return params;
    }
}
