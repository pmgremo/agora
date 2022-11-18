package agora.grammar;

import agora.errors.AgoraError;
import agora.errors.ProgramError;
import agora.objects.AgoraObject;
import agora.objects.FormalsAndPattern;
import agora.patterns.KeywordPattern;
import agora.reflection.Reified;
import agora.reflection.Unary;
import agora.runtime.Category;
import agora.runtime.Context;
import agora.tools.AgoraGlobals;

/**
 * This abstract class represents agora.patterns for ordinary messages. It has subclasses
 * for unary, operator and keyword agora.patterns.
 *
 * @author Wolfgang De Meuter (Programming Technology Lab)
 * Last change:  E    16 Nov 97    2:04 pm
 */
abstract public class UserPattern extends Pattern {
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
                return AgoraGlobals.glob.up.up(new FormalsAndPattern(makeFormals(context),
                        makePattern(context),
                        Category.emptyCategory));

            // Patterns in Other Categories Denote Accesses in the Local Part of An Object
            var client = makeClient(context, context.getSelf().wrap());
            client.actualsEval(context);
            return context.getPrivate().delegate(makePattern(context), client, context);
        } catch (AgoraError ex) {
            ex.setCode(this);
            throw ex;
        }
    }

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
        var agoraError = KeywordPattern.keywordPattern("agoraError:");
        var pattern = makePattern(context);
        var client = makeClient(context, null);
        client.actualsEval(context);
        if (agoraError.equals(pattern)) {
            var actuals = client.getActuals();
            if (((AgoraObject) actuals[0]).down() instanceof AgoraError e) throw e;
            throw new ProgramError("agoraError: is a reserved exception pattern. Its argument must be an exception.");
        }
        var exception = context.getException();
        exception.setPattern(pattern);
        exception.setClient(client);
        throw exception;
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
        var client = makeClient(context, context.getSelf().wrap());
        var pattern = makePattern(context);
        client.actualsEval(context);
        return context.getParent().delegate(pattern, client, context);
    }
}

