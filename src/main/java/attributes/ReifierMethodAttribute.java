package attributes;

import java.util.*;
import patterns.*;
import objects.*;
import runtime.*;
import errors.*;
import grammar.*;
import reflection.*;
import tools.*;
import java.io.*;

/**
   This class represents reifier attributes written by the Agora programmer.
   @author Wolfgang De Meuter (Programming Technology Lab).
	Last change:  E    16 Nov 97    2:05 pm
*/
public class ReifierMethodAttribute extends MethAttribute implements Serializable
{
  private UnaryPattern contextPattern;
  
  /**
     Creates a new reifier method with an array of strings as formal arguments of the reifier,
     an expression denoting the reifier method body and a unary pattern denoting the name
     of the formal argument to which the context will be bound when executing the 
     reifier.
     @param formals The names of the formal arguments.
     @param body The body expression of the reifier.
     @param contName A unary pattern to which the runtime context will be bound.
  */
  public ReifierMethodAttribute (String[] formals,Expression body,UnaryPattern contName)
    {
      super(formals,body);
      this.contextPattern=contName;
    }
  
  /**
     Executing a user reifier attribute consists of making the binding between formals
     and actuals, adding an extra read-only variable for the context argument,
     evaluating the method code, and bringing the result back to the Agora level.
     @param msg The message whose delegation gave rise to the invocation.
     @param client The client object containing the actual arguments.
     @param context The context of the object in which this attribute resides.
     @exception errors.AgoraError When something goes wrong during evaluation.
  */
  public AgoraObject doAttributeValue(AbstractPattern msg, Client client,Context context) throws AgoraError
    {
      InternalGenerator localPriv  = this.bind(client.getActuals(),context.getPrivate());
      InternalGenerator localPub   = context.getPub().funcAddLayer("Formals-Actuals Frame");
      localPub.setPrivate(localPriv);
      localPriv.installPattern(this.contextPattern,
			       new VarGetAttribute(new VariableContainer(Up.glob.up(client.newContext()))));
      // We ask the client for a context. Because this is a reifier method, the client
      // is a reifier client, and thus client.newContext() is the context of invocation
      AgoraObject result = this.methodCode.eval(context.setMultiple(context.getSelf(),
								    localPriv,
								    localPub,
								    context.getCategory(),
								    context.getParent()));
      if (!(result.down() instanceof AgoraObject))
	throw (new ProgramError("Result of reifier method would yield an invalid base-level object"));
      else
	return result;
    }
  
  /**
     Converts the attribute to a string.
  */
  public String toString()
    {
      return "     REIFIER METHOD:";
    }

  /**
     Inspects the method attribute in a given context.
     @param context The context of the object in which this attribute resides.
     @exception errors.AgoraError When something goes wrong while inspecting.
     @return The attribute as a string, ready to be displayed in the inspector.
  */
  public String inspect(Context context) throws AgoraError
    {
      return "REIFIER METHOD:\n"+this.methodCode.unparse(0);
    }
  

}
