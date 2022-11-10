package javaAdditions;

import errors.*;
import java.util.*;
import objects.*;
import attributes.*;
import patterns.*;
import java.lang.reflect.*;
import runtime.*;
import tools.*;
import java.io.*;

/**
   When the Java 'null' object is wrapped into the Agora 'null' object,
   this will not work since 'null' has no class in Java, and hence
   it has no methods. That's why the Agora object 'null' has a method
   table, whose methods (only one, the = method) have to be somewhere.
   Hence, this class contains static methods (i.e. procedures)
   that contain the code of the methods defined on Agora null.
   Dirty, we know, but it's the only way to make it work!
   @author Wolfgang De Meuter (Programming Technology Lab).
	Last change:  E    16 Nov 97    1:46 am
   */
public class JV_Nil extends Object implements Serializable
{

  public JV_Nil()
  {
    super();
  }

  /**
     When upping 'null', the 'up' routine will check whether this class
     has an ad-hoc generator creator. Well, it has! This is it.
     By convention, when upping an object of class X, the up routine
     will search for a method named generatorX and calls it. THe
     generator creator has to return an appropriate generator.
     @return The generator with the patterns null understands. These patterns
     are linked to the procedures of this class.
     @exception errors.AgoraError Is thrown when something goes wrong while building the
     methods generator (e.g. when you remove a method of this class without adjusting this
     procedure).
     */
  public static PrimGenerator generatorJV_Nil() throws AgoraError
  {
    Hashtable table = new Hashtable(1);
    PrimGenerator result = new PrimGenerator("Public",table,null);
    try
      {
	Class[] argtypes = new Class[1];
	argtypes[0] = Class.forName("java.lang.Object");
	Class thisOne = Class.forName("javaAdditions.JV_Nil");
	table.put(new OperatorPattern("="),new PrimMethAttribute(thisOne.getMethod("equals",argtypes)));
      }
    catch (Throwable e)
      {
	throw (new PrimException(e,"JV_Nil::generatorJV_Nil"));
	// This is impossible because 'equals' is simply here (see below)!!
      }
    return result;
  }
  
  /**
     Overrides the same method in 'java.lang.Object'. If its argument is of type
     JV_Nil, true is returned because there is only one Null object in the
     entire Agora system.
     @param arg The argument of the equality can be of any type.
     @return Returns whether the argument is also the 'null' object.
     */
  public boolean equals(Object arg)
  {
    return (arg  instanceof JV_Nil);
  }
}
