package net.jonp.armi.response;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.NotBoundException;

import net.jonp.armi.DefaultClassRegistry;
import net.jonp.armi.Initializable;
import net.jonp.armi.SyntaxException;
import net.jonp.armi.TestClass;

import org.junit.Test;

/**
 * Tests {@link ResponseParser}.
 */
public class ResponseParserTest
{
    /** Used to test {@link Initializable#init()}. */
    public static int flag = 0;

    /**
     * Test method for
     * {@link net.jonp.armi.response.ResponseParser#readNextResponse()}.
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
        flag = 0;

        final DefaultClassRegistry registry = new DefaultClassRegistry();
        registry.put("TestObject", TestClass2.class);

        final String commandString =
            "response label \"label\" (TestObject (field1 = \"val1\", field2 = 12, field3 = 13.45, field4 = true, field5 = array [\"string\", 1, 2.3, true]))";
        final InputStream in = new ByteArrayInputStream(commandString.getBytes());

        final ResponseParser parser = new ResponseParser(in, registry);

        final Response response = parser.readNextResponse();


        assertEquals("label", response.getLabel());

        assertEquals(true, response instanceof ValueResponse);
        final ValueResponse value = (ValueResponse)response;

        assertEquals(getTestObject(), value.getValue());
        assertEquals(commandString, value.toStatement(registry));
        assertEquals("TestClass(val1, 12, 13.450000, true, [string, 1, 2.3, true])", value.toString());

        // Make sure init() was called during deserialization
        assertEquals(flag, 1);
    }

    /**
     * Test method for
     * {@link net.jonp.armi.response.ResponseParser#readNextResponse()}.
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
        registry.put("TestObject", TestClass2.class);

        final String commandString = "error java.lang.Exception (\"Error message\")";
        final InputStream in = new ByteArrayInputStream(commandString.getBytes());

        final ResponseParser parser = new ResponseParser(in, registry);

        final Response response = parser.readNextResponse();


        assertEquals(null, response.getLabel());

        assertEquals(true, response instanceof ErrorResponse);
        final ErrorResponse error = (ErrorResponse)response;

        assertEquals("java.lang.Exception", error.getException());
        assertEquals(commandString, error.toStatement());
        assertEquals("java.lang.Exception[Error message]", error.toString());
    }

    /**
     * Test method for
     * {@link net.jonp.armi.response.ResponseParser#readNextResponse()}.
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

    private TestClass getTestObject()
    {
        final TestClass testObject = new TestClass();
        testObject.field1 = "val1";
        testObject.field2 = 12;
        testObject.field3 = 13.45;
        testObject.field4 = true;
        testObject.field5 = new Object[] {
            "string", 1L, 2.3, true
        };

        return testObject;
    }

    public static class TestClass2
        extends TestClass
        implements Initializable
    {
        public TestClass2()
        {
            // Nothing to do
        }

        /*
         * (non-Javadoc)
         * 
         * @see net.jonp.armi.Initializable#init()
         */
        @Override
        public void init()
        {
            ResponseParserTest.flag++;
        }
    }
}
