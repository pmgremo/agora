package agora.objects;

import agora.attributes.Attribute;
import agora.awt.Inspector;
import agora.errors.AgoraError;
import agora.errors.ProgramError;
import agora.patterns.Pattern;
import agora.runtime.Client;
import agora.runtime.Context;
import agora.tools.AgoraGlobals;

import java.util.Hashtable;
import java.util.Map;

/**
 * A PrimGenerator is a kind of methods frame used for primitive object
 * wrapping. The difference between a PrimGenerator and an InternalGenerator
 * is that a PrimitiveGenerator does not have a private part.
 *
 * @author Wolfgang De Meuter (Programming Technology Lab).
 * Last change:  E    17 Nov 97    1:02 am
 */
public class PrimitiveGenerator extends MethodsGenerator {
    /**
     * Creates a new PrimGenerator. The String argument is the name of the frame.
     * This will be used to display the PrimGenerator in the inspector.
     * The hashtable binds patterns to attributes and the additional generator is
     * a reference to the parent frame.
     *
     * @param nameOfFrame The name of the generator to be used in the inspector.
     * @param table       The initial value of the methods hashtable. This table links
     *                    patterns to their corresponding agora.attributes.
     */
    public PrimitiveGenerator(String nameOfFrame, Map<Pattern, Attribute> table) {
        super(nameOfFrame, table, null);
    }

    /**
     * Delegates the given message to the frame. If it is there, the corresponding attribute
     * will be invoked. Otherwise, the search is continued in the parent.
     *
     * @param msg     The message pattern to be delegated.
     * @param client  The client object containing the actual parameters.
     * @param context The knowledge of the receiving object so far.
     * @return The AgoraObject corresponding to evaluating the attribute associated to the
     * specified pattern.
     * @throws agora.errors.AgoraError When the message is not understood or when an error occurs
     *                                 during evaluation of the method associated to the pattern.
     */
    public AgoraObject delegate(Pattern msg, Client client, Context context) throws AgoraError {
        var lookupResult = methods.get(msg);
        return lookupResult == null ?
                parent.delegate(msg, client, context) :
                lookupResult.doAttributeValue(msg, client, context.setParent(parent));
    }

    /**
     * Opens an inspector for the object.
     *
     * @param context The context in which inspect is sent. This is needed to show
     *                object values in the inspector (the values are in the context parts).
     * @throws agora.errors.AgoraError If an error occurs during inspection, this exception is
     *                                 thrown. This would be a bug somewhere in the implementation.
     */
    public void inspect(Context context) throws AgoraError {
        var d = new Inspector(
                AgoraGlobals.glob.window,
                name,
                methods,
                null,
                parent,
                null,
                null,
                context,
                this
        );
        d.pack();
        d.setVisible(true);
    }

    /**
     * Throws an exception since there is no private part.
     *
     * @param newPrivate A private part.
     * @throws agora.errors.AgoraError Is always thrown as there is no private part to be set.
     */
    public void setPrivate(InternalGenerator newPrivate) throws AgoraError {
        throw new ProgramError("You cannot change the private part of a primitive object! (PrimGenerator:setPrivate)");
    }

    /**
     * Adds a view on the generator. This is normally used to temporarily extend
     * a user created object, for example for applying a view-attribute on the object.
     *
     * @param nameOfFrame The name of the new generator to be used in inspectors.
     * @return A newly created InternalGenerator with the receiver as parent generator.
     * @throws agora.errors.AgoraError For some generators, it is impossible to add a layer around
     *                                 them (i.e. we cannot inherit from primitive agora.objects). If this happens, the exception is
     *                                 thrown.
     */
    public InternalGenerator funcAddLayer(String nameOfFrame) throws AgoraError {
        return new InternalGenerator(nameOfFrame, new Hashtable<>(5), null, this);
    }
}
