package grammar;

import tools.*;
import runtime.*;
import errors.*;
import objects.*;
import patterns.*;
import java.io.*;

/**
  This class represents user operator patterns like + 4 or +arg.
  @author Wolfgang De Meuter (Programming technology Lab).
	Last change:  E    16 Nov 97    1:42 am
 */
public class UserOperatorPattern extends UserPattern implements Serializable
{
  protected String operator;
  
  protected Expression   operand;
  
  /**
   Creates a new user operator pattern.
   @param operator A string indicating the operator pattern (e.g. "+").
   @param argument The expression denoting the argument of the operator pattern.
   */
  public UserOperatorPattern (String operator, Expression argument)
    {
      this.operator = operator;
      this.operand  = argument;
    }
  
  /**
    To retrieve the string representing the operator itself.
    @return The internal string representation of the operator pattern.
    */
  public String getOperator()
    {
      return this.operator;
    }

  /**
    Returns the internal representation of the argument expression of the operator pattern.
    @return The expression being the argument of the operator pattern.
    */
  public Expression getOperand()
    {
      return this.operand;
    }
  
  /**
    Unparses the operator pattern.
    @param hor The number of spaces leading the unparsed representation.
    @returns A string that is the unparsed version of this expression.
    */
  public String unparse(int hor)
    {
      return AgoraIO.makeSpaces(hor) + this.operator + " " + this.operand.unparse(0);
    }

  /**
    Constructs a client that is needed to send this operator pattern to an object
    @param context The context from which the new client is to be obtained.
    @param receiver The already evaluated (or upped) receiver of this pattern.
    @return A new Client containing the (non-evaluated) actuals of this pattern.
    */
  public Client  makeClient(Context context,AgoraObject receiver)
    {
      Object[] actuals = new Object[1];
      actuals[0] = this.operand;
      return (context.newClient(actuals));
    }

  /**
    Create a new runtime version of this syntactic pattern.
    @param context The context in which this pattern is to be sent (usually the context
     of a message expression).
    @return The runtime variant of this syntactic pattern.
    */
  public AbstractPattern makePattern(Context context)
    {
      return (new OperatorPattern(this.operator));
    }

  /**
    Create a list of formal arguments that go with this syntactic pattern (if the pattern is
    a formal pattern, otherwise an error is thrown.).
    @param context The evaluation context in which the formals are needed.
    @return An array of strings (1 in this case because it is an operator) that are
    the formal arguments of this pattern.
    @exception errors.AgoraError When something goes wrong when constructing the
    formals (e.g. when it is an actual pattern).
    */
  public String[] makeFormals(Context context) throws AgoraError	
    {
      if (this.operand instanceof UserUnaryPattern)
	{
	  String[] f = new String[1];
	  f[0] = ((UserUnaryPattern)this.operand).getUnary();
	  return f;
	}
      else
	{
	  ProgramError ex = new ProgramError("Formal parameters must be identifiers");
	  ex.setCode(this);
	  throw ex;
	}
    }
}
