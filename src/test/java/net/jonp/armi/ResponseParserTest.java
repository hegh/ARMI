package net.jonp.armi;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.NotBoundException;

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
     * {@link net.jonp.armi.ResponseParser#readNextResponse()}.
     * 
     * @throws IOException If there is a was a problem building the response
     *             parser.
     * @throws CommandException If there was a problem parsing the response.
     * @throws NotBoundException If there was a problem with the class registry.
     */
    @Test
    public void testReadNextValue()
        throws IOException, CommandException, NotBoundException
    {
        flag = 0;

        final DefaultClassRegistry registry = new DefaultClassRegistry();
        registry.put("TestObject", TestClass2.class);

        final String commandString =
            "response label \"label\" (TestObject (field1 = \"val1\", field2 = 12, field3 = 13.45, field4 = true))";
        final InputStream in = new ByteArrayInputStream(commandString.getBytes());

        final ResponseParser parser = new ResponseParser(in, registry);

        final Response response = parser.readNextResponse();


        assertEquals("label", response.getLabel());

        assertEquals(true, response instanceof Value);
        final Value value = (Value)response;

        assertEquals(getTestObject(), value.getValue());
        assertEquals(commandString, value.toStatement(registry));
        assertEquals("TestClass(val1, 12, 13.450000, true)", value.toString());

        // Make sure init() was called during deserialization
        assertEquals(flag, 1);
    }

    /**
     * Test method for
     * {@link net.jonp.armi.ResponseParser#readNextResponse()}.
     * 
     * @throws IOException If there is a was a problem building the response
     *             parser.
     * @throws CommandException If there was a problem parsing the response.
     * @throws NotBoundException If there was a problem with the class registry.
     */
    @Test
    public void testReadNextError()
        throws IOException, CommandException, NotBoundException
    {
        final DefaultClassRegistry registry = new DefaultClassRegistry();
        registry.put("TestObject", TestClass2.class);

        final String commandString = "error java.lang.Exception (\"Error message\")";
        final InputStream in = new ByteArrayInputStream(commandString.getBytes());

        final ResponseParser parser = new ResponseParser(in, registry);

        final Response response = parser.readNextResponse();


        assertEquals(null, response.getLabel());

        assertEquals(true, response instanceof Error);
        final Error error = (Error)response;

        assertEquals("java.lang.Exception", error.getException());
        assertEquals(commandString, error.toStatement());
        assertEquals("java.lang.Exception[Error message]", error.toString());
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
