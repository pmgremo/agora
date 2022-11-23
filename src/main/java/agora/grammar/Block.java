package agora.grammar;

import agora.errors.AgoraError;
import agora.objects.AgoraObject;
import agora.patterns.OperatorReifierPattern;
import agora.patterns.Pattern;
import agora.runtime.Context;
import agora.tools.AgoraGlobals;

import java.util.List;

public class Block extends Expression {
    /**
     * The array of expressions the aggregate contains
     */
    protected List<Expression> expressions;

    /**
     * Constructor takes the number of expressions the aggregate contains, and the left and
     * right delimiting characters
     *
     * @param expressions that make up the aggregate
     */
    public Block(List<Expression> expressions) {
        this.expressions = expressions;
    }

    /**
     * To access the expressions of the aggregate.
     *
     * @param index The position at which the aggregate needs to be indexed.
     * @return The expression residing at the specified position in the aggregate.
     */
    public Expression at(int index) {
        return expressions.get(index);
    }

    /**
     * Evaluates the aggregate expression in the given context.
     *
     * @param context The context in which the expression must be evaluated.
     * @return The AgoraObject corresponding to the expression.
     * @throws agora.errors.AgoraError When something goes wrong during evaluation.
     */
    public AgoraObject eval(Context context) throws AgoraError {
        var result = AgoraGlobals.glob.up.up(null);
        for (var expression : expressions) result = expression.eval(context);
        return result;
    }

    /**
     * Makes a pattern object to send the [] or {} messages. Now,
     * aggregates are evaluated immediately, for efficiency. In previous releases,
     * [] or {} was actually sent to this expression. This method makes the operator
     * pattern [] or {} to do this send.
     *
     * @param context The evaluation context when sending the [] or {} message.
     * @return The operator pattern corresponding to this [] or {} expression.
     */
    public Pattern makePattern(Context context) {
        return new OperatorReifierPattern("{}");
    }

    /**
     * Unparses the aggregate to a string. The input parameter is the number of spaces
     * that lead the resulting unparsed text.
     *
     * @param hor The number of spaces leading the unparsed aggregate.
     * @return The string representation of the aggregate.
     */
    public String unparse(int hor) {
        var padding = " ".repeat(hor);
        var result = new StringBuilder(padding)
                .append("{");
        if (!expressions.isEmpty()) {
            for (int i = 0; i < expressions.size(); i++) {
                Expression expression = expressions.get(i);
                result.append(expression.unparse(i == 0 ? hor : hor + 2));
                if (i < expressions.size() - 1) result.append(";\n");
            }
            result.append(padding);
        }
        result.append("}");
        return result.toString();
    }
}
