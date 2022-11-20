package agora.grammar;

import agora.errors.AgoraError;
import agora.errors.AgoraHalt;
import agora.objects.AgoraObject;
import agora.reflection.Reified;
import agora.reflection.Unary;
import agora.runtime.Context;

/**
 * Parse tree node type for string literals.
 *
 * @author Wolfgang De Meuter
 * Last change:  E    16 Nov 97    2:16 pm
 */
public class StringLiteral extends Literal<String> {
    /**
     * To construct a new node.
     *
     * @param str The string represented by this literal.
     */
    public StringLiteral(String str) {
        super(str);
    }

    /**
     * Implements the HALT reifier. The receiver must evaluate to a string object.
     *
     * @param context The evaluation context while HALT was sent.
     * @return An agora object indicating the result (is never returned because an error is thrown)
     * @throws agora.errors.AgoraError The exception that will terminate the program.
     */
    @Unary(value = "HALT")
    @Reified
    public AgoraObject halt(Context context) throws AgoraError {
        throw new AgoraHalt(eval(context).down());
    }
}
