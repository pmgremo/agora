package objects;

import java.util.*;
import tools.*;
import patterns.*;
import runtime.*;
import errors.*;
import attributes.*;
import java.awt.*;
import java.io.*;

/**
  MethodsGenerator is the abstract class of internal method frames of objects. Currently
  it has two subclasses: InternalGenerator and PrimGenerator. InternalGenerator is the
  type of generator ex nihilo created objects use. PrimGenerator is the type of generator
  upped primitives use.
  @author Wolfgang De Meuter (Programming technology Lab).
	Last change:  E    16 Nov 97    1:48 am
  */
public abstract class MethodsGenerator extends AbstractGenerator implements Serializable
{
  /**
    Each methodsGenerator has a hashtable that links patterns to attributes.
    */
  protected Hashtable theMethodTable;
  /**
    Each methodsGenerator is linked to another generator, which is the next generator
    in the inheritance chain.
    */
  protected AbstractGenerator parent;
  
  /**
    Create a new methods generator.
    @param nameOfFrame The name of the methodsgenerator to be used in inspectors.
    @param table The initial hashtable of the methodsgenerator.
    @param parent The initial parent link of the MethodsGenerator.
    */
  public MethodsGenerator (String nameOfFrame,
			   Hashtable table,
			   AbstractGenerator parent)
    {
      super(nameOfFrame);
      this.theMethodTable = table;
      this.parent = parent;
    }
  
  /**
    Re-assigns the parent of this generator.
    @param newParent The new parent after doing the re-assigning.
    */
  public void setParent(AbstractGenerator newParent)
    {
      this.parent = newParent;
    }

  /**
    Returns the parent generator of the receiving generator.
    @return The parent-of link of this generator.
    */
  public AbstractGenerator getParent()
    {  
      return this.parent;
    }

  /**
    Returns the internal methods table of this generator.
    @return A hashtable being the internal table of (pattern,attribute) bindings.
    */
  public Hashtable getHashTable()
    {
      return this.theMethodTable;
    }

  /**
    To re-assign the internal methods hashtable.
    @param newTable The new hashtable after doing the re-assigning.
    */
  public void setHashTable(Hashtable newTable)
    {
      this.theMethodTable = newTable;
    }

  /**
    Installs a new attribute in the methods table of this generator.
    @param pattern The new runtime pattern serving as a key in the hashtable.
    @param attribute The attribute that will be the value associated to the pattern.
    */
  public void installPattern(AbstractPattern pattern,Attribute attribute)
    {
      this.theMethodTable.put(pattern,attribute);
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
  public abstract InternalGenerator funcAddLayer(String nameOfFrame) throws AgoraError;

  /**
    Some subclasses of this class have an extra pointer to a private part. This method
    assigns this private part. This method is actually an indication of bad design since
    not all sublasses feature a private part. These will raise an exception upon invoking this
    method.
    @param newPrivate The new value of the private part to be installed in the methods generator.
    @exception errors.AgoraError MethodsGenerator's that don't feature a private part will
    throw this error when we try to re-assign their private part.
    */
  public abstract void setPrivate(InternalGenerator newPrivate) throws AgoraError;
}
