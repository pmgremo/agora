package agora.attributes;

import java.util.*;
import java.io.*;
import agora.errors.*;
import agora.objects.*;
import agora.tools.*;
import agora.grammar.*;
import agora.runtime.*;
import agora.patterns.*;

/**
   This is the Agora attribute corresponding to cloning methods.
   @author Wolfgang De Meuter (Programming Technology Lab).
	Last change:  E    16 Nov 97    2:09 pm
*/
public class CloningAttribute extends MethAttribute implements Serializable
{
  /**
     Creates a new cloning method with the array of strings as formal arguments and the 
     given expression as body of the cloning method.
     @param formals The list of formal arguments of the cloning method.
     @param body The body expression of the cloning method.
  */
  public CloningAttribute (String[] formals,Expression body)
    {
      super(formals,body);
    }
  
  /**
     Executing a cloning method consists of making a clone of the receiver, adding
     local calling frames and evaluating the body of the cloning method in the
     context of these new method frames.
     @param msg The message whose delegation gave rise to the invocation.
     @param client The client object containing the actual arguments.
     @param context The context of the object in which this attribute resides.
     @exception agora.errors.AgoraError When something goes wrong during evaluation.
  */
  public AgoraObject doAttributeValue(AbstractPattern msg,Client client,Context context) throws AgoraError
    {
      if (Category.contains(context.getCategory(),Category.dontClone))
	return this.methodCode.eval(context);
      else
	{
	  Hashtable cloningMap = new Hashtable(3);
	  // Prevent root from beeing cloned
	  cloningMap.put(AgoraGlobals.glob.rootParent,AgoraGlobals.glob.rootParent);
	  cloningMap.put(AgoraGlobals.glob.rootPrivate,AgoraGlobals.glob.rootPrivate);
	  cloningMap.put(AgoraGlobals.glob.rootIdentity,AgoraGlobals.glob.rootIdentity);
	  // clone object and temporary scopes (may be added to the object in the method body)
	  IdentityGenerator clone      = (IdentityGenerator)context.getSelf().copy(cloningMap);
	  InternalGenerator privclone  = (InternalGenerator)context.getPrivate().copy(cloningMap);
	  MethodsGenerator  pubclone   = (MethodsGenerator)context.getPub().copy(cloningMap);
	  AbstractGenerator superclone = (AbstractGenerator)context.getParent().copy(cloningMap);
	  // actual parameters binding
	  privclone = this.bind(client.getActuals(),privclone);
	  // evaluate cloning method
	  this.methodCode.eval(context.setMultiple(clone,
						   privclone,
						   pubclone,
						   Category.dontClone,
						   superclone));
	  return (clone.wrap());
	}
    }
  
  /**
     Converts the attribute to a string.
  */

  public String toString()
    {
      return "CLONING METHOD:";
    }

  /**
     @param context The context of the object in which this attribute resides.
     @exception agora.errors.AgoraError When something goes wrong while inspecting.
     @return The attribute as a string, ready to be displayed in the inspector.
  */
  public String inspect(Context context) throws AgoraError
    {
      return "CLONING:\n" + this.methodCode.unparse(0);
    }
}


