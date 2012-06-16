package net.jonp.armi.command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
     * {@link net.jonp.armi.command.CommandParser#readNextCommand()}.
     * 
     * @throws IOException If there was a problem building or using the parser.
     * @throws SyntaxException If there was a problem parsing a command.
     * @throws NotBoundException If there is a problem with the class registry.
     */
    @Test
    public void testReadNextCallCommand1()
        throws IOException, SyntaxException, NotBoundException
    {
        flag = 0;

        final DefaultClassRegistry registry = new DefaultClassRegistry();
        registry.put("TestObject", TestClass2.class);

        final String commandString = "call label \"label\" object.method (" + //
                                     "TestObject (field1 = \"val\\\\1\"," + //
                                     " field2 = 12," + //
                                     " field3 = 13.45," + //
                                     " field4 = true," + //
                                     " field5 = null," + //
                                     " field6 = array(java.lang.String) []," + //
                                     " field7 = array(java.lang.Integer) [5, 4, 3]))";
        final InputStream in = new ByteArrayInputStream(commandString.getBytes());

        final CommandParser parser = new CommandParser(in, registry);

        final Command command = parser.readNextCommand();

        assertTrue(command instanceof CallCommand);

        final CallCommand callCommand = (CallCommand)command;
        assertEquals("label", callCommand.getLabel());
        assertEquals("object", callCommand.getObject());
        assertEquals("method", callCommand.getMethod());
        assertEquals(1, callCommand.getArguments().length);
        assertEquals(getTestObject(), callCommand.getArguments()[0]);
        assertEquals(commandString, callCommand.toStatement(registry));

        // Make sure init() was called during deserialization
        assertEquals(flag, 1);
    }

    @Test
    public void testReadNextCallCommand2()
        throws IOException, SyntaxException, NotBoundException
    {
        // Make sure we can pass an empty set of fields to an object

        final DefaultClassRegistry registry = new DefaultClassRegistry();
        registry.put("BlankObject", BlankClass.class);

        final String commandString = "call label \"label\" object.method (BlankObject ())";
        final InputStream in = new ByteArrayInputStream(commandString.getBytes());

        final CommandParser parser = new CommandParser(in, registry);

        final Command command = parser.readNextCommand();

        assertTrue(command instanceof CallCommand);

        final CallCommand callCommand = (CallCommand)command;
        assertEquals("label", callCommand.getLabel());
        assertEquals("object", callCommand.getObject());
        assertEquals("method", callCommand.getMethod());
        assertEquals(1, callCommand.getArguments().length);
        assertEquals(new BlankClass(), callCommand.getArguments()[0]);
        assertEquals(commandString, callCommand.toStatement(registry));
    }

    @Test
    public void testReadNextCallCommand3()
        throws IOException, SyntaxException, NotBoundException
    {
        // Make sure we can pass an empty set of parameters to a method

        final DefaultClassRegistry registry = new DefaultClassRegistry();
        registry.put("TestObject", TestClass.class);

        final String commandString = "call label \"label\" object.method ()";
        final InputStream in = new ByteArrayInputStream(commandString.getBytes());

        final CommandParser parser = new CommandParser(in, registry);

        final Command command = parser.readNextCommand();

        assertTrue(command instanceof CallCommand);

        final CallCommand callCommand = (CallCommand)command;
        assertEquals("label", callCommand.getLabel());
        assertEquals("object", callCommand.getObject());
        assertEquals("method", callCommand.getMethod());
        assertEquals(0, callCommand.getArguments().length);
        assertEquals(commandString, callCommand.toStatement(registry));
    }

    @Test
    public void testReadNextCallCommand4()
        throws IOException, SyntaxException, NotBoundException
    {
        // Make sure we can pass an array to a method

        final DefaultClassRegistry registry = new DefaultClassRegistry();
        registry.put("TestObject", TestClass.class);

        final String commandString = "call label \"label\" object.method (" + //
                                     "array(java.lang.Object) [" + //
                                     "1," + //
                                     " \"string\"," + //
                                     " TestObject (" + //
                                     "field1 = \"val\\\\1\"," + //
                                     " field2 = 12," + //
                                     " field3 = 13.45," + //
                                     " field4 = true," + //
                                     " field5 = null," + //
                                     " field6 = array(java.lang.String) []," + //
                                     " field7 = array(java.lang.Integer) [5, 4, 3])])";
        final InputStream in = new ByteArrayInputStream(commandString.getBytes());

        final CommandParser parser = new CommandParser(in, registry);

        final Command command = parser.readNextCommand();

        assertTrue(command instanceof CallCommand);

        final CallCommand callCommand = (CallCommand)command;
        assertEquals("label", callCommand.getLabel());
        assertEquals("object", callCommand.getObject());
        assertEquals("method", callCommand.getMethod());
        assertEquals(1, callCommand.getArguments().length);

        final Object[] args = (Object[])callCommand.getArguments()[0];
        assertEquals(1L, args[0]);
        assertEquals("string", args[1]);
        assertEquals(getTestObject(), args[2]);
        assertEquals(commandString, callCommand.toStatement(registry));
    }

    @Test
    public void testReadNextHelpCommand()
        throws IOException, SyntaxException
    {
        final DefaultClassRegistry registry = new DefaultClassRegistry();

        final String commandString = "help";
        final InputStream in = new ByteArrayInputStream(commandString.getBytes());

        final CommandParser parser = new CommandParser(in, registry);

        final Command command = parser.readNextCommand();

        assertTrue(command instanceof HelpCommand);

        final HelpCommand helpCommand = (HelpCommand)command;
        assertEquals(null, helpCommand.getLabel());
        assertEquals(commandString, helpCommand.toStatement(registry));
    }

    @Test
    public void testReadNextTwoCommands()
        throws IOException, SyntaxException
    {
        final DefaultClassRegistry registry = new DefaultClassRegistry();

        final String commandString = "help\nhelp";
        final InputStream in = new ByteArrayInputStream(commandString.getBytes());

        final CommandParser parser = new CommandParser(in, registry);

        final Command command1 = parser.readNextCommand();
        final Command command2 = parser.readNextCommand();

        assertTrue(command1 instanceof HelpCommand);
        assertTrue(command2 instanceof HelpCommand);

        final HelpCommand helpCommand1 = (HelpCommand)command1;
        assertEquals(null, helpCommand1.getLabel());
        assertEquals("help", helpCommand1.toStatement());

        final HelpCommand helpCommand2 = (HelpCommand)command2;
        assertEquals(null, helpCommand2.getLabel());
        assertEquals("help", helpCommand2.toStatement());
    }

    private TestClass getTestObject()
    {
        final TestClass testObject = new TestClass();
        testObject.field1 = "val\\1";
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

    public static class BlankClass
    {
        public BlankClass()
        {
            // Nothing to do
        }

        @Override
        public int hashCode()
        {
            return super.hashCode();
        }

        @Override
        public boolean equals(final Object rhs)
        {
            return (null != rhs && rhs instanceof BlankClass);
        }
    }
}
