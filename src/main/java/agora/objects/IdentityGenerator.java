package agora.objects;

import agora.errors.AgoraError;
import agora.patterns.Pattern;
import agora.runtime.Client;
import agora.runtime.Context;

/**
 * This class represents Agora object identities. It normally has two
 * subclasses: one for primitive object identities, and one for
 * identities representing ex nihilo created agora.objects.
 * An identity contains a method table and (possibly) a downed version
 * of the Java object it represents.
 * Last change:  E    16 Nov 97    1:49 am
 */
abstract public class IdentityGenerator extends AbstractGenerator {
    protected Object downedVersion;
    protected MethodsGenerator myMethods;

    /**
     * Creates a new identity. This constructor must be redefined in subclasses.
     *
     * @param nameOfFrame     The name of the generator to be used in inspectors.
     * @param theMethodTables A pointer to the first methods frame that is the
     *                        public part associated to this object identity.
     * @param theContents     The downed version of the object of which this generator is the identiy.
     *                        This only exists if it is a native object.
     */
    public IdentityGenerator(String nameOfFrame, MethodsGenerator theMethodTables, Object theContents) {
        super(nameOfFrame);
        this.myMethods = theMethodTables;
        this.downedVersion = theContents;
    }

    /**
     * Delegates a message to the public part (i.e. the method table) contained in the
     * object identity. The client contains the actual parameters. The context contains
     * the environmental information gathered so far.
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
        return this.myMethods.delegate(msg, client, context);
    }

    /**
     * Get the downed Java version of the object representing this Agora object identity.
     * For upped agora.objects, this simply returns the object that was upped. For ex nihilo created agora.objects,
     * it will construct a new Java object with the same interface as the corresponding Agora object.
     * At the moment this is not yet implemented: we will do this in the future by generating appropriate
     * Class files using the Jas assembler.
     *
     * @return The downed version of the object this identity generator represents.
     */
    public Object down() {
        // Here manual downing must be implemented
        // If you compute the downed version, store it in
        // downedVersion so that it only gets computed once.
        return this.downedVersion == null ? null : downedVersion;
    }

    /**
     * This method 'wraps' the identity to an Agora object. This is needed for encapsulation.
     * Although identities understand many methods, Agora agora.objects may understand only one meta
     * level method: the 'send'. THerefore the identity must be wrapped in an Agora object.
     * From that moment on, it is not longer possible to access the identity. Only the Agora object
     * can still do this since the identity is encapsulated inside the Agora object.
     *
     * @return A real Agora object that encapsulates this generator. The generator understands many meta
     * messages (like delegate and funcAddLayer and ...). The object resulting from wrapping the
     * generator encapsulates all this. The object only understands send, up and down.
     */
    public AgoraObject wrap() {
        return (new AgoraObject(this));
    }

    /**
     * This method changes the public part of the object identity. Of course, this method is the
     * very reason why we !have! object identities in the first place. Using this construction, an
     * object can change its public part, without changing its identity. Hence, this method will be
     * used to implement (destructive) mixin methods.
     */
    public void Change(MethodsGenerator theContents) {
        this.myMethods = theContents;
    }

    /**
     * Returns the public part contained in the object idenity.
     *
     * @return A pointer to the first method frame of the object represented by this
     * identity.
     */
    public MethodsGenerator getMe() {
        return this.myMethods;
    }

    /**
     * Adds a view on the generator. This is normally used to temporarily extend
     * a user created object, for example for applying a view-attribute on the object.
     *
     * @param NameOfFrame The name of the new generator to be used in inspectors.
     * @return A newly created InternalGenerator with the receiver as parent generator.
     * @throws agora.errors.AgoraError For some generators, it is impossible to add a layer around
     *                                 them (i.e. we cannot inherit from primitive agora.objects). If this happens, the exception is
     *                                 thrown.
     */
    public abstract IdentityGenerator funcAddLayer(String NameOfFrame) throws AgoraError;
}
