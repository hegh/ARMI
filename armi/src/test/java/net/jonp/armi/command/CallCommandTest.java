package net.jonp.armi.command;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.rmi.NotBoundException;

import net.jonp.armi.DefaultClassRegistry;
import net.jonp.armi.TestCase;
import net.jonp.armi.TestClass;

import org.junit.Test;

/**
 * Test the {@link CallCommand} object.
 */
public class CallCommandTest
    extends TestCase
{
    private final TestClass test;

    public CallCommandTest(final TestClass _test)
    {
        test = _test;
    }

    /**
     * Test method for
     * {@link net.jonp.armi.command.CallCommand#toStatement()}.
     * 
     * @throws NotBoundException If there was a problem with the class registry.
     */
    @Test
    public void testToStatement()
        throws NotBoundException
    {
        // Make sure we can pass multiple different argument types to a method
        final DefaultClassRegistry registry = new DefaultClassRegistry();
        registry.put(test.getName(), test.getClass());

        final String expected = "call label \"label\" name.of.method (\"arg1\", 2, 3.4, true, " + test.getCommand() + ")";
        final CallCommand command = getTestCommand();

        assertEquals(expected, command.toStatement(registry));
    }

    /**
     * Test method for
     * {@link net.jonp.armi.command.CallCommand#getObject()}.
     */
    @Test
    public void testGetObject()
    {
        final String expected = "name.of";
        final CallCommand command = getTestCommand();

        assertEquals(expected, command.getObject());
    }

    /**
     * Test method for
     * {@link net.jonp.armi.command.CallCommand#getMethod()}.
     */
    @Test
    public void testGetMethod()
    {
        final String expected = "method";
        final CallCommand command = getTestCommand();

        assertEquals(expected, command.getMethod());
    }

    /**
     * Test method for
     * {@link net.jonp.armi.command.CallCommand#getArguments()}.
     */
    @Test
    public void testGetArguments()
    {
        final Object[] expected = new Object[] {
            "arg1", Integer.valueOf(2), Double.valueOf(3.4), Boolean.TRUE, test,
        };
        final CallCommand command = getTestCommand();

        assertArrayEquals(expected, command.getArguments());
    }

    /**
     * Test method for
     * {@link net.jonp.armi.command.CallCommand#toString()}.
     */
    @Test
    public void testToString()
    {
        final String expected = "name.of.method(arg1, 2, 3.4, true, " + test.getString() + ")";
        final CallCommand command = getTestCommand();

        assertEquals(expected, command.toString());
    }

    /**
     * Test method for
     * {@link net.jonp.armi.AbstractLanguageObject#getLabel()}.
     */
    @Test
    public void testGetLabel()
    {
        final String expected = "label";
        final CallCommand command = getTestCommand();

        assertEquals(expected, command.getLabel());
    }

    private CallCommand getTestCommand()
    {
        return new CallCommand("label", "name.of", "method", new Object[] {
            "arg1", Integer.valueOf(2), Double.valueOf(3.4), Boolean.TRUE, test,
        });
    }
}
