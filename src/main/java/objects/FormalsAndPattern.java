package objects;

import patterns.*;
import java.io.*;

/**
   THis class represents a simple record that is used for collecting
   the pattern, category and formal parameters when walking
   through a list of modifiers like LOCAL PUBLIC MIXIN CLONING METHOD:.
   Since the arguments in this records are accessed hundreds of times
   when evaluating an Agora program, we have decided to make the
   items publically available, for efficiency reasons.
   @author Wolfgang De Meuter
	Last change:  E    16 Nov 97    1:49 am
*/

public class FormalsAndPattern extends Object implements Serializable
{
  /**
     Formal arguments in a method declaration.
  */
  public String[] formals;
  /**
     Pattern of a method declaration. This can be unary,
     keyword or operator.
  */
  public AbstractPattern pattern;
  /**
     Category of a method declaration. This is determined by the modifiers that
     associate the declaration, e.g. LOCAL, PUBLIC,...
  */
  public int cat;
  
  /**
     Constructor for filling the record in one stroke.
  */
  public FormalsAndPattern(String[] f,AbstractPattern p,int c) 
    {
      formals=f;
      pattern=p;
      cat=c;
    }
}
