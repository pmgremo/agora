package agora.attributes;

import java.lang.reflect.*;
import java.io.*;
import agora.errors.*;
import agora.runtime.*;
import agora.reflection.*;
import agora.objects.*;
import agora.patterns.*;

/**
   Some Agora methods are represented by Java static methods in which the receiver
   is explicitely passed as an argument. This class represents these Agora methods.
   Examples are +, - and so on, defined on integers.
	Last change:  E    16 Nov 97    1:43 pm
*/
public class PrimFunctionAttribute extends PrimMethAttribute implements Serializable
{
  /**
     This constructor makes a method that corresponds to the given Java
     function (i.e.e static method). Hence, the argument is supposed to be
     a static Java method that takes the receiver as a first argument.
     @param javaMethod The Java method acting as a function. It's first argument
     is supposed to be the receiver of this attribute invocation.
  */
  public PrimFunctionAttribute (Method javaMethod)
    {
      super(javaMethod);
    }
  
  /**
     Executing an attribute like this consists of invoking the Java static method
     with null as receiver, and the downed Agora receiver as first argument.
     The other arguments are the downed Agora arguments. The result is upped
     into Agora again.
     @param msg The message whose delegation gave rise to the invocation.
     @param client The client object containing the actual arguments.
     @param context The context of the object in which this attribute resides.
     @exception agora.errors.AgoraError When something goes wrong during evaluation.
  */
  public AgoraObject doAttributeValue(AbstractPattern msg, 
				      Client client, 
				      Context context) throws AgoraError
    {
      try
	{
        var realActuals = client.getActuals();
        var fakedActuals = new Object[realActuals.length + 1];
	  fakedActuals[0] = context.getSelf().down();
	  for (var j = 0; j<realActuals.length; j++)
	    fakedActuals[j+1] = ((AgoraObject)realActuals[j]).down();
	  return Up.glob.up(this.m.invoke(null,fakedActuals));
	}
      catch(IllegalAccessException e)
	{
	  throw (new ProgramError("IllegalAccesException while accessing a primitive function method"));
	}
      catch(InvocationTargetException e)
	{
	  if (e.getTargetException() instanceof AgoraError)
	    throw ((AgoraError)e.getTargetException());
	  else
	    throw (new PrimException(e.getTargetException(),"PrimFunctionAttribute::doAttributeValue"));
	}
    }
  
  /**
     Converts the attribute into a string.
  */
  public String toString()
    {
      return "PRIMITIVE FUNCTION";
    }
  
  /**
     Inspects the attribute in a given context.
     @param context The context of the object in which this attribute resides.
     @exception agora.errors.AgoraError When something goes wrong while inspecting.
     @return The attribute as a string, ready to be displayed in the inspector.
  */
  public String inspect(Context context) throws AgoraError
    {
      return "Primitive Static Java Method (Function)";
    }
}
