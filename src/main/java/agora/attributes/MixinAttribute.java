package agora.attributes;

import agora.errors.*;
import agora.objects.*;
import agora.runtime.*;
import agora.grammar.*;
import agora.patterns.*;
import java.io.*;


/**
   These are the Agora mixin agora.attributes, formerly known as imperative mixin methods.
   @author Wolfgang De Meuter (Programming Technology Lab).
	Last change:  E    16 Nov 97    2:00 am
*/
public class MixinAttribute extends MethAttribute implements Serializable
{
  /**
     Creates a new mixin attribute with the array of strings as formal arguments
     and the given expression as method body.
     @param formals The list of formal arguments of the mixin-method.
     @param body The body expression of the mixin-method.
  */
  public MixinAttribute (String[] formals,Expression body)
    {
      super(formals,body);
    }
  
  /**
     Executing a mixin corresponds to making new public and private slots,
     evaluating the body in this new context, and **updating** the object identity
     with these new values.
     @param msg The message whose delegation gave rise to the invocation.
     @param client The client object containing the actual arguments.
     @param context The context of the object in which this attribute resides.
     @exception agora.errors.AgoraError When something goes wrong during evaluation.
  */
  public AgoraObject doAttributeValue(AbstractPattern msg,Client client,Context context) throws AgoraError
    {
      var mixinPriv = this.bind(client.getActuals(),context.getPrivate());
      mixinPriv = mixinPriv.funcAddLayer("Local part of mixin " + msg.toString());
      mixinPriv.setPrivate(mixinPriv);
      var mixinPub = context.getSelf().getMe().funcAddLayer("Public part of mixin " + msg.toString());
      context.getSelf().Change(mixinPub);
      mixinPub.setPrivate(mixinPriv);
      this.methodCode.eval(context.setMultiple(context.getSelf(),
					    mixinPriv,
					    mixinPub,
					    context.getCategory(),
					    context.getSelf()));
      return context.getSelf().wrap();
    }
  
  /**
     Inpspects the attribute in a given context.
     @param context The context of the object in which this attribute resides.
     @exception agora.errors.AgoraError When something goes wrong while inspecting.
     @return The attribute as a string, ready to be displayed in the inspector.
  */
  public String inspect(Context context) throws AgoraError
    {
      return "MIXIN:\n" + this.methodCode.unparse(0);
    }

  /**
     Converts the attribute into a string.
  */
  public String toString()
    {
      return "MIXIN:";
    }
}

