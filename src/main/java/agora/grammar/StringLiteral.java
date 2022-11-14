package agora.grammar;

import agora.errors.AgoraError;
import agora.errors.AgoraHalt;
import agora.objects.AgoraObject;
import agora.reflection.Reified;
import agora.reflection.Unary;
import agora.reflection.Up;
import agora.runtime.Context;

/**
 * Parse tree node type for string literals.
 *
 * @author Wolfgang De Meuter
 * Last change:  E    16 Nov 97    2:16 pm
 */
public class StringLiteral extends Literal {
    /**
     * The String value as determined by the parser.
     */
    protected String theString;

    /**
     * To construct a new node.
     *
     * @param str The string represented by this literal.
     */
    public StringLiteral(String str) {
        this.theString = str;
    }

    /**
     * Unparse the String. The number denotes the number of blanks preceding the string.
     *
     * @param hor The number of spaces leading the unparsed representation of the literal.
     * @return The String representation of this literal node.
     */
    public String unparse(int hor) {
        return theString;
    }

    /**
     * Implements the HALT reifier. The receiver must evaluate to a string object.
     *
     * @param context The evaluation context while HALT was sent.
     * @return An agora object indicating the result (is never returned because an error is thrown)
     * @throws agora.errors.AgoraError The exception that will terminate the program.
     */
    @Unary(value = "halt")
    @Reified
    public AgoraObject halt(Context context) throws AgoraError {
        throw new AgoraHalt((String) eval(context).down());
    }


    /**
     * Evaluates the string node to an up-ped instance of java.lang.String.
     *
     * @param context The environment in which evaluation should occur.
     * @return The Agora object represented by this string literal.
     * @throws agora.errors.AgoraError When something goes wrong during evaluation.
     */
    public AgoraObject eval(Context context) throws AgoraError {
        return Up.glob.up(this.theString);
    }

    /**
     * Reads the string value denoted by the node.
     */
    public String getString() {
        return theString;
    }
}
