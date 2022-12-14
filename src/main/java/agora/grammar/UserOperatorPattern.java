package agora.grammar;

import agora.errors.AgoraError;
import agora.errors.ProgramError;
import agora.objects.AgoraObject;
import agora.patterns.OperatorPattern;
import agora.runtime.Client;
import agora.runtime.Context;

import java.util.Objects;

/**
 * This class represents user operator patterns like + 4 or +arg.
 *
 * @author Wolfgang De Meuter (Programming technology Lab).
 * Last change:  E    16 Nov 97    1:42 am
 */
public class UserOperatorPattern extends UserPattern {
    protected OperatorPattern operator;

    protected Expression operand;

    /**
     * Creates a new user operator pattern.
     *
     * @param operator A string indicating the operator pattern (e.g. "+").
     * @param argument The expression denoting the argument of the operator pattern.
     */
    public UserOperatorPattern(OperatorPattern operator, Expression argument) {
        this.operator = operator;
        this.operand = argument;
    }

    /**
     * To retrieve the string representing the operator itself.
     *
     * @return The internal string representation of the operator pattern.
     */
    public String getOperator() {
        return operator.operator();
    }

    /**
     * Returns the internal representation of the argument expression of the operator pattern.
     *
     * @return The expression being the argument of the operator pattern.
     */
    public Expression getOperand() {
        return operand;
    }

    /**
     * Unparses the operator pattern.
     *
     * @param hor The number of spaces leading the unparsed representation.
     * @return A string that is the unparsed version of this expression.
     */
    public String unparse(int hor) {
        return " ".repeat(hor) + operator.operator() + " " + operand.unparse(0);
    }

    /**
     * Constructs a client that is needed to send this operator pattern to an object
     *
     * @param context  The context from which the new client is to be obtained.
     * @param receiver The already evaluated (or upped) receiver of this pattern.
     * @return A new Client containing the (non-evaluated) actuals of this pattern.
     */
    public Client makeClient(Context context, AgoraObject receiver) {
        return context.newClient(operand);
    }

    /**
     * Create a new runtime version of this syntactic pattern.
     *
     * @param context The context in which this pattern is to be sent (usually the context
     *                of a message expression).
     * @return The runtime variant of this syntactic pattern.
     */
    public OperatorPattern makePattern(Context context) {
        return operator;
    }

    /**
     * Create a list of formal arguments that go with this syntactic pattern (if the pattern is
     * a formal pattern, otherwise an error is thrown.).
     *
     * @param context The evaluation context in which the formals are needed.
     * @return An array of strings (1 in this case because it is an operator) that are
     * the formal arguments of this pattern.
     * @throws agora.errors.AgoraError When something goes wrong when constructing the
     *                                 formals (e.g. when it is an actual pattern).
     */
    public String[] makeFormals(Context context) throws AgoraError {
        if (operand instanceof UserUnaryPattern u) return new String[]{u.getUnary()};
        var ex = new ProgramError("Formal parameters must be identifiers");
        ex.setCode(this);
        throw ex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserOperatorPattern that)) return false;
        return Objects.equals(operator, that.operator) && Objects.equals(operand, that.operand);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operator, operand);
    }
}
