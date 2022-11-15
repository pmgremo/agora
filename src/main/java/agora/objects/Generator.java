package agora.objects;

import agora.Copyable;
import agora.errors.AgoraError;
import agora.patterns.Pattern;
import agora.runtime.Client;
import agora.runtime.Context;

import java.io.Serializable;

/**
 * Internally, an Agora object is considered as a list of frames each containing
 * method tables and/or pointers to other frames or structures. All these building
 * blocks are called 'generators'. Several kinds of generators exist such as object
 * identities, method tables, and so on. All these building blocks constitute an object.
 * The general idea of a generator is that we can delegate it a message using the method
 * 'delegate'.
 *
 * @author Wolfgang De Meuter (Programming Technology Lab).
 * Last change:  E    16 Nov 97    1:49 am
 */
public abstract class Generator implements Serializable, Copyable<Generator> {

    /**
     * This variable is a string that gives a name to a generator. An example
     * of such a name might be the name of the mixin method that created
     * the generator. This name is used by the object inspector.
     */
    protected String name;

    /**
     * Creates a new generator with a given name.
     *
     * @param nameOfFrame The name to be used in inspectors for this generator frame.
     */
    public Generator(String nameOfFrame) {
        super();
        this.name = nameOfFrame;
    }

    /**
     * Looks up a message in the generator. The context contains the information
     * of the object determined 'so far'. The first parameter is the message
     * being looked up. The client argument contains the actual parameters.
     *
     * @param msg     The message pattern to be delegated.
     * @param client  The client object containing the actual parameters.
     * @param context The knowledge of the receiving object so far.
     * @return The AgoraObject corresponding to evaluating the attribute associated to the
     * specified pattern.
     * @throws agora.errors.AgoraError When the message is not understood or when an error occurs
     *                                 during evaluation of the method associated to the pattern.
     */
    public abstract AgoraObject delegate(Pattern msg,
                                         Client client,
                                         Context context) throws AgoraError;

    /**
     * To access the name of the generator.
     *
     * @return The name of the generator to be used in inspectors.
     */
    public String getFrameName() {
        return this.name;
    }

    /**
     * Opens an inspector for the object.
     *
     * @param context The context in which the inspect is sent. This is needed to show
     *                object values in the inspector (the values are in the context parts).
     * @throws agora.errors.AgoraError If an error occurs during inspection, this exception is
     *                                 thrown. This would be a bug somewhere in the implementation.
     */
    public abstract void inspect(Context context) throws AgoraError;
}
