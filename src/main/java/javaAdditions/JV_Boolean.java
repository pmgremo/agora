package javaAdditions;

import patterns.*;
import errors.*;
import objects.*;
import attributes.*;
import runtime.*;
import java.lang.reflect.*;
import java.lang.*;
import java.util.*;
import java.io.*;

/**
  This class contains a number of methods that should have been included in
  java.lang.Boolean. Since the latter class is final, we can not add the methods
  in a subclass of java.lang.Boolean. So we wrote a number of procedures, in which we
  explicitely pass the receiver and the arguments.
	Last change:  E    16 Nov 97    1:47 am
  */
public class JV_Boolean extends Object  implements Serializable
{
  /**
    This method generates an ad-hoc generator for this class containing the logic
    operators linked to the methods of this class.
    @return The generator associated to the procedures of this class.
    @exception errors.AgoraError When something goes wrong, e.g. when a method
    has been deleted from this class or when its type changes without updating
    this procedure.
    */
  public static PrimGenerator generatorJV_Boolean() throws AgoraError
    {
      Hashtable table = new Hashtable(5);
      PrimGenerator result = new PrimGenerator("JV_Boolean",table,null);
      try
	{
	  Class[] argtypes2 = new Class[2];
	  argtypes2[1] = Class.forName("java.lang.Object");
	  argtypes2[0] = Class.forName("java.lang.Boolean");
	  Class thisOne = Class.forName("javaAdditions.JV_Boolean");
	  table.put(new OperatorPattern("||"),
		    new PrimFunctionAttribute(thisOne.getMethod("andB",argtypes2)));
	  table.put(new OperatorPattern("&&"),
		    new PrimFunctionAttribute(thisOne.getMethod("orB",argtypes2)));
	  table.put(new OperatorPattern("="),
		    new PrimFunctionAttribute(thisOne.getMethod("equalsB",argtypes2)));
	  Class[] argtypes1 = new Class[1];
	  argtypes1[0] = Class.forName("java.lang.Boolean");
	  table.put(new UnaryPattern("not"),
		    new PrimFunctionAttribute(thisOne.getMethod("notB",argtypes1)));
	}
      catch (ClassNotFoundException e)
	{
	  // This is impossible since all the adressed classes are simply there!!
	}
      catch (NoSuchMethodException e)
	{
	  // This is impossible since all the adressed methods are simply here!
	}
      return result;
    }
  
  /**
     And. Receiver is of type boolean, the argument must be boolean.
     @param receiver The boolean indicating the receiver of the method
     @param arg The object indicating the argument.
     @returns A new boolean being the 'and' of the receiver and the argument.
     @exception errors.AgoraError Is thrown when the argument is of wrong type.
  */
  public static Object andB(Boolean receiver, Object arg) throws AgoraError
    {
      if (arg instanceof Boolean)
	{
	  return (new Boolean(receiver.booleanValue() & ((Boolean)arg).booleanValue()));
	}
      else 
	{
	  throw (new ProgramError("Illegal Argument for &&"));
	}
    }
  
  /**
     Or. Receiver is of type boolean, the argument must be boolean.
     @param receiver The boolean indicating the receiver of the method
     @param arg The object indicating the argument.
     @returns A new boolean being the 'or' of the receiver and the argument.
     @exception errors.AgoraError Is thrown when the argument is of wrong type.
  */
  public static Object orB(Boolean receiver, Object arg) throws AgoraError
    {
      if (arg instanceof Boolean)
	{
	  return (new Boolean(receiver.booleanValue() | ((Boolean)arg).booleanValue()));
	}
      else
	{
	  throw (new ProgramError("Illegal Argument for ||"));
	}
    }

  /**
     Comparision =.
     The receiver must be boolean and the argument can be anything.
     @param receiver The boolean indicating the receiver of the method
     @param arg The object indicating the argument.
     @returns A new boolean being the comparision of the receiver and the argument.
     @exception errors.AgoraError Is thrown when the argument is of wrong type.
  */
  public static Object equalsB(Boolean receiver, Object arg) throws AgoraError
    {
      if (arg instanceof Boolean)
	{
	  return (new Boolean(receiver.booleanValue() == ((Boolean)arg).booleanValue()));
	}
      else 
	return (new Boolean(false));
    }
  
  /**
     Negation. Receiver must be boolean.
     @param receiver Is supposed to be a boolean object.
     @return The inverse of the receiver
     @exception errors.AgoraError Is never thrown in this particular case.
  */
  public static Object not(Boolean receiver) throws AgoraError
    {
      return (new Boolean(!((Boolean)receiver).booleanValue()));
    }
}
