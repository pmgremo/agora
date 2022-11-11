package agora.grammar;

import agora.tools.*;
import agora.runtime.*;
import agora.errors.*;
import agora.patterns.*;
import agora.objects.*;
import java.io.*;

/**
  This class represents user keyword agora.patterns like at:3 put:5 or at:i put:thing.
  @author Wolfgang De Meuter (Programming Technology Lab).
	Last change:  E    16 Nov 97    1:43 am
  */
public class UserKeywordPattern extends UserPattern implements Serializable
{
  protected String[] keywords;
  protected Expression[] arguments;
  protected int size;
  
  /**
    Creates a new user keyword pattern.
    @param sz The number of keywords the pattern will contain.
    */
  public UserKeywordPattern (int sz)
    {
      this.size = sz;
      this.keywords = new String[sz];
      this.arguments = new Expression[sz];
    }
  
  /**
    Accesses the keyword pattern.
    @param index position in the keyword pattern of which the keyword is needed.
    @return The keyword at the specified position.
    */
  public String keywordAt(int index)
    {
      return this.keywords[index];
    }

  /**
    Access an argument in the keyword pattern.
    @param index Gives the position at which the argument is to be read.
    @return The expression being the argument at the given position.
    */
  public Expression argumentAt(int index)
    {
      return this.arguments[index];
    }

  /**
    Stores a new keyword with argument at a given position in the keyword pattern.
    @param index The position to be updated.
    @param keyw The new keyword to be stored.
    @param arg The new expression to be stored as argument of the associated keyword.
    */
  public void atPut(int index,String keyw, Expression arg)
    {
      this.keywords[index] = keyw;
      this.arguments[index] = arg;
    }
  
  /**
    Unparses the keyword pattern node.
    @param hor The number of blanks leading the unparsed variant.
    @return The stringrepresentation of the node.
    */
  public String unparse(int hor)
    {
        var msg = AwtIo.makeSpaces(hor);
      for(var i = 0; i<this.size; i++)
	{
	  msg = msg + keywords[i];
	  msg = msg + arguments[i].unparse(0);
	  if(i < this.size - 1)
	    msg = msg + " ";
	}
      return msg;
    }

  /**
    Make a new client of actual arguments that go with this pattern.
    @param context The context from which the client is to be obtained.
    @param receiver The already-evaluated (or upped) receiver that goes with
    this pattern expression.
    @return A client containing the (non-evaluated) arguments.
    */
  public Client makeClient(Context context,AgoraObject receiver)
    {
        var actuals = new Object[size];
      for(var i = 0; i<size; i++)
	actuals[i] = this.arguments[i];
      return (context.newClient(actuals));
    }

  /**
    Creates a new agora.runtime pattern associated to this syntactic pattern.
    @param context The context of evaluation when the agora.runtime variant of this
    syntactic pattern is needed.
    @return A agora.runtime representation of this syntactic pattern.
    */
  public AbstractPattern makePattern(Context context)
    {
        var pattern = new KeywordPattern(size);
      for(var i = 0; i<size; i++)
	pattern.atPut (i,keywords[i]);
      return pattern;
    }

  /**
    Create a list of formal arguments that go with the keyword pattern (if it is a formal pattern,
    otherwise an error is thrown.
    @param context The context in which this pattern is needed as a formal pattern (usually the
    evaluation context during evaluating reifiers like VARIABLE or METHOD:.
    @return An array of strings indicating the formal names that belong to this pattern.
    @exception agora.errors.AgoraError When something goes wrong while constructing the
    formals (e.g. when it is not a formal pattern).
    */
  public String[] makeFormals(Context context) throws AgoraError
    {
        var formals = new String[size];
      for(var i = 0; i<size; i++)
	{
	  if (arguments[i] instanceof UserUnaryPattern)
	    formals[i] = ((UserUnaryPattern)arguments[i]).getUnary();
	  else
	    {
            var ex = new ProgramError("Formal parameters must be identifiers");
	      ex.setCode(this);
	      throw ex;
	    }
	}
      return formals;
    }
}
