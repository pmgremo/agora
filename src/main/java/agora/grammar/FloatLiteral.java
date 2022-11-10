package agora.grammar;

import agora.tools.*;
import agora.runtime.*;
import agora.errors.*;
import agora.reflection.*;
import agora.objects.*;
import java.io.*;

/**
  Parse tree nodes for floating point literals.

  @author Wolfgang De Meuter
	Last change:  E    16 Nov 97    2:18 pm
  */
public class FloatLiteral extends Literal implements Serializable
{
  /**
    Internal floating point number after parsing.
    */
  protected float theReal;
  
  /**
    Constructs a new floating point parse tree node.
    @param value The floating point value represented by this literal.
    */
  public FloatLiteral (float value)
  {
    this.theReal = value;
  }
  
  /**
    Unparses the node to a string. The integer is the number of blanks preceding the string.
    @param hor The number of spaces leading the unparsed expression.
    @return The string representation of this floating point literal.
    */
  public String unparse(int hor)
  {
    return AgoraIO.makeSpaces(hor) + (new Float(theReal)).toString();
  }
  
  /**
    Evaluates the floating point node to an up-ped version of a java.lang.Float
    object.
    @param context The evaluation context when evaluating this literal.
    @return The Agora Object represented by the literal.
    @exception agora.errors.AgoraError When something goes wrong during evaluation.
    */
  public AgoraObject eval(Context context) throws AgoraError
  { 
    return Up.glob.up(new Float(this.theReal));
  }
  
  /**
    To inspect the real value of the node.
    @return The actual floating point value of this literal.
    */
  public float getReal()
  {
    return theReal;
  }
}
