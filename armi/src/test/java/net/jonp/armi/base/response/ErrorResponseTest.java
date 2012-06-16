package net.jonp.armi.base.response;

import static org.junit.Assert.assertEquals;

import net.jonp.armi.base.AbstractLanguageObject;
import net.jonp.armi.base.response.ErrorResponse;

import org.junit.Test;

/**
 * Tests the ErrorResponse class.
 */
public class ErrorResponseTest
{
    /**
     * Test method for {@link net.jonp.armi.base.response.ErrorResponse#toStatement()}.
     */
    @Test
    public void testToStatement()
    {
        final String expected = "error label \"label\" java.lang.Exception (\"Message text\")";
        final ErrorResponse error = getTestError();

        assertEquals(expected, error.toStatement());
    }

    /**
     * Test method for {@link net.jonp.armi.base.response.ErrorResponse#getException()}
     * .
     */
    @Test
    public void testGetException()
    {
        final String expected = "java.lang.Exception";
        final ErrorResponse error = getTestError();

        assertEquals(expected, error.getException());
    }

    /**
     * Test method for {@link net.jonp.armi.base.response.ErrorResponse#getMessage()}.
     */
    @Test
    public void testGetMessage()
    {
        final String expected = "Message text";
        final ErrorResponse error = getTestError();

        assertEquals(expected, error.getMessage());
    }

    /**
     * Test method for {@link net.jonp.armi.base.response.ErrorResponse#toString()}.
     */
    @Test
    public void testToString()
    {
        final String expected = "java.lang.Exception[Message text]";
        final ErrorResponse error = getTestError();

        assertEquals(expected, error.toString());
    }

    /**
     * Test method for
     * {@link net.jonp.armi.base.AbstractLanguageObject#getLabel()}.
     */
    @Test
    public void testGetLabel()
    {
        final String expected = "label";
        final ErrorResponse error = getTestError();

        assertEquals(expected, error.getLabel());
    }

    private ErrorResponse getTestError()
    {
        return new ErrorResponse("label", "java.lang.Exception", "Message text");
    }
}
