package attributes;

import errors.*;
import objects.*;
import runtime.*;
import tools.*;
import grammar.*;
import patterns.*;
import java.io.*;

/**
   This class represents view attributes (formerly known as functional mixin methods)
   Evaluation of a view consists of creating a view on the public and the 
   local, and evaluating the body of the view method in the context of this view.
   @author Wolfgang De Meuter (Programming Technology Lab)
	Last change:  E    16 Nov 97    1:34 am
*/
public class ViewAttribute extends MethAttribute implements Serializable
{
  /**
     Create a new view attribute with an array of strings as formals parameter
     and a given expression as the method body of the view.
     @param formals The formal attributes to be bound upon invocation.
     @param body The body of the view.
  */
  public ViewAttribute (String[] formals,Expression body)
    {
      super(formals,body);
    }
  
  /**
     Execute a view method in a given context. The second client argument is a client
     containing the actual arguments given when the message was sent.
     The first pattern argument is the name of the message that invoked the view.
     @param msg The message whose delegation gave rise to the invocation.
     @param client The client object containing the actual arguments.
     @param context The context of the object in which this attribute resides.
     @exception errors.AgoraError When something goes wrong during evaluation.
  */
  public AgoraObject doAttributeValue(AbstractPattern msg,
				      Client client, 
				      Context context) throws AgoraError
    {
      InternalGenerator viewPriv = this.bind(client.getActuals(),context.getPrivate());
      viewPriv = viewPriv.funcAddLayer("Local part of view " + msg.toString());
      viewPriv.setPrivate(viewPriv);
      IdentityGenerator viewSelf = context.getSelf().funcAddLayer("Public part of view " + msg.toString());
      MethodsGenerator  viewPub  = viewSelf.getMe();
      //We cannot avoid this cast. Normally, the public is a methodsgenerator, but in a view,
      //we know it is an internal generator, because only user created objects can be extended.
      viewPub.setPrivate(viewPriv);
      this.methodCode.eval(context.setMultiple(viewSelf,viewPriv,viewPub,context.getCategory(),context.getSelf()));
      return viewSelf.wrap();
    }
  
  /**
     Converts the view attribute to a string
  */
  public String toString()
    {
      return "             VIEW: ";
    }

  /**
     Inspects the attribute in a given context.
     @param context The context of the object in which this attribute resides.
     @exception errors.AgoraError When something goes wrong while inspecting.
     @return The attribute as a string, ready to be displayed in the inspector.
  */
  public String inspect(Context context) throws AgoraError
    {
      return "VIEW:\n" + this.methodCode.unparse(0);
    }
}
