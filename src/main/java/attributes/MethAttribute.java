package attributes;

import grammar.*;
import errors.*;
import runtime.*;
import objects.*;
import tools.*;
import javaAdditions.*;
import patterns.*;
import java.util.*;
import java.io.*;

/**
   A method attribute represents an ordinary Agora method. Subclasses of this class
   exist such as view, mixin, cloning and so on. These subclasses must only
   override the way the are executed in 'doAttributeValue'.
   @autor Wolfgang De Meuter (Programming Technology Lab).
	Last change:  E    16 Nov 97    1:34 am
*/
public class MethAttribute extends Attribute implements Serializable
{
  protected Expression methodCode;
  
  protected String[] formals;
  
  /**
     Creates a new Agora method with the array of strings as formal arguments
     and the given expression as method body.
     @param formals The formal arguments of the method.
     @param body The body expression of the method.
  */
  public MethAttribute (String[] formals,Expression body)
    {
      super();
      this.formals = formals;
      this.methodCode = body;
    }
  
  /**
     Executing a new method consists of making a local view on the local and private
     (being a new frame for lexical scoping) and evaluating the method code in the
     context of this frame. The original context is not touched.
     @param msg The message whose delegation gave rise to the invocation.
     @param client The client object containing the actual arguments.
     @param context The context of the object in which this attribute resides.
     @exception errors.AgoraError When something goes wrong during evaluation.
  */
  public AgoraObject doAttributeValue(AbstractPattern msg,Client client,Context context) throws AgoraError
    {
      InternalGenerator localPriv = this.bind(client.getActuals(),context.getPrivate());
      InternalGenerator localPub  = context.getPub().funcAddLayer("Calling Frame of:"+msg.toString());
      localPub.setPrivate(localPriv);
      return this.methodCode.eval(context.setMultiple(context.getSelf(),
						      localPriv,
						      localPub,
						      context.getCategory(),
						      context.getParent()));
    }
  
  /**
     This method is called from within 'doAttributeValue'. It takes a private part and an array
     of actual parameters. It adds a local frame to make the formals-actuals bindings and returns
     this one.
     @param actuals The list of actual parameters of type Object: can be evaluated or not.
     @param privPart The private part to which the new frame must be attached.
     @return A new frame linked to the private part. It contains the bindings.
     @exception errors.AgoraError Something can always go wrong (Murphy!).
  */
  public InternalGenerator bind(Object[] actuals,InternalGenerator privPart) throws AgoraError
    {
      int size = actuals.length;
      InternalGenerator bindingsPriv = privPart.funcAddLayer("Formals Actuals Frame");
      bindingsPriv.setPrivate(bindingsPriv);
      for (int i=0; i<size; i++)
	{
	  VariableContainer var     = new VariableContainer((AgoraObject)(actuals[i]));
	  // Impossible to avoid ugly type cast, unless clients become horribly inefficient (see 'doAttributeValue')
	  VarGetAttribute   readatt = new VarGetAttribute(var);
	  VarSetAttribute   writeatt= new VarSetAttribute(var);
	  UnaryPattern      readpat = new UnaryPattern(this.formals[i]);
	  KeywordPattern    writepat= readpat.makeWritePattern();
	  bindingsPriv.installPattern(readpat,readatt);
	  bindingsPriv.installPattern(writepat,writeatt);
	}
      return bindingsPriv;
    }
  
  /**
     Converts the attribute to a string.
  */
  public String toString()
    {
      return "METHOD:";
    }
  
  /**
     Inspects the method attribute in a given context.
     @param context The context of the object in which this attribute resides.
     @exception errors.AgoraError When something goes wrong while inspecting.
     @return The attribute as a string, ready to be displayed in the inspector.
  */
  public String inspect(Context context) throws AgoraError
    {
      return "METHOD:\n"+this.methodCode.unparse(0);
    }
  
  /**
     All (! see final!) methods are cloned by simply returing them. This is because methods
     are re-entrant by the context used in 'doAttributeValue'.
     @param cloneMap A table of already-copied-things such that nothins is copied twice.
     @return A copy of this attribute.
  */
  public final Object copy(Hashtable cloneMap)
    {
      return this;
    }
}
