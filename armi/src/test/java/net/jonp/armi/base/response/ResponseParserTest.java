package net.jonp.armi.base.response;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.NotBoundException;

import net.jonp.armi.base.SyntaxException;
import net.jonp.armi.base.TestCase;
import net.jonp.armi.base.TestClass;
import net.jonp.armi.comm.DefaultClassRegistry;

import org.junit.Test;

/**
 * Tests {@link ResponseParser}.
 */
public class ResponseParserTest
    extends TestCase
{
    private final TestClass test;

    public ResponseParserTest(final TestClass _test)
    {
        test = _test;
    }

    /**
     * Test method for
     * {@link net.jonp.armi.base.response.ResponseParser#readNextResponse()}.
     * 
     * @throws IOException If there is a was a problem building the response
     *             parser.
     * @throws SyntaxException If there was a problem parsing the response.
     * @throws NotBoundException If there was a problem with the class registry.
     */
    @Test
    public void testReadNextValue()
        throws IOException, SyntaxException, NotBoundException
    {
        final DefaultClassRegistry registry = new DefaultClassRegistry();
        registry.put(test.getName(), test.getClass());

        final String commandString = "response label \"label\" (" + test.getCommand() + ")";
        final InputStream in = new ByteArrayInputStream(commandString.getBytes());
        final ResponseParser parser = new ResponseParser(in, registry);
        final Response response = parser.readNextResponse();

        assertEquals("label", response.getLabel());

        assertEquals(true, response instanceof ValueResponse);
        final ValueResponse value = (ValueResponse)response;

        assertEquals(test, value.getValue());
        assertEquals(commandString, value.toStatement(registry));
        assertEquals(test.getString(), value.toString());
        assertEquals(true, test.isValid());
    }

    /**
     * Test method for
     * {@link net.jonp.armi.base.response.ResponseParser#readNextResponse()}.
     * 
     * @throws IOException If there is a was a problem building the response
     *             parser.
     * @throws SyntaxException If there was a problem parsing the response.
     * @throws NotBoundException If there was a problem with the class registry.
     */
    @Test
    public void testReadNextError()
        throws IOException, SyntaxException, NotBoundException
    {
        final DefaultClassRegistry registry = new DefaultClassRegistry();
        final String commandString = "error (java.lang.Exception" + //
                                     " (java.lang.Throwable.cause = ref 0," + //
                                     " java.lang.Throwable.detailMessage = \"Error message\"," + //
                                     " java.lang.Throwable.stackTrace = array(java.lang.StackTraceElement) []," + //
                                     " java.lang.Throwable.suppressedExceptions = null))";
        final InputStream in = new ByteArrayInputStream(commandString.getBytes());
        final ResponseParser parser = new ResponseParser(in, registry);
        final Response response = parser.readNextResponse();

        assertEquals(null, response.getLabel());

        assertEquals(true, response instanceof ErrorResponse);
        final ErrorResponse error = (ErrorResponse)response;

        assertEquals("java.lang.Exception", error.getException().getClass().getName());
        assertEquals(commandString, error.toStatement(registry));
        assertEquals("Exception[Error message]", error.toString());
    }

    /**
     * Test method for
     * {@link net.jonp.armi.base.response.ResponseParser#readNextResponse()}.
     * 
     * @throws IOException If there is a was a problem building the response
     *             parser.
     * @throws SyntaxException If there was a problem parsing the response.
     * @throws NotBoundException If there was a problem with the class registry.
     */
    @Test
    public void testReadNextUnsolicited()
        throws IOException, SyntaxException, NotBoundException
    {
        final DefaultClassRegistry registry = new DefaultClassRegistry();
        final String commandString = "unsol (type.of.message, \"value\")";
        final InputStream in = new ByteArrayInputStream(commandString.getBytes());
        final ResponseParser parser = new ResponseParser(in, registry);
        final Response response = parser.readNextResponse();

        assertEquals(null, response.getLabel());

        assertEquals(true, response instanceof UnsolicitedResponse);
        final UnsolicitedResponse unsol = (UnsolicitedResponse)response;

        assertEquals("type.of.message", unsol.getType());
        assertEquals("value", unsol.getValue());
        assertEquals(commandString, unsol.toStatement(registry));
        assertEquals("type.of.message(value)", unsol.toString());
    }
}
