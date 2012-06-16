package net.jonp.armi.response;

import static org.junit.Assert.assertEquals;

import java.rmi.NotBoundException;

import net.jonp.armi.DefaultClassRegistry;
import net.jonp.armi.TestCase;
import net.jonp.armi.TestClass;

import org.junit.Test;

/**
 * Test the {@link UnsolicitedResponse} class.
 */
public class UnsolicitedResponseTest
    extends TestCase
{
    private final TestClass test;

    public UnsolicitedResponseTest(final TestClass _test)
    {
        test = _test;
    }

    /**
     * Test method for
     * {@link net.jonp.armi.response.UnsolicitedResponse#toStatement(net.jonp.ms5.command.ClassRegistry)}
     * .
     * 
     * @throws NotBoundException If the class registry was not set up correctly.
     */
    @Test
    public void testToStatement()
        throws NotBoundException
    {
        final DefaultClassRegistry registry = new DefaultClassRegistry();
        registry.put(test.getName(), test.getClass());

        final String expected = "unsol (type.of.response, " + test.getCommand() + ")";
        final UnsolicitedResponse unsol = getTestUnsolicited();

        assertEquals(expected, unsol.toStatement(registry));
        assertEquals(true, ((TestClass)unsol.getValue()).isValid());
    }

    /**
     * Test method for
     * {@link net.jonp.armi.response.UnsolicitedResponse#getType()}.
     */
    @Test
    public void testGetType()
    {
        final String expected = "type.of.response";
        final UnsolicitedResponse unsol = getTestUnsolicited();

        assertEquals(expected, unsol.getType());
    }

    /**
     * Test method for
     * {@link net.jonp.armi.response.UnsolicitedResponse#getValue()}.
     */
    @Test
    public void testGetValue()
    {
        final Object expected = test;
        final UnsolicitedResponse unsol = getTestUnsolicited();

        assertEquals(expected, unsol.getValue());
        assertEquals(true, ((TestClass)unsol.getValue()).isValid());
    }

    /**
     * Test method for
     * {@link net.jonp.armi.response.UnsolicitedResponse#toString()}.
     */
    @Test
    public void testToString()
    {
        final String expected = "type.of.response(" + test.getString() + ")";
        final UnsolicitedResponse unsol = getTestUnsolicited();

        assertEquals(expected, unsol.toString());
    }

    /**
     * Test method for
     * {@link net.jonp.armi.AbstractLanguageObject#getLabel()}.
     */
    @Test
    public void testGetLabel()
    {
        final String expected = null;
        final UnsolicitedResponse unsol = getTestUnsolicited();

        assertEquals(expected, unsol.getLabel());
    }

    private UnsolicitedResponse getTestUnsolicited()
    {
        return new UnsolicitedResponse("type.of.response", test);
    }
}
