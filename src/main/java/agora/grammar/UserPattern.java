package agora.grammar;

import agora.errors.AgoraError;
import agora.errors.ProgramError;
import agora.objects.AgoraObject;
import agora.objects.FormalsAndPattern;
import agora.objects.PrimGenerator;
import agora.patterns.KeywordPattern;
import agora.reflection.*;
import agora.runtime.Category;
import agora.runtime.Context;

import java.io.Serializable;

/**
 * This abstract class represents agora.patterns for ordinary messages. It has subclasses
 * for unary, operator and keyword agora.patterns.
 *
 * @author Wolfgang De Meuter (Programming Technology Lab)
 * Last change:  E    16 Nov 97    2:04 pm
 */
abstract public class UserPattern extends Pattern implements Serializable {
    /**
     * Implements the RAISE reifier. Raises an AgoraException with the receiving pattern as content.
     *
     * @param context The context of evaluation when RAISE was sent.
     * @return The return value of the RAISE expression. This will never be returned since
     * an exception is thrown.
     * @throws agora.errors.AgoraError The exception raised.
     */
    @Unary(value = "RAISE")
    @Reified
    public AgoraObject raise(Context context) throws AgoraError {
        var agoError = new KeywordPattern();
        agoError.atPut(0, "agoraError:");
        var runtimePat = this.makePattern(context);
        var theClient = this.makeClient(context, null);
        theClient.actualsEval(context);
        if (agoError.equals(runtimePat)) {
            var actuals = theClient.getActuals();
            if (((AgoraObject) actuals[0]).down() instanceof AgoraError) {
                throw (AgoraError) ((AgoraObject) actuals[0]).down();
            } else
                throw new ProgramError("agoraError: is a reserved exception pattern. Its argument must be an exception.");
        } else {
            var theException = context.getException();
            theException.setPattern(runtimePat);
            theException.setClient(theClient);
            throw theException;
        }
    }

    /**
     * Method to evaluate a user pattern.
     *
     * @param context The environment in which the user patter is evaluated. If this
     *                contains the 'flags' category, a new formal pattern is returned. Otherwise,
     *                the pattern is delegated to the private part of the context.
     * @return The Agora Object associated with this pattern expression.
     * @throws agora.errors.AgoraError When something goes wrong during evaluation.
     */
    public AgoraObject eval(Context context) throws AgoraError {
        try {
            // Patterns in Flags Category Must Be Declared: Just Return a new pattern
            if (Category.contains(context.getCategory(), Category.flags))
                return Up.glob.up(new FormalsAndPattern(this.makeFormals(context),
                        this.makePattern(context),
                        Category.emptyCategory));
                // Patterns in Other Categories Denote Accesses in the Local Part of An Object
            else {
                var client = this.makeClient(context, context.getSelf().wrap());
                client.actualsEval(context);
                return context.getPrivate().delegate(this.makePattern(context), client, context);
            }
        } catch (AgoraError ex) {
            ex.setCode(this);
            throw ex;
        }
    }

    /**
     * Implementation of the SUPER reifier defined on user agora.patterns.
     *
     * @param context The evaluation context where SUPER was sent.
     * @return The result of sending the SUPER reifier.
     * @throws agora.errors.AgoraError When something went wrong (e.g. pattern is not in the super)
     */
    @Unary(value = "SUPER")
    @Reified
    public AgoraObject superReifier(Context context) throws AgoraError {
        var client = this.makeClient(context, context.getSelf().wrap());
        var pattern = this.makePattern(context);
        client.actualsEval(context);
        return context.getParent().delegate(pattern, client, context);
    }
}

