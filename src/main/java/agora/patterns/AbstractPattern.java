package agora.patterns;

import agora.Copyable;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Objects;

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
    The default constructor initializes the pattern. The boolean 'reifierPattern' is set 
    to false.
    */
  public AbstractPattern()
  {
    this.reifierPattern = false;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AbstractPattern that = (AbstractPattern) o;
    return reifierPattern == that.reifierPattern;
  }

  @Override
  public int hashCode() {
    return Objects.hash(reifierPattern);
  }
}
