package agora.grammar;

import java.util.*;
import agora.runtime.*;
import agora.patterns.*;
import agora.errors.*;
import agora.objects.*;
import agora.attributes.*;
import agora.reflection.*;
import java.io.*;

/**
   This abstract class represents agora.patterns for ordinary messages. It has subclasses
   for unary, operator and keyword agora.patterns.
   @author Wolfgang De Meuter (Programming Technology Lab)
	Last change:  E    16 Nov 97    2:04 pm
*/
abstract public class UserPattern extends Pattern implements Serializable
{ 
  
  /**
     Method to create an ad-hoc 'up' generator for the agora.objects of this class.
     @return A generator containing the agora.patterns and associated agora.attributes
     that a user pattern understands, from within Agora.
     @exception agora.errors.AgoraError When something goes wrong, e.g. when a method has been deleted from this
     class.
  */
  public static PrimGenerator generatorUserPattern() throws AgoraError
    {
      Hashtable table = new Hashtable(4);
      PrimGenerator result = new PrimGenerator("UserPattern",table,null);
      try
	{
	  Class[] argtypes1 = new Class[1];
	  argtypes1[0] = Class.forName("agora.runtime.Context");
	  Class thisOne = Class.forName("agora.grammar.UserPattern");
	  UnaryPattern unary = new UnaryPattern("RAISE");
	  unary.setReifier();
	  table.put(unary,new PrimMethAttribute(thisOne.getMethod("raise",argtypes1)));
	  unary = new UnaryPattern("SUPER");
	  unary.setReifier();
	  table.put(unary,new PrimMethAttribute(thisOne.getMethod("superReifier",argtypes1)));
	}
      catch (Throwable e)
	{
	  throw (new PrimException(e,"UserPattern::generatorUserPattern"));
	  // This is impossible simply because the method raise and superReifier are here
	}
      return result;
    }
  
  /**
    Implements the RAISE reifier. Raises an AgoraException with the receiving pattern as content.
    @param context The context of evaluation when RAISE was sent.
    @return The return value of the RAISE expression. This will never be returned since
    an exception is thrown.
    @exception agora.errors.AgoraError The exception raised.
    */
  public AgoraObject raise(Context context) throws AgoraError
  {
    KeywordPattern agoError = new KeywordPattern(1);
    agoError.atPut(0,"agoraError:");
    AbstractPattern runtimePat= this.makePattern(context);
    Client          theClient = this.makeClient(context,null);
    theClient.actualsEval(context);
    if (agoError.equals(runtimePat))
      {
	Object[] actuals = theClient.getActuals();
	if (((AgoraObject)actuals[0]).down() instanceof AgoraError)
	  {
	    throw (AgoraError)((AgoraObject)actuals[0]).down();
	  }
	else
	  throw new ProgramError("agoraError: is a reserved exception pattern. Its argument must be an exception.");
      }
    else
      {
	AgoraException theException = context.getException();
	theException.setPattern(runtimePat);
	theException.setClient(theClient);
	throw theException;
      }
  }
  
  /**
     Method to evaluate a user pattern.
     @param context The environment in which the user patter is evaluated. If this
     contains the 'flags' category, a new formal pattern is returned. Otherwise,
     the pattern is delegated to the private part of the context.
     @return The Agora Object associated with this pattern expression.
     @exception agora.errors.AgoraError When something goes wrong during evaluation.
  */
  public AgoraObject eval(Context context) throws AgoraError
    {
      try
	{
	  // Patterns in Flags Category Must Be Declared: Just Return a new pattern
	  if (Category.contains(context.getCategory(),Category.flags))
	    return Up.glob.up(new FormalsAndPattern(this.makeFormals(context),
                                                    this.makePattern(context),
					            Category.emptyCategory));
	  // Patterns in Other Categories Denote Accesses in the Local Part of An Object
	  else
	    {
	      Client client    = this.makeClient(context,context.getSelf().wrap());
	      client.actualsEval(context);
	      return context.getPrivate().delegate(this.makePattern(context),client,context);
	    }
	}
      catch (AgoraError ex)
	{
	  ex.setCode(this);
	  throw ex;
	}
    }

  /**
    Implementation of the SUPER reifier defined on user agora.patterns.
    @param context The evaluation context where SUPER was sent.
    @return The result of sending the SUPER reifier.
    @exception agora.errors.AgoraError When something went wrong (e.g. pattern is not in the super)
    */
  public AgoraObject superReifier(Context context) throws AgoraError
  {
    Client client           = this.makeClient(context,context.getSelf().wrap());
    AbstractPattern pattern = this.makePattern(context);
    client.actualsEval(context);
    return context.getParent().delegate(pattern,client,context);
  }
}

