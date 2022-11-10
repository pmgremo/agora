package agora.attributes;

import java.lang.reflect.*;
import java.io.*;
import agora.errors.*;
import agora.runtime.*;
import agora.reflection.*;
import agora.patterns.*;
import agora.objects.*;

/**
   This class represents a primitive public (non static) method in Java.
	Last change:  E    17 Nov 97    1:30 am
*/
public class PrimMethAttribute extends PrimAttribute implements Serializable
{
  protected Method m;

  private void writeObject(ObjectOutputStream stream) throws IOException
    {
       stream.writeObject(m.getDeclaringClass().getName());
       stream.writeObject(m.getName());
       Class[] types = m.getParameterTypes();
       stream.writeInt(types.length);
       for ( int i=0;i<types.length ;i++ )
	  stream.writeObject(types[i].getName());
    }
  private void readObject(ObjectInputStream stream) throws IOException
    {
	String metName = "";
	String argName = "";
	Class[] sig = null;
	Class decl = null;
	int ln = 0;
	try
	  {
	    decl    = Class.forName((String)stream.readObject());
	    metName = (String)stream.readObject();
	    ln      = stream.readInt();
	    sig     = new Class[ln];
	    for ( int i=0;i<ln ;i++ )
	    {
		argName = (String)stream.readObject();
	        if (argName.equals("int"))
	 	  sig[i] = java.lang.Integer.TYPE;
	        else if (argName.equals("boolean"))
		  sig[i] = java.lang.Boolean.TYPE;
	        else if (argName.equals("char"))
		  sig[i] = java.lang.Character.TYPE;
	        else if (argName.equals("short"))
		  sig[i] = java.lang.Short.TYPE;
	        else if (argName.equals("byte"))
		  sig[i] = java.lang.Byte.TYPE;
	        else if (argName.equals("float"))
		  sig[i] = java.lang.Float.TYPE;
	        else if (argName.equals("long"))
		  sig[i] = java.lang.Long.TYPE;
	        else if (argName.equals("double"))
		  sig[i] = java.lang.Double.TYPE;
	        else if (argName.equals("void"))
		  sig[i] = java.lang.Void.TYPE;
	        else
                  sig[i] = Class.forName(argName);
	    }
	    m = decl.getMethod(metName,sig);
	  }
	catch(NoSuchMethodException error)
          {
	    java.lang.System.out.println("NATIVE SYSTEM ERROR IN READING METHOD(nosuchmethod:PrimMethAttribute)");
	  }
	catch(ClassNotFoundException error)
          {
	    java.lang.System.out.println("NATIVE SYSTEM ERROR IN READING METHOD(nosuchclass:PrimMethAttribute)");
	  }
     }
 
  /**
     Creates a new PrimMethAttribute given the (non static) java method as argument.
     @param javaMethod The Java method to which this attribute is associated.
  */
  public PrimMethAttribute (Method javaMethod)
    {
      super();
      this.m = javaMethod;
    }
  
  /** 
      Executing a PrimMethAttribute consists of invoking the corresponding java method
      on the downed version of the receiver. The arguments are the downed versions of the actual
      Agora arguments. The result is back upped into Agora.
     @param msg The message whose delegation gave rise to the invocation.
     @param client The client object containing the actual arguments.
     @param context The context of the object in which this attribute resides.
     @exception agora.errors.AgoraError When something goes wrong during evaluation.
  */
  public AgoraObject doAttributeValue(AbstractPattern msg,Client client,Context context) throws AgoraError
    {
      try
	{
	  return Up.glob.up(this.m.invoke(context.getSelf().down(),client.makeNativeArguments()));
	}
      catch(IllegalAccessException e)
	{
	  throw (new ProgramError("Illegal Access Exception while accessing a primitive method"));
	}
      catch(InvocationTargetException e)
	{
	  if (e.getTargetException() instanceof AgoraError)
	    throw ((AgoraError)e.getTargetException());
	  else
	    throw (new PrimException(e.getTargetException(),"PrimMethAttribute::doAttributeValue"));
	}
      
    }
  
  /**
     Converts the attribute into a string.
  */
  public String toString()
    {
      return "PRIMITIVE METHOD";
    }

  /**
     Inspects the attribute in a given context.
     @param context The context of the object in which this attribute resides.
     @exception agora.errors.AgoraError When something goes wrong while inspecting.
     @return The attribute as a string, ready to be displayed in the inspector.
  */
  public String inspect(Context context) throws AgoraError
    {
      return "Primitive Java method: " + this.m.toString();
    }
}
