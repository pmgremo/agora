package agora.grammar;

import agora.errors.AgoraError;
import agora.objects.AgoraObject;
import agora.objects.FormalsAndPattern;
import agora.reflection.Reified;
import agora.reflection.Unary;
import agora.runtime.Category;
import agora.runtime.Context;
import agora.tools.AgoraGlobals;

abstract public class ReifPattern extends Pattern {
    /**
     * Method to evaluate a reifier pattern.
     *
     * @param context Environment in which the expression must be evaluated.
     * @return The Agora value of the expression.
     * @throws agora.errors.AgoraError When something goes wrong during evaluation.
     */
    public AgoraObject eval(Context context) throws AgoraError {
        try {
            if (Category.contains(context.getCategory(), Category.flags))
                return AgoraGlobals.glob.up.up(
                        new FormalsAndPattern(
                                makeFormals(context),
                                makePattern(context),
                                Category.emptyCategory
                        )
                );

            var client = makeClient(context, AgoraGlobals.glob.up.up(context));
            client.actualsUp();
            return context.getPrivate().delegate(makePattern(context), client, context).down();
        } catch (AgoraError ex) {
            ex.setCode(this);
            throw ex;
        }
    }

    /**
     * Implementation of the SUPER reifier for reifier patterns (e.g. SELF SUPER).
     *
     * @param context The evaluation context in which SUPER was sent.
     * @return The result of executing the reifier on the parent object.
     * @throws agora.errors.AgoraError When something went wrong during delegation of the
     *                                 reifier pattern to the parent (e.g. when it is not there!).
     */
    @Unary(value = "SUPER")
    @Reified
    public AgoraObject superReifier(Context context) throws AgoraError {
        var pattern = makePattern(context);
        var client = makeClient(context, AgoraGlobals.glob.up.up(context));
        client.actualsUp();
        return context.getParent().delegate(pattern, client, context);
    }
}
