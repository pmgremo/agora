package agora.attributes;

import agora.errors.AgoraError;
import agora.grammar.Expression;
import agora.objects.AgoraObject;
import agora.objects.InternalGenerator;
import agora.patterns.AbstractPattern;
import agora.patterns.UnaryPattern;
import agora.runtime.Client;
import agora.runtime.Context;

/**
 * A method attribute represents an ordinary Agora method. Subclasses of this class
 * exist such as view, mixin, cloning and so on. These subclasses must only
 * override the way the are executed in 'doAttributeValue'.
 *
 * @author Wolfgang De Meuter (Programming Technology Lab).
 * Last change:  E    16 Nov 97    1:34 am
 */
public class MethAttribute implements Attribute {
    protected Expression methodCode;

    protected String[] formals;

    /**
     * Creates a new Agora method with the array of strings as formal arguments
     * and the given expression as method body.
     *
     * @param formals The formal arguments of the method.
     * @param body    The body expression of the method.
     */
    public MethAttribute(String[] formals, Expression body) {
        super();
        this.formals = formals;
        this.methodCode = body;
    }

    /**
     * Executing a new method consists of making a local view on the local and private
     * (being a new frame for lexical scoping) and evaluating the method code in the
     * context of this frame. The original context is not touched.
     *
     * @param msg     The message whose delegation gave rise to the invocation.
     * @param client  The client object containing the actual arguments.
     * @param context The context of the object in which this attribute resides.
     * @throws agora.errors.AgoraError When something goes wrong during evaluation.
     */
    public AgoraObject doAttributeValue(AbstractPattern msg, Client client, Context context) throws AgoraError {
        var localPriv = this.bind(client.getActuals(), context.getPrivate());
        var localPub = context.getPub().funcAddLayer("Calling Frame of:" + msg.toString());
        localPub.setPrivate(localPriv);
        return this.methodCode.eval(context.setMultiple(context.getSelf(),
                localPriv,
                localPub,
                context.getCategory(),
                context.getParent()));
    }

    /**
     * This method is called from within 'doAttributeValue'. It takes a private part and an array
     * of actual parameters. It adds a local frame to make the formals-actuals bindings and returns
     * this one.
     *
     * @param actuals  The list of actual parameters of type Object: can be evaluated or not.
     * @param privPart The private part to which the new frame must be attached.
     * @return A new frame linked to the private part. It contains the bindings.
     * @throws agora.errors.AgoraError Something can always go wrong (Murphy!).
     */
    public InternalGenerator bind(Object[] actuals, InternalGenerator privPart) throws AgoraError {
        var size = actuals.length;
        var bindingsPriv = privPart.funcAddLayer("Formals Actuals Frame");
        bindingsPriv.setPrivate(bindingsPriv);
        for (var i = 0; i < size; i++) {
            var var = new VariableContainer((AgoraObject) (actuals[i]));
            // Impossible to avoid ugly type cast, unless clients become horribly inefficient (see 'doAttributeValue')
            var readatt = new VarGetAttribute(var);
            var writeatt = new VarSetAttribute(var);
            var readpat = new UnaryPattern(this.formals[i]);
            var writepat = readpat.makeWritePattern();
            bindingsPriv.installPattern(readpat, readatt);
            bindingsPriv.installPattern(writepat, writeatt);
        }
        return bindingsPriv;
    }

    /**
     * Converts the attribute to a string.
     */
    public String toString() {
        return "METHOD:";
    }

    /**
     * Inspects the method attribute in a given context.
     *
     * @param context The context of the object in which this attribute resides.
     * @return The attribute as a string, ready to be displayed in the inspector.
     * @throws agora.errors.AgoraError When something goes wrong while inspecting.
     */
    public String inspect(Context context) throws AgoraError {
        return "METHOD:\n" + this.methodCode.unparse(0);
    }
}
