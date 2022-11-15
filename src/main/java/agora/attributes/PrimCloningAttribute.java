package agora.attributes;

import agora.errors.AgoraError;
import agora.errors.PrimException;
import agora.errors.ProgramError;
import agora.objects.AgoraObject;
import agora.patterns.Pattern;
import agora.reflection.Up;
import agora.runtime.Client;
import agora.runtime.Context;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serial;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * A primitive cloning attribute is actually a Java constructor.
 *
 * @author Wolfgang De Meuter (Programming Technology Lab)
 * Last change:  E    17 Nov 97    0:01 am
 */
public class PrimCloningAttribute extends PrimAttribute {

    protected Constructor<?> c;

    @Serial
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.writeObject(c.getDeclaringClass().getName());
        var types = c.getParameterTypes();
        stream.writeInt(types.length);
        for (var type : types) stream.writeObject(type.getName());
    }

    @Serial
    private void readObject(ObjectInputStream stream) throws IOException {
        try {
            var decl = Class.forName((String) stream.readObject());
            var ln = stream.readInt();
            var sig = new Class[ln];
            for (var i = 0; i < ln; i++) {
                var argName = (String) stream.readObject();
                sig[i] = switch (argName) {
                    case "int" -> Integer.TYPE;
                    case "boolean" -> Boolean.TYPE;
                    case "char" -> Character.TYPE;
                    case "short" -> Short.TYPE;
                    case "byte" -> Byte.TYPE;
                    case "float" -> Float.TYPE;
                    case "long" -> Long.TYPE;
                    default -> Class.forName(argName);
                };
            }
            c = decl.getConstructor(sig);
        } catch (NoSuchMethodException error) {
            java.lang.System.out.println("NATIVE SYSTEM ERROR IN READING CONSTRUCTOR(nosuchconstructor)");
        } catch (ClassNotFoundException error) {
            java.lang.System.out.println("NATIVE SYSTEM ERROR IN READING CONSTRUCTOR(nosuchclass");
        }
    }

    /**
     * Creates a primitive cloning attribute corresponding to the given constructor.
     *
     * @param c The native Java constructor corresponding to this attribute.
     */
    public PrimCloningAttribute(Constructor<?> c) {
        super();
        this.c = c;
    }

    /**
     * Executing the cloning method consists of invoking the constructor with the downed arguments.
     * The newly created object is upped into Agora.
     *
     * @param msg     The message whose delegation gave rise to the invocation.
     * @param client  The client object containing the actual arguments.
     * @param context The context of the object in which this attribute resides.
     * @throws agora.errors.AgoraError When something goes wrong during evaluation.
     */
    public AgoraObject doAttributeValue(Pattern msg, Client client, Context context) throws AgoraError {
        try {
            client.actualsDown();
            return Up.glob.up(c.newInstance(client.getActuals()));
        } catch (IllegalAccessException e) {
            throw new ProgramError("Illegal Access Exception when invoking constructor");
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof AgoraError a) throw a;
            throw new PrimException(e.getTargetException(), "PrimCloningMethAttribute::doAttributeValue");
        } catch (InstantiationException e) {
            throw new ProgramError("Primitive CLoning Method (constructor) could not instantiate");
        }
    }

    /**
     * Converts the attribute into a string.
     */
    public String toString() {
        return "PRIMITIVE CLONING:";
    }

    /**
     * Inspects the attribute in a given context.
     *
     * @param context The context of the object in which this attribute resides.
     * @return The attribute as a string, ready to be displayed in the inspector.
     * @throws agora.errors.AgoraError When something goes wrong while inspecting.
     */
    public String inspect(Context context) throws AgoraError {
        return "Primitive Java Constructor";
    }
}
