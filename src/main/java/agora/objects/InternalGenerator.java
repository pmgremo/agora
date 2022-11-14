package agora.objects;

import agora.Inspector;
import agora.attributes.Attribute;
import agora.errors.AgoraError;
import agora.patterns.Pattern;
import agora.runtime.Category;
import agora.runtime.Client;
import agora.runtime.Context;
import agora.tools.AgoraGlobals;

import java.io.Serializable;
import java.util.Hashtable;

/**
 * An internal generator is a frame of methods for ex-nihilo created agora.objects
 * Such a generator contains a method table, a link to a private part and a parent part.
 *
 * @author Wolfgang De Meuter (Programming Technology Lab)
 * Last change:  E    17 Nov 97    1:01 am
 */
public class InternalGenerator extends MethodsGenerator implements Serializable {
    /**
     * The private part of the methods generator. The private part of the private part
     * is that part itself. Internalgenerators are used both for representing public
     * parts and private parts.
     */
    protected InternalGenerator privPart;

    /**
     * Opens an inspector for the object.
     *
     * @param context The context in which the inspect is sent. This is needed to show
     *                object values in the inspector (the values are in the context parts).
     */
    public void inspect(Context context) {
        var d = new Inspector(AgoraGlobals.glob.agoraWindow,
                this.name,
                theMethodTable,
                privPart,
                parent,
                this,
                null,
                context);
        d.pack();
        d.show();
    }

    /**
     * Creates a new internal generator as part of an object.
     *
     * @param nameOfFrame The name of the generator that has to be used in inspectors.
     * @param myPart      A hashtable in which the keys are agora.runtime agora.patterns and in which
     *                    the values are agora.attributes. Hence this is the method table of the generator.
     * @param privPart    An initial private part.
     * @param parentPart  The generator to which this method frame has to be linked
     *                    with a parent-of link.
     */
    public InternalGenerator(String nameOfFrame,
                             Hashtable<Pattern, Attribute> myPart,
                             InternalGenerator privPart,
                             AbstractGenerator parentPart) {
        super(nameOfFrame, myPart, parentPart);
        this.privPart = privPart;
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
        return new InternalGenerator(nameOfFrame, new Hashtable<>(3), null, this);
    }

    /**
     * Accesses the private part of the internal methods generator.
     *
     * @return The private part of the generator.
     */
    public InternalGenerator getPrivate() {
        return this.privPart;
    }

    /**
     * Re-assigns the private part of this method generator. This is needed to
     * make the private of the private, the private itself.
     *
     * @param newPrivate The new value of the private part with which the generator has
     *                   to be updated.
     * @throws agora.errors.AgoraError In this case, the exception will never be thrown, but if
     *                                 we try to invoke 'setPrivate' on other subclasses of MethodsGenerator it might be thrown.
     *                                 This is for example the case when trying to assign the private part of a PrimGenerator,
     *                                 also a sublass of MethodsGenerator.
     */
    public void setPrivate(InternalGenerator newPrivate) throws AgoraError {
        this.privPart = newPrivate;
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
    public AgoraObject delegate(Pattern msg,
                                Client client,
                                Context context) throws AgoraError {
        var lookupResult = this.theMethodTable.get(msg);
        if (lookupResult == null) return parent.delegate(msg, client, context);
        return lookupResult.doAttributeValue(
                msg,
                client,
                context.setMultiple(
                        this.privPart,
                        Category.emptyCategory,
                        this.parent
                )
        );
    }

    /**
     * Makes a deep clone of the internal generator by copying all the constituents of the
     * generator. The parameter is a clone map such that a thing is not copied twice.
     *
     * @param cache A table of already-copied-things such that nothing gets copied twice.
     * @return A deep copy of the receiver.
     */
    public InternalGenerator copy(Hashtable<Object, Object> cache) {
        var existing = (InternalGenerator) cache.get(this);
        if (existing != null) return existing;
        var result = new InternalGenerator(getFrameName(), null, null, null);
        cache.put(this, result);
        result.parent = parent.copy(cache);
        result.theMethodTable = new Hashtable<>(theMethodTable.size());
        theMethodTable.forEach((key, value) -> result.theMethodTable.put(key.copy(cache), value.copy(cache)));
        result.privPart = privPart.copy(cache);
        return result;
    }
}
