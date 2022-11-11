package agora.patterns;

import agora.Copyable;

import java.io.Serializable;
import java.util.Hashtable;

/**
  This abstract class represents agora.runtime Agora agora.patterns. There are three subclasses of this
  class:UnaryPattern, OperatorPattern and KeywordPattern.
  
  @author Wolfgang De Meuter
	Last change:  E    16 Nov 97    1:50 am
  */
public abstract class AbstractPattern implements Serializable, Copyable<AbstractPattern>
{
  /** 
    This variable indicates whether it concerns a reifier pattern or an
    ordinary pattern
    */
  protected boolean reifierPattern;
  
  /**
    Calculating the hashvalue of a pattern each time it is looked up in a dictionary
    (during method lookup) is a waste of CPU time. Therefore we memoize it the first
    time it is computed.
    */
  protected int hashValue; // Optimisation
  
  /**
    The default constructor initializes the pattern. The boolean 'reifierPattern' is set 
    to false.
    */
  public AbstractPattern()
  {
    this.reifierPattern = false;
    hashValue=0;
  }

  /**
    Sets the 'reifierPattern' variable to 'true'.
    */
  public void setReifier()
  {
    this.reifierPattern = true;
  }
  
  /**
    Reads the 'reifierPattern' variable.
    @return A boolean indicating whether the receiver is a reifier pattern or not.
    */
  public boolean isReifier()
  {
    return this.reifierPattern;
  }
  
  /**
    Tests whether the argument object is the same pattern. This is not a 'pointer equality',
    but a deep compare of the several variables inside a pattern.
    @param object An arbitrary object to be compared with the receiver.
    @return If the argument is also a pattern, and the 'reifier value' (i.e. the boolean) is the
    same as that of the receiver, true is returned. Otherwise false is returned.
    */
  public boolean equals(Object object)
  {
    if (object instanceof AbstractPattern)
      return (this.reifierPattern==((AbstractPattern)object).isReifier());
    else
      return false;
  }
  
  /**
    Internal hashing method. Depends on the type of pattern we are dealing with.
    @return The hash value of the receiver.
    */
  protected abstract int doHash();
  
  /**
    Overides the same method in 'Object'. Calls the internal protected hashing method.
    The hash result is cached such that it get computed only once.
    @return The hash value of the receiver.
    */
  public int hashCode()
  {
    if (hashValue == 0)
      hashValue = this.doHash();
    return hashValue;
  }
  
  /**
    Overrides the same method in 'Object' to abstract. Hence, it MUST be overriden in each pattern.
    */
  public abstract String toString();
}
