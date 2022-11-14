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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This class represents a primitive public (non static) method in Java.
 * Last change:  E    17 Nov 97    1:30 am
 */
public class PrimMethAttribute extends PrimAttribute {
    protected Method m;

    @Serial
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.writeObject(m.getDeclaringClass().getName());
        stream.writeObject(m.getName());
        var types = m.getParameterTypes();
        stream.writeInt(types.length);
        for (var type : types) stream.writeObject(type.getName());
    }

    @Serial
    private void readObject(ObjectInputStream stream) throws IOException {
        try {
            var decl = Class.forName((String) stream.readObject());
            var metName = (String) stream.readObject();
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
                    case "double" -> Double.TYPE;
                    case "void" -> Void.TYPE;
                    default -> Class.forName(argName);
                };
            }
            m = decl.getMethod(metName, sig);
        } catch (NoSuchMethodException error) {
            java.lang.System.out.println("NATIVE SYSTEM ERROR IN READING METHOD(nosuchmethod:PrimMethAttribute)");
        } catch (ClassNotFoundException error) {
            java.lang.System.out.println("NATIVE SYSTEM ERROR IN READING METHOD(nosuchclass:PrimMethAttribute)");
        }
    }

    /**
     * Creates a new PrimMethAttribute given the (non static) java method as argument.
     *
     * @param javaMethod The Java method to which this attribute is associated.
     */
    public PrimMethAttribute(Method javaMethod) {
        super();
        this.m = javaMethod;
    }

    /**
     * Executing a PrimMethAttribute consists of invoking the corresponding java method
     * on the downed version of the receiver. The arguments are the downed versions of the actual
     * Agora arguments. The result is back upped into Agora.
     *
     * @param msg     The message whose delegation gave rise to the invocation.
     * @param client  The client object containing the actual arguments.
     * @param context The context of the object in which this attribute resides.
     * @throws agora.errors.AgoraError When something goes wrong during evaluation.
     */
    public AgoraObject doAttributeValue(Pattern msg, Client client, Context context) throws AgoraError {
        try {
            return Up.glob.up(this.m.invoke(context.getSelf().down(), client.makeNativeArguments()));
        } catch (IllegalAccessException e) {
            throw new ProgramError("Illegal Access Exception while accessing a primitive method");
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof AgoraError)
                throw (AgoraError) e.getTargetException();
            else
                throw new PrimException(e.getTargetException(), "PrimMethAttribute::doAttributeValue");
        }

    }

    /**
     * Converts the attribute into a string.
     */
    public String toString() {
        return "PRIMITIVE METHOD";
    }

    /**
     * Inspects the attribute in a given context.
     *
     * @param context The context of the object in which this attribute resides.
     * @return The attribute as a string, ready to be displayed in the inspector.
     * @throws agora.errors.AgoraError When something goes wrong while inspecting.
     */
    public String inspect(Context context) throws AgoraError {
        return "Primitive Java method: " + this.m.toString();
    }
}
