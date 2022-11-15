package agora.tools;

import agora.attributes.Attribute;
import agora.attributes.PrimReifierMethAttribute;
import agora.attributes.VarGetAttribute;
import agora.attributes.VariableContainer;
import agora.errors.AgoraError;
import agora.errors.PrimException;
import agora.javaAdditions.JV_Package;
import agora.objects.*;
import agora.patterns.UnaryPattern;
import agora.reflection.Up;
import agora.runtime.Context;

import java.applet.Applet;
import java.awt.*;
import java.io.Serializable;
import java.util.Hashtable;

/**
 * This class implements the global variables of the Agora System.
 * Amongst others, this includes the globally available root objects,
 * which are filled with native patterns by the routines in this class.
 *
 * @author Wolfgang De Meuter (Programming Technology Lab).
 * Last change:  E    17 Nov 97    1:31 am
 */
public class AgoraGlobals implements Serializable {

    /**
     * The only instance of this class. The only correct way of instantiating this
     * class is :<p>
     * AgoraGlobals.glob = new AgoraGlobals(this) where this is the applet.
     */
    public static AgoraGlobals glob;

    /**
     * The top level window of Agora
     */
    public Frame agoraWindow;
    /**
     * The root private part.
     */
    public InternalGenerator rootPrivate;

    /**
     * The root parent part. This is an empty generator.
     */
    public Generator rootParent;

    /**
     * The root public part, i.e. all the messages understood by SELF in the
     * global Agora System.
     */
    public MethodsGenerator rootPublic;

    /**
     * The root identity, i.e. the identity of SELF in the global Agora System.
     */
    public IdentityGenerator rootIdentity;

    /**
     * There is only one Agora 'null' object that will not be upped each time
     * the evaluator needs to return 'null'. Hence, we made it a unique
     * global variable.
     */
    public AgoraObject uppedNull;

    /**
     * This constructor must be called at System startup time. It creates the
     * global variables, and fills the root objects with the standardly
     * available methods.
     *
     * @param agora       The Agora applet or null if it concerns the application.
     * @param agoraWindow The top level window in which Agora runs.
     * @throws agora.errors.AgoraError When something goes wrong during initialisation of the root
     *                                 object (e.g. when a bug exists in some upping procedure or so...).
     */
    public AgoraGlobals(Applet agora, Frame agoraWindow) throws AgoraError {
        glob = this;

        // Construct The Root Object
        var pubTop = new EmptyGenerator("Root Public Super");
        var priTop = new EmptyGenerator("Root Private Super");
        var pubRoot = new InternalGenerator("Root Public", new Hashtable<>(3), null, pubTop);
        var priRoot = new InternalGenerator("Root Private", new Hashtable<>(3), null, priTop);
        pubRoot.setPrivate(priRoot);
        priRoot.setPrivate(priRoot);
        this.rootPublic = pubRoot;
        this.rootParent = pubTop;
        this.rootPrivate = priRoot;
        this.rootIdentity = new UserIdentityGenerator("Root Object", pubRoot, null);

        Up.glob = new Up();
        this.agoraWindow = agoraWindow;

        // Create heavily used constants (cache them for efficiency reasons)
        this.uppedNull = Up.glob.up(null);

        // Fill the ROOT object with the standard methods
        this.standardMethods(agora);
    }

    /**
     * This method fills the root public and private parts
     * with the standardly available agora.attributes.
     *
     * @param applet The applet itself that called this method.
     * @throws agora.errors.AgoraError Whenever something goes wrong during installation of the
     *                                 standard methods in the root. A bug is always possible because a lot of dynamic access
     *                                 of the underlying Java (and my own implementation) is used.
     */
    private void standardMethods(Applet applet) throws AgoraError {
        // java
        var java = new UnaryPattern("java");
        var javaPackage = new VariableContainer(Up.glob.up(new JV_Package("java")));
        Attribute javaReader = new VarGetAttribute(javaPackage);
        this.rootPrivate.installPattern(java, javaReader);

        //null
        var nil = new UnaryPattern("null");
        var nilobject = new VariableContainer(uppedNull);
        Attribute nilReader = new VarGetAttribute(nilobject);
        this.rootPrivate.installPattern(nil, nilReader);

        //true
        var trueP = new UnaryPattern("true");
        var trueObject = new VariableContainer(Up.glob.up(true));
        Attribute trueReader = new VarGetAttribute(trueObject);
        this.rootPrivate.installPattern(trueP, trueReader);

        //false
        var falseP = new UnaryPattern("false");
        var falseObject = new VariableContainer(Up.glob.up(false));
        Attribute falseReader = new VarGetAttribute(falseObject);
        this.rootPrivate.installPattern(falseP, falseReader);

        //agora
        var agoraP = new UnaryPattern("agora");
        var agoraObject = new VariableContainer(rootIdentity.wrap());
        Attribute agoraReader = new VarGetAttribute(agoraObject);
        this.rootPrivate.installPattern(agoraP, agoraReader);

        //applet
        var appletP = new UnaryPattern("applet");
        var appletObject = new VariableContainer(Up.glob.up(applet));
        Attribute appletReader = new VarGetAttribute(appletObject);
        this.rootPrivate.installPattern(appletP, appletReader);

        //primitive
        var primitiveP = new UnaryPattern("primitive");
        this.rootPublic.installPattern(primitiveP, falseReader);

        //SELF
        try {
            var selfP = new UnaryPattern("SELF");
            selfP.setReifier();
            var selfMeth = new PrimReifierMethAttribute(Context.class.getMethod("Self", Context.class));
            this.rootPrivate.installPattern(selfP, selfMeth);
        } catch (Throwable e) {
            throw new PrimException(e, "AgoraGlobals::standardMethods");
        }

        // inspect
        try {
            var inspect = new UnaryPattern("inspect");
            var inspectMeth = new PrimReifierMethAttribute(Context.class.getMethod("inspectPrimitive"));
            this.rootPublic.installPattern(inspect, inspectMeth);
        } catch (Throwable e) {
            throw new PrimException(e, "AgoraGlobals::standardMethods");
        }
    }

    /**
     * After loading an image, the ..applet.. global variable still refers to the old
     * applet from where the applet was saved. This method updates this global
     * variable. Hence, the method must be used whenever an image is loaded
     * from within an applet.
     */
    public void updateApplet(Applet applet) {
        try {
            var appletPat = new UnaryPattern("applet");
            var appletObject = new VariableContainer(Up.glob.up(applet));
            var appletReader = new VarGetAttribute(appletObject);
            this.rootPrivate.getHashTable().put(appletPat, appletReader);
        } catch (AgoraError ex) {
            java.lang.System.out.println("A SERIOUS SYSTEM ERROR HAS OCCURED:updateApplet");
        }
    }
}
