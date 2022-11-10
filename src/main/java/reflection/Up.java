package reflection;

import java.lang.*;
import java.lang.reflect.*;
import java.util.*;
import java.io.*;
import objects.*;
import errors.*;
import patterns.*;
import tools.*;
import javaAdditions.*;
import attributes.*;
import runtime.*;

/**
   This class consists of procedural code. Normally, the Agora design requires that
   every implementation level object understands the message up(), but since 
   we cannot change the Object of java, we must define Up() as a procedure which
   we can then !apply! to every implementation level object.
   
   @author Wolfgang De Meuter
	Last change:  E    16 Nov 97    1:58 pm
*/
public class Up extends Object implements Serializable
{

  /**
     This is the only instance of this class. The only valid way to
     instantiate this class is:<p>
     Up.glob = new Up();
  */
  static public Up glob;

  /**
     This private variable caches the generators created by Up, such that all the work
     to 'up' a Java implementation level object is done only once. The table maps
     Java class names onto generators that contain the method tables for the
     associated class.
  */
  private Hashtable uptable;
  private VariableContainer primitive; // temporary variable
  
  /**
     This must be called at system-startup time to initialise the cache.
  */
  public Up()
    {
      this.primitive = null;
      this.uptable = new Hashtable(30);
    }

  /**
     This method must be called right after system startup, when the boolean
     objects have been upped. Somewhere, the up version of the booleans define
     a method 'primitive' that returns an upped boolean. Hence, when calling
     Up with a boolean, this upped boolean is not yet known.
     */
  public void fixBooleanCircularity()
    {
      primitive.write(AgoraGlobals.glob.cachedUppedBoolean(true));
    }

  /**
     This is a  public procedure.
     It wraps any Java implementation level object into a corresponding Agora object.
     It reads the fields, methods and constructors of the class of the object, and turns
     them into an Agora method table.
     @param o The object of which the upped Agora version is needed.
     @return The upped version of the input. This upped version understands
     send and down.
     @exception errors.AgoraError When something goes wrong during the upping. For example,
     a generatorcreator might be invoked that accesses a Java method that does not exists.
  */
  public AgoraObject up(Object o) throws AgoraError
    {
      if (o == null) // If null object is to be upped, take JV_Nil (reason: in Agora, null is an object!)
	{
	  try
	    {
	      return (new PrimIdentityGenerator("Object",
						generatorFor(Class.forName("javaAdditions.JV_Nil"),true)
						,new JV_Nil())).wrap();
	    }
	  catch (ClassNotFoundException e)
	    {
	      throw new ProgramError("Something went wrong when upping JV_Nil.");
	    }
	}
      else
	{
	  Class c = o.getClass();
	  if (o instanceof Class) // Dirty type cast because Java has no classes of classes of ...
	    return (new PrimIdentityGenerator("Class",generatorFor((Class)o,false),o)).wrap();
	  else
	    return (new PrimIdentityGenerator("Object",generatorFor(c,true),o)).wrap();
	  
	}
    }
  
  /**
     This private procedure generators a generator (i.e. a lookup table that understands 'delegate')
     for a Java implementation level class. It is called by 'Up' with the class of the object.
     It checks whether the method table is already in the cache. If so, it is simply returned.
     Otherwise, the method table is constructed and put in the cache for the next usage. The second
     argument indicates whether object attributes (apart from class attributes like constructors
     and statics) should also be considered
  */
  private PrimGenerator generatorFor(Class c,boolean isInstance) throws AgoraError
    {
      String name = c.getName();
      if (!isInstance)
	name = name + "CLASS";
      Object  methodTableOfc = uptable.get(name);
      if (methodTableOfc == null)
	{
	  methodTableOfc = createGeneratorFor(c,isInstance);
	  uptable.put(name,methodTableOfc);
	}
      return (PrimGenerator)methodTableOfc; // Downcast because methodTableOfc is an Object because of the hashtables
    }
  
