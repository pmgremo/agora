package agora.grammar;

import agora.errors.AgoraError;
import agora.objects.AgoraObject;
import agora.patterns.UnaryPattern;
import agora.runtime.Client;
import agora.runtime.Context;

import java.util.Objects;

/**
 * A UserUnaryPattern is an Agora expression denoting ordinary unary agora.patterns.
 * Examples are 'abs' or other identifiers.
 *
 * @author Wolfgang De Meuter (Programming Technology Lab).
 * Last change:  E    16 Nov 97    1:42 am
 */
public class UserUnaryPattern extends UserPattern {
    protected UnaryPattern unary;

    /**
     * Creates a new user unary pattern.
     *
     * @param unary The string indicating the pattern.
     */
    public UserUnaryPattern(UnaryPattern unary) {
        super();
        this.unary = unary;
    }

    /**
     * Get the string representation of the unary pattern.
     *
     * @return The internal string representation of the user unary pattern,e.g. "abs"
     */
    public String getUnary() {
        return unary.pattern();
    }

    /**
     * Unparses the user unary pattern towards a string.
     *
     * @param hor The number of spaces that must lead the unparsed version.
     * @return The user unary pattern as a string.
     */
    public String unparse(int hor) {
        return " ".repeat(hor) + unary.pattern();
    }

    /**
     * Creates a new client object corresponding this pattern. This is a client object
     * with zero arguments (because this is a unary pattern).
     *
     * @param context  The evaluation context when the client is needed (right before send).
     * @param receiver The already-evaluated receiver of the unary pattern.
     * @return A new client object with zero unevaluated arguments.
     */
    public Client makeClient(Context context, AgoraObject receiver) {
        return context.newClient();
    }

    /**
     * Creates a runtime pattern corresponding to this syntactic pattern.
     *
     * @param context The evaluation context where the runtime pattern is needed (right
     *                before send or delegate.
     * @return A new runtime pattern representing this syntactic pattern.
     */
    public UnaryPattern makePattern(Context context) {
        return unary;
    }

    /**
     * Create an array of strings representing the formal arguments of the unary pattern.
     * Of course, this will be a string with zero arguments because it concerns a unary
     * pattern.
     *
     * @param context The evaluation context at the point where the formals are needed.
     * @return An array of length zero.
     * @throws agora.errors.AgoraError When something goes wrong (will never happen in this
     *                                 particular case, but it does in other versions of 'makeFormals').
     */
    public String[] makeFormals(Context context) throws AgoraError {
        return new String[0];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserUnaryPattern that)) return false;
        return Objects.equals(unary, that.unary);
    }

    @Override
    public int hashCode() {
        return Objects.hash(unary);
    }
}
