package agora.tools;

import agora.attributes.PrimReifierMethAttribute;
import agora.attributes.VarGetAttribute;
import agora.attributes.VariableContainer;
import agora.errors.AgoraError;
import agora.errors.PrimException;
import agora.javaAdditions.JV_Package;
import agora.objects.*;
import agora.patterns.UnaryPattern;
import agora.patterns.UnaryReifierPattern;
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

    public Up up;

    /**
     * The top level window of Agora
     */
    public Frame window;
    /**
     * The root private part.
     */
    public InternalGenerator rootPrivate;

    /**
     * The root parent part. This is an empty generator.
     */
    public Generator rootParent;

    /**
     * The root identity, i.e. the identity of SELF in the global Agora System.
     */
    public IdentityGenerator rootIdentity;

    /**
     * This constructor must be called at System startup time. It creates the
     * global variables, and fills the root objects with the standardly
     * available methods.
     *
     * @param applet       The Agora applet or null if it concerns the application.
     * @param window The top level window in which Agora runs.
     * @throws agora.errors.AgoraError When something goes wrong during initialisation of the root
     *                                 object (e.g. when a bug exists in some upping procedure or so...).
     */
    public AgoraGlobals(Applet applet, Frame window) throws AgoraError {
        glob = this;

        // Construct The Root Object
        var publicTop = new EmptyGenerator("Root Public Super");
        var privateTop = new EmptyGenerator("Root Private Super");
        var rootPublic = new InternalGenerator("Root Public", new Hashtable<>(3), null, publicTop);
        var privateRoot = new InternalGenerator("Root Private", new Hashtable<>(3), null, privateTop);
        rootPublic.setPrivate(privateRoot);
        privateRoot.setPrivate(privateRoot);
        rootParent = publicTop;
        rootPrivate = privateRoot;
        rootIdentity = new UserIdentityGenerator("Root Object", rootPublic, null);

        up = new Up();
        this.window = window;

        // Fill the ROOT object with the standard methods
        // java
        var java = new UnaryPattern("java");
        var javaPackage = new VariableContainer(up.up(new JV_Package("java")));
        var javaReader = new VarGetAttribute(javaPackage);
        rootPrivate.installPattern(java, javaReader);

        //null
        var nil = new UnaryPattern("null");
        var nilObject = new VariableContainer(up.up(null));
        var nilReader = new VarGetAttribute(nilObject);
        rootPrivate.installPattern(nil, nilReader);

        //true
        var trueP = new UnaryPattern("true");
        var trueObject = new VariableContainer(up.up(true));
        var trueReader = new VarGetAttribute(trueObject);
        rootPrivate.installPattern(trueP, trueReader);

        //false
        var falseP = new UnaryPattern("false");
        var falseObject = new VariableContainer(up.up(false));
        var falseReader = new VarGetAttribute(falseObject);
        rootPrivate.installPattern(falseP, falseReader);

        //agora
        var agoraP = new UnaryPattern("agora");
        var agoraObject = new VariableContainer(rootIdentity.wrap());
        var agoraReader = new VarGetAttribute(agoraObject);
        rootPrivate.installPattern(agoraP, agoraReader);

        //applet
        var appletP = new UnaryPattern("applet");
        var appletObject = new VariableContainer(up.up(applet));
        var appletReader = new VarGetAttribute(appletObject);
        rootPrivate.installPattern(appletP, appletReader);

        //primitive
        var primitiveP = new UnaryPattern("primitive");
        rootPublic.installPattern(primitiveP, falseReader);

        //SELF
        try {
            var selfP = new UnaryReifierPattern("SELF");
            var selfMeth = new PrimReifierMethAttribute(Context.class.getMethod("Self", Context.class));
            rootPrivate.installPattern(selfP, selfMeth);
        } catch (Throwable e) {
            throw new PrimException(e, "AgoraGlobals::standardMethods");
        }

        // inspect
        try {
            var inspect = new UnaryPattern("inspect");
            var inspectMeth = new PrimReifierMethAttribute(Context.class.getMethod("inspectPrimitive"));
            rootPublic.installPattern(inspect, inspectMeth);
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
            var appletObject = new VariableContainer(up.up(applet));
            var appletReader = new VarGetAttribute(appletObject);
            rootPrivate.getHashTable().put(appletPat, appletReader);
        } catch (AgoraError ex) {
            java.lang.System.out.println("A SERIOUS SYSTEM ERROR HAS OCCURED:updateApplet");
        }
    }
}
