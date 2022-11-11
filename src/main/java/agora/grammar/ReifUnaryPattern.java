package agora.grammar;

import agora.tools.*;
import agora.runtime.*;
import agora.patterns.*;
import agora.objects.*;
import agora.errors.*;
import java.io.*;

/**
  This class represents reifier unary agora.patterns like SELF.
  @author Wolfgang De Meuter (Programming Technology Lab).
	Last change:  E    16 Nov 97    1:43 am
  */
public class ReifUnaryPattern extends ReifPattern implements Serializable
{
  protected String unary;
  
  /**
    Create a new reifier unary pattern.
    @param unary The String representing the unary pattern (e.g. "SELF").
  */
  public ReifUnaryPattern (String unary)
    {
      super();
      this.unary = unary;
    }
  
  /**
    Get the internal string representation of the reifier unary pattern.
    @return The internal string representation.
    */
  public String getUnary()
    {
      return this.unary;
    }
  
  /**
    Unparses the receiving expression.
    @param hor The number of spaces leading the unparsed version.
    @return The string representation of this reifier unary patttern.
    */
  public String unparse(int hor)
    {
      return AwtIo.makeSpaces(hor)+ this.unary;
    }

  /**
    Creates a new client object that can be used to send the agora.runtime variant of
    this syntactic pattern. The client object will contain zero arguments because
    it concerns a unary reifier.
    @param context The context from which the client must be obtained.
    @param receiver The already evaluated (or upped) receiver to which this
    reifier message was sent.
    @return A new Client object containing zero actual arguments.
    */
  public Client makeClient(Context context,AgoraObject receiver)
    {
      return context.newReifierClient(new Object[0]);
    }

  /**
    Construct a new agora.runtime pattern associated to this syntactic pattern.
    @param context The evaluation context that is used to evaluated the message
    expression that uses this pattern.
    */
  public AbstractPattern makePattern(Context context)
    {
      var unary = new UnaryPattern(this.unary);
      unary.setReifier();
      return unary;
    }
  /**
    Create a list of formal names that go with this reifier unary pattern (of course
    this will always return an array with zero formals since it is a unary pattern.
    @param context The evaluation context in which the formals are needed (this will usually
    be the evaluation context of a reifier like REIFIER:IS:.
    @return An array of strings indicating the formal arguments of this pattern.
    @exception agora.errors.AgoraError When something goes wrong (e.g. when it concerns an
    actual pattern instead of a formal pattern).
    */
  public String[] makeFormals(Context context) throws AgoraError
    {
      return (new String[0]);
    }
}
