package agora.attributes;

import java.util.*;
import java.io.*;

import agora.Copyable;
import agora.runtime.*;
import agora.errors.*;
import agora.patterns.*;
import agora.objects.*;

/**
   Each AgoraObject consists of a linked structure with method tables.
   Inside these method tables, agora.patterns are related in a dictionary to waht we call
   agora.attributes. Several kinds of agora.attributes exist, such as methods, mixin methods,...
   This class is the abstract class every attribute has to be a subclass of.
   @author Wolfgang De Meuter(Programming Technology Lab).
	Last change:  E    16 Nov 97    1:34 am
*/
public abstract class Attribute implements Serializable, Copyable<Attribute>
{
  
  /**
     Default constructor: does nothing.
  */
  public Attribute ()
    {
      super();
    }
  
  /**
     To copy an attribute in a given clone map. The clone map takes care an attribute is copied only
     once. Must be overriden.
     @param cloneMap A table of already-copied-things such that nothing is copied twice.
     @return A copy of the attribute.
  */
  public abstract Attribute copy(Hashtable cloneMap);
  
  /**
     Each attribute must implement how 'to do itself'. For example, doing methods
     will evaluate their body, doing variableread agora.attributes will read their variable,
     and so on. This method is abstract. The result must always be an AgoraObject.
     @param msg The message whose delegation gave rise to the invocation.
     @param client The client object containing the actual arguments.
     @param context The context of the object in which this attribute resides.
     @exception agora.errors.AgoraError When something goes wrong during evaluation.
  */
  public abstract AgoraObject doAttributeValue(AbstractPattern msg, Client client, Context context) throws AgoraError;

  /**
     Inspects the attribute in a given context.
     @param context The context of the object in which this attribute resides.
     @exception agora.errors.AgoraError When something goes wrong while inspecting.
     @return The attribute as a string, ready to be displayed in the inspector.
  */
  public abstract String inspect(Context context) throws AgoraError;
}
