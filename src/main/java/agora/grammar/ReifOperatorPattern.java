package agora.grammar;

import agora.errors.AgoraError;
import agora.errors.ProgramError;
import agora.objects.AgoraObject;
import agora.patterns.OperatorReifierPattern;
import agora.runtime.Client;
import agora.runtime.Context;

/**
 * Reifier Operator Patterns are currently not used in Agora, because there is no
 * capitalised analogue of things like +. When we have a decent programming environment,
 * reifiers are denoted boldfaced instead of capitalised. Then this code will make sense
 * again. THIS CODE HAS NOT BEEN DEBUGGED!
 * Last change:  E    16 Nov 97    2:16 pm
 */
public class ReifOperatorPattern extends ReifPattern {
    protected OperatorReifierPattern operator;

    protected Expression operand;

    public ReifOperatorPattern(OperatorReifierPattern operator, Expression arg) {
        super();
        this.operator = operator;
        this.operand = arg;
    }

    public String getOperator() {
        return operator.operator();
    }

    public Expression getOperand() {
        return operand;
    }

    public String unparse(int hor) {
        return " ".repeat(hor) + operator.operator() + " " + operand.unparse(0);
    }

    public Client makeClient(Context context, AgoraObject receiver) {
        return context.newReifierClient(operand);
    }

    public OperatorReifierPattern makePattern(Context context) {
        return operator;
    }

    /**
     * @throws agora.errors.AgoraError When something goes wrong (e.g. the pattern
     *                                 is not a formal pattern, but an actual pattern).
     */
    public String[] makeFormals(Context context) throws AgoraError {
        if (operand instanceof UserUnaryPattern u) return new String[]{u.getUnary()};
        var ex = new ProgramError("Formal parameters must be identifiers");
        ex.setCode(this);
        throw ex;
    }
}
