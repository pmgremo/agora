package objects;

import objects.*;
import errors.*;
import runtime.*;
import patterns.*;
import java.util.*;
import java.awt.*;
import tools.*;
import java.io.*;

/**
  This class of empty generators is the closing generator of all object generators.
  Characteristic for an empty generator is that it is on top of the inheritance hierachy.
  Each message that is delegated to it results in a Message not understood error. Hence,
  it contains no methods.
  @author Wolfgang De Meuter (Programming Technology Lab)
	Last change:  E    17 Nov 97    1:03 am
  */
public class EmptyGenerator extends AbstractGenerator implements Serializable
{
  /**
  Create a new empty generator.
  @param nameOfFrame This is the name of the generator to be used in inspectors.
  This will be usually something like "Root Object"
  */
  public EmptyGenerator(String nameOfFrame)
    {
      super(nameOfFrame);
    }

  /**
     Adds a view on the generator. This is normally used to temporarily extend
     a user created object, for example for applying a view-attribute on the object.
     @param nameOfFrame The name of the new generator to be used in inspectors.
     @return A newly created InternalGenerator with the receiver as parent generator.
     @exception errors.AgoraError For some generators, it is impossible to add a layer around
     them (i.e. we cannot inherit from primitive objects). If this happens, the exception is
     thrown.
  */
  public AbstractGenerator funcAddLayer(String NameOfFrame) throws AgoraError
    {
      //Should never ocur
      return null;
    }
  
  /**
     Looks up a message in the generator. The context contains the information
     of the object determined 'so far'. The first parameter is the message
     being looked up. The client argument contains the actual parameters.
     @param msg The message pattern to be delegated.
     @param client The client object containing the actual parameters.
     @param context The knowledge of the receiving object so far.
     @return The AgoraObject corresponding to evaluating the attribute associated to the
     specified pattern.
     @exception errors.AgoraError When the message is not understood or when an error occurs
     during evaluation of the method associated to the pattern.
  */
  public AgoraObject delegate(AbstractPattern msg,
			      Client client,
			      Context context) throws AgoraError
    {
      throw (new MessageNotUnderstood(msg,context.getSelf().wrap()));
    }
  
  /**
     Opens an inspector for the object.
     @param context The context in which the inspect is sent. This is needed to show
     object values in the inspector (the values are in the context parts).
     @exception errors.AgoraError If an error occurs during inspection, this exception is
     thrown. This would be a bug somewhere in the implementation.
  */
  public void inspect(Context context) throws AgoraError
    {
      Inspector d = new Inspector(AgoraGlobals.glob.agoraWindow,
				  "Empty Method Frame",
				  null,
				  null,
				  null,
				  null,
				  null,
				  context);
      d.pack();
      d.show();
    }
  
  /**
     Makes a deep clone of the generator by copying all the constituents of the
     identity. The parameter is a clone map such that a thing is not copied twice.
     @param cloneMap A table of already-copied-things such that nothing gets copied twice.
     @return A deep copy of the receiver.
  */
  public Object copy(Hashtable cloneMap)
    {
      Object myclone = cloneMap.get(this);
      if (myclone != null)
	return myclone;
      else
	{
	  EmptyGenerator newclone = new EmptyGenerator(this.getFrameName());
	  cloneMap.put(this,newclone);
	return newclone;
	}
    }
  
}
