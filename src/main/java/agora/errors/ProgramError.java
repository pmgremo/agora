package agora.errors;

import java.lang.*;
import java.io.*;

/**
   This class represents general program agora.errors that occured during the
   execution of an Agora program. This can be anything except for the
   special purpose agora.errors that are in this class hierarchy.
	Last change:  E    16 Nov 97    1:36 am
*/
public class ProgramError extends AgoraError implements Serializable
{
  /**
     The error message.
  */
  protected String msg;
  
  /**
     Creates a new program error with the given message.
     @param msg The error message to be displayed when reporting this error.
  */
  public ProgramError(String msg)
    {
      super();
      this.msg = msg;
    }
  
  /**
     Signals the error message in a dialog box. No receiver can be inspected.
  */
  public void signal()
    {
      this.setUpDialog("Program Error:\n\n"+this.msg,null);
    }
  
  /**
     Accesses the error message.
     @return The error message to be displayed when reporting this error.
  */
  public String getErrorMessage()
    {
      return this.msg;
    }
}

