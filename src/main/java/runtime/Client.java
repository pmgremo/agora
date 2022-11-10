package runtime;

import objects.*;
import errors.*;
import grammar.*;
import reflection.*;
import java.io.*;

/**
  Client objects are used for message passing to transport the actual arguments from the
  sender of a message to the receiver of that message. Clients must NEVER be created from
  scratch (i.e. using the constructor), but form a contract with Context's. Each time you
  need a new client, you should ask it the context, and each time you need a new context,
  you should ask it the client. This is possible since running an Agora program is a recursive
  game between send and eval. During eval, contexts exist and during send, clients exist. Hence,
  you always have one of both available to find the other one.
  <p>
  The benefit of this contract of clients and contexts creating each other, is that they
  can copy around information during the evaluation of an Agora program. This information
  is then transparantly copied through the evaluator.
  @author Wolfgang De Meuter (Programming Technology Lab)
  @see runtime.Context
  @see runtime.ReifierClient
	Last change:  E    16 Nov 97    1:58 pm
  */
public class Client extends Object implements Serializable
{
  protected AgoraException currentException;
  protected Object[] actuals;
  
  /**
    This constructor should never be used except from within context objects. The arguments
    of this constructor will be provided by the 'client-creator' in the Context class.
    @param actuals An array of expression objects that can later be evaluated or upped
    by the client
    @param exception Currently, the information that is passed around by clients
    and contexts consists of the 'last installed agoraexception', that is, the last
    encountered catch code.
    */
  public Client (Object[] actuals,AgoraException exception)
    {
      this.actuals  = actuals;
      this.currentException = exception;
    }
  
  /**
    This method is part of the contract between contexts and clients. It creates a new
    context and thereby copies the passed around exception into that context.
    @param cat The evaluation category
    @param self The receiver of the object that got the message (when the client was used).
    @param publik The public part of that receiver.
    @return a new Context object containing this information.
    */
  public Context newContext(int cat,IdentityGenerator self,MethodsGenerator publik)
    {
      return (new Context (self,null,publik,cat,null,this.currentException));
    }

  /**
    This method does the same as the previous one, except that more arguments are
    unknown at the time of using it. They will be filled in later when using the
    context.
    @param cat The given category
    @return A new context object in which the client information is transparantly
    copied.
    */
  public Context newContext(int cat)
    {
      return (new Context (null,null,null,cat,null,this.currentException));
    }

  /**
    Does the same as the previous two messages. When using this one, it meand that no
    information about the new context is known, and that everything will be provided
    later.
    @return A new context in which the client information is transparantly copied.
    */
  public Context newContext()
    {
      return (new Context(null,null,null,Category.emptyCategory,null,this.currentException));
    }
  
  /**
    Get the actual arguments our of the client. The type is an array of Object's, because
    actual arguments may be evaluated objects (i.e. AgoraObject's) or expressions.
    @return An array of actual arguments.
    */
  public Object[] getActuals ()
    {
      return this.actuals;
    }
  /**
    Assigns the actual arguments in the client.
    @param actuals An array of new actual arguments.
    */
  public void setActuals(Object[] actuals)
    {
      this.actuals = actuals;
    }

  /**
    Reads the exception that is transparantly passed around by contexts and clients.
    @return The exception that is passed around.
    */
  public AgoraException getException()
    {
      return this.currentException;
    }

  /**
    Asuming that the arguments are all expressions, this method up's them one by one.
    @exception errors.AgoraError This exception is thrown when something goes wrong during
    the upping process (exception propagated by Up.up).
    */
  public void actualsUp() throws AgoraError
    {
      for (int j =0 ; j<actuals.length ; j++)
	actuals[j] = Up.glob.up(actuals[j]);
    }

  /**
    Assuming all actuals are upped objects, this method downs them one by one.
    */
  public void actualsDown()
    {
      for (int j=0 ; j<actuals.length ; j++)
	actuals[j] = ((AgoraObject)actuals[j]).down();
    }

  /**
    Assuming all actuals are expression objects, this method evaluates the one by one.
    @param context The evaluation context in which the actual arguments are to be evaluated.
    @exception errors.AgoraError When something goes wrong during the evaluation of the
    actual arguments.
    */
  public void actualsEval(Context context) throws AgoraError
    {
      for (int j=0 ; j<actuals.length ; j++)
	actuals[j] = ((Expression)actuals[j]).eval(context);
    }

  /**
    Assuming that all arguments are upped objects, this method downs them and returns them
    as an array of objects. This array is usually used to be passed to 'invoke' such that
    a Java method is invoked with the appropriate set of downed Java objects.
    @return An Array of Java (downed!) objects.
    @exception errors.AgoraError Is thrown when something goes wron during the downing
    of objects.
    */
  public Object[] makeNativeArguments() throws AgoraError
    {
      this.actualsDown();
      return this.actuals;
    }
}
