package agora.grammar;

import agora.errors.AgoraError;
import agora.objects.AgoraObject;
import agora.runtime.Context;
import agora.tools.AgoraGlobals;

/**
 * Representation of reifier message expressions.
 * Last change:  E    16 Nov 97    2:17 pm
 */
public class ReifierMessage extends Message {
    /**
     * Does nothing but calling the super constructor.
     *
     * @param receiver The expression denoting the receiving expression of the reifier message.
     * @param pattern  The name of the reifier message and the actual arguments.
     */
    public ReifierMessage(Expression receiver, ReifPattern pattern) {
        super(receiver, pattern);
    }

    /**
     * Evaluates the reifier message in a given environment.
     *
     * @param context The environment in which the reifier message must be evaluated.
     * @return The Agora object corresponding to sending the reifier.
     * @throws agora.errors.AgoraError When something goes wrong during evaluation.
     */
    public AgoraObject eval(Context context) throws AgoraError {
        try {
            var target = AgoraGlobals.glob.up.up(receiver);
            var client = pattern.makeClient(
                    context,
                    target
            );
            client.actualsUp();
            return target.send(
                    pattern.makePattern(context),
                    client
            ).down();
        } catch (AgoraError ex) {
            ex.setCode(this);
            throw ex;
        }
    }
}
