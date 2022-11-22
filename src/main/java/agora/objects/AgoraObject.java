package agora.objects;

import agora.errors.AgoraError;
import agora.patterns.Pattern;
import agora.runtime.Category;
import agora.runtime.Client;

import java.io.Serializable;

/**
 * This class represents Agora objects as seen through the eyes of the evaluator.
 * In other words, this class represents Agora meta-objects. There are only two
 * methods understood by the objects of this class. This is why Agora is the
 * language with the simplest MOP in the world.
 *
 * @author Wolfgang De Meuter (Programming Technology Lab).
 * Last change:  E    16 Nov 97    1:49 am
 */
public class AgoraObject implements Serializable {
    /**
     * A reference to the identity generator being the identity of this object. This object
     * is merely a wrapper around a much richer structure of generators. AgoraObject's
     * encapsulate this structure. It is the only part of the object that will be visible
     * to programmers.
     */
    protected IdentityGenerator objectID;

    /**
     * Given an identity generator, a new Agora object of that identity is created.
     *
     * @param id The identity generator that is the object identity of this object.
     *           This object is merely a wrapper to encapsulate this id.
     */
    public AgoraObject(IdentityGenerator id) {
        this.objectID = id;
    }

    /**
     * Send a message to the Agora object. The client parameter contains
     * the actual arguments.
     *
     * @param msg    The pattern of the message to be sent.
     * @param client A client object that contains the actual arguments of the message. The
     *               client can also contain a hidden context argument if the message is a reifier message
     * @return The Result of sending the message is an Agora object, just like the arguments
     * in the client ought to be agora objects.
     * @throws agora.errors.AgoraError If inside the object an error occurs (e.g. message not understood
     *                                 or an error while evaluating a method), this exception is thrown.
     * @see agora.runtime.Client
     * @see agora.runtime.ReifierClient
     */
    public AgoraObject send(Pattern msg, Client client) throws AgoraError {
        return objectID.delegate(
                msg,
                client,
                client.newContext(
                        Category.emptyCategory,
                        objectID,
                        objectID.getMe()
                )
        );
    }

    /**
     * Returns the Java-analog of this Agora object.
     *
     * @return The Java version of this Agora Object. This is either the original Java
     * object (if this Agora object was an upped one) or a dynamically created Java
     * object that encapsulates this object.
     */
    public <T> T down() {
        return (T) objectID.down();
    }

}

