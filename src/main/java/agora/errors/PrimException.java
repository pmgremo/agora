package agora.errors;

import java.io.*;

/**
   This class represents a wrapper for a primitive exception. That is, when
   underlying Java generates an exception, this class represents the Agora
   variant for it. The original exception is stored inside the wrapper.
   @author Wolfgang De Meuter (Programming Technology Lab).
	Last change:  E    16 Nov 97    1:36 am
   */
public class PrimException extends AgoraError implements Serializable
{
  /**
     The original exception.
  */
  protected Throwable exception;
  /**
     The name of the Java method in which the excpetion occured.
  */
  protected String javaMethodName;
  
  /**
     Create a new primitive exception with the given Java exception and the given
     name of the Java method in which the exception occured.
     @param exception The primitive exception that was thrown.
     @param javaMethod The name of the Java method that threw the primitive exception.
  */
  public PrimException(Throwable exception,String javaMethod)
    {
      super();
      this.exception = exception;
    this.javaMethodName = javaMethod;
    }
  
  /**
     Accesses the original Java exception.
     @return The primitive exception as specified in the constructor.
  */
  public Throwable getException()
    {
      return exception;
    }
  
  /**
     Signals the exception by explaining to the programmer
     that a native exception has occured. The native exception is printed together with
     the name of the method in which it occured. No receiver can be inspected.
  */
  public void signal()
    {
      this.setUpDialog("Native Java Exception: "+exception.toString()+"\nIn the method :"+this.javaMethodName,null);
    }
}
