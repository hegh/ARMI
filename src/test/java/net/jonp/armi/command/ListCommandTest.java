package net.jonp.armi.command;

import static org.junit.Assert.assertEquals;
import net.jonp.armi.ClassRegistry;
import net.jonp.armi.DefaultClassRegistry;

import org.junit.Test;

/**
 * Test the {@link ListCommand} object.
 */
public class ListCommandTest
{
    /**
     * Test method for
     * {@link net.jonp.armi.command.ListCommand#toStatement(net.jonp.ms5.command.ClassRegistry)}
     * .
     */
    @Test
    public void testToStatement1()
    {
        final ClassRegistry registry = new DefaultClassRegistry();

        final String expected = "list label \"label\" objects";
        final ListCommand list = getTestListObjects();

        assertEquals(expected, list.toStatement(registry));
    }

    @Test
    public void testToStatement2()
    {
        final ClassRegistry registry = new DefaultClassRegistry();

        final String expected = "list label \"label\" methods object";
        final ListCommand list = getTestListMethods();

        assertEquals(expected, list.toStatement(registry));
    }

    /**
     * Test method for
     * {@link net.jonp.armi.command.ListCommand#getObject()}.
     */
    @Test
    public void testGetObject1()
    {
        final ClassRegistry registry = new DefaultClassRegistry();

        final String expected = null;
        final ListCommand list = getTestListObjects();

        assertEquals(expected, list.getObject());
    }

    @Test
    public void testGetObject2()
    {
        final ClassRegistry registry = new DefaultClassRegistry();

        final String expected = "object";
        final ListCommand list = getTestListMethods();

        assertEquals(expected, list.getObject());
    }

    /**
     * Test method for
     * {@link net.jonp.armi.AbstractLanguageObject#getLabel()}.
     */
    @Test
    public void testGetLabel()
    {
        final ClassRegistry registry = new DefaultClassRegistry();

        final String expected = "label";
        final ListCommand list = getTestListObjects();

        assertEquals(expected, list.getLabel());
    }

    private ListCommand getTestListObjects()
    {
        return new ListCommand("label", null);
    }

    private ListCommand getTestListMethods()
    {
        return new ListCommand("label", "object");
    }
}
