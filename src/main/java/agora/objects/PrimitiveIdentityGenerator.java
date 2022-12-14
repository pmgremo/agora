package agora.objects;

import agora.awt.Inspector;
import agora.errors.AgoraError;
import agora.errors.ProgramError;
import agora.runtime.Context;
import agora.tools.AgoraGlobals;

/**
 * A PrimIdentityGenerator represents the part of an Agora object that is the
 * very identity of the object, in this case a primitive (upped) object.
 * A PrimIdentityGenerator has a name (to be used in the inspector),
 * a method table generator (i.e. a public part) and a primitive
 * object that is the wrapped object.
 *
 * @author Wolfgang De Meuter (Programming Technology Lab)
 * Last change:  E    17 Nov 97    1:03 am
 */
public class PrimitiveIdentityGenerator extends IdentityGenerator {

    /**
     * To create a new primitive identity generator with a given name, a given public part (normally
     * a PrimGenerator) and a Java object that is represented by this primitive identity.
     *
     * @param nameOfFrame     The name of this generator to be used in inspectors.
     * @param theMethodTables A MethodGenerator that is the first methods frame in the lookup
     *                        chain of the primitive object. This lookup chain follows the inheritance hierarchy of the
     *                        upped primitive.
     * @param theContents     The original primitive object of which this generator is the Agora
     *                        identity.
     */
    public PrimitiveIdentityGenerator(String nameOfFrame, MethodsGenerator theMethodTables, Object theContents) {
        super(nameOfFrame, theMethodTables, theContents);
    }

    /**
     * Adds a view on the identity generator. This is normally used to temporarily extend
     * a user created object, for example for applying a view-attribute on the object.
     *
     * @param nameOfFrame The name of the new generator to be used in inspectors.
     * @return A newly created InternalGenerator with the receiver as parent generator.
     * @throws agora.errors.AgoraError For some generators, it is impossible to add a layer around
     *                                 them (i.e. we cannot inherit from primitive agora.objects). If this happens, the exception is
     *                                 thrown.
     */
    public IdentityGenerator funcAddLayer(String nameOfFrame) throws AgoraError {
        throw new ProgramError("Cannot extend a primitive object! (PrimIdentityGenerator::funcAddLayer)");
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
        var d = new Inspector(AgoraGlobals.glob.window,
                this.name,
                this.myMethods.getHashTable(),
                null,
                this.myMethods.getParent(),
                this.myMethods,
                this.downedVersion,
                context,
                this
        );
        d.pack();
        d.setVisible(true);
    }
}
