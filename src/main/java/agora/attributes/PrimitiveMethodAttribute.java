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

/**
 * This class represents a primitive public (non-static) method in Java.
 * Last change:  E    17 Nov 97    1:30 am
 */
public class PrimitiveMethodAttribute extends PrimitiveAttribute {
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
     * Creates a new PrimMethAttribute given the (non-static) java method as argument.
     *
     * @param javaMethod The Java method to which this attribute is associated.
     */
    public PrimitiveMethodAttribute(Method javaMethod) {
        super();
        this.method = javaMethod;
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
            return AgoraGlobals.glob.up.up(method.invoke(context.getSelf().down(), client.makeNativeArguments()));
        } catch (IllegalAccessException e) {
            throw new ProgramError("Illegal Access Exception while accessing a primitive method");
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof AgoraError a) throw a;
            throw new PrimitiveException(e.getTargetException(), "PrimMethAttribute::doAttributeValue");
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
        return "Primitive Java method: " + method.toString();
    }
}
