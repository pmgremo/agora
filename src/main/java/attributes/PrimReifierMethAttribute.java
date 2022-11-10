package attributes;

import tools.*;
import java.lang.reflect.*;
import java.io.*;
import errors.*;
import runtime.*;
import reflection.*;
import patterns.*;
import objects.*;

/**
   A primitive reifier method is a reifier method whose implementation is in some
   method hardcoded in the Context. An example is the SELF.
	Last change:  E    17 Nov 97    1:30 am
*/
public class PrimReifierMethAttribute extends PrimAttribute implements Serializable
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
	    java.lang.System.out.println("NATIVE SYSTEM ERROR IN READING METHOD(nosuchmethod)");
	  }
	catch(ClassNotFoundException error)
          {
	    java.lang.System.out.println("NATIVE SYSTEM ERROR IN READING METHOD(nosuchclass)");
	  }
     }

  /**
     Creates a new primitive reifier method attribute. The java method parameter is
     supposed to be a method defined on context objects.
     @param javaMethod The method corresponding to the primitive reifier. Its first
     argument is supposed to be a context parameter. In this argument,
     the context of invocation will be passed to the implementation of the reifier.
  */
  public PrimReifierMethAttribute (Method javaMethod)
    {
      m = javaMethod;
    }
  
  /**
     Executing a primitive reifier method consists of invoking the associated
     java method (stored in the attribute) on the context object, with the
     arguments as present in the client object.
     @param msg The message whose delegation gave rise to the invocation.
     @param client The client object containing the actual arguments.
     @param context The context of the object in which this attribute resides.
     @exception errors.AgoraError When something goes wrong during evaluation.
  */
  public AgoraObject doAttributeValue(AbstractPattern msg,Client client,Context context) throws AgoraError
    {
      try
	{
	  return Up.glob.up(this.m.invoke(context,client.makeNativeArguments()));
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
	    throw (new PrimException(e.getTargetException(),"PrimReifierMethAttribute::doAttributeValue"));
	}
    }    
  
  /**
     Converts the attribute into a string.
  */
  public String toString()
    {
      return "PRIM REIFIER METHOD:";
    }
  
  /**
     Inspects the attribute in a given context.
     @param context The context of the object in which this attribute resides.
     @exception errors.AgoraError When something goes wrong while inspecting.
     @return The attribute as a string, ready to be displayed in the inspector.
  */
  public String inspect(Context context) throws AgoraError
    {
      return "Primitive Reifier (Context Dependent Method)";
    }
}
