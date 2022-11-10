package tools;

import objects.*;
import javaAdditions.*;
import reflection.*;
import java.util.*;
import errors.*;
import patterns.*;
import attributes.*;
import runtime.*;
import java.awt.*;
import java.applet.*;
import java.io.*;

/**
   This class implements the global variables of the Agora System.
   Amongst others, this includes the globally available root objects,
   which are filled with native patterns by the routines in this class.
   @author Wolfgang De Meuter (Programming Technology Lab).
	Last change:  E    17 Nov 97    1:31 am
*/
public class AgoraGlobals implements Serializable
{

  /**
     The only instance of this class. The only correct way of instantiating this
     class is :<p>
     AgoraGlobals.glob = new AgoraGlobals(this) where this is the applet.
  */
  public static AgoraGlobals glob;

  /**
     The top level window of Agora
  */
  public Frame agoraWindow;
  /**
     The root private part.
  */
  public InternalGenerator rootPrivate;
  
  /**
     The root parent part. This is an empty generator.
  */
  public AbstractGenerator rootParent;
  
  /**
     The root public part, i.e. all the messages understood by SELF in the
     global Agora System.
  */
  public MethodsGenerator rootPublic;
  
  /**
     The root identity, i.e. the identity of SELF in the global Agora System.
  */
  public IdentityGenerator rootIdentity;
  
  /**
     There is only one Agora 'null' object that will not be upped each time
     the evaluator needs to return 'null'. Hence, we made it a unique
     global variable.
  */
  public AgoraObject uppedNull;
  /**
     There is only one Boolean object 'true'. In order to save execution time,
     both objects (the Java version and the Agora version) are stored here,
     such that they will never be garbage collected and will never be constructed
     again. The same holds for 'false'.
  */
  Object ttrue;
  AgoraObject uppedTrue;
  Object ffalse;
  AgoraObject uppedFalse;
  
  /**
     This constructor must be called at System startup time. It creates the
     global variables, and fills the root objects with the standarly 
     available methods.
     @param agora The Agora applet or null if it concerns the application.
     @param agoraWindow The top level window in which Agora runs.
     @exception errors.AgoraError When something goes wrong during initialisation of the root
     object (e.g. when a bug exists in some upping procedure or so...).
  */
  public AgoraGlobals(Applet agora,Frame agoraWindow) throws AgoraError
    {
      glob = this;

      // Construct The Root Object
      EmptyGenerator pubTop = new EmptyGenerator("Root Public Super");
      EmptyGenerator priTop = new EmptyGenerator("Root Private Super");
      InternalGenerator pubRoot = new InternalGenerator("Root Public",new Hashtable(3),null,pubTop);
      InternalGenerator priRoot = new InternalGenerator("Root Private",new Hashtable(3),null,priTop);
      pubRoot.setPrivate(priRoot);
      priRoot.setPrivate(priRoot);
      this.rootPublic = pubRoot;
      this.rootParent = pubTop;
      this.rootPrivate = priRoot;
      this.rootIdentity = new UserIdentityGenerator("Root Object",pubRoot,null);

      Up.glob = new Up();
      this.agoraWindow = agoraWindow;

      // Create heavily used constants (cache them for efficiency reasons)
      this.uppedNull = Up.glob.up(null);
      JV_Nil dummy = new JV_Nil(); // This must be here to force class loading!!!
      this.ttrue = new Boolean(true);
      this.ffalse = new Boolean(false);
      this.uppedTrue = Up.glob.up(ttrue);
      this.uppedFalse = Up.glob.up(ffalse);
      
      Up.glob.fixBooleanCircularity();
      // Fill the ROOT object with the standard methods
      this.standardMethods(agora);
    }
  
