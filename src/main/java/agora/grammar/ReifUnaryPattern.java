package agora.grammar;

import agora.errors.AgoraError;
import agora.objects.AgoraObject;
import agora.patterns.UnaryReifierPattern;
import agora.runtime.Client;
import agora.runtime.Context;

/**
 * This class represents reifier unary patterns like SELF.
 *
 * @author Wolfgang De Meuter (Programming Technology Lab).
 * Last change:  E    16 Nov 97    1:43 am
 */
public class ReifUnaryPattern extends ReifPattern {
    private final UnaryReifierPattern unary;

    /**
     * Create a new reifier unary pattern.
     *
     * @param unary The String representing the unary pattern (e.g. "SELF").
     */
    public ReifUnaryPattern(UnaryReifierPattern unary) {
        super();
        this.unary = unary;
    }

    /**
     * Get the internal string representation of the reifier unary pattern.
     *
     * @return The internal string representation.
     */
    public String getUnary() {
        return unary.pattern();
    }

    /**
     * Unparses the receiving expression.
     *
     * @param hor The number of spaces leading the unparsed version.
     * @return The string representation of this reifier unary patttern.
     */
    public String unparse(int hor) {
        return " ".repeat(hor) + unary.pattern();
    }

    /**
     * Creates a new client object that can be used to send the runtime variant of
     * this syntactic pattern. The client object will contain zero arguments because
     * it concerns a unary reifier.
     *
     * @param context  The context from which the client must be obtained.
     * @param receiver The already evaluated (or upped) receiver to which this
     *                 reifier message was sent.
     * @return A new Client object containing zero actual arguments.
     */
    public Client makeClient(Context context, AgoraObject receiver) {
        return context.newReifierClient();
    }

    /**
     * Construct a new runtime pattern associated to this syntactic pattern.
     *
     * @param context The evaluation context that is used to evaluate the message
     *                expression that uses this pattern.
     */
    public UnaryReifierPattern makePattern(Context context) {
        return unary;
    }

    /**
     * Create a list of formal names that go with this reifier unary pattern (of course
     * this will always return an array with zero formals since it is a unary pattern.
     *
     * @param context The evaluation context in which the formals are needed (this will usually
     *                be the evaluation context of a reifier like REIFIER:IS:.
     * @return An array of strings indicating the formal arguments of this pattern.
     * @throws agora.errors.AgoraError When something goes wrong (e.g. when it concerns an
     *                                 actual pattern instead of a formal pattern).
     */
    public String[] makeFormals(Context context) throws AgoraError {
        return new String[0];
    }
}
