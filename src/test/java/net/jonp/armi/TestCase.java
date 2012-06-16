package net.jonp.armi;

import java.util.Arrays;
import java.util.Collection;

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
        return Arrays.asList(new Object[] {
            new BlankTestClass()
        }, new Object[] {
            new InitializableTestClass()
        }, new Object[] {
            new PrimitiveTestClass()
        }, new Object[] {
            new AccessibleTestClass(AccessibleTestClass.FINALFIELD, AccessibleTestClass.TRANSIENTFIELD)
        }, new Object[] {
            new ArrayTestClass()
        });
    }
}
