package net.jonp.armi.base.command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
 * Tests {@link CommandParser}.
 */
public class CommandParserTest
    extends TestCase
{
    private final TestClass test;

    public CommandParserTest(final TestClass _test)
    {
        test = _test;
    }

    /**
     * Test method for
     * {@link net.jonp.armi.base.command.CommandParser#readNextCommand()}.
     * 
     * @throws IOException If there was a problem building or using the parser.
     * @throws SyntaxException If there was a problem parsing a command.
     * @throws NotBoundException If there is a problem with the class registry.
     */
    @Test
    public void testReadNextCallCommand1()
        throws IOException, SyntaxException, NotBoundException
    {
        // Make sure an object can be passed to a method, and that (if it is an
        // Initialized type) it got initialized on receipt
        final DefaultClassRegistry registry = new DefaultClassRegistry();
        registry.put(test.getName(), test.getClass());

        final String commandString = "call label \"label\" object.method (" + test.getCommand() + ")";
        final InputStream in = new ByteArrayInputStream(commandString.getBytes());
        final CommandParser parser = new CommandParser(in, registry);
        final Command command = parser.readNextCommand();

        assertTrue(command instanceof CallCommand);

        final CallCommand callCommand = (CallCommand)command;
        assertEquals("label", callCommand.getLabel());
        assertEquals("object", callCommand.getObject());
        assertEquals("method", callCommand.getMethod());
        assertEquals(1, callCommand.getArguments().length);
        assertEquals(test, callCommand.getArguments()[0]);
        assertEquals(commandString, callCommand.toStatement(registry));
        assertEquals(true, ((TestClass)callCommand.getArguments()[0]).isValid());
    }

    @Test
    public void testReadNextCallCommand2()
        throws IOException, SyntaxException, NotBoundException
    {
        // Make sure we can pass an empty set of parameters to a method

        final DefaultClassRegistry registry = new DefaultClassRegistry();
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
}
