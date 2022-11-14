package agora.grammar;

import agora.errors.AgoraError;
import agora.errors.ProgramError;
import agora.objects.AgoraObject;
import agora.patterns.AbstractPattern;
import agora.patterns.OperatorPattern;
import agora.runtime.Client;
import agora.runtime.Context;
import agora.tools.AwtIo;

/**
 * Reifier Operator Patterns are currently not used in Agora, because there is no
 * capitalised analogue of things like +. When we have a decent programming environment,
 * reifiers are denoted boldfaced instead of capitalised. Then this code will make sense
 * again. THIS CODE HAS NOT BEEN DEBUGGED!
 * Last change:  E    16 Nov 97    2:16 pm
 */
public class ReifOperatorPattern extends ReifPattern {
    protected String operator;

    protected Expression operand;

    public ReifOperatorPattern(String operator, Expression arg) {
        super();
        this.operator = operator;
        this.operand = arg;
    }

    public String getOperator() {
        return this.operator;
    }

    public Expression getOperand() {
        return this.operand;
    }

    public String unparse(int hor) {
        return AwtIo.makeSpaces(hor) + this.operator + " " + this.operand.unparse(0);
    }

    public Client makeClient(Context context, AgoraObject receiver) {
        var actuals = new Object[1];
        actuals[0] = this.operand;
        return context.newReifierClient(actuals);
    }

    public AbstractPattern makePattern(Context context) {
        var pattern = new OperatorPattern(this.operator);
        pattern.setReifier();
        return pattern;
    }

    /**
     * @throws agora.errors.AgoraError When something goes wrong (e.g. the pattern
     *                                 is not a formal pattern, but an actual pattern).
     */
    public String[] makeFormals(Context context) throws AgoraError {
        if (this.operand instanceof UserUnaryPattern u) return new String[]{u.getUnary()};
        var ex = new ProgramError("Formal parameters must be identifiers");
        ex.setCode(this);
        throw ex;
    }
}