  /**
     This method fills the root public and private parts
     with the standardly available attributes.
     @param agora The applet itself that called this method.
     @exception errors.AgoraError Whenever something goes wrong during installation of the
     standard methods in the root. A bug is always possible because a lot of dynamic access
     of the underlying Java (and my own implementation) is used.
  */
  private void standardMethods(Applet agora) throws AgoraError
    {
      // java
      UnaryPattern java = new UnaryPattern("java");
      VariableContainer javaPackage = new VariableContainer(Up.glob.up(new JV_Package("java")));
      Attribute javaReader = new VarGetAttribute(javaPackage);
      this.rootPrivate.installPattern(java,javaReader);
      
      //null
      UnaryPattern nil = new UnaryPattern("null");
      VariableContainer nilobject = new VariableContainer(uppedNull);
      Attribute nilReader = new VarGetAttribute(nilobject);
      this.rootPrivate.installPattern(nil,nilReader);
      
      //true
      UnaryPattern trueP = new UnaryPattern("true");
      VariableContainer trueObject = new VariableContainer(uppedTrue);
      Attribute trueReader = new VarGetAttribute(trueObject);
      this.rootPrivate.installPattern(trueP,trueReader);
      
      //false
      UnaryPattern falseP = new UnaryPattern("false");
      VariableContainer falseObject = new VariableContainer(uppedFalse);
      Attribute falseReader = new VarGetAttribute(falseObject);
      this.rootPrivate.installPattern(falseP,falseReader);
      
      //agora
      UnaryPattern agoraP = new UnaryPattern("agora");
      VariableContainer agoraObject = new VariableContainer(rootIdentity.wrap());
      Attribute agoraReader = new VarGetAttribute(agoraObject);
      this.rootPrivate.installPattern(agoraP,agoraReader);
      
      //applet
      UnaryPattern appletP = new UnaryPattern("applet");
      VariableContainer appletObject = new VariableContainer(Up.glob.up(agora));
      Attribute appletReader = new VarGetAttribute(appletObject);
      this.rootPrivate.installPattern(appletP,appletReader);

      //primitive
      UnaryPattern primitiveP = new UnaryPattern("primitive");
      this.rootPublic.installPattern(primitiveP,falseReader);
      
      //SELF
      UnaryPattern selfP = new UnaryPattern("SELF");
      selfP.setReifier();
      Attribute selfMeth = null;
      try
	{
	  Class[] args = new Class[1];
	  args[0]     =  Class.forName("runtime.Context");
	  selfMeth = new PrimReifierMethAttribute(Class.forName("runtime.Context").getMethod("Self",args));
	  this.rootPrivate.installPattern(selfP,selfMeth);
	}
      catch(Throwable e)
	{
	  throw (new PrimException(e,"AgoraGlobals::standardMethods"));
	}
      
      // inspect
      UnaryPattern inspect = new UnaryPattern("inspect");
      Attribute inspectMeth = null;
      try
	{
	  Class[] args = new Class[0];
	  inspectMeth = new PrimReifierMethAttribute(Class.forName("runtime.Context").getMethod("inspectPrimitive",args));
	  this.rootPublic.installPattern(inspect,inspectMeth);
	}
      catch(Throwable e)
	{
	  throw (new PrimException(e,"AgoraGlobals::standardMethods"));
	}
    }
  
  /**
     Given a native boolean, this method returns the corresponding wrapped Java version, which
     is cached in the globals class. Otherwise, the if-test executed in this code would be
     in the evaluator for about 100 times.
     @param b The boolean of which the cached version is required.
     @return The corresponding cached boolean.
  */
  public Object cachedBoolean(boolean b)
    {
      if (b)
	return ttrue;
      else
	return ffalse;
    }

  /**
     Given a native boolean, this method returns the correspoding upped version of the Java
     wrapper. Otherwise, the if-test executed in this code would be in the evaluator
     for about a 100 times.
     @param b The boolean of which the Agora version is required.
     @return The corresponding Agora version of the boolean.
  */
  public AgoraObject cachedUppedBoolean(boolean b)
    {
      if (b)
	return uppedTrue;
      else
	return uppedFalse;
    }

  /**
    After loading an image, the ..applet.. global variable still refers to the old
    applet from where the applet was saved. This method updates this global
    variable. Hence, the method must be used whenever an image is loaded
    from within an applet.
  */
  public void updateApplet(Applet applet)
   {
    try
    {
      UnaryPattern appletPat = new UnaryPattern("applet");
      UnaryPattern appletP = new UnaryPattern("applet");
      VariableContainer appletObject = new VariableContainer(Up.glob.up(applet));
      Attribute appletReader = new VarGetAttribute(appletObject);
      this.rootPrivate.getHashTable().put(appletPat,appletReader);
    }
    catch(AgoraError ex)
    {
	java.lang.System.out.println("A SERIOUS SYSTEM ERROR HAS OCCURED:updateApplet");
    }
   }
}
