package agora.grammar;

import agora.errors.AgoraError;
import agora.objects.AgoraObject;
import agora.patterns.OperatorPattern;
import agora.patterns.Pattern;
import agora.runtime.Context;
import agora.tools.AgoraGlobals;

/**
 * This class represents the parsetree node for aggregates of Agora expressions.
 * These can be [] and {} expressions.
 *
 * @author Wolfgang De Meuter
 * Last change:  E    16 Nov 97    2:25 pm
 */
public class Aggregate extends Expression {
    /**
     * Represents the left delimitting character of the aggregate (usually [ or {)
     */
    protected char leftDel;

    /**
     * Represents the right delimitting character of the aggregate (usually ] or })
     */
    protected char rightDel;

    /**
     * The array of expressions the aggregate contains
     */
    protected Expression[] expressions;

    /**
     * Constructor takes the number of expressions the aggregate contains, and the left and
     * right delimitting characters
     *
     * @param sz    The initial number of expressions in the aggregate.
     * @param left  The left delimiter of the aggregate.
     * @param right The right delimiter of the aggregate.
     */
    public Aggregate(int sz, char left, char right) {
        expressions = new Expression[sz];
        leftDel = left;
        rightDel = right;
    }

    /**
     * To access the expressions of the aggregate.
     *
     * @param index The position at which the aggregate needs to be indexed.
     * @return The expression residing at the specified position in the aggregate.
     */
    public Expression at(int index) {
        return expressions[index];
    }

    /**
     * To access the expressions of the aggregate.
     *
     * @param index      The position at which the aggregate must be updated.
     * @param expression The new expression to be installed at the given position.
     */
    public void atPut(int index, Expression expression) {
        expressions[index] = expression;
    }

    /**
     * Evaluates the aggregate expression in the given context.
     *
     * @param context The context in which the expression must be evaluated.
     * @return The AgoraObject corresponding to the expression.
     * @throws agora.errors.AgoraError When something goes wrong during evaluation.
     */
    public AgoraObject eval(Context context) throws AgoraError {
        if (leftDel == '[') {
            var exnihiloSelf = AgoraGlobals.glob.rootIdentity.funcAddLayer("public of ex nihilo");
            var exnihiloPriv = context.getPrivate().funcAddLayer("private of ex nihilo");
            exnihiloSelf.getMe().setPrivate(exnihiloPriv);
            exnihiloPriv.setPrivate(exnihiloPriv);
            var exnihiloCont = context.setMultiple(
                    exnihiloSelf,
                    exnihiloPriv,
                    exnihiloSelf.getMe(),
                    context.getCategory(),
                    AgoraGlobals.glob.rootIdentity
            );
            for (var expression : expressions) expression.eval(exnihiloCont);
            return exnihiloSelf.wrap();
        }
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
        var pat = new OperatorPattern(leftDel == '[' ? "[]" : "{}");
        pat.setReifier();
        return pat;
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
                .append(leftDel);
        if (expressions.length != 0) {
            for (var i = 0; i < expressions.length; i++) {
                result.append(expressions[i].unparse(i == 0 ? hor : hor + 2));
                if (i < expressions.length - 1) result.append(";\n");
            }
            result.append(padding);
        }
        result.append(rightDel);
        return result.toString();
    }
}
