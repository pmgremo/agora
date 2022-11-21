package agora.objects;

import agora.errors.AgoraError;
import agora.runtime.Context;

import java.util.Hashtable;

/**
 * A UserIdentityGenerator represents an object ID for an ex-nihilo
 * created Agora object. It consists of a method table and possibly caches
 * its downed version if this already happened. The method table is usually an
 * 'InternalGenerator'.
 *
 * @author Wolfgang De Meuter (Programming Technology Lab).
 * Last change:  E    16 Nov 97    1:48 am
 */
public class UserIdentityGenerator extends IdentityGenerator {

    /**
     * Create a new identity with the given name, a method table (usually an InternalGenerator)
     * and a possibly downed object as Java representation (this will usually be null)
     *
     * @param nameOfFrame     The name of this generator to be used in inspectors.
     * @param theMethodTables A generator containing methods.
     * @param theContents     A downed version of the object corresponding to this userid. If this
     *                        version is unknown, just pass null and it will be calculated the first time 'down' is sent.
     */
    public UserIdentityGenerator(String nameOfFrame, MethodsGenerator theMethodTables, Object theContents) {
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
        return new UserIdentityGenerator(
                Object.class.getSimpleName(),
                myMethods.funcAddLayer(nameOfFrame),
                null
        );
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
        this.myMethods.inspect(context);
    }

    /**
     * Makes a deep clone of the identity by copying all the constituents of the
     * identity. The parameter is a clone map such that a thing is not copied twice.
     *
     * @param cache A table of already-copied-things such that nothing gets copied twice.
     * @return A deep copy of the receiver.
     */
    public UserIdentityGenerator copy(Hashtable<Object, Object> cache) {
        var existing = cache.get(this);
        if (existing != null) return (UserIdentityGenerator) existing;
        var result = new UserIdentityGenerator(name(), null, null);
        cache.put(this, result);
        result.myMethods = (MethodsGenerator) myMethods.copy(cache);
        return result;
    }
}
