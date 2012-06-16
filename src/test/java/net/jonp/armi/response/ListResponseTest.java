package net.jonp.armi.response;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.rmi.NotBoundException;

import net.jonp.armi.DefaultClassRegistry;

import org.junit.Test;

/**
 * Test the {@link ListResponse} object.
 */
public class ListResponseTest
{
    /**
     * Test method for
     * {@link net.jonp.armi.response.ListResponse#toStatement(net.jonp.ms5.command.ClassRegistry)}
     * .
     * 
     * @throws NotBoundException Should not happen.
     */
    @Test
    public void testToStatement()
        throws NotBoundException
    {
        final DefaultClassRegistry registry = new DefaultClassRegistry();

        final String expected = "list label \"label\" (\"object1\", \"object2\")";
        final ListResponse list = getTestList();

        assertEquals(expected, list.toStatement(registry));
    }

    /**
     * Test method for
     * {@link net.jonp.armi.response.ListResponse#getValues()}.
     */
    @Test
    public void testGetValues()
    {
        final String[] expected = new String[] {
            "object1", "object2"
        };
        final ListResponse list = getTestList();

        assertArrayEquals(expected, list.getValues());
    }

    /**
     * Test method for
     * {@link net.jonp.armi.AbstractLanguageObject#getLabel()}.
     */
    @Test
    public void testGetLabel()
    {
        final String expected = "label";
        final ListResponse list = getTestList();

        assertEquals(expected, list.getLabel());
    }

    private ListResponse getTestList()
    {
        return new ListResponse("label", new String[] {
            "object1", "object2"
        });
    }
}
