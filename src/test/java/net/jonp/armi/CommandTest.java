package net.jonp.armi;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.rmi.NotBoundException;

import org.junit.Test;

/**
 * Test the Command object.
 */
public class CommandTest
{
    /**
     * Test method for {@link net.jonp.armi.Command#toStatement()}.
     * 
     * @throws NotBoundException If there was a problem with the class registry.
     */
    @Test
    public void testToStatement()
        throws NotBoundException
    {
        final DefaultClassRegistry registry = new DefaultClassRegistry();
        registry.put("TestObject", TestClass.class);

        final String expected =
            "call label \"label\" name.of.method (\"arg1\", 2, 3.4, true,"
                + " TestObject (field1 = \"val1\", field2 = 12, field3 = 13.45, field4 = true))";
        final Command command = getTestCommand();

        assertEquals(expected, command.toStatement(registry));
    }

    /**
     * Test method for {@link net.jonp.armi.Command#getObject()}.
     */
    @Test
    public void testGetObject()
    {
        final String expected = "name.of";
        final Command command = getTestCommand();

        assertEquals(expected, command.getObject());
    }

    /**
     * Test method for {@link net.jonp.armi.Command#getMethod()}.
     */
    @Test
    public void testGetMethod()
    {
        final String expected = "method";
        final Command command = getTestCommand();

        assertEquals(expected, command.getMethod());
    }

    /**
     * Test method for {@link net.jonp.armi.Command#getArguments()}.
     */
    @Test
    public void testGetArguments()
    {
        final Object[] expected = new Object[] {
            "arg1", Integer.valueOf(2), Double.valueOf(3.4), Boolean.TRUE, getTestObject(),
        };
        final Command command = getTestCommand();

        assertArrayEquals(expected, command.getArguments());
    }

    /**
     * Test method for {@link net.jonp.armi.Command#toString()}.
     */
    @Test
    public void testToString()
    {
        final String expected = "name.of.method(arg1, 2, 3.4, true, TestClass(val1, 12, 13.450000, true))";
        final Command command = getTestCommand();

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
        final Command command = getTestCommand();

        assertEquals(expected, command.getLabel());
    }

    private TestClass getTestObject()
    {
        final TestClass testObject = new TestClass();
        testObject.field1 = "val1";
        testObject.field2 = 12;
        testObject.field3 = 13.45;
        testObject.field4 = true;

        return testObject;
    }

    private Command getTestCommand()
    {
        return new Command("label", "name.of", "method", new Object[] {
            "arg1", Integer.valueOf(2), Double.valueOf(3.4), Boolean.TRUE, getTestObject(),
        });
    }
}
