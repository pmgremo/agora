package agora.errors;

import java.lang.*;
import java.io.*;

/**
   This error is thrown whenever the programmer misuses a reifier, e.g. when she
   sends SUPER to a number or something like that.
   @author Wolfgang De Meuter(Programming Technology Lab)
	Last change:  E    16 Nov 97    1:36 am
*/
public class ReifierMisused extends AgoraError implements Serializable
{
  protected String message;
  
  /**
     Creates a new ReifierMisused error with the given error message.
     @param message The error message to be displayed when reporting this error.
  */
  public ReifierMisused(String message)
    {
      super();
      this.message = message;
    }
  
  /**
     Method to access the error message.
     @return The error message to be displayed when reporting this error.
  */
  public String getErrorMessage()
    {
      return this.message;
    }
  
  /**
     Signals the error. A dialog with the message is shown, and it is not possible
     to inspect any receiver.
  */
  public void signal()
    {
      this.setUpDialog("Misused Reifier: \n\n"+this.getErrorMessage()+"\n\n",null);
    }
}

