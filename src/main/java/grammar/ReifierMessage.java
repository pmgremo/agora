package grammar;

import reflection.*;
import patterns.*;
import objects.*;
import errors.*;
import runtime.*;
import tools.*;
import java.io.*;

/**
   Representation of reifier message expressions.
	Last change:  E    16 Nov 97    2:17 pm
*/
public class ReifierMessage extends Message implements Serializable
{
  /**
     Does nothing but calling the super constructor.
     @param receiver The expression denoting the receiving expression of the reifier message.
     @param pattern The name of the reifier message and the actual arguments.
  */
  public ReifierMessage (Expression receiver,ReifPattern pattern)
    {
      super(receiver,pattern);
    }
  
  /** 
      Evaluates the reifier message in a given environment.
      @param context The environment in which the reifier message must be evaluated.
      @return The Agora object corresponding to sending the reifier.
      @exception errors.AgoraError When something goes wrong during evaluation.
  */
  public AgoraObject eval(Context context) throws AgoraError
    { 
      try
	{
	  AgoraIO.checkEscape();
	  AgoraObject     receiver = Up.glob.up(this.receiver);
	  Client          client   = this.pattern.makeClient(context,receiver);
	  AbstractPattern pattern  = this.pattern.makePattern(context);
	  client.actualsUp();
	  return (AgoraObject)receiver.send(pattern,client).down();
	  //The result of downing the result is surely an Agora object. This is where the dynamic
	  //typing of Agora meets the static typing of Java => Impossible to remove the cast.
	}
      catch (AgoraError ex)
	{
	  ex.setCode(this);
	  throw ex;
	}
    }
}
