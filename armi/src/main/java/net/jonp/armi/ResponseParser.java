package net.jonp.armi;

import java.io.IOException;
import java.io.InputStream;

import net.jonp.armi.response.ErrorResponse;
import net.jonp.armi.response.Response;
import net.jonp.armi.response.ValueResponse;

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
        ARMIParser.response_return r;
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
            default:
                throw new SyntaxException("Root of response is not RESPONSE or ERROR: " + ast.getType());
        }
    }

    /**
     * Parse the tree from a Value response.
     * 
     * @param ast The tree.
     * @return The Value object.
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
     * @return The Error object.
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
}
