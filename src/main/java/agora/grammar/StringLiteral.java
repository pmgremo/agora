package agora.grammar;

import agora.runtime.*;
import agora.reflection.*;
import agora.errors.*;
import agora.objects.*;
import agora.patterns.*;
import java.util.*;
import agora.attributes.*;
import java.io.*;

/**
  Parse tree node type for string literals.

  @author Wolfgang De Meuter
	Last change:  E    16 Nov 97    2:16 pm
  */
public class StringLiteral extends Literal implements Serializable
{
  /**
    The String value as determined by the parser.
    */
  protected String theString;
  
  /**
    To construct a new node.
    @param str The string represented by this literal.
    */
  public StringLiteral (String str)
  {
    this.theString = str;
  }
  
  /**
    Unparse the String. The number denotes the number of blanks preceding the string.
    @param hor The number of spaces leading the unparsed representation of the literal.
    @return The String representation of this literal node.
    */
  public String unparse(int hor)
  {
    return theString;
  }

  /**
    Generate an ad-hoc up wrapper for string literals.
    @return A Generator containing the agora.patterns (and associated agora.attributes) that can
    be sent to string literals.
    @exception agora.errors.AgoraError When something goes wrong during the creation of the
    generator (e.g. when a given method has been deleted from this class.
    */
  public static PrimGenerator generatorStringLiteral() throws AgoraError
  {
    Hashtable table = new Hashtable(4);
    PrimGenerator result = new PrimGenerator("StringLiteral",table,null);
    try
      {
	Class[] argtypes1 = new Class[1];
	argtypes1[0] = Class.forName("agora.runtime.Context");
	Class thisOne = Class.forName("agora.grammar.StringLiteral");
	
	UnaryPattern unary = new UnaryPattern("HALT");
	unary.setReifier();
	table.put(unary,new PrimMethAttribute(thisOne.getMethod("halt",argtypes1)));
      }
    catch (Throwable e)
      {
	throw (new PrimException(e,"StringLiteral::generatorStringLiteral"));
	// This is impossible because 'halt' is here.
      }
    return result;
  }
  
  /**
     Implements the HALT reifier. The receiver must evaluate to a string object.
     @param context The evaluation context while HALT was sent.
     @return An agora object indicating the result (is never returned because an error is thrown)
     @exception agora.errors.AgoraError The exception that will terminate the program.
  */
  public AgoraObject halt(Context context) throws AgoraError
    {
      throw (new AgoraHalt((String)this.eval(context).down()));
    }
  
  
  /**
     Evaluates the string node to an up-ped instance of java.lang.String.
     @param context The environment in which evaluation should occur.
     @return The Agora object represented by this string literal.
     @exception agora.errors.AgoraError When something goes wrong during evaluation.
  */
  public AgoraObject eval(Context context) throws AgoraError
    { 
      return Up.glob.up(this.theString);
    }
  
  /**
     Reads the string value denoted by the node.
  */
  public String getString()
    {
      return theString;
    }
}
