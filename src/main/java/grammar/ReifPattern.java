package grammar;

import objects.*;
import patterns.*;
import errors.*;
import runtime.*;
import reflection.*;
import java.util.*;
import attributes.*;
import java.io.*;

abstract public class ReifPattern extends Pattern implements Serializable
{ 
  /**
     Method to create an ad-hoc 'up' generator for the objects of this class.
     @return The generator containing the patterns that can be sent to reifier patterns.
     @exception errors.AgoraError When something goes wrong while creating the generator
     (e.g. if a method has been deleted from this class).
	Last change:  E    16 Nov 97    2:25 pm
  */
  public static PrimGenerator generatorReifPattern() throws AgoraError
    {
      Hashtable table = new Hashtable(4);
      PrimGenerator result = new PrimGenerator("ReifPattern",table,null);
      try
	{
	  Class[] argtypes1 = new Class[1];
	  argtypes1[0] = Class.forName("runtime.Context");
	  Class thisOne = Class.forName("grammar.ReifPattern");
	  UnaryPattern unary = new UnaryPattern("SUPER");
	  unary.setReifier();
	  table.put(unary,new PrimMethAttribute(thisOne.getMethod("superReifier",argtypes1)));
	}
      catch (Throwable e)
	{
	  throw (new PrimException(e,"ReifPattern::generatorReifPattern"));
	  // This is impossible simply because the method superReifier is here
	}
      return result;
    }

  /**
     Method to evaluate a reifier pattern.
     @param context Environment in which the expression must be evaluated.
     @return The Agora value of the expression.
     @exception errors.AgoraError When something goes wrong during evaluation.
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
	      Client client    = this.makeClient(context,Up.glob.up(context));
	      client.actualsUp();
	      return (AgoraObject)context.getPrivate().delegate(this.makePattern(context),client,context).down();
	      //The result of downing the result is surely an Agora object. This is where the dynamic
	      //typing of Agora meets the static typing of Java => Impossible to remove the cast.
	    }
	}
      catch (AgoraError ex)
	{
	  ex.setCode(this);
	  throw ex;
	}
    }
  /**
    Implementation of the SUPER reifier for reifier patterns (e.g. SELF SUPER).
    @param context The evaluation context in which SUPER was sent.
    @return The result of executing the reifier on the parent object.
    @exception errors.AgoraError When something went wrong during delegation of the
    reifier pattern to the parent (e.g. when it is not there!).
  */
  public AgoraObject  superReifier(Context context) throws AgoraError
    {
      Client client    = this.makeClient(context,Up.glob.up(context));
      AbstractPattern pattern  = this.makePattern(context);
      client.actualsUp();
      return context.getParent().delegate(pattern,client,context);
    }
}
