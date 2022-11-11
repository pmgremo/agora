package agora.attributes;

import java.util.*;
import java.io.*;

/**
   This is the abstract class of primitive agora.attributes. A primitive attribute is an Agora attribute
   that is a link to a corresponding Java attribute. Examples are fields, methods and constructors.
   @author Wolfgang De Meuter (Programming Technology Lab).
	Last change:  E    16 Nov 97    1:34 am
*/
public abstract class PrimAttribute implements Attribute
{
  /**
     Default constructor: does nothing but caaling the super.
  */

  public PrimAttribute ()
    {
      super();
    }
}