  /**
     This procedure really creates the generator for the class by recursively
     traversing the class hierarchy and creating (or looking up in the cache)
     the generator for the subclasses. The generator for 'java.lang.Object' is linked
     to the root of the Agora system.
  */
  private PrimGenerator createGeneratorFor(Class c,boolean isInstance) throws AgoraError
    {
      AbstractGenerator superMethodTable = null;
      if (c.getName().equals("java.lang.Object"))
	superMethodTable = AgoraGlobals.glob.rootIdentity;             // Link table to the root table of Agora
      else   
	superMethodTable = generatorFor(c.getSuperclass(),isInstance); // Link table to table of the super
      
      PrimGenerator methodTableOfc = null; // The method table of this class
      Method generatorCreator;             // Does the Java class have an ad hoc 'generator creator' method ??
      
      try  // See if there is an ad hoc 'generator creator' for this class c
	{
	  generatorCreator = c.getMethod("generator"+unqualified(c.toString()),new Class[0]);
	}
      catch (NoSuchMethodException e)
	{
	  generatorCreator = null;
	}
      
      if ((generatorCreator != null)&isInstance) // 1) If there is an ad hoc generator creator, call it!
	{
	  int modifiers = generatorCreator.getModifiers();
	  if (!(Modifier.isPublic(modifiers)&&Modifier.isStatic(modifiers)))
	    throw (new ProgramError("'generator"+c.getName()+"' method must be public static"));
	  try
	    {
	      methodTableOfc = (PrimGenerator)generatorCreator.invoke(null,new Object[0]);
	      PrimGenerator asIsTable = constructGeneratorFor(c,isInstance);
	      asIsTable.setParent(superMethodTable);
	      superMethodTable = asIsTable;
	    }
	  catch(IllegalAccessException e)
	    {
	      throw (new ProgramError("'wrong modifiers for 'generator"+c.getName()+"'"));
	    }
	  catch(InvocationTargetException e)
	    {
	      if (e.getTargetException() instanceof AgoraError) // We cannot avoid this downcast
		throw ((AgoraError)e.getTargetException());
	      else
		throw (new PrimException(e.getTargetException(),"Up::createGeneratorFor"));
	    }
	}
      else // 2) If there is no ad hoc generator creator, we create the generator ourself!
	{	
	  methodTableOfc = constructGeneratorFor(c,isInstance);
	}
      
      methodTableOfc.setParent(superMethodTable);
      
      //Natives have special wrappers that contain operations not supported in java.lang
      if (c.getName().equals("java.lang.Object")) // Hack to make all upped objects primitive.
	{
	  UnaryPattern unary = new UnaryPattern("primitive");
	  if (primitive == null)
	    primitive = new VariableContainer(null); // make a reference, because the container has to be adjusted later.
	  VarGetAttribute getter = new VarGetAttribute(primitive);
	  methodTableOfc.installPattern(unary,getter);
	}
	  
      if (c.getName().equals("java.lang.Integer")) // Treat integers
	{
	  PrimGenerator operators = JV_Integer.generatorJV_Integer();
	  operators.setParent(methodTableOfc);
	  return operators;
	}
      else
	if (c.getName().equals("java.lang.Float")) // Treat floats
	  {
	    PrimGenerator operators = JV_Float.generatorJV_Float();
	    operators.setParent(methodTableOfc);
	    return operators;
	  }
	else
	  if (c.getName().equals("java.lang.Boolean")) // Treat booleans
	    {
	      PrimGenerator operators = JV_Boolean.generatorJV_Boolean();
	      operators.setParent(methodTableOfc);
	      return operators;
	    }
	  else
	  return methodTableOfc;
    }
  
  /**
     This procedure is called by 'createGeneratorFor' that recursively walks through a hierarchy.
     The procedure creates a generator for a single class. It is called by 'createGeneratorFor'
     for every class in the hierarchy.
  */
  private PrimGenerator constructGeneratorFor(Class c,boolean isInstance) throws AgoraError
    {
      JV_Queue q = new JV_Queue();  // Make a new Queue
      putFieldsInQueue(c,q,isInstance);        // Insert patterns and fields
      putMethodsInQueue(c,q,isInstance);       // Insert patterns and methods
      putConstructorsInQueue(c,q,isInstance);  // Insert patterns and constructors
      int sz = q.size() / 2;        // queue = (pat,att)(pat,att)....(pat,att)
      Hashtable theTable = new Hashtable(sz+1);//The size may not be zero, so add one
      for (int j=0; j<sz; j++)
	{
	  Object pattern = q.deQueue(); // These statements are really necessary because
	  Object attrib  = q.deQueue(); // if we would simply write put(q.deQueue(),q.deQeueue)
	  theTable.put(pattern,attrib); // the wrong order could be used (if Java does it right to left)
	}
      return (new PrimGenerator(c.getName(),theTable,null));    // Create a new generator with the members
    }
  
