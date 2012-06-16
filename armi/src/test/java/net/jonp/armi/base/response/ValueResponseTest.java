package net.jonp.armi.base.response;

import static org.junit.Assert.assertEquals;

import java.rmi.NotBoundException;

import net.jonp.armi.base.TestCase;
import net.jonp.armi.base.TestClass;
import net.jonp.armi.base.response.ValueResponse;
import net.jonp.armi.comm.DefaultClassRegistry;

import org.junit.Test;

/**
 * Tests the ValueResponse class.
 */
public class ValueResponseTest
    extends TestCase
{
    private final TestClass test;

    public ValueResponseTest(final TestClass _test)
    {
        test = _test;
    }

    /**
     * Test method for
     * {@link net.jonp.armi.base.response.ValueResponse#toStatement()}.
     * 
     * @throws NotBoundException If there was a problem with the class registry.
     */
    @Test
    public void testToStatement()
        throws NotBoundException
    {
        final DefaultClassRegistry registry = new DefaultClassRegistry();
        registry.put(test.getName(), test.getClass());

        final String expected = "response label \"label\" (" + test.getCommand() + ")";
        final ValueResponse value = getTestValue();

        assertEquals(expected, value.toStatement(registry));
    }

    /**
     * Test method for
     * {@link net.jonp.armi.base.response.ValueResponse#getValue()}.
     */
    @Test
    public void testGetValue()
    {
        final TestClass expected = test;
        final ValueResponse value = getTestValue();

        assertEquals(expected, value.getValue());
    }

    /**
     * Test method for
     * {@link net.jonp.armi.base.response.ValueResponse#toString()}.
     */
    @Test
    public void testToString()
    {
        final String expected = test.toString();
        final ValueResponse value = getTestValue();

        assertEquals(expected, value.toString());
    }

    /**
     * Test method for
     * {@link net.jonp.armi.base.AbstractLanguageObject#getLabel()}.
     */
    @Test
    public void testGetLabel()
    {
        final String expected = "label";
        final ValueResponse value = getTestValue();

        assertEquals(expected, value.getLabel());
    }

    private ValueResponse getTestValue()
    {
        return new ValueResponse("label", test);
    }
}
