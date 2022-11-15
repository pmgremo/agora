package agora.attributes;

import agora.errors.AgoraError;
import agora.errors.ProgramError;
import agora.objects.AgoraObject;
import agora.patterns.Pattern;
import agora.runtime.Client;
import agora.runtime.Context;
import agora.tools.AgoraGlobals;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serial;
import java.lang.reflect.Field;

/**
 * A PrimVarGetAttribute is an 'Agorification' of a corresponding Java Field.
 * Last change:  E    16 Nov 97    8:22 pm
 */
public class PrimVarGetAttribute extends PrimAttribute {

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
     * Create a new primitive var get attribute that corresponds to the given Java field.
     *
     * @param variable The Java field corresponding to this primitive variable getter.
     */
    public PrimVarGetAttribute(Field variable) {
        super();
        this.f = variable;
    }

    /**
     * Executing a PrimVarGetAttribute consists of reading the field from the object
     * that corresponds to the downed version of the receiver.
     *
     * @param msg     The message whose delegation gave rise to the invocation.
     * @param client  The client object containing the actual arguments.
     * @param context The context of the object in which this attribute resides.
     * @throws agora.errors.AgoraError When something goes wrong during evaluation.
     */
    public AgoraObject doAttributeValue(Pattern msg, Client client, Context context) throws AgoraError {
        try {
            return AgoraGlobals.glob.up.up(f.get(context.getSelf().down()));
        } catch (IllegalAccessException e) {
            throw new ProgramError("Illegal Access Exception while accessing a primitive variable");
        }
    }

    /**
     * Converts the attribute to a string.
     */
    public String toString() {
        return "PRIMITIVE VARIABLE READER";
    }

    /**
     * Inspects the given attribute in the given context.
     *
     * @param context The context of the object in which this attribute resides.
     * @return The attribute as a string, ready to be displayed in the inspector.
     * @throws agora.errors.AgoraError When something goes wrong while inspecting.
     */
    public String inspect(Context context) throws AgoraError {
        return "Primitive Variable Reader";
    }
}

