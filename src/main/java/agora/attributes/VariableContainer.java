package agora.attributes;

import agora.objects.*;
import java.util.*;
import java.io.*;

/**
   When a variable is declared, a read and write slot are created. These two agora.attributes must point to
   the same structure such that they stay consistent. That's why they both point to a container like this.
	Last change:  E    16 Nov 97    1:57 am
*/
public class VariableContainer extends Object implements Serializable
{
  protected AgoraObject variableValue;
  
  /**
     Create a new container with the argument as initial value. This value must always be a valid
     AgoraObject.
     @param value The AgoraObject with which the variable container is initialised.
  */
  public VariableContainer (AgoraObject value)
    {
      this.variableValue = value;
    }
  
  /**
     Method to write the variable container.
     @param value The new value of the container.
  */
  public void write (AgoraObject value)
    {
      this.variableValue = value;
    }
  
  /**
     Method to read the variable container.
     @return The value held by the container.
  */
  public AgoraObject read ()
    {
      return (this.variableValue);
    }
  
  /**
     Copying the variable container consists of looking it up in the cloning map.
     If it is not there, it is really copied. Otherwise the copy is returned.
     @param cloneMap A table of already-copied-things such that nothing gets copied twice.
     @return A copy of this attribute.
  */
  public Object copy(Hashtable cloneMap)
    {
      Object myclone = cloneMap.get(this);
      if (myclone != null)
	return myclone;
      else
	{
	  VariableContainer newclone = new VariableContainer(this.variableValue);
	  cloneMap.put(this,newclone);
	  return newclone;
	}
    }
  
}
