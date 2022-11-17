package agora.attributes;

import agora.errors.AgoraError;
import agora.objects.AgoraObject;
import agora.patterns.Pattern;
import agora.runtime.Client;
import agora.runtime.Context;

import java.util.Hashtable;


/**
 * An Agora variable gives rise to two slots: a VarSetAttribute and a VarGetAttribute.
 * Both point to the same VariableContainer such that they stay consistent.
 *
 * @author Wolfgang De Meuter (Programming Technology Lab)
 * Last change:  E    16 Nov 97    1:35 am
 */
public class VarSetAttribute implements Attribute {
    protected VariableContainer theContents;

    /**
     * Creates a new VarSetAttribute with the given VariableContainer. The VariableContainer
     * holds the value.
     *
     * @param variable The container in which the variable resides. This indirection is necessary
     *                 to make sure write and read slots both point to the same object.
     */
    public VarSetAttribute(VariableContainer variable) {
        this.theContents = variable;
    }

    /**
     * A VarSetAttribute is executed in a given context, with a client of only one actual
     * parameters and the name of the message x:. The value of the variablecontainer will
     * be updated.
     *
     * @param msg     The message whose delegation gave rise to the invocation.
     * @param client  The client object containing the actual arguments.
     * @param context The context of the object in which this attribute resides.
     * @throws agora.errors.AgoraError When something goes wrong during evaluation.
     */
    public AgoraObject doAttributeValue(Pattern msg,
                                        Client client,
                                        Context context) throws AgoraError {
        var parameter = (AgoraObject) (client.getActuals()[0]);
        this.theContents.write(parameter);
        return parameter;
    }

    /**
     * Clones the attribute.
     *
     * @param cloneMap A table of already-copied-things such that nothing is copied twice.
     * @return A copy of this attribute.
     */
    public VarSetAttribute copy(Hashtable<Object, Object> cloneMap) {
        var myclone = (VarSetAttribute) cloneMap.get(this);
        if (myclone != null) return myclone;
        var clonedAttribute = new VarSetAttribute(null);
        cloneMap.put(this, clonedAttribute);
        clonedAttribute.theContents = this.theContents.copy(cloneMap);
        // We cannot avoid this type cast because of the genericity of clone maps.
        return clonedAttribute;
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
        return "Variable Writer";
    }

}
