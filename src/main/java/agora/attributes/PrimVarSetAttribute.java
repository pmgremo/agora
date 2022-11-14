package agora.attributes;

import agora.errors.AgoraError;
import agora.errors.ProgramError;
import agora.objects.AgoraObject;
import agora.patterns.AbstractPattern;
import agora.runtime.Client;
import agora.runtime.Context;
import agora.tools.AgoraGlobals;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serial;
import java.lang.reflect.Field;

/**
 * A PrimVarSetAttribute is a reading method associated to an implementation level
 * Java field.
 * Last change:  E    16 Nov 97    8:22 pm
 */
public class PrimVarSetAttribute extends PrimAttribute {

    protected Field f;

    @Serial
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.writeObject(f.getDeclaringClass());
        stream.writeObject(f.getName());
    }

    @Serial
    private void readObject(ObjectInputStream stream) throws IOException {
        try {
            var decl = (Class<?>) stream.readObject();
            var nme = (String) stream.readObject();
            f = decl.getDeclaredField(nme);
        } catch (NoSuchFieldException e) {
            java.lang.System.out.println("NATIVE SYSTEM ERROR IN READING FIELD(nosuchfield)");
        } catch (ClassNotFoundException error) {
            java.lang.System.out.println("NATIVE SYSTEM ERROR IN READING FIELD(nosuchclass)");
        }
    }

    /**
     * Creates a new PrimVarSetAttribute that corresponds to the given field.
     *
     * @param variable The field corresponding to this primitive variable
     *                 setter.
     */
    public PrimVarSetAttribute(Field variable) {
        super();
        this.f = variable;
    }

    /**
     * Executing a PrimVarSetAttribute consists of writing the field in the downed version
     * of the receiver. Its value will be the downed version of the argument in the client.
     *
     * @param msg     The message whose delegation gave rise to the invocation.
     * @param client  The client object containing the actual arguments.
     * @param context The context of the object in which this attribute resides.
     * @throws agora.errors.AgoraError When something goes wrong during evaluation.
     */
    public AgoraObject doAttributeValue(
            AbstractPattern msg,
            Client client,
            Context context
    ) throws AgoraError {
        try {
            f.set(context.getSelf().down(), ((AgoraObject) client.getActuals()[0]).down());
            return AgoraGlobals.glob.uppedNull;
        } catch (IllegalAccessException e) {
            throw new ProgramError("Illegal Access Exception while accessing a primitive assignment");
        }
    }

    /**
     * Converts the attribute to a string.
     */
    public String toString() {
        return "PRIM         VARSET: ";
    }

    /**
     * Inspects the given attribute in the given context.
     *
     * @param context The context of the object in which this attribute resides.
     * @return The attribute as a string, ready to be displayed in the inspector.
     * @throws agora.errors.AgoraError When something goes wrong while inspecting.
     */
    public String inspect(Context context) throws AgoraError {
        return "Primitive Variable Writer";
    }
}

