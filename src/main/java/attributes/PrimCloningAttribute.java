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
   A primitive cloning attribute is actually a Java constructor.
   @author Wolfgang De Meuter (Programming Technology Lab)
	Last change:  E    17 Nov 97    0:01 am
*/
public class PrimCloningAttribute extends PrimAttribute implements Serializable
{	
  
  protected Constructor c;

  private void writeObject(ObjectOutputStream stream) throws IOException
    {
       stream.writeObject(c.getDeclaringClass().getName());
       Class[] types = c.getParameterTypes();
       stream.writeInt(types.length);
       for ( int i=0;i<types.length ;i++ )
	  stream.writeObject(types[i].getName());
    }
  private void readObject(ObjectInputStream stream) throws IOException
    {
	String argName = "";
	Class[] sig = null;
	Class decl = null;
	int ln = 0;
	try
	  {
	    decl    = Class.forName((String)stream.readObject());
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
	        else
                  sig[i] = Class.forName(argName);
	    }
	    c = decl.getConstructor(sig);
	  }
	catch(NoSuchMethodException error)
          {
	    java.lang.System.out.println("NATIVE SYSTEM ERROR IN READING CONSTRUCTOR(nosuchconstructor)");
 	  }
	catch(ClassNotFoundException error)
	  {
	    java.lang.System.out.println("NATIVE SYSTEM ERROR IN READING CONSTRUCTOR(nosuchclass");
	  }
     }

  /**
     Creates a primitive cloning attribute corresponding to the given constructor.
     @param c The native Java constructor corresponding to this attribute.
  */
  public PrimCloningAttribute (Constructor c)
    {
      super();
      this.c = c;
    }
  
  /**
     Executing the cloning method consists of invoking the constructor with the downed arguments.
     The newly created object is upped into Agora.
     @param msg The message whose delegation gave rise to the invocation.
     @param client The client object containing the actual arguments.
     @param context The context of the object in which this attribute resides.
     @exception errors.AgoraError When something goes wrong during evaluation.
  */
  public AgoraObject doAttributeValue(AbstractPattern msg,Client client,Context context) throws AgoraError
    {
      try
	{
	  client.actualsDown();
	  return Up.glob.up(c.newInstance(client.getActuals()));
	}
      catch(IllegalAccessException e)
	{
	  throw (new ProgramError("Illegal Access Exception when invoking constructor"));
	}
      catch(InvocationTargetException e)
	{
	  if (e.getTargetException() instanceof AgoraError)
	    throw ((AgoraError)e.getTargetException());
	  else
	    throw (new PrimException(e.getTargetException(),"PrimCloningMethAttribute::doAttributeValue"));
	}
      catch(InstantiationException e)
	{
	  throw (new ProgramError("Primitive CLoning Method (constructor) could not instantiate"));
	}
    }
  
  /**
     Converts the attribute into a string.
  */
  public String toString()
    {
      return "PRIMITIVE CLONING:";
    }
  
  /**
     Inspects the attribute in a given context.
     @param context The context of the object in which this attribute resides.
     @exception errors.AgoraError When something goes wrong while inspecting.
     @return The attribute as a string, ready to be displayed in the inspector.
  */
  public String inspect(Context context) throws AgoraError
    {
      return "Primitive Java Constructor";
    }
}
