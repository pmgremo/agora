package agora.grammar;

import agora.tools.*;
import agora.objects.*;
import agora.errors.*;
import agora.patterns.*;
import agora.runtime.*;

import java.io.*;

/** 
    This class represents the parsetree node for aggregates of Agora expressions.
    These can be [] and {} expressions.
    
    @author Wolfgang De Meuter
	Last change:  E    16 Nov 97    2:25 pm
*/
public class Aggregate extends Expression implements Serializable
{
  /**
     Represents the left delimitting character of the aggregate (usually [ or {)
  */
  protected char leftDel;
  
  /**
     Represents the right delimitting character of the aggregate (usually ] or })
  */
  protected char rightDel;
  
  /**
     The array of expressions the aggregate contains
  */
  protected Expression[] expressions;
  
  /**
     Constructor takes the number of expressions the aggregate contains, and the left and
     right delimitting characters
     @param sz The initial number of expressions in the aggregate.
     @param left The left delimiter of the aggregate.
     @param riht The right delimiter of the aggregate.
  */
  public Aggregate (int sz,char left,char right)
    {
      this.expressions = new Expression[sz];
      this.leftDel = left;
      this.rightDel = right;
    }
  
  /**
     To access the expressions of the aggregate.
     @param index The position at which the aggregate needs to be indexed.
     @return The expression residing at the specified position in the aggregate.
  */
  public Expression at(int index)
    {
      return this.expressions[index];
    }
  
  /**
     To access the expressions of the aggregate.
     @param index The position at which the aggregate must be updated.
     @param expression The new expression to be installed at the given position.
  */
  public void atPut(int index, Expression expression)
    {
      this.expressions[index] = expression;
    }
  
  /**
     Evaluates the aggregate expression in the given context.
     @param context The context in which the expression must be evaluated.
     @return The AgoraObject corresponding to the expression.
     @exception agora.errors.AgoraError When something goes wrong during evaluation.
  */
  public AgoraObject eval(Context context) throws AgoraError
    {
      if (this.leftDel=='[')
	{
	  IdentityGenerator exnihiloSelf = AgoraGlobals.glob.rootIdentity.funcAddLayer("public of ex nihilo");
	  InternalGenerator exnihiloPriv = context.getPrivate().funcAddLayer("private of ex nihilo");
	  exnihiloSelf.getMe().setPrivate(exnihiloPriv);
	  exnihiloPriv.setPrivate(exnihiloPriv);
	  Context exnihiloCont = context.setMultiple(exnihiloSelf,
						     exnihiloPriv,
						     exnihiloSelf.getMe(),
						     context.getCategory(),
						     AgoraGlobals.glob.rootIdentity);
	  AgoraObject dummy = null;
	  for (int i = 0 ; i < expressions.length ; i++)
	    dummy = expressions[i].eval(exnihiloCont);
	  return exnihiloSelf.wrap();
	}
      else // leftDel=='{'
	{
	  AgoraObject result = AgoraGlobals.glob.uppedNull;
	  for (int i = 0 ; i < expressions.length ; i++)
	    result = expressions[i].eval(context);
	  return result;
	} 
    }

  /**
     Makes a pattern object to send the [] or {} messages. Now,
     aggregates are evaluated immediately, for efficiency. In previous releases,
     [] or {} was actually sent to this expression. This method makes the operator
     pattern [] or {} to do this send.
     @param context The evaluation context when sending the [] or {} message.
     @return The operator pattern corresponding to this [] or {} expression.
  */
  public AbstractPattern makePattern(Context context)
    {
      OperatorPattern pat = null;
      if (this.leftDel=='[')
	pat = new OperatorPattern("[]");
      else
	pat = new OperatorPattern("{}");
      pat.setReifier();
      return pat;
    }
 
  /**
     Unparses the aggregate to a string. The input parameter is the number of spaces
     that lead the resulting unparsed text.
     @param hor The number of spaces leading the unparsed aggregate.
     @return The string representation of the aggregate.
  */
  public String unparse(int hor)
    {
      String msg = AgoraIO.makeSpaces(hor);
      msg = msg + (new Character(this.leftDel)).toString();
      if (this.expressions.length == 0)
	msg = msg + (new Character(this.rightDel)).toString();
      else
	{
	  for(int i = 0; i< this.expressions.length; i++)
	    {
	      if (i==0) msg = msg + this.expressions[i].unparse(hor);
	      else msg = msg + this.expressions[i].unparse(hor+2);
	      if (i<this.expressions.length-1) msg = msg + ";\n";
	    }
	  msg = msg + AgoraIO.makeSpaces(hor);
	  msg = msg + (new Character(this.rightDel)).toString();
	}
      return msg;
    }
}