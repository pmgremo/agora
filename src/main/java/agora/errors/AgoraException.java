package agora.errors;

import agora.patterns.*;
import agora.runtime.*;
import agora.grammar.*;

import java.io.*;

/**
   Objects of this class can be thrown by the evaluator whenever an
   Agora exception is thrown by the RAISE reifier. I.e., Agora
   exception handling is implemented by the underlying Java
   exception handling.
   Have a look at the implementation of RAISE and TRY:CATCH: for
   more details.
   @author Wolfgang De Meuter (Programming Technology Lab).
	Last change:  E    16 Nov 97    1:36 am
*/
public class AgoraException extends AgoraError implements Serializable
{
  /**
     The expression to be evaluated when the exception is caught.
  */
  protected Expression toCatch;
  /**
     The pattern that will be used to invoke the catch-code.
  */
  protected AbstractPattern pattern;
  /**
     The saved client object.
  */
  protected Client client;
  
  /**
     To create a new Agora exception with the given expression.
     This is the expression to be executed when the exception is thrownby RAISE.
     @param c The catch-code to be executed when the exception happens.
  */
  public AgoraException(Expression c)
    {
      super();
      this.pattern=null;
      this.client=null;
      this.toCatch = c;
    }
  
  /**
     Accessor to read the expression
     @return Get the catch-code from the exception.
  */
  public Expression getExpression()
    {
      return toCatch;
    }
  
  /**
     Accessor to read the pattern of the exception.
     @return Gives the agora.runtime (i.e. actual) pattern that occurs in RAISE.
  */
  public AbstractPattern getPattern()
    {
      return pattern;
    }
  
  /**
     Accessor to access the client.
     @return Returns the client with the actual arguments as created by RAISE.
  */
  public Client  getClient()
    {
      return client;
    }
  
  /**
     To set the pattern.
     @param p The pattern to be filled in by raise.
  */
  public void setPattern(AbstractPattern p)
    {
      pattern=p;
    }
  
  /**
     To set the client.
     @param c The client with the actual arguments as determined by RAISE.
  */
  public void setClient(Client c)
    {
      client=c;
    }
  
  /**
     When the programming environment has to signal an exception like this,
     it meand that the Agora programmer did not catch the exception himself.
     Otherwise, this exception would have been caught by the implementation
     of the TRY:CATCH: reifier.
  */
  public void signal()
    {
      String msg = "Uncaught Agora96 Exception.\n\n";
      this.setUpDialog(msg,null);
    }
}
