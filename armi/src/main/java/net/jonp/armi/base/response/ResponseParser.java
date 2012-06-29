package net.jonp.armi.base.response;

import java.io.IOException;
import java.io.InputStream;

import net.jonp.armi.ARMIParser;
import net.jonp.armi.base.AbstractParser;
import net.jonp.armi.base.ClassRegistry;
import net.jonp.armi.base.Conversion;
import net.jonp.armi.base.SyntaxException;

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
     * @return The Response object that was read, or <code>null</code> at EOF.
     * @throws IOException If there was a problem reading the response.
     * @throws SyntaxException If there was a problem parsing the response.
     */
    public Response readNextResponse()
        throws IOException, SyntaxException
    {
        parserSetup();

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
     * @return The Response object, or <code>null</code> at EOF.
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
            case ARMIParser.UNSOLICITED:
                return unsolicited(ast);
            default:
                if (ast.getType() == 0) {
                    return null;
                }
                else {
                    throw new SyntaxException("Root of response is not RESPONSE or ERROR: " + ast.getType());
                }
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

        if (null != value && !(value instanceof Throwable)) {
            throw new SyntaxException("Exception attached to error is not Throwable: " + value.getClass().getName());
        }

        return new ErrorResponse(label, (Throwable)value);
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
