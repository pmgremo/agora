package agora.grammar;

import agora.errors.AgoraError;
import agora.errors.ProgramError;
import agora.objects.AgoraObject;
import agora.patterns.KeywordReifierPattern;
import agora.runtime.Client;
import agora.runtime.Context;

import java.util.List;
import java.util.Objects;

/**
 * This class represents reifier keyword agora.patterns like METHOD:{}
 *
 * @author Wolfgang De Meuter (Programming Technology Lab)
 * Last change:  E    16 Nov 97    1:38 am
 */
public class ReifKeywordPattern extends ReifPattern {
    private final KeywordReifierPattern pattern;
    private final List<Expression> arguments;

    public ReifKeywordPattern(KeywordReifierPattern pattern, List<Expression> arguments) {
        this.pattern = pattern;
        this.arguments = arguments;
    }

    /**
     * Unparses the keyword pattern.
     *
     * @param hor The number of spaces that must lead the unparsed version.
     * @return The string representation representing the unparsed keyword pattern.
     */
    public String unparse(int hor) {
        var msg = new StringBuilder(" ".repeat(hor));
        for (var i = 0; i < arguments.size(); i++) {
            msg.append(pattern.keywords().get(i)).append(arguments.get(i).unparse(0));
            if (i < arguments.size() - 1) msg.append(" ");
        }
        return msg.toString();
    }

    /**
     * Create a client object with the actual arguments of this keyword pattern.
     * The actuals are not evaluated by this method.
     *
     * @param context  The context from which the new client must be obtained.
     * @param receiver The already evaluated (or upped) receiver to which
     *                 the keyword pattern will be sent.
     * @return A client containing the actual arguments.
     */
    public Client makeClient(Context context, AgoraObject receiver) {
        return context.newReifierClient(arguments.toArray(Object[]::new));
    }

    /**
     * Creates a runtime pattern that goes with this syntactic pattern.
     *
     * @param context The context of the expression in which the runtime argument is needed.
     * @return A runtime pattern associated to this syntactic pattern.
     */
    public KeywordReifierPattern makePattern(Context context) {
        return pattern;
    }

    /**
     * Create a list of formal arguments that are in this keyword pattern (if the keyword
     * pattern is a pattern used for a declaration).
     *
     * @param context context in which the formals are needed (usually the context of a
     *                VARIABLE: or a METHOD: reifier).
     * @return An array of strings indicating the formal arguments of the keyword pattern.
     * @throws agora.errors.AgoraError If something goes wrong (e.g. it is not a valid formal
     *                                 pattern, but an actual pattern).
     */
    public String[] makeFormals(Context context) throws AgoraError {
        var formals = new String[arguments.size()];
        for (var i = 0; i < arguments.size(); i++) {
            if (!(arguments.get(i) instanceof UserUnaryPattern u)) {
                var ex = new ProgramError("Formal parameters must be  identifiers");
                ex.setCode(this);
                throw ex;
            }
            formals[i] = u.getUnary();
        }
        return formals;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReifKeywordPattern that)) return false;
        return Objects.equals(pattern, that.pattern) && Objects.equals(arguments, that.arguments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pattern, arguments);
    }
}
