package agora.attributes;

import agora.errors.AgoraError;
import agora.errors.PrimitiveException;
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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * A primitive reifier method is a reifier method whose implementation is in some
 * method hardcoded in the Context. An example is the SELF.
 * Last change:  E    17 Nov 97    1:30 am
 */
public class PrimitiveReifierMethodAttribute extends PrimitiveAttribute {
    protected Method method;

    @Serial
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.writeObject(method.getDeclaringClass());
        stream.writeUTF(method.getName());
        stream.writeObject(method.getParameterTypes());
    }

    @Serial
    private void readObject(ObjectInputStream stream) throws IOException {
        try {
            var owner = (Class<?>) stream.readObject();
            var name = stream.readUTF();
            var types = (Class<?>[]) stream.readObject();
            method = owner.getMethod(name, types);
        } catch (NoSuchMethodException error) {
            System.err.println("NATIVE SYSTEM ERROR IN READING METHOD(nosuchmethod)");
        } catch (ClassNotFoundException error) {
            System.err.println("NATIVE SYSTEM ERROR IN READING METHOD(nosuchclass)");
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
    public PrimitiveReifierMethodAttribute(Method javaMethod) {
        method = javaMethod;
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
    public AgoraObject doAttributeValue(Pattern msg, Client client, Context context) throws AgoraError {
        try {
            return AgoraGlobals.glob.up.up(method.invoke(context, client.makeNativeArguments()));
        } catch (IllegalAccessException e) {
            throw new ProgramError("Illegal Access Exception while accessing a primitive method");
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof AgoraError a) throw a;
            throw new PrimitiveException(e.getTargetException(), "PrimReifierMethAttribute::doAttributeValue");
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PrimitiveReifierMethodAttribute that)) return false;
        return Objects.equals(method, that.method);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method);
    }
}
