package net.jonp.armi;

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

        return params;
    }
}
