package agora.grammar;

import agora.tools.*;
import agora.errors.*;
import agora.runtime.*;
import agora.objects.*;
import agora.patterns.*;
import java.io.*;

/**
  Reifier Operator Patterns are currently not used in Agora, because there is no
  capitalised analogue of things like +. When we have a decent programming environment,
  reifiers are denoted boldfaced instead of capitalised. Then this code will make sense
  again. THIS CODE HAS NOT BEEN DEBUGGED!
	Last change:  E    16 Nov 97    2:16 pm
  */
public class ReifOperatorPattern extends ReifPattern implements Serializable
{
  protected String operator;
  
  protected Expression operand;

  public ReifOperatorPattern (String operator,Expression arg)
    {
      super();
      this.operator = operator;
      this.operand = arg;
    }

  public String getOperator()
    {
      return this.operator;
    }
  
  public Expression getOperand()
    {
      return this.operand;
    }

  public String unparse(int hor)
    {
      return AwtIo.makeSpaces(hor) + this.operator + " " + this.operand.unparse(0);
    }
    
  public Client makeClient(Context context,AgoraObject receiver)
    {
        var actuals = new Object[1];
      actuals[0] = this.operand;
      return context.newReifierClient(actuals);
    }
  
  public AbstractPattern makePattern(Context context)
    {
        var pattern = new OperatorPattern(this.operator);
      pattern.setReifier();
      return pattern;
    }

  /**
    @exception agora.errors.AgoraError When something goes wrong (e.g. the pattern
    is not a formal pattern, but an actual pattern).
    */
  public String[] makeFormals(Context context) throws AgoraError
    {
      if (this.operand instanceof UserUnaryPattern)
	{
        var f = new String[1];
	  f[0] = ((UserUnaryPattern)this.operand).getUnary();
	  return f;
	}
      else
	{
        var ex = new ProgramError("Formal parameters must be identifiers");
	  ex.setCode(this);
	  throw ex;
	}
    }
}
