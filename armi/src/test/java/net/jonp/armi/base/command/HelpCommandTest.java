package net.jonp.armi.base.command;

import static org.junit.Assert.assertEquals;
import net.jonp.armi.base.ClassRegistry;
import net.jonp.armi.base.command.HelpCommand;
import net.jonp.armi.comm.DefaultClassRegistry;

import org.junit.Test;

/**
 * Test the {@link HelpCommand} object.
 */
public class HelpCommandTest
{
    /**
     * Test method for
     * {@link net.jonp.armi.base.command.HelpCommand#toStatement(net.jonp.ms5.command.ClassRegistry)}
     * .
     */
    @Test
    public void testToStatement()
    {
        final ClassRegistry registry = new DefaultClassRegistry();

        final String expected = "help";
        final HelpCommand help = getTestHelp();

        assertEquals(expected, help.toStatement(registry));
    }

    /**
     * Test method for
     * {@link net.jonp.armi.base.AbstractLanguageObject#getLabel()}.
     */
    @Test
    public void testGetLabel()
    {
        final String expected = null;
        final HelpCommand help = getTestHelp();

        assertEquals(expected, help.getLabel());
    }

    private HelpCommand getTestHelp()
    {
        return new HelpCommand();
    }
}
