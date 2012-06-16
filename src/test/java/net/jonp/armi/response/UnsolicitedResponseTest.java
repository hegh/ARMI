package net.jonp.armi.response;

import static org.junit.Assert.assertEquals;

import java.rmi.NotBoundException;

import net.jonp.armi.DefaultClassRegistry;
import net.jonp.armi.TestClass;

import org.junit.Test;

/**
 * Test the {@link UnsolicitedResponse} class.
 */
public class UnsolicitedResponseTest
{
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
        registry.put("TestObject", TestClass.class);

        final String expected =
            "unsol (type.of.response, TestObject (field1 = \"val1\", field2 = 12, field3 = 13.45, field4 = true, field5 = array [\"string\"]))";
        final UnsolicitedResponse unsol = getTestUnsolicited();

        assertEquals(expected, unsol.toStatement(registry));
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
        final Object expected = getTestObject();
        final UnsolicitedResponse unsol = getTestUnsolicited();

        assertEquals(expected, unsol.getValue());
    }

    /**
     * Test method for
     * {@link net.jonp.armi.response.UnsolicitedResponse#toString()}.
     */
    @Test
    public void testToString()
    {
        final String expected = "type.of.response(" + getTestObject().toString() + ")";
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

    private TestClass getTestObject()
    {
        final TestClass testObject = new TestClass();
        testObject.field1 = "val1";
        testObject.field2 = 12;
        testObject.field3 = 13.45;
        testObject.field4 = true;
        testObject.field5 = new Object[] {
            "string"
        };

        return testObject;
    }

    private UnsolicitedResponse getTestUnsolicited()
    {
        return new UnsolicitedResponse("type.of.response", getTestObject());
    }
}
