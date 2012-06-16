package net.jonp.armi;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Tests the Error class.
 */
public class ErrorTest
{
    /**
     * Test method for {@link net.jonp.armi.Error#toStatement()}.
     */
    @Test
    public void testToStatement()
    {
        final String expected = "error label \"label\" java.lang.Exception (\"Message text\")";
        final Error error = getTestError();

        assertEquals(expected, error.toStatement());
    }

    /**
     * Test method for {@link net.jonp.armi.Error#getException()}.
     */
    @Test
    public void testGetException()
    {
        final String expected = "java.lang.Exception";
        final Error error = getTestError();

        assertEquals(expected, error.getException());
    }

    /**
     * Test method for {@link net.jonp.armi.Error#getMessage()}.
     */
    @Test
    public void testGetMessage()
    {
        final String expected = "Message text";
        final Error error = getTestError();

        assertEquals(expected, error.getMessage());
    }

    /**
     * Test method for {@link net.jonp.armi.Error#toString()}.
     */
    @Test
    public void testToString()
    {
        final String expected = "java.lang.Exception[Message text]";
        final Error error = getTestError();

        assertEquals(expected, error.toString());
    }

    /**
     * Test method for
     * {@link net.jonp.armi.AbstractLanguageObject#getLabel()}.
     */
    @Test
    public void testGetLabel()
    {
        final String expected = "label";
        final Error error = getTestError();

        assertEquals(expected, error.getLabel());
    }

    private Error getTestError()
    {
        return new Error("label", "java.lang.Exception", "Message text");
    }
}
