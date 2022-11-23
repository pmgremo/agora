package agora.errors;

import agora.grammar.Expression;
import agora.patterns.Pattern;
import agora.runtime.Client;

/**
 * Objects of this class can be thrown by the evaluator whenever an
 * Agora exception is thrown by the RAISE reifier. I.e., Agora
 * exception handling is implemented by the underlying Java
 * exception handling.
 * Have a look at the implementation of RAISE and TRY:CATCH: for
 * more details.
 *
 * @author Wolfgang De Meuter (Programming Technology Lab).
 * Last change:  E    16 Nov 97    1:36 am
 */
public class AgoraException extends AgoraError {
    /**
     * The expression to be evaluated when the exception is caught.
     */
    protected Expression toCatch;
    /**
     * The pattern that will be used to invoke the catch-code.
     */
    protected Pattern pattern;
    /**
     * The saved client object.
     */
    protected Client client;

    /**
     * To create a new Agora exception with the given expression.
     * This is the expression to be executed when the exception is thrown by RAISE.
     *
     * @param c The catch-code to be executed when the exception happens.
     */
    public AgoraException(Expression c) {
        super("Uncaught Agora Exception.\n\n");
        this.pattern = null;
        this.client = null;
        this.toCatch = c;
    }

    /**
     * Accessor to read the expression
     *
     * @return Get the catch-code from the exception.
     */
    public Expression getExpression() {
        return toCatch;
    }

    /**
     * Accessor to read the pattern of the exception.
     *
     * @return Gives the agora.runtime (i.e. actual) pattern that occurs in RAISE.
     */
    public Pattern getPattern() {
        return pattern;
    }

    /**
     * Accessor to access the client.
     *
     * @return Returns the client with the actual arguments as created by RAISE.
     */
    public Client getClient() {
        return client;
    }

    /**
     * To set the pattern.
     *
     * @param p The pattern to be filled in by raise.
     */
    public void setPattern(Pattern p) {
        pattern = p;
    }

    /**
     * To set the client.
     *
     * @param c The client with the actual arguments as determined by RAISE.
     */
    public void setClient(Client c) {
        client = c;
    }
}
