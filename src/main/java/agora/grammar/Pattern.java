package agora.grammar;

import agora.runtime.*;
import agora.objects.*;
import agora.patterns.*;
import agora.errors.*;
import java.io.*;

/**
   Abstract class denoting Agora agora.patterns, i.e. pattern kinds together with actual arguments.
   @author Wolfgang De Meuter(Programming Technology Lab)
	Last change:  E    16 Nov 97    1:38 am
*/
abstract public class Pattern extends Expression implements Serializable
{
    /**
      A pattern indicates the name of a (receiverless or receiverful) message expression,
      together with the actual arguments. This method constructs a client object with
      the actual arguments. The arguments in the client object are not yet evaluated.
      @param context The evaluation context when the client is needed.
      @param receiver The (already evaluated or upped) receiving expression.
      @return A new client object in which the actual parameters reside.
    */
  public abstract Client   makeClient(Context context,AgoraObject receiver);

  /**
    This method constructs the agora.runtime patter associated to the name of this syntactic
    pattern.
    @param context The evaluation context when the pattern is needed (right before sending.)
    @return A new agora.runtime pattern that can be used for send or delegate.
    */
  public abstract AbstractPattern makePattern(Context context);

  /**
    This method constructs the formal arguments for this pattern expression.
    @param context The environment in which the formals are needed (usually in reifiers).
    @return An array of strings indicating the several formal arguments.
    @exception agora.errors.AgoraError Is thrown when there are wrong formals
    (for example when the pattern is at:3 put:formal2, 3 is not a good formal.)
    */
  public abstract String[] makeFormals(Context context) throws AgoraError;

  /**
  Evaluates the pattern expression in the given context. The category in the context
  determines whether the pattern will really be evaluated (as a receiverless message),
  or whether a new 'FormalsAndPattern' will be returned.
  @param The evaluation context containing the evaluation category.
  @return The Agora object resulting from looking up the pattern in the private (when
  the category is not 'flags', or an upped FormalsAndPattern when the pattern is evaluated
  in the 'flags' category, indicating that the patter must not be looked up, but simply
  be returned to be installed in some object.
  @exception agora.errors.AgoraError When something goes wrong. For example, if we evaluate the
  expression in the 'flags' category, the pattern might not be a valid formal pattern. If we
  evaluated in another category, we might get 'message not understood' if the pattern
  is not found in the private part.
  */
  public abstract AgoraObject eval(Context context) throws AgoraError;
      
      }
