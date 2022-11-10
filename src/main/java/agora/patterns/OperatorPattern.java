package agora.patterns;

import java.io.*;

/**
   Concrete class representing agora.runtime operator agora.patterns. An operator pattern
   is essentially nothing but a string (containing the operator symbols) with
   the appropriate methods defined on it.
   @author Wolfgang De Meuter (Programming Technology Lab).
	Last change:  E    16 Nov 97    1:50 am
  */
public class OperatorPattern extends AbstractPattern implements Serializable
{
  /**
    String representing the operator.
    */
  protected String operator;
  
  /**
    Creates a new operator pattern that is 'non-reifier' by default.
    @param theOperator The string representation of the new operator pattern.
    */
  public OperatorPattern(String theOperator)
  {
    super();
    this.operator = theOperator;
  }
  
  /**
    Internal hashing method.
    @return The hash value of the operator pattern.
    */
  protected int doHash()
  {
    var i=0;
    for (var j = 0; j<this.operator.length(); j++)
      i = i + this.operator.charAt(j);
    return i;
  }
  
  /**
    Equality testing. If the argumnt is an operator pattern, both strings are tested
    for equality. This method overrides the corresponding method in 'java.lang.Object'.
    @param object An arbitrary object to be compared against this operator pattern.
    @return If the argument is also an operator pattern, and it has the same operator,
    and if the super.equals gives true, the result of this method is true. In all other
    cases, the result is false.
    */
  public boolean equals(Object object)
  {
    if (object instanceof AbstractPattern)
      if (super.equals(object))
	if (object instanceof OperatorPattern)
	  return (this.operator.equals(((OperatorPattern)object).getOperatorPattern()));
    return false;
  }
  
  /**
    Reads the string representing the operator pattern.
    @return The internal string representation of this operator pattern.
    */
  public String getOperatorPattern() 
  {
    return this.operator;
  }
  
  /**
    Converts the operator to a plain string. Overrides the corresponding method
    in 'java.lang.Object'.
    @return This operator pattern in string form.
    */
  public String toString()
  {
    return this.operator;
  }
}
