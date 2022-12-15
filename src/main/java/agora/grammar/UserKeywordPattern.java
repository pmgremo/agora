package agora.grammar;

import agora.errors.AgoraError;
import agora.errors.ProgramError;
import agora.objects.AgoraObject;
import agora.patterns.KeywordPattern;
import agora.runtime.Client;
import agora.runtime.Context;

import java.util.List;
import java.util.Objects;

/**
 * This class represents user keyword patterns like at:3 put:5 or at:i put:thing.
 *
 * @author Wolfgang De Meuter (Programming Technology Lab).
 * Last change:  E    16 Nov 97    1:43 am
 */
public class UserKeywordPattern extends UserPattern {
    private final KeywordPattern pattern;
    private final List<Expression> arguments;

    public UserKeywordPattern(List<String> keys, List<Expression> values){
        pattern = new KeywordPattern(keys);
        arguments = values;
    }

    /**
     * Unparses the keyword pattern node.
     *
     * @param hor The number of blanks leading the unparsed variant.
     * @return The stringrepresentation of the node.
     */
    public String unparse(int hor) {
        var msg = new StringBuilder(" ".repeat(hor));
        for (var i = 0; i < arguments.size(); i++) {
            msg
                    .append(pattern.keywords().get(i))
                    .append(arguments.get(i).unparse(0));
            if (i < arguments.size() - 1)
                msg.append(" ");
        }
        return msg.toString();
    }

    /**
     * Make a new client of actual arguments that go with this pattern.
     *
     * @param context  The context from which the client is to be obtained.
     * @param receiver The already-evaluated (or upped) receiver that goes with
     *                 this pattern expression.
     * @return A client containing the (non-evaluated) arguments.
     */
    public Client makeClient(Context context, AgoraObject receiver) {
        return context.newClient(arguments.toArray(Object[]::new));
    }

    /**
     * Creates a new runtime pattern associated to this syntactic pattern.
     *
     * @param context The context of evaluation when the runtime variant of this
     *                syntactic pattern is needed.
     * @return A runtime representation of this syntactic pattern.
     */
    public KeywordPattern makePattern(Context context) {
        return pattern;
    }

    /**
     * Create a list of formal arguments that go with the keyword pattern (if it is a formal pattern,
     * otherwise an error is thrown.
     *
     * @param context The context in which this pattern is needed as a formal pattern (usually the
     *                evaluation context during evaluating reifiers like VARIABLE or METHOD:.
     * @return An array of strings indicating the formal names that belong to this pattern.
     * @throws agora.errors.AgoraError When something goes wrong while constructing the
     *                                 formals (e.g. when it is not a formal pattern).
     */
    public String[] makeFormals(Context context) throws AgoraError {
        var formals = new String[arguments.size()];
        for (var i = 0; i < arguments.size(); i++) {
            if (!(arguments.get(i) instanceof UserUnaryPattern u)) {
                var ex = new ProgramError("Formal parameters must be identifiers");
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
        if (!(o instanceof UserKeywordPattern that)) return false;
        return Objects.equals(pattern, that.pattern) && Objects.equals(arguments, that.arguments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pattern, arguments);
    }
}