  /**
     This procedure reads all the fields (i.e. data members) of a class.
     It creates appropriate read and write Agora attributes and puts them 
     all in a queue.
  */
  private void putFieldsInQueue(Class c,JV_Queue q,boolean isInstance)
    { // Create a pattern and an attribute for every publically accessible field
      Field[] fields   = c.getFields();
      for (int j=0; j<fields.length ; j++)
	{
	  if (Modifier.isPublic(fields[j].getModifiers())&& 
	      (!Modifier.isAbstract(fields[j].getModifiers()))&&
	      (!Modifier.isPrivate(fields[j].getModifiers()))&&
	      (!Modifier.isProtected(fields[j].getModifiers()))&&
	      (!Modifier.isInterface(fields[j].getModifiers()))&&
	      (fields[j].getDeclaringClass().equals(c))&&
	      (isInstance | (Modifier.isStatic(fields[j].getModifiers())))) // not isInstance -> isStatic
	    {
	      q.enQueue(createVariableReadPatFor(fields[j]));
	      q.enQueue(createVariableReadAttFor(fields[j]));
	      if (!(Modifier.isFinal(fields[j].getModifiers())))
		{ // Only a write pattern if the field is non-final (i.e. not constant)
		  q.enQueue(createVariableWritePatFor(fields[j]));
		  q.enQueue(createVariableWriteAttFor(fields[j]));
		}
	    }
	}
    }
  
  /**
     This procedure reads all the methods in a class. It creates an appropriate
     Agora attribute and puts a pattern for the methods together with the attribute in
     a queue.
  */
  private void putMethodsInQueue(Class c,JV_Queue q,boolean isInstance)
    { // Create a pattern and a method attribute for every publically accessible method
      Method[] methods = c.getMethods();
      for (int j=0; j<methods.length ; j++)
	{
	  if (Modifier.isPublic(methods[j].getModifiers())&&
	      (!Modifier.isAbstract(methods[j].getModifiers()))&&
	      (!Modifier.isPrivate(methods[j].getModifiers()))&&
	      (!Modifier.isProtected(methods[j].getModifiers()))&&
	      (!Modifier.isInterface(methods[j].getModifiers()))&&
	      (methods[j].getDeclaringClass().equals(c))&&
	      (isInstance | (Modifier.isStatic(methods[j].getModifiers())))) // not isInstance -> isStatic
	    {
	      q.enQueue(createMethodPatFor(methods[j]));
	      q.enQueue(createMethodAttFor(methods[j]));
	    }
	}
    }
  
  /**
     This procedure reads all the constructors in a class. For each constructor,
     a pattern 'new' is created and the appropriate Agora attribute is constructed.
     All the patterns and the attributes are gathered together in a queue.
  */
  private void putConstructorsInQueue(Class c,JV_Queue q,boolean isInstance)
    { // Create a pattern and a cloning method for every publically accessible constructor
      Constructor[] constructors = c.getConstructors();
      for (int j=0; j<constructors.length ; j++)
	{
	  if (Modifier.isPublic(constructors[j].getModifiers())&&
	      (!Modifier.isNative(constructors[j].getModifiers()))&&
	      (!Modifier.isAbstract(constructors[j].getModifiers()))&&
	      (!Modifier.isPrivate(constructors[j].getModifiers()))&&
	      (!Modifier.isProtected(constructors[j].getModifiers()))&&
	      (!Modifier.isInterface(constructors[j].getModifiers()))&&
	      (constructors[j].getDeclaringClass().equals(c))&&
	      (!isInstance))
	    {
	      q.enQueue(createConstructorPatFor(constructors[j]));
	      q.enQueue(createConstructorAttFor(constructors[j]));
	    }
	}
    }
  
