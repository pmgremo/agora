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
   An Agora integer will be programmed as an 'up'ed Java integer and all Agora
   messages on integers will be forwarded to the underlying Java messages.
   However, java.lang.Integer does not contain messages like +, and it is
   forbidden to make a subclass of java.lang.Integer to implement +.
   That's why we implement a number of operators as procedures in
   this class. When a message is sent to an Agora integer, and it is
   in the java.lang.Integer class, it will be forwarded to that method.
   Otherwise, it will be invoked here and its receiver will be passed
   as a procedure parameter. Pretty dirty, but it's the only way to make
   it work.
   @author Wolfgang De Meuter (Programming Technology Lab)
	Last change:  E    16 Nov 97    1:47 am
*/
public class JV_Integer extends Object implements Serializable
{
  /**
     This is the ad hoc generator creator for integers. This will be
     called when an integer is upped by 'Up'. It returns
     a method table that links Agora messages to procedures
     defined in this class.
     @return A generator containing Agora patterns that are linked to the operator
     procedures defined in this class.
     @exception errors.AgoraError Is thrown when something goes wrong while building the
     generator (e.g. when you remove a method from this class and forget to adjust this
     method).
  */
  public static PrimGenerator generatorJV_Integer() throws AgoraError
    {
      Hashtable table = new Hashtable(25);
      PrimGenerator result = new PrimGenerator("JV_Integer",table,null);
      try
	{
	  Class[] argtypes2 = new Class[2];
	  argtypes2[1] = Class.forName("java.lang.Object");
	  argtypes2[0] = Class.forName("java.lang.Integer");
	  Class thisOne = Class.forName("javaAdditions.JV_Integer");
	  table.put(new OperatorPattern("+"),
		    new PrimFunctionAttribute(thisOne.getMethod("plus",argtypes2)));
	  table.put(new OperatorPattern("-"),
		    new PrimFunctionAttribute(thisOne.getMethod("min",argtypes2)));
	  table.put(new OperatorPattern("*"),
		    new PrimFunctionAttribute(thisOne.getMethod("mult",argtypes2)));
	  table.put(new OperatorPattern("/"),
		    new PrimFunctionAttribute(thisOne.getMethod("divide",argtypes2)));
	  table.put(new OperatorPattern("#"),
		    new PrimFunctionAttribute(thisOne.getMethod("div",argtypes2)));
	  table.put(new OperatorPattern("%"),
		    new PrimFunctionAttribute(thisOne.getMethod("mod",argtypes2)));
	  table.put(new OperatorPattern("^"),
		    new PrimFunctionAttribute(thisOne.getMethod("power",argtypes2)));
	  table.put(new OperatorPattern("="),
		    new PrimFunctionAttribute(thisOne.getMethod("equalsI",argtypes2)));
	  table.put(new OperatorPattern("<"),
		    new PrimFunctionAttribute(thisOne.getMethod("smI",argtypes2)));
	  table.put(new OperatorPattern(">"),
		    new PrimFunctionAttribute(thisOne.getMethod("gtI",argtypes2)));
	  table.put(new OperatorPattern("<="),
		    new PrimFunctionAttribute(thisOne.getMethod("smeI",argtypes2)));
	  table.put(new OperatorPattern(">="),
		    new PrimFunctionAttribute(thisOne.getMethod("gteI",argtypes2)));
	  table.put(new OperatorPattern("|"),
		    new PrimFunctionAttribute(thisOne.getMethod("orI",argtypes2)));
	  table.put(new OperatorPattern("&"),
		    new PrimFunctionAttribute(thisOne.getMethod("andI",argtypes2)));
	  Class[] argtypes1 = new Class[1];
	  argtypes1[0] = Class.forName("java.lang.Integer");
	  table.put(new UnaryPattern("abs"),
		    new PrimFunctionAttribute(thisOne.getMethod("abs",argtypes1)));
	  table.put(new UnaryPattern("inc"),
		    new PrimFunctionAttribute(thisOne.getMethod("inc",argtypes1)));
	  table.put(new UnaryPattern("dec"),
		    new PrimFunctionAttribute(thisOne.getMethod("dec",argtypes1)));
	  table.put(new UnaryPattern("sqrt"),
		    new PrimFunctionAttribute(thisOne.getMethod("sqrt",argtypes1)));
	  table.put(new UnaryPattern("sqr"),
		    new PrimFunctionAttribute(thisOne.getMethod("sqr",argtypes1)));
	  table.put(new UnaryPattern("not"),
		    new PrimFunctionAttribute(thisOne.getMethod("not",argtypes1)));
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
     Addition. Receiver is of type integer, the argument can be integer or float.
     @param receiver The integer indicating the receiver of the method
     @param arg The object indicating the argument.
     @returns A new float or int being the sum of the receiver and the argument.
     @exception errors.AgoraError Is thrown when the argument is of wrong type.
  */
  public static Object plus(Integer receiver, Object arg) throws AgoraError
    {
      if (arg instanceof Integer)
	{
	  return (new Integer(receiver.intValue() + ((Integer)arg).intValue()));
	}
      else 
	if (arg instanceof Float)
	  {
	    return (new Float(receiver.intValue() + ((Float)arg).floatValue()));
	  }
	else
	  {
	    throw (new ProgramError("Illegal Argument for +"));
	  }
    }
  
  /**
     Substraction.
     The receiver is an integer. The argument can be an integer or a float.
     @param receiver The integer indicating the receiver of the method
     @param arg The object indicating the argument.
     @returns A new float or int being the substraction of the receiver and the argument.
     @exception errors.AgoraError Is thrown when the argument is of wrong type.
  */
  public static Object min(Integer receiver, Object arg) throws AgoraError
    {
      if (arg instanceof Integer)
	{
	  return (new Integer(receiver.intValue() - ((Integer)arg).intValue()));
	}
      else 
	if (arg instanceof Float)
	  {
	  return (new Float(receiver.intValue() - ((Float)arg).floatValue()));
	  }
	else
	  {
	    throw (new ProgramError("Illegal Argument for -"));
	  }
    }

  /**
     Multiplication.
     The receiver is an integer, the argument can be integer or float.
     @param receiver The integer indicating the receiver of the method
     @param arg The object indicating the argument.
     @returns A new float or int being the multiplication of the receiver and the argument.
     @exception errors.AgoraError Is thrown when the argument is of wrong type.
  */
  public static Object mult(Integer receiver, Object arg) throws AgoraError
    {
      if (arg instanceof Integer)
	{
	  return (new Integer(receiver.intValue() * ((Integer)arg).intValue()));
	}
      else 
	if (arg instanceof Float)
	  {
	    return (new Float(receiver.intValue() * ((Float)arg).floatValue()));
	  }
	else
	  {
	    throw (new ProgramError("Illegal Argument for *"));
	  }
    }

  /**
     Division.
     The receiver is an integer, the argument can be an integer or a float.
     @param receiver The integer indicating the receiver of the method
     @param arg The object indicating the argument.
     @returns A new float being the floating division of the receiver and the argument.
     @exception errors.AgoraError Is thrown when the argument is of wrong type.
     */
  public static Object divide(Integer receiver, Object arg) throws AgoraError
    {
      if (arg instanceof Integer)
	{
	  return (new Float(receiver.intValue() / ((Integer)arg).intValue()));
	}
      else 
	if (arg instanceof Float)
	  {
	    return (new Float(receiver.intValue() / ((Float)arg).floatValue()));
	  }
	else
	  {
	    throw (new ProgramError("Illegal Argument for /"));
	  }
    }

  /**
     Integer division.
     Both the receiver and the arguments must be integers.
     @param receiver The integer indicating the receiver of the method
     @param arg The object indicating the argument.
     @returns A new integer being the integer division of the receiver and the argument.
     @exception errors.AgoraError Is thrown when the argument is of wrong type.
     */
  public static Object div(Integer receiver, Object arg) throws AgoraError
    {
      if (arg instanceof Integer)
	{
	  return (new Integer(receiver.intValue() / ((Integer)arg).intValue()));
	}
      else 
	{
	  throw (new ProgramError("Illegal Argument for #"));
	}
    }

  /**
     Remainder.
     Both the receiver and the arguments must be integers.
     @param receiver The integer indicating the receiver of the method
     @param arg The object indicating the argument.
     @returns A new integer being the remainder of dividing the receiver and the argument.
     @exception errors.AgoraError Is thrown when the argument is of wrong type.
  */
  public static Object mod(Integer receiver, Object arg) throws AgoraError
    {
      if (arg instanceof Integer)
	{
	  return (new Integer(receiver.intValue() % ((Integer)arg).intValue()));
	}
      else
	{
	  throw (new ProgramError("Illegal Argument for %"));
	}
  }

  /**
     Power.
     The receiver is an integer. The exponent can be an integer or a float.
     @param receiver The int indicating the receiver of the method
     @param arg The object indicating the argument.
     @returns A new float or integer being the power of the receiver and the argument.
     @exception errors.AgoraError Is thrown when the argument is of wrong type.
  */
  public static Object power(Integer receiver, Object arg) throws AgoraError
    {
      if (arg instanceof Integer)
	{
	  return (new Integer((int)java.lang.Math.pow(receiver.intValue(),
						      ((Integer)arg).intValue())));
	}
      else 
	if (arg instanceof Float)
	  {
	    return (new Float((float)java.lang.Math.pow(receiver.intValue(),
							((Float)arg).floatValue())));
	  }
	else
	  {
	    throw (new ProgramError("Illegal Argument for ^"));
	  }
    }

  /**
     Comparision =.
     The receiver must be integer and the argument can be anything.
     @param receiver The integer indicating the receiver of the method
     @param arg The object indicating the argument.
     @returns A new boolean being the comparision of the receiver and the argument.
     @exception errors.AgoraError Is thrown when the argument is of wrong type.
  */
  public static Object equalsI(Integer receiver, Object arg) throws AgoraError
    {
      if (arg instanceof Integer)
	{
	  return (new Boolean(receiver.intValue() == ((Integer)arg).intValue()));
	}
      else 
	return (new Boolean(false));
    }
  
  /**
     Smaller than test.
     The receiver must be an integer, the argument an integer or a float.
     @param receiver The integer indicating the receiver of the method
     @param arg The object indicating the argument.
     @returns A new boolean being the comparision of the receiver and the argument.
     @exception errors.AgoraError Is thrown when the argument is of wrong type.
  */
  public static Object smI(Integer receiver, Object arg) throws AgoraError
    {
      if (arg instanceof Integer)
	{
	  return (new Boolean(receiver.intValue() < ((Integer)arg).intValue()));
	}
      else 
	if (arg instanceof Float)
	  {
	    return (new Boolean(receiver.intValue() < ((Float)arg).floatValue()));
	  }
	else
	  {
	    throw (new ProgramError("Illegal Argument for <"));
	  }
    }
  
  /**
     Greater than test.
     The receiver must be integer, the argument integer or float.
     @param receiver The integer indicating the receiver of the method
     @param arg The object indicating the argument.
     @returns A new boolean being the comparision of the receiver and the argument.
     @exception errors.AgoraError Is thrown when the argument is of wrong type.
  */
  public static Object gtI(Integer receiver, Object arg) throws AgoraError
    {
      if (arg instanceof Integer)
	{
	  return (new Boolean(receiver.intValue() > ((Integer)arg).intValue()));
	}
      else 
	if (arg instanceof Float)
	  {
	    return (new Boolean(receiver.intValue() > ((Float)arg).floatValue()));
	  }
	else
	  {
	    throw (new ProgramError("Illegal Argument for >"));
	  }
    }
  
  /**
     Smaller than or equal.
     Receiver must be integer, the argument integer or float.
     @param receiver The integer indicating the receiver of the method
     @param arg The object indicating the argument.
     @returns A new boolean being the comparision of the receiver and the argument.
     @exception errors.AgoraError Is thrown when the argument is of wrong type.
  */
  public static Object smeI(Integer receiver, Object arg) throws AgoraError
    {
      if (arg instanceof Integer)
	{
	  return (new Boolean(receiver.intValue() <= ((Integer)arg).intValue()));
	}
      else 
	if (arg instanceof Float)
	  {
	    return (new Boolean(receiver.intValue() <= ((Float)arg).floatValue()));
	  }
	else
	  {
	    throw (new ProgramError("Illegal Argument for <="));
	  }
    }
  
  /**
     Greater than or equal.
     The receiver must be integer, the argument integer or float.
     @param receiver The integer indicating the receiver of the method
     @param arg The object indicating the argument.
     @returns A new boolean being the comparision of the receiver and the argument.
     @exception errors.AgoraError Is thrown when the argument is of wrong type.
  */
  public static Object gteI(Integer receiver, Object arg) throws AgoraError
    {
      if (arg instanceof Integer)
	{
	  return (new Boolean(receiver.intValue() >= ((Integer)arg).intValue()));
	}
      else 
	if (arg instanceof Float)
	  {
	    return (new Boolean(receiver.intValue() >= ((Float)arg).floatValue()));
	  }
	else
	  {
	    throw (new ProgramError("Illegal Argument for +"));
	  }
    }
  
  /**
     Bitwise or.
     Both receiver and argument must be integers.
     @param receiver The integer indicating the receiver of the method
     @param arg The object indicating the argument.
     @returns A new boolean being the comparision of the receiver and the argument.
     @exception errors.AgoraError Is thrown when the argument is of wrong type.
  */
  public static Object orI(Integer receiver, Object arg) throws AgoraError
    {
      if (arg instanceof Integer)
	{
	  return (new Integer(receiver.intValue() | ((Integer)arg).intValue()));
	}
      else 
	{
	  throw (new ProgramError("Illegal Argument for |"));
	}
    }
  
  /**
     Bitwise and.
     Both receiver and argument must be integers.
     @param receiver The integer indicating the receiver of the method
     @param arg The object indicating the argument.
     @returns A new boolean being the comparision of the receiver and the argument.
     @exception errors.AgoraError Is thrown when the argument is of wrong type.
  */
  public static Object andI(Integer receiver, Object arg) throws AgoraError
    {
      if (arg instanceof Integer)
	{
	  return (new Integer(receiver.intValue() & ((Integer)arg).intValue()));
	}
      else 
	{
	  throw (new ProgramError("Illegal Argument for &"));
	}
    }
  
  /**
     Absolute value. Receiver is integer.
     @param receiver The receiver of the message
     @return The integer indicating the absolute value of the receiver.
     @exception errors.AgoraError Is never thrown in this case.
  */
  public static Object abs(Integer receiver) throws AgoraError
    {
      return (new Integer(java.lang.Math.abs(receiver.intValue())));
    }
  
  /**
     Increment. Receicer is integer.
     @param receiver The receiver of the message
     @return The integer indicating the increment of the receiver.
     @exception errors.AgoraError Is never thrown in this case.
  */
  public static Object inc(Integer receiver) throws AgoraError
    {
      return (new Integer(receiver.intValue()+1));
    }
  
  /**
     Decrement. Receicer is integer.
     @param receiver The receiver of the message
     @return The integer indicating the decrement of the receiver.
     @exception errors.AgoraError Is never thrown in this case.
  */
  public static Object dec(Integer receiver) throws AgoraError
    {
      return (new Integer(receiver.intValue() - 1));
    }
  
  /**
     Square Root. Receicer is integer.
     @param receiver The receiver of the message
     @return The float indicating the square root of the receiver.
     @exception errors.AgoraError Is never thrown in this case.
  */
  public static Object sqrt(Integer receiver) throws AgoraError
    {
      return (new Float(java.lang.Math.sqrt(receiver.intValue())));
    }
  
  /**
     Square. Receiver is integer.
     @param receiver The receiver of the message
     @return The integer indicating the square of the receiver.
     @exception errors.AgoraError Is never thrown in this case.
  */
  public static Object sqr(Integer receiver) throws AgoraError
    {
      return (new Integer(receiver.intValue() * receiver.intValue()));
    }
  
  /**
     Bitwise not. Receiver is integer.
     @param receiver The receiver of the message
     @return The integer indicating the bitwise not of the receiver.
     @exception errors.AgoraError Is never thrown in this case.
  */
  public static Object not(Integer receiver) throws AgoraError
    {
      return (new Integer(~ receiver.intValue()));
    }
}
