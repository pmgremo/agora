package attributes;

import tools.*;
import java.lang.reflect.*;
import java.io.*;
import errors.*;
import runtime.*;
import reflection.*;
import objects.*;
import patterns.*;

/**
   A PrimStaticMethttribute is an 'Agorification' of a class method
   in Java.
   @author Wolfgang De Meuter(Programming Technology Lab).
	Last change:  E    16 Nov 97    1:42 pm
*/
public class PrimStaticMethAttribute extends PrimMethAttribute implements Serializable
{
  /**
     Create a new PrimStaticMethAttribute. The Method parameter is
     assumed to be a static Java method.
     @param javaMethod The static Java method corresponding to this attribute.
  */
  public PrimStaticMethAttribute (Method javaMethod)
    {
      super(javaMethod);
    }
  
  /**
     Executing a PrimStaticMethAttribute consists of invoking the corresponding static method
     with the arguments that reside in the client parameter. The receiver is ignored if it is there.
     @param msg The message whose delegation gave rise to the invocation.
     @param client The client object containing the actual arguments.
     @param context The context of the object in which this attribute resides.
     @exception errors.AgoraError When something goes wrong during evaluation.
  */
  public AgoraObject doAttributeValue(AbstractPattern msg, 
				      Client client, 
				      Context context) throws AgoraError
    {
      try
	{
	  return Up.glob.up(this.m.invoke(null,client.makeNativeArguments()));
	}
      catch(IllegalAccessException e)
	{
	  throw (new ProgramError("IllegalAccesException while accessing a primitive static method"));
	}
      catch(InvocationTargetException e)
	{
	  if (e.getTargetException() instanceof AgoraError)
	    throw ((AgoraError)e.getTargetException());
	  else
	    throw (new PrimException(e.getTargetException(),"PrimStaticMethAttribute::doAttributeValue"));
	}
    }
  
  /**
     Converts the attribute to a string.
  */
  public String toString()
    {
      return "PRIMITIVE STATIC METHOD: (Java static method)";
    }

  /**
     Inspects the attribute in a given context.
     @param context The context of the object in which this attribute resides.
     @exception errors.AgoraError When something goes wrong while inspecting.
     @return The attribute as a string, ready to be displayed in the inspector.
  */
  public String inspect(Context context) throws AgoraError
    {
      return "Primitive Static Java Method";
    }
  
}
