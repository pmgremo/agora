package agora.grammar;

import agora.errors.*;
import agora.runtime.*;
import agora.tools.*;
import agora.objects.*;
import java.io.*;

/**
 * @author: Wolfgang De Meuter(Programming technology Lab).
 * This class represents user messages in Agora. Each UserMessage contains an
 * expression as the receiver of that message, and a UserPattern as the name
 * of the message and possible arguments.
	Last change:  E    16 Nov 97    2:26 pm
 */

public class UserMessage extends Message implements Serializable
{

  /**
    Creates a new message expression.
   * @param receiver The expression representing the receiver.
   * @param pattern The expression representing the name and arguments of the message.
   */
  public UserMessage (Expression receiver,UserPattern pattern)
  {
    super(receiver,pattern);
  }
  
  /**
   * Evaluates the user message in the given context.
   * @param context A valid context containing the current object's parts.
   * @return The resulting Agora object.
   * @exception agora.errors.AgoraError When something goes wrong during the evaluation.
   */
  public AgoraObject eval(Context context) throws AgoraError
  {
    try
      {
	AgoraIO.checkEscape();
	Client client     = this.pattern.makeClient(context,null);
	client.actualsEval(context);
	return this.receiver.eval(context).send(this.pattern.makePattern(context),client);
      }
    catch (AgoraError ex)
      {
	ex.setCode(this);
	throw ex;
      }
  }
}