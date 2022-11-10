package agora.attributes;

import agora.errors.AgoraError;
import agora.objects.AgoraObject;
import agora.patterns.AbstractPattern;
import agora.runtime.Client;
import agora.runtime.Context;

import java.io.Serializable;
import java.util.Hashtable;

/**
 * An Agora variable gives rise to two slots: a VarSetAttribute and a VarGetAttribute.
 * Both point to the same VariableContainer such that they stay consistent.
 *
 * @author Wolfgang De Meuter(Programming Technology Lab).
 * Last change:  E    16 Nov 97    1:35 am
 */
public class VarGetAttribute extends Attribute implements Serializable {
    protected VariableContainer theContents;

    /**
     * Creates a new VarGetAttribute with the given VariableContainer. The VariableContainer
     * holds the value. Normally, the variable container is shared between the VarGet and VarSet.
     *
     * @param variable The container in which the variable resides. This indirection is necessary to make
     *                 sure both variable read and write slots point to the same object.
     */
    public VarGetAttribute(VariableContainer variable) {
        super();
        this.theContents = variable;
    }

    /**
     * Executing a VarGetAttribute consists of reading its associated variable container.
     *
     * @param msg     The message whose delegation gave rise to the invocation.
     * @param client  The client object containing the actual arguments.
     * @param context The context of the object in which this attribute resides.
     * @throws agora.errors.AgoraError When something goes wrong during evaluation.
     */
    public AgoraObject doAttributeValue(AbstractPattern msg, Client client, Context context) throws AgoraError {
        return this.theContents.read();
    }

    /**
     * Converts the attribute to a string.
     */
    public String toString() {
        return "             VARGET:";
    }

    /**
     * Inspects the attribute in a given context.
     *
     * @param context The context of the object in which this attribute resides.
     * @return The attribute as a string, ready to be displayed in the inspector.
     * @throws agora.errors.AgoraError When something goes wrong while inspecting.
     */
    public String inspect(Context context) throws AgoraError {
        return "Variable Reader";
    }

    /**
     * Makes a copy of the attribute and its associated variable container.
     *
     * @param cloneMap A table of already-copied-things such that nothing is copied twice.
     * @return A clone of this attribute.
     */
    public VarGetAttribute copy(Hashtable cloneMap) {
        var myclone = (VarGetAttribute) cloneMap.get(this);
        if (myclone != null)
            return myclone;
        else {
            var clonedAttribute = new VarGetAttribute(null);
            cloneMap.put(this, clonedAttribute);
            clonedAttribute.theContents = (VariableContainer) (this.theContents.copy(cloneMap));
            //We cannot avoid this type cast because of the genericity of clone maps.
            return clonedAttribute;
        }
    }
}