  /**
     Creates a Variable Read pattern for a field f. 
  */
  private AbstractPattern createVariableReadPatFor(Field f)
    {
      return (new UnaryPattern(decaps(f.getName())));
    }
  
  /**
     Creates a variable read attribute for f.
  */
  private Attribute createVariableReadAttFor(Field f)
    {
      return new PrimVarGetAttribute(f);
    }
  
  /**
     Creates a variable write pattern for a field f, i.e. the name of f with a colon.
  */
  private AbstractPattern createVariableWritePatFor(Field f)
    {
      KeywordPattern writePat = new KeywordPattern(1);
      writePat.atPut(0,decaps(f.getName()) + ":");
      return writePat;
    }
  
  /**
     Creates a variable write attribute for a field f. This is only called when
     the field is not final.
  */
  private Attribute createVariableWriteAttFor(Field f)
    {
      return new PrimVarSetAttribute(f);
    }
  
  /**
     Creates a method pattern for a java method. Depending on the
     signature of the Java method, a unary, an operator or a
     keyword pattern is created.
  */
  private AbstractPattern createMethodPatFor(Method m)
    {
      Class[] types = m.getParameterTypes();
      if (types.length == 0)
	return  new UnaryPattern(m.getName());
      else
	{
	  KeywordPattern pat = new KeywordPattern(types.length);
	  pat.atPut(0,decaps(m.getName())+typeNameFor(types[0])+":");
	  for (int j=1 ; j<types.length; j++)
	    pat.atPut(j,typeNameFor(types[j])+":");
	  return pat;
	}
    }
  
  /**
     This procedure creates a method attribute for the given
     Java implementation level method.
  */
  private Attribute createMethodAttFor(Method m)
    {
      return new PrimMethAttribute(m);
    }
  
  /**
     This procedure creates a cloning method pattern for a given constructor.
     Depending on the signature of the constructor, a unary pattern or a keyword
     pattern is created.
  */
  private AbstractPattern createConstructorPatFor(Constructor c)
    {
      AbstractPattern pat = null;
      Class[] types = c.getParameterTypes();
      if (types.length ==0)
	pat = new UnaryPattern("new");
      else 
	{
	  if (types.length == 1)
	    {
	      pat = new KeywordPattern(1);
	      ((KeywordPattern)pat).atPut(0,"new"+typeNameFor(types[0])+":");
	    }
	  else
	    {
	      pat = new KeywordPattern(types.length);
	      ((KeywordPattern)pat).atPut(0,"new"+typeNameFor(types[0])+":");
	      for (int j=1; j<types.length; j++)
		((KeywordPattern)pat).atPut(j,typeNameFor(types[j])+":");
	    }
	}
      return pat;
    }
  
  /**
     Creates a cloning method for the given constructor c.
  */
  private Attribute createConstructorAttFor(Constructor c)
    {
      return new PrimCloningAttribute(c);
    }
  
  /**
     Determined the unqualified, decapitalized name of a class. Array information
     is also removed. Hence, the 'real' type name results.
  */
  private String typeNameFor(Class s)
    {
      return decaps(unqualified(unArrayed(s))); // Remove [], remove package structure, remove capitals
    }
  
  /**
     Removes the package qualifiers from a class name.
  */
  private String unqualified(String s)
    {
      if (s.length() == 0)
	return s;
      int pos = s.length()-1;
      while (s.charAt(pos) != '.')
	{
	  pos--;
	  if (pos==-1)
	    return s;
	}
      if (pos != -1)
	return s.substring(pos+1,s.length());
      else
	return s;
    }
  
  /**
     Removes all the array information from a class. For example,
     the type Vector[][] is transformed into VectorAA.
  */
  private String unArrayed(Class c)
    {
      if (c.isArray())
	return (unArrayed(c.getComponentType()) + "A");
      else
	return c.getName();
    }
  
  /**
     Transforms a string into its fully decapitalized version.
  */
  private String decaps(String s)
    {
      char[] arr = s.toCharArray();
      boolean allcaps = true;
      for (int i = 0; i<arr.length; i++)
	allcaps = allcaps & (((arr[i] >= 'A') & (arr[i] <= 'Z')) | (arr[i] == '_'));
      if (allcaps)
	return "j" + s;
      else
	return s;
    }
}

