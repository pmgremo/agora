package objects;

import tools.*;
import patterns.*;
import runtime.*;
import java.util.*;
import errors.*;
import java.awt.*;
import objects.*;
import java.io.*;

/**
   A PrimIdentityGenerator represents the part of an Agora object that is the
   very identity of the object, in this case a primitive (upped) object.
   A PrimIdentityGenerator has a name (to be used in the inspector),
   a method table generator (i.e. a public part) and a primitive
   object that is the wrapped object.
   @author Wolfgang De Meuter (Programming Technology Lab)
	Last change:  E    17 Nov 97    1:03 am
*/
public class PrimIdentityGenerator extends IdentityGenerator implements Serializable
{
  
  /**
     To create a new primitive identity generator with a given name, a given public part (normally
     a PrimGenerator) and a Java object that is represented by this primitive identity.
     @param nameOfFrame The name of this generator to be used in inspectors.
     @param theMethodTables A MethodGenerator that is the first methods frame in the lookup
     chain of the primitive object. This lookup chain follows the inheritance hierarchy of the
     upped primitive.
     @param theContents The original primitive object of which this generator is the Agora
     identity.
  */
  public PrimIdentityGenerator (String nameOfFrame,MethodsGenerator theMethodTables,Object theContents)
    {
      super(nameOfFrame,theMethodTables,theContents);
    }
  
  /**
     Adds a view on the identity generator. This is normally used to temporarily extend
     a user created object, for example for applying a view-attribute on the object.
     @param nameOfFrame The name of the new generator to be used in inspectors.
     @return A newly created InternalGenerator with the receiver as parent generator.
     @exception errors.AgoraError For some generators, it is impossible to add a layer around
     them (i.e. we cannot inherit from primitive objects). If this happens, the exception is
     thrown.
  */
  public IdentityGenerator funcAddLayer(String nameOfFrame) throws AgoraError
    {
      throw new ProgramError("Cannot extend a primitive object! (PrimIdentityGenerator::funcAddLayer)");
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
				  this.name,
				  this.myMethods.getHashTable(),
				  null,
				  this.myMethods.getParent(),
				  this.myMethods,
				  this.downedVersion,
				  context);
      d.pack();
      d.show();
    }
  
  /**
     Makes a deep clone of the identity by copying all the constituents of the 
     identity. The parameter is a clone map such that a thing is not copied twice.
     @param cloneMap A table of already-copied-things such that nothing gets copied twice.
     @return A deep copy of the receiver.
  */
  public Object copy(Hashtable cloneMap)
    {
      return this;
    }
}
