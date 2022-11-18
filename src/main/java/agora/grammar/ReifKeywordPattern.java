package agora.grammar;

import agora.errors.AgoraError;
import agora.errors.ProgramError;
import agora.objects.AgoraObject;
import agora.patterns.KeywordReifierPattern;
import agora.patterns.Pattern;
import agora.runtime.Client;
import agora.runtime.Context;

import static agora.patterns.KeywordReifierPattern.keywordReifierPattern;

/**
 * This class represents reifier keyword agora.patterns like METHOD:{}
 *
 * @author Wolfgang De Meuter (Programming Technology Lab)
 * Last change:  E    16 Nov 97    1:38 am
 */
public class ReifKeywordPattern extends ReifPattern {
    protected String[] keywords;
    protected Expression[] arguments;
    protected int size;

    /**
     * Creates a new reifier keyword pattern.
     *
     * @param sz The number of keywords that will be contained.
     */
    public ReifKeywordPattern(int sz) {
        super();
        this.size = sz;
        this.keywords = new String[sz];
        this.arguments = new Expression[sz];
    }

    /**
     * Access a keyword in the pattern.
     *
     * @param index The position of the keyword to be read.
     * @return The keyword (e.g. at:) corresponding to the possition.
     */
    public String keywordAt(int index) {
        return keywords[index];
    }

    /**
     * Access an argument (that associates a keyword) in the pattern.
     *
     * @param index The position of the argument to be read.
     * @return The expression denoting the argument of the keyword at that position.
     */
    public Expression argAt(int index) {
        return arguments[index];
    }

    /**
     * Update the keyword pattern with a new keyword and associated argument.
     *
     * @param index The position to be updated.
     * @param keyw  The new keyword to be stored at that position.
     * @param arg   The new argument to be stored at that position.
     */
    public void atPut(int index, String keyw, Expression arg) {
        this.keywords[index] = keyw;
        this.arguments[index] = arg;
    }

    /**
     * Unparses the keyword pattern.
     *
     * @return The string representation representing the unparsed keyword pattern.
     * @param hor The number of spaces that must lead the unparsed version.
     */
    public String unparse(int hor) {
        var msg = new StringBuilder(" ".repeat(hor));
        for (var i = 0; i < this.size; i++) {
            msg.append(keywords[i]).append(arguments[i].unparse(0));
            if (i < size - 1) msg.append(" ");
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
        var actuals = new Object[size];
        System.arraycopy(arguments, 0, actuals, 0, size);
        return context.newReifierClient(actuals);
    }

    /**
     * Creates a runtime pattern that goes with this syntactic pattern.
     *
     * @param context The context of the expression in which the runtime argument is needed.
     * @return A runtime pattern asssociated to this syntactic pattern.
     */
    public Pattern makePattern(Context context) {
        return keywordReifierPattern(keywords);
    }

    /**
     * Create a list of formal arguments that are in this keyword pattern (if the keyword
     * pattern is a pattern used for a declaration.
     *
     * @param context context in which the formals are needed (usually the context of a
     *                VARIABLE: or a METHOD: reifier.
     * @return An array of strings indicating the formal arguments of the keyword pattern.
     * @throws agora.errors.AgoraError If something goes wrong (e.g. it is not a valid formal
     *                                 pattern, but an actual pattern).
     */
    public String[] makeFormals(Context context) throws AgoraError {
        var formals = new String[arguments.length];
        for (var i = 0; i < arguments.length; i++) {
            if (!(arguments[i] instanceof UserUnaryPattern u)) {
                var ex = new ProgramError("Formal parameters must be  identifiers");
                ex.setCode(this);
                throw ex;
            }
            formals[i] = u.getUnary();
        }
        return formals;
    }
}
