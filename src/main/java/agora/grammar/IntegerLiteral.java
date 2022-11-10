package agora.grammar;

import agora.tools.*;
import agora.reflection.*;
import java.lang.*;
import agora.runtime.*;
import agora.errors.*;
import agora.objects.*;
import java.io.*;


/**
  Grammar component representing integer literal parse tree nodes.
  @author Wolfgang De Meuter
	Last change:  E    16 Nov 97    2:18 pm
  */
public class IntegerLiteral extends Literal implements Serializable
{
  /**
    Internal value of the literal as denoted in the Agora program.
    */
  protected int theInt;
  
  /**
    Constructs a new integer literal parse tree node.
    @param value The value represented by this literal.
    */
  public IntegerLiteral (int value)
  {
    this.theInt = value;
  }
  
  /**
    Unparse the component to a string. The integer parameter denoted the number of leading spaces.
    @param hor The nunber of spaces leading the string representation of the literal.
    @return The String representation of the literal.
    */
  public String unparse(int hor)
  {
    return AgoraIO.makeSpaces(hor) + ((new Integer(theInt)).toString());
  }
  
  /**
    Evaluates the integer literal in the given context. This creates an up-ped instance
    of the java.lang.Integer class.
    @param context The context in which the literal must be evaluated. Of course, this
    is not used for evaluating literals.
    @return The Agora Object represented by this literal.
    @exception agora.errors.AgoraError When something goes wrong during evaluation.
    */
  public AgoraObject eval(Context context) throws AgoraError
  { 
    return Up.glob.up(new Integer(this.theInt));
  }
      
/**
    Returns the internal value of the integer literal.
    @return The actual integer represented by this literal.
    */
  public int getInt()
  {
    return theInt;
  }
}
