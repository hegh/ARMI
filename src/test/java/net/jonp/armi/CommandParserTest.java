package net.jonp.armi;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.NotBoundException;

import org.junit.Test;

/**
 * Tests {@link CommandParser}.
 */
public class CommandParserTest
{
    /**
     * Used to verify that {@link TestClass2#init()} is called during
     * deserialization.
     */
    public static int flag;

    /**
     * Test method for
     * {@link net.jonp.armi.CommandParser#readNextCommand()}.
     * 
     * @throws IOException If there was a problem building or using the parser.
     * @throws CommandException If there was a problem parsing a command.
     * @throws NotBoundException If there is a problem with the class registry.
     */
    @Test
    public void testReadNextCommand()
        throws IOException, CommandException, NotBoundException
    {
        flag = 0;

        final DefaultClassRegistry registry = new DefaultClassRegistry();
        registry.put("TestObject", TestClass2.class);

        final String commandString =
            "call label \"label\" object.method (TestObject (field1 = \"val1\", field2 = 12, field3 = 13.45, field4 = true))";
        final InputStream in = new ByteArrayInputStream(commandString.getBytes());

        final CommandParser parser = new CommandParser(in, registry);

        final Command command = parser.readNextCommand();

        assertEquals("label", command.getLabel());
        assertEquals("object", command.getObject());
        assertEquals("method", command.getMethod());
        assertEquals(1, command.getArguments().length);
        assertEquals(getTestObject(), command.getArguments()[0]);
        assertEquals(commandString, command.toStatement(registry));

        // Make sure init() was called during deserialization
        assertEquals(flag, 1);
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
            CommandParserTest.flag++;
        }
    }
}
