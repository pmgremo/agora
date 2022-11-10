package runtime;

import objects.*;
import errors.*;
import java.io.*;

/**
  During normal message passing, a client object transports the actual arguments
  from the sender of the message to the receiver of the message. But when the
  message sent is a reifier message, a hidden context must be passed from the
  sender to the receiver, because reifiers must evaluate their arguments in the
  context where the reifier was sent, and not in the context where the reifier was
  defined. Hence the existance of ReifierClient's. They contain the same information
  as an ordinary client, but carry around an extra context object.
  When you ask a reifier client for a new context, the carried around context is returned
  instead of creating a new context.
  @see runtime.Client
  @see runtime.Context
  @author Wolfgang De Meuter (Programming Technology Lab).
	Last change:  E    16 Nov 97    1:51 am
  */
public class ReifierClient extends Client implements Serializable
{
  protected Context context;
  
  /**
    This constructor cannot be used by anyone, but context objects. If you want to send
    a reifier message, ask your context for a new reifier client. This method (defined
    on contexts) will invoke this constructor.
    @param actuals The actual parameters of the reifier message for which this client is used.
    @param context The context of evaluation during sending of the reifier message.
    @param exception The exception as is carried around by the context that calls this constructor.
    */
  public ReifierClient (Object[] actuals,
			Context context,
			AgoraException exception)
    {
      super(actuals,exception);
      this.context  = context;
    }
  /**
    Returns the context that created this client, that is the context of invocation of
    the message that needed this client. (Dynamic Scoping!)
    @return The context residing in the reifier client.
    */
  public Context newContext()
    {
      return this.context;
    }
  
  /**
    Returns the context carried around in this reifier client.
    @return The context residing in this reifier client.
    */
  public Context getContext ()
    {
      return this.context;
    } 

  /**
    This method overrides the one in 'Client'. When native arguments
    are constructed from the actuals in an ordinary client, this is just an
    array of objects, being the actuals. These are used for 'invoke'.
    But when native arguments are to be prepared for a reifier method,
    the context residing in this client (i.e. the context where the reifier was sent),
    has to be passed around as the first argument when invoking the reifier implementation.
    @return An array of downed actual arguments (as residing in the receiving client),
    but the first of this array is the context where the reifier was sent.
    @exception errors.AgoraError Is thrown whenever something goes wrong during
    downing of the actuals.
    */
  public Object[] makeNativeArguments() throws AgoraError
    {
      this.actualsDown();
      Object[] result = new Object[this.actuals.length + 1];
      result[0] = this.context;
      for (int j=0; j<this.actuals.length; j++)
	result[j+1] = this.actuals[j];
      return result;
    }
}

