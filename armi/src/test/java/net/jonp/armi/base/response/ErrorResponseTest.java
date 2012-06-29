package net.jonp.armi.base.response;

import static org.junit.Assert.assertEquals;

import java.rmi.NotBoundException;

import net.jonp.armi.comm.DefaultClassRegistry;

import org.junit.Test;

/**
 * Tests the ErrorResponse class.
 */
public class ErrorResponseTest
{
    /**
     * Test method for
     * {@link net.jonp.armi.base.response.ErrorResponse#toStatement()}.
     */
    @Test
    public void testToStatement()
        throws NotBoundException
    {
        // This is, unfortunately, very ugly, but Exceptions have a bunch of
        // hidden fields
        final String expected = "error label \"label\" (java.lang.Exception" + //
                                " (java.lang.Throwable.cause = ref 0," + //
                                " java.lang.Throwable.detailMessage = \"Message text\"," + //
                                " java.lang.Throwable.stackTrace = array(java.lang.StackTraceElement) []," + //
                                " java.lang.Throwable.suppressedExceptions = collection(java.util.Collections$UnmodifiableRandomAccessList) []))";
        final ErrorResponse error = getTestError();

        final DefaultClassRegistry registry = new DefaultClassRegistry();
        assertEquals(expected, error.toStatement(registry));
    }

    /**
     * Test method for
     * {@link net.jonp.armi.base.response.ErrorResponse#getException()} .
     */
    @Test
    public void testGetException()
    {
        final Exception expected = new Exception("Message text");
        final ErrorResponse error = getTestError();

        // Can't use .equals() for exceptions, so assertEquals() on its own
        // doesn't work
        assertEquals(expected.getClass(), error.getException().getClass());
        assertEquals(expected.getMessage(), error.getException().getMessage());
    }

    /**
     * Test method for
     * {@link net.jonp.armi.base.response.ErrorResponse#toString()}.
     */
    @Test
    public void testToString()
    {
        final String expected = "Exception[Message text]";
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
        return new ErrorResponse("label", new Exception("Message text"));
    }
}
