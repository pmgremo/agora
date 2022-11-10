package attributes;

import runtime.*;
import tools.*;
import errors.*;
import java.util.*;
import java.io.*;

/**
   This is the abstract class of primitive attributes. A primitive attribute is an Agora attribute
   that is a link to a corresponding Java attribute. Examples are fields, methods and constructors.
   @author Wolfgang De Meuter (Programming Technology Lab).
	Last change:  E    16 Nov 97    1:34 am
*/
public abstract class PrimAttribute extends Attribute implements Serializable
{
  /**
     Default constructor: does nothing but caaling the super.
  */

  public PrimAttribute ()
    {
      super();
    }
  
  /**
     Characteristic for primitive attributes is that copying them is a trivial operation.
     Just return the attribute.
     @param cloneMap The table of already-copied-things such that nothing gets copied twice.
     @return A copy of the attribute.
  */
  public final Object copy(Hashtable cloneMap)
    {
      return this;
    }
}
