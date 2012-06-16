package net.jonp.armi.command;

import static org.junit.Assert.assertEquals;
import net.jonp.armi.ClassRegistry;
import net.jonp.armi.DefaultClassRegistry;

import org.junit.Test;

/**
 * Test the {@link HelpCommand} object.
 */
public class HelpCommandTest
{
    /**
     * Test method for
     * {@link net.jonp.armi.command.HelpCommand#toStatement(net.jonp.ms5.command.ClassRegistry)}
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
     * {@link net.jonp.armi.AbstractLanguageObject#getLabel()}.
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
