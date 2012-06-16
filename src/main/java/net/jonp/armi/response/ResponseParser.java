package net.jonp.armi.response;

import java.io.IOException;
import java.io.InputStream;

import net.jonp.armi.ARMIParser;
import net.jonp.armi.AbstractParser;
import net.jonp.armi.ClassRegistry;
import net.jonp.armi.Conversion;
import net.jonp.armi.SyntaxException;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTree;

/**
 * Parses command responses into {@link Response} objects.
 */
public class ResponseParser
    extends AbstractParser
{
    /**
     * Parses responses to commands.
     * 
     * @param in The stream from which to read the responses.
     * @param _registry The class registry for looking up instance classes from
     *            command language names.
     * @throws IOException If there was a problem integrating the stream into
     *             the response parsing framework.
     */
    public ResponseParser(final InputStream in, final ClassRegistry _registry)
        throws IOException
    {
        super(in, _registry);
    }

    /**
     * Read the next response.
     * 
     * @return The Response object that was read.
     * @throws SyntaxException If there was a problem parsing the response.
     */
    public Response readNextResponse()
        throws SyntaxException
    {
        final ARMIParser.response_return r;
        try {
            r = parser.response();
        }
        catch (final RecognitionException re) {
            throw new SyntaxException("Error parsing response: " + re.getMessage(), re);
        }

        final CommonTree ast = (CommonTree)r.getTree();
        return generalResponse(ast);
    }

    /**
     * Parse the tree from a top-level response.
     * 
     * @param ast The tree.
     * @return The Response object.
     * @throws SyntaxException If there was a problem parsing the tree.
     */
    private Response generalResponse(final CommonTree ast)
        throws SyntaxException
    {
        switch (ast.getType()) {
            case ARMIParser.RESPONSE:
                return response(ast);
            case ARMIParser.ERROR:
                return error(ast);
            case ARMIParser.LIST:
                return list(ast);
            case ARMIParser.UNSOLICITED:
                return unsolicited(ast);
            default:
                throw new SyntaxException("Root of response is not RESPONSE or ERROR: " + ast.getType());
        }
    }

    /**
     * Parse the tree from a Value response.
     * 
     * @param ast The tree.
     * @return The ValueResponse object.
     * @throws SyntaxException If there was a problem parsing the tree.
     */
    private ValueResponse response(final CommonTree ast)
        throws SyntaxException
    {
        if (ast.getType() != ARMIParser.RESPONSE) {
            throw new SyntaxException("Root of value response is not RESPONSE: " + ast.getType());
        }

        String label = null;
        Object value = null;

        for (final Object childAST : ast.getChildren()) {
            final CommonTree child = (CommonTree)childAST;
            switch (child.getType()) {
                case ARMIParser.LABEL:
                    label = label(child);
                    break;
                default:
                    value = val(child);
                    break;
            }
        }

        return new ValueResponse(label, value);
    }

    /**
     * Parse the tree from an Error response.
     * 
     * @param ast The tree.
     * @return The ErrorResponse object.
     * @throws SyntaxException If there was a problem parsing the tree.
     */
    private ErrorResponse error(final CommonTree ast)
        throws SyntaxException
    {
        if (ast.getType() != ARMIParser.ERROR) {
            throw new SyntaxException("Root of error response is not ERROR: " + ast.getType());
        }

        String label = null;
        String[] path = null;
        String message = null;

        for (final Object childAST : ast.getChildren()) {
            final CommonTree child = (CommonTree)childAST;
            switch (child.getType()) {
                case ARMIParser.LABEL:
                    label = label(child);
                    break;
                case ARMIParser.IDENT:
                    path = ident(child);
                    break;
                case ARMIParser.STR:
                    message = str(child);
                    break;
                default:
                    throw new SyntaxException("Illegal child of ERROR: " + child.getType());
            }
        }

        return new ErrorResponse(label, Conversion.arrayToString(path, "."), message);
    }

    /**
     * Parse the tree from a List response.
     * 
     * @param ast The tree.
     * @return The ListResponse object.
     * @throws SyntaxException If there was a problem parsing the tree.
     */
    private ListResponse list(final CommonTree ast)
        throws SyntaxException
    {
        if (ast.getType() != ARMIParser.LIST) {
            throw new SyntaxException("Root of list response is not LIST: " + ast.getType());
        }

        String label = null;
        String[] values = null;

        for (final Object childAST : ast.getChildren()) {
            final CommonTree child = (CommonTree)childAST;
            switch (child.getType()) {
                case ARMIParser.LABEL:
                    label = label(child);
                    break;
                case ARMIParser.STRINGS:
                    values = strings(child);
                    break;
                default:
                    throw new SyntaxException("Illegal child of LIST: " + child.getType());
            }
        }

        return new ListResponse(label, values);
    }

    /**
     * Parse the tree from an Unsolicited response.
     * 
     * @param ast The tree.
     * @return The UnsolicitedResponse object.
     * @throws SyntaxException If there was a problem parsing the tree.
     */
    private UnsolicitedResponse unsolicited(final CommonTree ast)
        throws SyntaxException
    {
        if (ast.getType() != ARMIParser.UNSOLICITED) {
            throw new SyntaxException("Root of unsolicited response is not UNSOLICITED: " + ast.getType());
        }

        String[] path = null;
        Object value = null;

        for (final Object childAST : ast.getChildren()) {
            final CommonTree child = (CommonTree)childAST;
            switch (child.getType()) {
                case ARMIParser.IDENT:
                    path = ident(child);
                    break;
                default:
                    value = val(child);
                    break;
            }
        }

        return new UnsolicitedResponse(Conversion.arrayToString(path, "."), value);
    }
}
