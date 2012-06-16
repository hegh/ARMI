package net.jonp.armi.response;

import static org.junit.Assert.assertEquals;

import java.rmi.NotBoundException;

import net.jonp.armi.DefaultClassRegistry;
import net.jonp.armi.TestClass;

import org.junit.Test;

/**
 * Tests the ValueResponse class.
 */
public class ValueResponseTest
{
    /**
     * Test method for
     * {@link net.jonp.armi.response.ValueResponse#toStatement()}.
     * 
     * @throws NotBoundException If there was a problem with the class registry.
     */
    @Test
    public void testToStatement()
        throws NotBoundException
    {
        final DefaultClassRegistry registry = new DefaultClassRegistry();
        registry.put("TestObject", TestClass.class);

        final String expected = "response label \"label\" (TestObject" + //
                                " (field1 = \"val1\"," + //
                                " field2 = 12," + //
                                " field3 = 13.45," + //
                                " field4 = true," + //
                                " field5 = null," + //
                                " field6 = array(java.lang.String) []," + //
                                " field7 = array(java.lang.Integer) [5, 4, 3]))";
        final ValueResponse value = getTestValue();

        assertEquals(expected, value.toStatement(registry));
    }

    /**
     * Test method for
     * {@link net.jonp.armi.response.ValueResponse#getValue()}.
     */
    @Test
    public void testGetValue()
    {
        final TestClass expected = getTestObject();
        final ValueResponse value = getTestValue();

        assertEquals(expected, value.getValue());
    }

    /**
     * Test method for
     * {@link net.jonp.armi.response.ValueResponse#toString()}.
     */
    @Test
    public void testToString()
    {
        final String expected = getTestObject().toString();
        final ValueResponse value = getTestValue();

        assertEquals(expected, value.toString());
    }

    /**
     * Test method for
     * {@link net.jonp.armi.AbstractLanguageObject#getLabel()}.
     */
    @Test
    public void testGetLabel()
    {
        final String expected = "label";
        final ValueResponse value = getTestValue();

        assertEquals(expected, value.getLabel());
    }

    private TestClass getTestObject()
    {
        final TestClass testObject = new TestClass();
        testObject.field1 = "val1";
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

    private ValueResponse getTestValue()
    {
        return new ValueResponse("label", getTestObject());
    }
}
