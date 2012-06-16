package net.jonp.armi;

import static org.junit.Assert.assertEquals;

import java.rmi.NotBoundException;

import org.junit.Test;

/**
 * Tests the Value class.
 */
public class ValueTest
{
    /**
     * Test method for {@link net.jonp.armi.Value#toStatement()}.
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
            "response label \"label\" (TestObject" + " (field1 = \"val1\", field2 = 12, field3 = 13.45, field4 = true))";
        final Value value = getTestValue();

        assertEquals(expected, value.toStatement(registry));
    }

    /**
     * Test method for {@link net.jonp.armi.Value#getValue()}.
     */
    @Test
    public void testGetValue()
    {
        final TestClass expected = getTestObject();
        final Value value = getTestValue();

        assertEquals(expected, value.getValue());
    }

    /**
     * Test method for {@link net.jonp.armi.Value#toString()}.
     */
    @Test
    public void testToString()
    {
        final String expected = getTestObject().toString();
        final Value value = getTestValue();

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
        final Value value = getTestValue();

        assertEquals(expected, value.getLabel());
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

    private Value getTestValue()
    {
        return new Value("label", getTestObject());
    }
}
