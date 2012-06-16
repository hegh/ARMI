package net.jonp.armi.command;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.rmi.NotBoundException;

import net.jonp.armi.DefaultClassRegistry;
import net.jonp.armi.TestClass;

import org.junit.Test;

/**
 * Test the {@link CallCommand} object.
 */
public class CallCommandTest
{
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
        final DefaultClassRegistry registry = new DefaultClassRegistry();
        registry.put("TestObject", TestClass.class);

        final String expected = "call label \"label\" name.of.method (\"arg1\", 2, 3.4, true," + //
                                " TestObject (field1 = \"val\\\\1\"," + //
                                " field2 = 12," + //
                                " field3 = 13.45," + //
                                " field4 = true," + //
                                " field5 = null," + //
                                " field6 = array(java.lang.String) []," + //
                                " field7 = array(java.lang.Integer) [5, 4, 3]))";
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
            "arg1", Integer.valueOf(2), Double.valueOf(3.4), Boolean.TRUE, getTestObject(),
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
        final String expected = "name.of.method(arg1, 2, 3.4, true, TestClass(val\\1, 12, 13.450000, true, null, [], [5, 4, 3]))";
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

    private TestClass getTestObject()
    {
        final TestClass testObject = new TestClass();
        testObject.field1 = "val\\1";
        testObject.field2 = 12;
        testObject.field3 = 13.45;
        testObject.field4 = true;
        testObject.field5 = null;
        testObject.field6 = new String[] { };
        testObject.field7 = new Integer[] {
            5, 4, 3
        };

        return testObject;
    }

    private CallCommand getTestCommand()
    {
        return new CallCommand("label", "name.of", "method", new Object[] {
            "arg1", Integer.valueOf(2), Double.valueOf(3.4), Boolean.TRUE, getTestObject(),
        });
    }
}
