package agora.attributes;

import agora.errors.AgoraError;
import agora.errors.PrimException;
import agora.errors.ProgramError;
import agora.objects.AgoraObject;
import agora.patterns.AbstractPattern;
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
 * A primitive reifier method is a reifier method whose implementation is in some
 * method hardcoded in the Context. An example is the SELF.
 * Last change:  E    17 Nov 97    1:30 am
 */
public class PrimReifierMethAttribute extends PrimAttribute {
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
            java.lang.System.out.println("NATIVE SYSTEM ERROR IN READING METHOD(nosuchmethod)");
        } catch (ClassNotFoundException error) {
            java.lang.System.out.println("NATIVE SYSTEM ERROR IN READING METHOD(nosuchclass)");
        }
    }

    /**
     * Creates a new primitive reifier method attribute. The java method parameter is
     * supposed to be a method defined on context agora.objects.
     *
     * @param javaMethod The method corresponding to the primitive reifier. Its first
     *                   argument is supposed to be a context parameter. In this argument,
     *                   the context of invocation will be passed to the implementation of the reifier.
     */
    public PrimReifierMethAttribute(Method javaMethod) {
        m = javaMethod;
    }

    /**
     * Executing a primitive reifier method consists of invoking the associated
     * java method (stored in the attribute) on the context object, with the
     * arguments as present in the client object.
     *
     * @param msg     The message whose delegation gave rise to the invocation.
     * @param client  The client object containing the actual arguments.
     * @param context The context of the object in which this attribute resides.
     * @throws agora.errors.AgoraError When something goes wrong during evaluation.
     */
    public AgoraObject doAttributeValue(AbstractPattern msg, Client client, Context context) throws AgoraError {
        try {
            return Up.glob.up(this.m.invoke(context, client.makeNativeArguments()));
        } catch (IllegalAccessException e) {
            throw new ProgramError("Illegal Access Exception while accessing a primitive method");
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof AgoraError)
                throw (AgoraError) e.getTargetException();
            else
                throw new PrimException(e.getTargetException(), "PrimReifierMethAttribute::doAttributeValue");
        }
    }

    /**
     * Converts the attribute into a string.
     */
    public String toString() {
        return "PRIM REIFIER METHOD:";
    }

    /**
     * Inspects the attribute in a given context.
     *
     * @param context The context of the object in which this attribute resides.
     * @return The attribute as a string, ready to be displayed in the inspector.
     * @throws agora.errors.AgoraError When something goes wrong while inspecting.
     */
    public String inspect(Context context) throws AgoraError {
        return "Primitive Reifier (Context Dependent Method)";
    }
}
