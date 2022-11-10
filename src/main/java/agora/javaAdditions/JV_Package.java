package agora.javaAdditions;

import java.lang.*;

import agora.objects.*;
import agora.patterns.*;
import agora.attributes.*;
import java.util.*;
import java.io.*;

/**
   This is the implementation level class representing 'package' agora.objects in Agora.
   Each package abstractly consist of a name (of directories separated by dots).
   If a package is sent 'more' with a string, there are two possibilities.
   1) Either the resulting string denotes a valid class. In this case the 
   class is returned such that an Agora programmer can start making agora.objects
   of that class.
   2) Either the resulting string does not denote a valid class. In that case,
   the string is returned as a new (longer) package.
   @author Wolfgang De Meuter (Programming technology Lab).
	Last change:  E    15 Nov 97   11:52 pm
   */
public class JV_Package extends Object implements Serializable
{
  protected String packageName;
  
  /**
     Creates a new package with the given name.
     @param packageName The name of the package, e.g. "java.lang"
  */
  public JV_Package(String packageName)
    {
      this.packageName = packageName;
    }
  
  /**
     Here we implemented an ad hoc generator creator, because the method
     'more' is to be reified as an operator pattern . in Agora.
     @return A generator containing agora.patterns linked to the procedures in this class.
  */
  public static PrimGenerator generatorJV_Package()
    {
      Hashtable table = new Hashtable(5);
      PrimGenerator result = new PrimGenerator("JV_Package",table,null);
      Class[] argtypes = new Class[1];
      Class thisOne = null;
      try
	{
	  argtypes[0] = Class.forName("java.lang.String");
	  thisOne = Class.forName("agora.javaAdditions.JV_Package");
	}
      catch(ClassNotFoundException e)
	{
	  // Impossible to occur because 'java.lang.String' is surely there.
	}
      try
	{
	  table.put(new OperatorPattern("."),
		    new PrimMethAttribute(thisOne.getMethod("more",argtypes)));
	}
      catch(NoSuchMethodException e)
	{
	  // Can never occur because the method 'more' is here (see below)
	}
      return result;
    }
  
  /**
     This method takes a string. It is added to the package string after a dot.
     If the result is a valid class, the class is returned, otherwise a new
     'package' is created with the resulting string.
     @param s The string to be added to the current string in the package.
     @return If the result of adding the argument to the package is a class in Java,
     that class is returned. Otherwise a new package is created with the concatenation
     of both strings (with an extra dot in between them).
  */
  public  Object more(String s)
    {
      String newName = this.packageName.concat(".".concat(s)); // Determine new name
      try
	{
	  return Class.forName(newName);                // Try to return the class of that name
	}
      catch(Throwable e)
	{
	  return (new JV_Package(newName));             // If it isn't there, create a new package
	}
    }
}
