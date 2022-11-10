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
   An Agora float will be programmed as an 'up'ed Java float and all Agora
   messages on floats will be forwarded to the underlying Java messages.
   However, java.lang.Float does not contain messages like +, and it is
   forbidden to make a subclass of java.lang.Float to implement +.
   That's why we implement a number of operators as procedures in
   this class. When a message is sent to an Agora float, and it is
   in the java.lang.Float class, it will be forwarded to that method.
   Otherwise, it will be invoked here and its receiver will be passed
   as a procedure parameter. Pretty dirty, but it's the only way to make
   it work.
	Last change:  E    16 Nov 97    1:47 am
*/
public class JV_Float extends Object  implements Serializable
{
  /**
     This is the ad hoc generator creator for floats. This will be
     called when an float is upped by 'Up'. It returns
     a method table that links Agora messages to procedures
     defined in this class.
     @return An ad hoc generator containing patterns linked to the procedures
     of this class.
     @exception errors.AgoraError When something goes wrong while building the generator
     (e.g. when you remove a method from this class, without updating this procedure).
  */
  public static PrimGenerator generatorJV_Float() throws AgoraError
    {
      Hashtable table = new Hashtable(25);
      PrimGenerator result = new PrimGenerator("JV_Float",table,null);
      try
	{
	  Class[] argtypes2 = new Class[2];
	  argtypes2[1] = Class.forName("java.lang.Object");
	  argtypes2[0] = Class.forName("java.lang.Float");
	  Class thisOne = Class.forName("javaAdditions.JV_Float");
          table.put(new OperatorPattern("+"),
		    new PrimFunctionAttribute(thisOne.getMethod("plus",argtypes2)));
	  table.put(new OperatorPattern("-"),
		    new PrimFunctionAttribute(thisOne.getMethod("min",argtypes2)));
	  table.put(new OperatorPattern("*"),
		    new PrimFunctionAttribute(thisOne.getMethod("mult",argtypes2)));
	  table.put(new OperatorPattern("/"),
		    new PrimFunctionAttribute(thisOne.getMethod("divide",argtypes2)));
	  table.put(new OperatorPattern("^"),
		    new PrimFunctionAttribute(thisOne.getMethod("power",argtypes2)));
	  table.put(new OperatorPattern("="),
		    new PrimFunctionAttribute(thisOne.getMethod("equalsF",argtypes2)));
	  table.put(new OperatorPattern("<"),
		    new PrimFunctionAttribute(thisOne.getMethod("smF",argtypes2)));
	  table.put(new OperatorPattern(">"),
		    new PrimFunctionAttribute(thisOne.getMethod("gtF",argtypes2)));
	  table.put(new OperatorPattern("<="),
		    new PrimFunctionAttribute(thisOne.getMethod("smeF",argtypes2)));
	  table.put(new OperatorPattern(">="),
		    new PrimFunctionAttribute(thisOne.getMethod("gteF",argtypes2)));
	  Class[] argtypes1 = new Class[1];
	  argtypes1[0] = Class.forName("java.lang.Float");
	  table.put(new UnaryPattern("abs"),
		    new PrimFunctionAttribute(thisOne.getMethod("abs",argtypes1)));
	  table.put(new UnaryPattern("sqrt"),
		    new PrimFunctionAttribute(thisOne.getMethod("sqrt",argtypes1)));
	  table.put(new UnaryPattern("sqr"),
		    new PrimFunctionAttribute(thisOne.getMethod("sqr",argtypes1)));
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
     Addition. Receiver is of type float, the argument can be integer or float.
     @param receiver The float indicating the receiver of the method
     @param arg The object indicating the argument.
     @returns A new float being the '+' of the receiver and the argument.
     @exception errors.AgoraError Is thrown when the argument is of wrong type.
  */
  public static Object plus(Float receiver, Object arg) throws AgoraError
    {
      if (arg instanceof Integer)
	{
	  return (new Float(receiver.floatValue() + ((Integer)arg).intValue()));
	}
      else 
	if (arg instanceof Float)
	  {
	    return (new Float(receiver.floatValue() + ((Float)arg).floatValue()));
	  }
	else
	  {
	    throw (new ProgramError("Illegal Argument for +"));
	  }
    }
  
  /**
     Substraction.
     The receiver is a float. The argument can be an integer or a float.
     @param receiver The float indicating the receiver of the method
     @param arg The object indicating the argument.
     @returns A new float being the '-' of the receiver and the argument.
     @exception errors.AgoraError Is thrown when the argument is of wrong type.
  */
  public static Object min(Float receiver, Object arg) throws AgoraError
    {
      if (arg instanceof Integer)
	{
	  return (new Float(receiver.floatValue() - ((Integer)arg).intValue()));
	}
      else 
	if (arg instanceof Float)
	  {
	  return (new Float(receiver.floatValue() - ((Float)arg).floatValue()));
	  }
	else
	  {
	    throw (new ProgramError("Illegal Argument for -"));
	  }
    }

  /**
     Multiplication.
     The receiver is a float, the argument can be integer or float.
     @param receiver The float indicating the receiver of the method
     @param arg The object indicating the argument.
     @returns A new float being the '*' of the receiver and the argument.
     @exception errors.AgoraError Is thrown when the argument is of wrong type.
  */
  public static Object mult(Float receiver, Object arg) throws AgoraError
    {
      if (arg instanceof Integer)
	{
	  return (new Float(receiver.floatValue() * ((Integer)arg).intValue()));
	}
      else 
	if (arg instanceof Float)
	  {
	    return (new Float(receiver.floatValue() * ((Float)arg).floatValue()));
	  }
	else
	  {
	    throw (new ProgramError("Illegal Argument for *"));
	  }
    }

  /**
     Division.
     The receiver is a float, the argument can be an integer or a float.
     @param receiver The float indicating the receiver of the method
     @param arg The object indicating the argument.
     @returns A new float being the '/' of the receiver and the argument.
     @exception errors.AgoraError Is thrown when the argument is of wrong type.
     */
  public static Object divide(Float receiver, Object arg) throws AgoraError
    {
      if (arg instanceof Integer)
	{
	  return (new Float(receiver.floatValue() / ((Integer)arg).intValue()));
	}
      else 
	if (arg instanceof Float)
	  {
	    return (new Float(receiver.floatValue() / ((Float)arg).floatValue()));
	  }
	else
	  {
	    throw (new ProgramError("Illegal Argument for /"));
	  }
    }

  /**
     Power.
     The receiver is a float. The exponent can be an integer or a float.
     @param receiver The float indicating the receiver of the method
     @param arg The object indicating the argument.
     @returns A new float being the '^' of the receiver and the argument.
     @exception errors.AgoraError Is thrown when the argument is of wrong type.
  */
  public static Object power(Float receiver, Object arg) throws AgoraError
    {
      if (arg instanceof Integer)
	{
	  return (new Float((int)java.lang.Math.pow(receiver.floatValue(),
						      ((Integer)arg).intValue())));
	}
      else 
	if (arg instanceof Float)
	  {
	    return (new Float((float)java.lang.Math.pow(receiver.floatValue(),
							((Float)arg).floatValue())));
	  }
	else
	  {
	    throw (new ProgramError("Illegal Argument for ^"));
	  }
    }

  /**
     Comparision =.
     The receiver must be float and the argument can be anything.
     @param receiver The float indicating the receiver of the method
     @param arg The object indicating the argument.
     @returns A new boolean being the comparision of the receiver and the argument.
     @exception errors.AgoraError Is thrown when the argument is of wrong type.
  */
  public static Object equalsF(Float receiver, Object arg) throws AgoraError
    {
      if (arg instanceof Float)
	{
	  return (new Boolean(receiver.floatValue() == ((Float)arg).intValue()));
	}
      else 
	return (new Boolean(false));
    }
  
  /**
     Smaller than test.
     The receiver must be a float, the argument an integer or a float.
     @param receiver The float indicating the receiver of the method
     @param arg The object indicating the argument.
     @returns A new boolean being the comparision of the receiver and the argument.
     @exception errors.AgoraError Is thrown when the argument is of wrong type.
  */
  public static Object smF(Float receiver, Object arg) throws AgoraError
    {
      if (arg instanceof Integer)
	{
	  return (new Boolean(receiver.floatValue() < ((Integer)arg).intValue()));
	}
      else 
	if (arg instanceof Float)
	  {
	    return (new Boolean(receiver.floatValue() < ((Float)arg).floatValue()));
	  }
	else
	  {
	    throw (new ProgramError("Illegal Argument for <"));
	  }
    }
  
  /**
     Greater than test.
     The receiver must be float, the argument integer or float.
     @param receiver The float indicating the receiver of the method
     @param arg The object indicating the argument.
     @returns A new boolean being the comparision of the receiver and the argument.
     @exception errors.AgoraError Is thrown when the argument is of wrong type.
  */
  public static Object gtF(Float receiver, Object arg) throws AgoraError
    {
      if (arg instanceof Integer)
	{
	  return (new Boolean(receiver.floatValue() > ((Integer)arg).intValue()));
	}
      else 
	if (arg instanceof Float)
	  {
	    return (new Boolean(receiver.floatValue() > ((Float)arg).floatValue()));
	  }
	else
	  {
	    throw (new ProgramError("Illegal Argument for >"));
	  }
    }
  
  /**
     Smaller than or equal.
     Receiver must be float, the argument integer or float.
     @param receiver The float indicating the receiver of the method
     @param arg The object indicating the argument.
     @returns A new boolean being the comparision of the receiver and the argument.
     @exception errors.AgoraError Is thrown when the argument is of wrong type.
  */
  public static Object smeF(Float receiver, Object arg) throws AgoraError
    {
      if (arg instanceof Integer)
	{
	  return (new Boolean(receiver.floatValue() <= ((Integer)arg).intValue()));
	}
      else 
	if (arg instanceof Float)
	  {
	    return (new Boolean(receiver.floatValue() <= ((Float)arg).floatValue()));
	  }
	else
	  {
	    throw (new ProgramError("Illegal Argument for <="));
	  }
    }
  
  /**
     Greater than or equal.
     The receiver must be float, the argument integer or float.
     @param receiver The float indicating the receiver of the method
     @param arg The object indicating the argument.
     @returns A new boolean being the comparision of the receiver and the argument.
     @exception errors.AgoraError Is thrown when the argument is of wrong type.
  */
  public static Object gteF(Float receiver, Object arg) throws AgoraError
    {
      if (arg instanceof Integer)
	{
	  return (new Boolean(receiver.floatValue() >= ((Integer)arg).intValue()));
	}
      else 
	if (arg instanceof Float)
	  {
	    return (new Boolean(receiver.floatValue() >= ((Float)arg).floatValue()));
	  }
	else
	  {
	    throw (new ProgramError("Illegal Argument for +"));
	  }
    }
  
  /**
     Absolute value. Receiver is floatr.
     @param receiver The receiver of the abs message
     @return The float indicating the absolute value of the receiver.
     @exception errors.AgoraError Is never thrown in this case.
  */
  public static Object abs(Float receiver) throws AgoraError
    {
      return (new Float(java.lang.Math.abs(receiver.floatValue())));
    }
  
  /**
     Square Root. Receicer is float.
     @param receiver The receiver of the sqrt message
     @return The float indicating the square root of the receiver.
     @exception errors.AgoraError Is never thrown in this case.
  */
  public static Object sqrt(Float receiver) throws AgoraError
    {
      return (new Float(java.lang.Math.sqrt(receiver.floatValue())));
    }
  
  /**
     Square. Receiver is float.
     @param receiver The receiver of the sqr message
     @return The float indicating the square of the receiver.
     @exception errors.AgoraError Is never thrown in this case.
  */
  public static Object sqr(Float receiver) throws AgoraError
    {
      return (new Float(receiver.floatValue() * receiver.floatValue()));
    }
  
}
