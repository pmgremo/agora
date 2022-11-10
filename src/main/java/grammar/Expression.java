package grammar;

import java.lang.*;
import tools.*;
import runtime.*;
import errors.*;
import objects.*;
import java.util.*;
import patterns.*;
import attributes.*;
import java.lang.reflect.*;
import reflection.*;
import java.io.*;

/**
  This is the abstract class representing nodes in Agora parse trees. All other
  node classes are a subclass of this class.
  
  @author Wolfgang De Meuter
	Last change:  E    16 Nov 97    1:58 pm
  */
abstract public class Expression implements Serializable
{
  /**
    Constructor does nothing but calling the super.
    */
  public Expression ()
  {
    super();
  }
  
  /**
    To unparse the expression towards a string. The integer parameter denotes the number of spaces
    that must precede the expression.
    @param hor The number of space leading the unparsed expression.
    */
  public abstract String unparse(int hor);
  
  /**
    To evaluate the expression in a given context. Might throw an AgoraError.
    @param context The evaluation context.
    @return The value associated to the expression.
    @exception errors.AgoraError Errors occured during evaluation.
    */
  public abstract AgoraObject eval(Context context) throws AgoraError;
  
  /**
    To evaluate the expression in the root context.
    @return The value of the expresssion in the root context.
    @exception errors.AgoraError Errors occuring during evaluation.
    */
  public AgoraObject defaultEval() throws AgoraError
  {
    return this.eval(new Context(AgoraGlobals.glob.rootIdentity,
				 AgoraGlobals.glob.rootPrivate,
				 AgoraGlobals.glob.rootIdentity.getMe(),
				 Category.emptyCategory,
				 AgoraGlobals.glob.rootParent,
				 new AgoraException(this)));
  }
  
  /**
    Besides the standard mapping of Java methods onto Agora patterns, expressions also have
    a special suite of Agora patterns that can be sent to them. This includes all the reifiers
    which have no associated Java syntax.
    @return A Primitive Generator containing the Agora patterns and primitive attributes.
    @exception errors.AgoraError Happens when a method does not exist.
    */
  public static PrimGenerator generatorExpression() throws AgoraError
  {
    Hashtable table = new Hashtable(40);
    PrimGenerator result = new PrimGenerator("Expression",table,null);
    try
      {
	Class[] argtypes1 = new Class[1];
	Class[] argtypes2 = new Class[2];
	Class[] argtypes3 = new Class[3];
	Class[] argtypes4 = new Class[4];
	Class[] argtypes5 = new Class[5];
	argtypes1[0] = Class.forName("runtime.Context");
	argtypes2[0] = Class.forName("runtime.Context");
	argtypes2[1] = Class.forName("grammar.Expression");
	argtypes3[0] = Class.forName("runtime.Context");
	argtypes3[1] = Class.forName("grammar.Expression");
	argtypes3[2] = Class.forName("grammar.Expression");
	argtypes4[0] = Class.forName("runtime.Context");
	argtypes4[1] = Class.forName("grammar.Expression");
	argtypes4[2] = Class.forName("grammar.Expression");
	argtypes4[3] = Class.forName("grammar.Expression");
	argtypes5[0] = Class.forName("runtime.Context");
	argtypes5[1] = Class.forName("grammar.Expression");
	argtypes5[2] = Class.forName("grammar.Expression");
	argtypes5[3] = Class.forName("grammar.Expression");
	argtypes5[4] = Class.forName("grammar.Expression");
	Class thisOne = Class.forName("grammar.Expression");
	UnaryPattern unary = null;
	KeywordPattern keyw = null;

	unary = new UnaryPattern("VARIABLE");
	unary.setReifier();
	table.put(unary, new PrimMethAttribute(thisOne.getMethod("variable",argtypes1)));
	
	unary = new UnaryPattern("VAR");
	unary.setReifier();
	table.put(unary,new PrimMethAttribute(thisOne.getMethod("variable",argtypes1)));
	
	unary = new UnaryPattern("ARRAY");
	unary.setReifier();
	table.put(unary,new PrimMethAttribute(thisOne.getMethod("array",argtypes1)));
	
	unary = new UnaryPattern("PUBLIC");
	unary.setReifier();
	table.put(unary, new PrimMethAttribute(thisOne.getMethod("publik",argtypes1)));
	
	unary = new UnaryPattern("PUB");
	unary.setReifier();
	table.put(unary,new PrimMethAttribute(thisOne.getMethod("publik",argtypes1)));

	unary = new UnaryPattern("LOCAL");
	unary.setReifier();
	table.put(unary,new PrimMethAttribute(thisOne.getMethod("local",argtypes1)));
	
	unary = new UnaryPattern("LOC");
	unary.setReifier();
	table.put(unary,new PrimMethAttribute(thisOne.getMethod("local",argtypes1)));

	unary = new UnaryPattern("JAVA");
	unary.setReifier();
	table.put(unary,new PrimMethAttribute(thisOne.getMethod("java",argtypes1)));

	unary = new UnaryPattern("QUOTE");
	unary.setReifier();
	table.put(unary,new PrimMethAttribute(thisOne.getMethod("quote",argtypes1)));

	unary = new UnaryPattern("UNQUOTE");
	unary.setReifier();
	table.put(unary,new PrimMethAttribute(thisOne.getMethod("unquote",argtypes1)));
	
	unary = new UnaryPattern("UP");
	unary.setReifier();
	table.put(unary,new PrimMethAttribute(thisOne.getMethod("up",argtypes1)));
	
	unary = new UnaryPattern("DOWN");
	unary.setReifier();
	table.put(unary,new PrimMethAttribute(thisOne.getMethod("down",argtypes1)));

	keyw = new KeywordPattern(1);
	keyw.atPut(0,"VARIABLE:");
	keyw.setReifier();
	table.put(keyw,new PrimMethAttribute(thisOne.getMethod("variableColon",argtypes2)));
	
	keyw = new KeywordPattern(1);
	keyw.atPut(0,"VAR:");
	keyw.setReifier();
	table.put(keyw, new PrimMethAttribute(thisOne.getMethod("variableColon",argtypes2)));
	
	keyw = new KeywordPattern(1);
	keyw.atPut(0,"CONST:");
	keyw.setReifier();
	table.put(keyw, new PrimMethAttribute(thisOne.getMethod("constColon",argtypes2)));

	keyw = new KeywordPattern(1);
	keyw.atPut(0,"ARRAY:");
	keyw.setReifier();
	table.put(keyw, new PrimMethAttribute(thisOne.getMethod("arrayColon",argtypes2)));

	keyw = new KeywordPattern(1);
	keyw.atPut(0,"METHOD:");
	keyw.setReifier();
	table.put(keyw,  new PrimMethAttribute(thisOne.getMethod("methodColon",argtypes2)));

	keyw = new KeywordPattern(1);
	keyw.atPut(0,"VIEW:");
	keyw.setReifier();
	table.put(keyw, new PrimMethAttribute(thisOne.getMethod("viewColon",argtypes2)));

	keyw = new KeywordPattern(1);
	keyw.atPut(0,"MIXIN:");
	keyw.setReifier();
	table.put(keyw, new PrimMethAttribute(thisOne.getMethod("mixinColon",argtypes2)));

	keyw = new KeywordPattern(1);
	keyw.atPut(0,"CLONING:");
	keyw.setReifier();
	table.put(keyw, new PrimMethAttribute(thisOne.getMethod("cloningColon",argtypes2)));

	keyw = new KeywordPattern(2);
	keyw.atPut(0,"TRY:");
	keyw.atPut(1,"CATCH:");
	keyw.setReifier();
	table.put(keyw, new PrimMethAttribute(thisOne.getMethod("trycatch",argtypes3)));
	
	keyw = new KeywordPattern(2);
	keyw.atPut(0,"REIFIER:");
	keyw.atPut(1,"IS:");
	keyw.setReifier();
	table.put(keyw, new PrimMethAttribute(thisOne.getMethod("reifierIs",argtypes3)));
	
	keyw = new KeywordPattern(3);
	keyw.atPut(0,"FOR:");
	keyw.atPut(1,"TO:");
	keyw.atPut(2,"DO:");
	keyw.setReifier();
	table.put(keyw, new PrimMethAttribute(thisOne.getMethod("fortodo",argtypes4)));

	keyw = new KeywordPattern(3);
	keyw.atPut(0,"FOR:");
	keyw.atPut(1,"DOWNTO:");
	keyw.atPut(2,"DO:");
	keyw.setReifier();
	table.put(keyw, new PrimMethAttribute(thisOne.getMethod("fordowntodo",argtypes4)));

	keyw = new KeywordPattern(4);
	keyw.atPut(0,"FOR:");
	keyw.atPut(1,"TO:");
	keyw.atPut(2,"BY:");
	keyw.atPut(3,"DO:");
	keyw.setReifier();
	table.put(keyw, new PrimMethAttribute(thisOne.getMethod("fortobydo",argtypes5)));

	keyw = new KeywordPattern(4);
	keyw.atPut(0,"FOR:");
	keyw.atPut(1,"DOWNTO:");
	keyw.atPut(2,"BY:");
	keyw.atPut(3,"DO:");
	keyw.setReifier();
	table.put(keyw, new PrimMethAttribute(thisOne.getMethod("fordowntobydo",argtypes5)));

	keyw = new KeywordPattern(1);
	keyw.atPut(0,"IFTRUE:");
	keyw.setReifier();
	table.put(keyw, new PrimMethAttribute(thisOne.getMethod("ifTrue",argtypes2)));

	keyw = new KeywordPattern(1);
	keyw.atPut(0,"IFFALSE:");
	keyw.setReifier();
	table.put(keyw, new PrimMethAttribute(thisOne.getMethod("ifFalse",argtypes2)));

	keyw = new KeywordPattern(2);
	keyw.atPut(0,"IFTRUE:");
	keyw.atPut(1,"IFFALSE:");
	keyw.setReifier();
	table.put(keyw, new PrimMethAttribute(thisOne.getMethod("ifTrueifFalse",argtypes3)));

	keyw = new KeywordPattern(2);
	keyw.atPut(0,"IFFALSE:");
	keyw.atPut(1,"IFTRUE:");
	keyw.setReifier();
	table.put(keyw, new PrimMethAttribute(thisOne.getMethod("ifFalseifTrue",argtypes3)));

	keyw = new KeywordPattern(1);
	keyw.atPut(0,"WHILETRUE:");
	keyw.setReifier();
	table.put(keyw, new PrimMethAttribute(thisOne.getMethod("whileTrue",argtypes2)));

	keyw = new KeywordPattern(1);
	keyw.atPut(0,"WHILEFALSE:");
	keyw.setReifier();
	table.put(keyw, new PrimMethAttribute(thisOne.getMethod("whileFalse",argtypes2)));

	keyw = new KeywordPattern(1);
	keyw.atPut(0,"UNTILTRUE:");
	keyw.setReifier();
	table.put(keyw,  new PrimMethAttribute(thisOne.getMethod("untilTrue",argtypes2)));

	keyw = new KeywordPattern(1);
	keyw.atPut(0,"UNTILFALSE:");
	keyw.setReifier();
	table.put(keyw, new PrimMethAttribute(thisOne.getMethod("untilFalse",argtypes2)));
 	
	unary = new UnaryPattern("COMMENT");
	unary.setReifier();
	table.put(unary, new PrimMethAttribute(thisOne.getMethod("comment",argtypes1)));
	
      }
    catch (Throwable e)
      {
	throw (new PrimException(e,"Expression::generatorExpression"));
	// This is impossible!!
      }
    return result;
  }
  
  /**
    Code to evaluate the VARIABLE reifier message in a given context. A new pair of
    read and write methods will be installed in the self of the context.
    @param context The context of evaluation when sending the reifier
    @return The initial value of the variable:null.
    @exception errors.AgoraError Happens when illegal patterns are given.
    */
  public AgoraObject variable(Context context) throws AgoraError
    {
      FormalsAndPattern  leftside= (FormalsAndPattern)this.eval(context.setCat(Category.flags)).down();
      AbstractPattern thePattern = leftside.pattern;
      int     theCat     = leftside.cat;
      // Validate Pattern
      if (!(thePattern instanceof UnaryPattern))
	throw (new ReifierMisused("VARIABLE can only be sent to unary patterns"));
      if (!(Category.containsLessThan(theCat,Category.local | Category.publik)))
	throw (new ReifierMisused("Illegal Adjectives Used With VARIABLE"));
      // Fill In Default Values
      if ((!Category.contains(theCat,Category.local))&&
	  (!Category.contains(theCat,Category.publik)))
	theCat=theCat | Category.publik;
      // Install variable and initial value in the appropriate object part(s)
      VariableContainer theVarCont = new VariableContainer(Up.glob.up(new Integer(0)));
      VarSetAttribute varSetAtt = new VarSetAttribute(theVarCont);
      VarGetAttribute varGetAtt = new VarGetAttribute(theVarCont);
      if (Category.contains(theCat,Category.publik))
	{
	  UnaryPattern getPat = (UnaryPattern)thePattern;
	  context.getPub().installPattern(getPat.makeWritePattern(),varSetAtt);
	  context.getPub().installPattern(getPat,varGetAtt);
	}
      if (Category.contains(theCat,Category.local))
	{
	  UnaryPattern      getPat = (UnaryPattern)thePattern;
	  context.getPrivate().installPattern(getPat.makeWritePattern(),varSetAtt);
	  context.getPrivate().installPattern(getPat,varGetAtt);
	}
      return AgoraGlobals.glob.uppedNull;
    }
  
  /**
     Code to evaluate the VARIABLE: message in a given context. The expression denotes
     the initial value. It will be evaluated. The new variable
     will install a read and write method in the self of the context.
     @param context The context where the reifier was sent.
     @param value The expression indicating the initial value.
     @return The agora object associated to this reifier invocation.
     @exception errors.AgoraError When something goes wrong.
  */
  public AgoraObject variableColon(Context context,Expression value) throws AgoraError
    {
      FormalsAndPattern leftside = (FormalsAndPattern)this.eval(context.setCat(Category.flags)).down();
      AbstractPattern thePattern = leftside.pattern;
      int     theCat     = leftside.cat;
      // Validate Pattern
      if (!(thePattern instanceof UnaryPattern))
	throw (new ReifierMisused("VARIABLE can only be sent to unary patterns"));
      if (!(Category.containsLessThan(theCat,Category.local | Category.publik)))
	throw (new ReifierMisused("Illegal Adjectives Used With VARIABLE"));
      // Fill In Default Values
      if ((!Category.contains(theCat,Category.local))&&
	  (!Category.contains(theCat,Category.publik)))
	theCat=theCat | Category.publik;
      //Install variable and initial value
      AgoraObject initValue = value.eval(context);
      VariableContainer theVarCont = new VariableContainer(initValue);
      VarSetAttribute varSetAtt = new VarSetAttribute(theVarCont);
      VarGetAttribute varGetAtt = new VarGetAttribute(theVarCont);
      if (Category.contains(theCat,Category.publik))
	{
	  UnaryPattern      getPat = (UnaryPattern)thePattern;
	  context.getPub().installPattern(getPat.makeWritePattern(),varSetAtt);
	  context.getPub().installPattern(getPat,varGetAtt);
	}
      if (Category.contains(theCat,Category.local))
	{
	  UnaryPattern      getPat = (UnaryPattern)thePattern;
	  context.getPrivate().installPattern(getPat.makeWritePattern(),varSetAtt);
	  context.getPrivate().installPattern(getPat,varGetAtt);
	}
      return initValue;
    }
  
  /**
     Code to evaluate the CONST: message in a given context. The expression denotes
     the initial value. It will be evaluated. The new variable
     will install a read method in the self of the context.
     @param context The context of invocation of this reifier.
     @param value The expression denoting the value of the constant.
     @return The agoraobject being the value of this reifier expression.
     @exception errors.AgoraError When something goes wrong.
  */
  public AgoraObject constColon(Context context,Expression value) throws AgoraError
    {
      FormalsAndPattern leftside = (FormalsAndPattern)this.eval(context.setCat(Category.flags)).down();
      AbstractPattern thePattern = leftside.pattern;
      int     theCat     = leftside.cat;
      // Validate Pattern
      if (!(thePattern instanceof UnaryPattern))
	throw (new ReifierMisused("CONST can only be sent to unary patterns"));
      if (!(Category.containsLessThan(theCat,Category.local |Category.publik)))
	throw (new ReifierMisused("Illegal Adjectives Used With CONST"));
      // Fill In Default Values
      if ((!Category.contains(theCat,Category.local))&&
	  (!Category.contains(theCat,Category.publik)))
	theCat=theCat | Category.publik;
      // Install variable and initial value in the appropriate object part(s)
      AgoraObject initValue = value.eval(context);
      VariableContainer theVarCont = new VariableContainer(initValue);
      VarGetAttribute varGetAtt = new VarGetAttribute(theVarCont);
      if (Category.contains(theCat,Category.publik))
	{
	  UnaryPattern      getPat = (UnaryPattern)thePattern;
	  context.getPub().installPattern(getPat,varGetAtt);
	}
      if (Category.contains(theCat,Category.local))
	{
	  UnaryPattern      getPat = (UnaryPattern)thePattern;
	  context.getPrivate().installPattern(getPat,varGetAtt);
	}
      return initValue;
    }
  
  /** 
    Code to evaluate the ARRAY reifier message.
    @param context The context of invocation of this reifier message.
    @return The agora object representing the array.
    @exception errors.AgoraError When something goes wrong.
   */
  public AgoraObject array(Context context) throws AgoraError
    {
      int size = this.evalAsInteger(context);
      Vector theArray = new Vector(size);
      try
	{
	  for (int i=0;i<size;i++)
	    theArray.insertElementAt("empty",i);
	}
      catch(ArrayIndexOutOfBoundsException e)
	{
	  throw (new PrimException(e,"Expression::array"));
	  // impossible because we created it large enough
	}
      return Up.glob.up(theArray);
    }
  
  /**
     Code to evaluate the ARRAY: reifier message. The expression argument denotes the initial value
     each array location will contain. It will be evaluated for each array location.
     @param context The calling context of this reifier message.
     @param value The expression denoting the initial values of the array.
     @return The agora object representing the newly created array.
     @exception errors.AgoraError When something goes wrong.
  */
  public AgoraObject arrayColon(Context context,Expression value) throws AgoraError
    {
      int size = this.evalAsInteger(context);
      Vector theArray = new Vector(size);
      try
	{
	  for (int i=0;i<size;i++)
	    theArray.insertElementAt(value.eval(context).down(),i);
	}
      catch(ArrayIndexOutOfBoundsException e)
	{
	  throw (new PrimException(e,"Expression::arrayColon"));
	  // impossible because we created it large enough
	}
      return Up.glob.up(theArray);
    }
  
  /**
     Code that implements the METHOD: reifier message in a given context. This will install a new method
     in the self of the context. The expression argument
     denotes the body of the method.
     @param context The calling context of this reifier.
     @param body The body expression of the method.
     @return The return value of installing this method.
     @exception errors.AgoraError When something goes wrong during installing the method.
  */
  public AgoraObject methodColon(Context context,Expression body) throws AgoraError
    {
      FormalsAndPattern   leftside= (FormalsAndPattern)this.eval(context.setCat(Category.flags)).down();
      AbstractPattern  thePattern = leftside.pattern;
      int      theCat     = leftside.cat;
      String[] formals    = leftside.formals;
      // Validate Pattern
      if (!(Category.containsLessThan(theCat,Category.local | Category.publik)))
	throw (new ReifierMisused("Illegal Adjectives Used With METHOD"));
      // Fill In Default Values
      if ((!Category.contains(theCat,Category.local))&&
	  (!Category.contains(theCat,Category.publik)))
	theCat=theCat | Category.publik;
      Attribute methAtt = new MethAttribute(formals,body);
      if (Category.contains(theCat,Category.publik))
	{
	  context.getPub().installPattern(thePattern,methAtt);
	}
      if (Category.contains(theCat,Category.local))
	{
	  context.getPrivate().installPattern(thePattern,methAtt);
	}
      return AgoraGlobals.glob.uppedNull;
    }
  
  /**
     Code to evaluate the MIXIN: reifier message in a given context. This will install a new mixin
     method in the self of the context with the expression argument as body.
     @param context The calling context of this reifier message.
     @param body The body expression of the mixin.
     @return The return value of installing this mixin.
     @exception errors.AgoraError When something goes wrong during installing the mixin.
  */
  public AgoraObject mixinColon(Context context,Expression body) throws AgoraError
    {
      FormalsAndPattern   leftside= (FormalsAndPattern)this.eval(context.setCat(Category.flags)).down();
      AbstractPattern  thePattern = leftside.pattern;
      int      theCat     = leftside.cat;
      String[] formals    = leftside.formals;
      // Validate Pattern
      if (!(Category.containsLessThan(theCat,Category.local | 
				      Category.publik)))
	throw (new ReifierMisused("Illegal Adjectives Used With MIXIN"));
      // Fill In Default Values
      if ((!Category.contains(theCat,Category.local))&&
	  (!Category.contains(theCat,Category.publik)))
	theCat=theCat | Category.publik;
      // Install method in the appropriate object part(s)
      Attribute methAtt = new MixinAttribute(formals,body);
      if (Category.contains(theCat,Category.publik))
	{
	  context.getPub().installPattern(thePattern,methAtt);
	}
      if (Category.contains(theCat,Category.local))
	{
	  context.getPrivate().installPattern(thePattern,methAtt);
	}
      return AgoraGlobals.glob.uppedNull;
    }
  
  /**
     Code to evaluate the VIEW: reifier message in a given context. This will install a new mixin
     method in the self of the context with the expression argument as body.
     @param context The calling context of this reifier message.
     @param body The body expression of the view.
     @return The return value of installing the new view.
     @exception errors.AgoraError When something goes wrong when installing the new view.
  */
  public AgoraObject viewColon(Context context,Expression body) throws AgoraError
    {
      FormalsAndPattern   leftside= (FormalsAndPattern)this.eval(context.setCat(Category.flags)).down();
      AbstractPattern  thePattern = leftside.pattern;
      int      theCat     = leftside.cat;
      String[] formals    = leftside.formals;
      // Validate Pattern
      if (!(Category.containsLessThan(theCat,Category.local | Category.publik)))
	throw (new ReifierMisused("Illegal Adjectives Used With VIEW"));
      // Fill In Default Values
      if ((!Category.contains(theCat,Category.local))&&
	  (!Category.contains(theCat,Category.publik)))
	theCat=theCat | Category.publik;
      // Install method in the appropriate object part(s)
      Attribute methAtt = new ViewAttribute(formals,body);
      if (Category.contains(theCat,Category.publik))
	{
	  context.getPub().installPattern(thePattern,methAtt);
	}
      if (Category.contains(theCat,Category.local))
	{
	  context.getPrivate().installPattern(thePattern,methAtt);
	}
      return AgoraGlobals.glob.uppedNull;
    }
  
  /**
     This method implements the CLONING: reifier message. The method will install a new cloning
     method in the self that is inside the context object. The extra expression argument will be used
     as the body of the cloning method.
     @param context The context of sending this reifier message.
     @param body The body expression of the new cloning method.
     @return The return value of installing the new method.
     @exception errors.AgoraError When something goes wrong during installation of the new method.
  */
  public AgoraObject cloningColon(Context context,Expression body) throws AgoraError
    {
      FormalsAndPattern   leftside = (FormalsAndPattern)this.eval(context.setCat(Category.flags)).down();
      AbstractPattern   thePattern = leftside.pattern;
      int      theCat     = leftside.cat;
      String[] formals    = leftside.formals;
      // Validate Pattern
      if (!(Category.containsLessThan(theCat,Category.local | Category.publik)))
	throw (new ReifierMisused("Illegal Adjectives Used With CLONING"));
      // Fill In Default Values
      if ((!Category.contains(theCat,Category.local))&&
	  (!Category.contains(theCat,Category.publik)))
	theCat=theCat | Category.publik;
      // Install method in the appropriate object part(s)
      Attribute methAtt = new CloningAttribute(formals,body);
      if (Category.contains(theCat,Category.publik))
	{
	  context.getPub().installPattern(thePattern,methAtt);
	}
      if (Category.contains(theCat,Category.local))
	{
	  context.getPrivate().installPattern(thePattern,methAtt);
	}
      return AgoraGlobals.glob.uppedNull;
    }
  
  /**
     Implements the PUBLIC reifier as sent to the receiving expression.
     @param context The context where the reifier was sent.
     @return The FormalsAndPattern where the public bit is set to 1.
     @exception errors.AgoraError Perhaps a wrong pattern was there.
  */
  public AgoraObject publik(Context context) throws AgoraError
    {
      FormalsAndPattern   leftside = (FormalsAndPattern)this.eval(context).down();
      leftside.cat = leftside.cat | Category.publik;
      return Up.glob.up(leftside);
    }
  
  /** 
      Implements the LOCAL reifier sent to the receiving expression.
      @param context  The context of the place the reifier was sent.
      @return The FormalsAndPattern(upped!) where the local bit is set to 1.
      @exception errors.AgoraError Perhaps a wrong pattern is the receiver.
  */
  public AgoraObject local(Context context) throws AgoraError
    {
      FormalsAndPattern   leftside   = (FormalsAndPattern)this.eval(context).down();
      leftside.cat = leftside.cat | Category.local;
      return Up.glob.up(leftside);
    }
  
  /**
     Implements the FOR:TO:DO: reifier message. The receiving expression must be a unary pattern.
     The 'from' expression must evaluate to a number, as well as the 'to' parameter.
     The third expression argument is the block that will be iterated.
     @param context The context the reifier occurs in.
     @param from The expression indicating the lower bound. Must evaluate to an integer.
     @param to The expression indicating the upper bound. Must evaluate to an integer.
     @param doblock The expression that will be iterated.
     @return The value of the FOR: expression.
     @exception errors.AgoraError When something goes wrong during evaluation.
  */
  public AgoraObject fortodo(Context context,
			     Expression from, 
			     Expression to, 
			     Expression doblock) throws AgoraError
    {
      int init = from.evalAsInteger(context);
      int term = to.evalAsInteger(context);
      // Determine unary receiverless pattern
      FormalsAndPattern   leftside = (FormalsAndPattern)this.eval(context.setCat(Category.flags)).down();
      AbstractPattern thePattern = leftside.pattern;
      int             theCat     = leftside.cat;
      // Validate Pattern
      if (!(thePattern instanceof UnaryPattern))
	throw (new ReifierMisused("FOR:TO:DO: can only be sent to unary patterns"));
      // Extend Private Temporarily and install variable and initial value in the new private object part
      InternalGenerator    newPriv = (InternalGenerator)(context.getPrivate().funcAddLayer("FOR:TO:DO: scope"));
      newPriv.setPrivate(newPriv);
      VariableContainer theVarCont = new VariableContainer(Up.glob.up(new Integer(init)));
      VarGetAttribute   varGetAtt  = new VarGetAttribute(theVarCont);
      UnaryPattern         getPat  = new UnaryPattern(((UnaryPattern)thePattern).getUnaryPattern());
      newPriv.installPattern(getPat,varGetAtt);
      // Do the Looping
      Context locCont = context.setPrivate(newPriv);
      AgoraObject result = AgoraGlobals.glob.uppedNull;
      for (int current=init;current<=term;current++)
	{
	  theVarCont.write(Up.glob.up(new Integer(current)));
	  result = doblock.eval(locCont);
	}
      return result;
    }
  
  /**
     See fortodo.
     @exception errors.AgoraError When something goes wrong during evaluation.
  */
  public AgoraObject fordowntodo(Context context,
				 Expression from, 
				 Expression downto, 
				 Expression doblock) throws AgoraError
    {
      int init = from.evalAsInteger(context);
      int term = downto.evalAsInteger(context);
      // Determine unary receiverless pattern
      FormalsAndPattern   leftside = (FormalsAndPattern)this.eval(context.setCat(Category.flags)).down();
      AbstractPattern   thePattern = leftside.pattern;
      int     theCat     = leftside.cat;
      // Validate Pattern
      if (!(thePattern instanceof UnaryPattern))
	throw (new ReifierMisused("FOR:DOWNTO:DO: can only be sent to unary patterns"));
      // Extend Private Temporarily and install variable and initial value in the new private object part
      InternalGenerator    newPriv = (InternalGenerator)context.getPrivate().funcAddLayer("FOR:DOWNTO:DO: scope");
      newPriv.setPrivate(newPriv);
      VariableContainer theVarCont = new VariableContainer(Up.glob.up(new Integer(init)));
      VarGetAttribute   varGetAtt  = new VarGetAttribute(theVarCont);
      UnaryPattern         getPat  
	= new UnaryPattern(((UnaryPattern)thePattern).getUnaryPattern());
      newPriv.installPattern(getPat,varGetAtt);
      // Do the Looping
      Context locCont = context.setPrivate(newPriv);
      AgoraObject result = AgoraGlobals.glob.uppedNull;
      for (int current=init; current>=term;current--)
	{
	  theVarCont.write(Up.glob.up(new Integer(current)));
	  result = doblock.eval(locCont);
	}
      return result;
    }
  
  /**
     See fortodo. The extra 'by' argument must evaluate to an integer.
     @param context The context the reifier occurs in.
     @param from The expression indicating the lower bound. Must evaluate to an integer.
     @param to The expression indicating the upper bound. Must evaluate to an integer.
     @param doblock The expression that will be iterated.
     @param by The expression denoting the step size. Must evaluate to an integer.
     @return The value of the FOR: expression.
     @exception errors.AgoraError When something goes wrong during evaluation.
  */
  public AgoraObject fortobydo(Context context,
			       Expression from, 
			       Expression to,
			       Expression by, 
			       Expression doblock) throws AgoraError
    {
      int init = from.evalAsInteger(context);
      int term = to.evalAsInteger(context);
      int step = by.evalAsInteger(context);
      // Determine unary receiverless pattern
      FormalsAndPattern   leftside = (FormalsAndPattern)this.eval(context.setCat(Category.flags)).down();
      AbstractPattern thePattern = leftside.pattern;
      int     theCat     = leftside.cat;
      // Validate Pattern
      if (!(thePattern instanceof UnaryPattern))
	throw (new ReifierMisused("FOR:TO:BY:DO: can only be sent to unary patterns"));
      // Extend Private Temporarily and Install variable and initial value in the new private object part
      InternalGenerator    newPriv = (InternalGenerator)context.getPrivate().funcAddLayer("FOR:TO:BY:DO: scope");
      newPriv.setPrivate(newPriv);
      VariableContainer theVarCont = new VariableContainer(Up.glob.up(new Integer(init)));
      VarGetAttribute   varGetAtt  = new VarGetAttribute(theVarCont);
      UnaryPattern         getPat  = new UnaryPattern(((UnaryPattern)thePattern).getUnaryPattern());
      newPriv.installPattern(getPat,varGetAtt);
      // Do the Looping
      Context locCont = context.setPrivate(newPriv);
      AgoraObject result = AgoraGlobals.glob.uppedNull;
      for (int current=init; current<=term; current=current + step)
	{
	  theVarCont.write(Up.glob.up(new Integer(current)));
	  result = doblock.eval(locCont);
	}
      return result;
    }
  
  /**
     See fortobydo
     @exception errors.AgoraError When something goes wrong during evaluation.
  */
  public AgoraObject fordowntobydo(Context context,
				   Expression from, 
				   Expression downto,
				   Expression by, 
				   Expression doblock) throws AgoraError
    {
      int init = from.evalAsInteger(context);  
      int term = downto.evalAsInteger(context);
      int step = by.evalAsInteger(context);
      // Determine unary receiverless pattern
      FormalsAndPattern leftside = (FormalsAndPattern)this.eval(context.setCat(Category.flags)).down();
      AbstractPattern thePattern = leftside.pattern;
      int     theCat     = leftside.cat;
      // Validate Pattern
      if (!(thePattern instanceof UnaryPattern))
	throw (new ReifierMisused("FOR:DOWNTO:BY:DO: can only be sent to unary patterns"));
      // Install variable and initial value in the new private object part
      InternalGenerator    newPriv = (InternalGenerator)context.getPrivate().funcAddLayer("FOR:DOWNTO:BY:DO: scope");
      newPriv.setPrivate(newPriv);
      VariableContainer theVarCont = new VariableContainer(Up.glob.up(new Integer(init)));
      VarGetAttribute   varGetAtt  = new VarGetAttribute(theVarCont);
      UnaryPattern          getPat = new UnaryPattern(((UnaryPattern)thePattern).getUnaryPattern());
      newPriv.installPattern(getPat,varGetAtt);
      // Do the Looping
      Context      locCont = context.setPrivate(newPriv);
      AgoraObject result = AgoraGlobals.glob.uppedNull;
      for (int current=init; current>=term;current=current-step)
	{
	  theVarCont.write(Up.glob.up(new Integer(current)));
	  result = doblock.eval(locCont);
	}
      return result;
    }
  
  /**
     Implements the IFTRUE: reifier message in the given context. The expression argument
     is evaluated if the receiving expression evaluates to the true object.
     @param context The context where the reifier was sent.
     @param thenPart The expression to be evaluated when the receiving expression
     evaluates to true.
     @return The return value of this expression
     @exception errors.AgoraError When something goes wrong during evaluation.
  */
  public AgoraObject ifTrue(Context context,Expression thenPart) throws AgoraError
    {
      if (this.evalAsBoolean(context))
	return thenPart.eval(context);
      else
	return AgoraGlobals.glob.uppedNull;
    }
  
  /**
     Implements the IFFALSE: reifier message. The argument expression is evaluated in the given context
     whenever the receiving expression evaluates to 'false'.
     @param context The context where the reifier was sent.
     @param thenPart The expression to be evaluated when the receiver evaluates to false.
     @return The value of this agora expression.
     @exception errors.AgoraError When something goes wrong during evaluation.
  */
  public AgoraObject ifFalse(Context context,Expression thenPart) throws AgoraError
    {
      if (!this.evalAsBoolean(context))
	return thenPart.eval(context);
      else
	return AgoraGlobals.glob.uppedNull;
    }
  
  /**
     See ifTrue and ifFalse
     @exception errors.AgoraError When something goes wrong during evaluation.
  */
  public AgoraObject ifTrueifFalse(Context context,Expression thenPart,Expression elsePart) throws AgoraError
    {
      if (evalAsBoolean(context))
	return thenPart.eval(context);
      else
	return elsePart.eval(context);
    }
  
  /**
     See ifTrue and ifFalse
     @exception errors.AgoraError When something goes wrong during evaluation.
  */
  public AgoraObject ifFalseifTrue(Context context,Expression thenPart,Expression elsePart) throws AgoraError
    {
      if (!this.evalAsBoolean(context))
	return thenPart.eval(context);
      else
	return elsePart.eval(context);
    }
  
  /**
     Contains the code for the WHILETRUE: reifier message. The receiving expression
     will be evaluated as long as its evaluation yields the true object. Whenever this is
     the case, the argument expression is evaluated in the given context.
     @param context The context where the reifier was sent.
     @param body the expression to be evaluated.
     @exception errors.AgoraError When something goes wrong during evaluation.
  */
  public AgoraObject whileTrue(Context context,Expression body) throws AgoraError
    {
      AgoraObject result = AgoraGlobals.glob.uppedNull;
      while(this.evalAsBoolean(context))
	result = body.eval(context);
      return result;
    }
  
  /**
     See whiletrue
     @exception errors.AgoraError When something goes wrong during evaluation.
  */
  public AgoraObject whileFalse(Context context,Expression body) throws AgoraError
    {
      AgoraObject result = AgoraGlobals.glob.uppedNull;
      while(!this.evalAsBoolean(context))
	result = body.eval(context);
      return result;
    }
  
  /**
     The receiving expression will be evaluated until the evaluation of the argument expression
     yields the true object.
     @param context The context where this reifier was sent.
     @param textExp The expression that will evaluate to a boolean value.
     @return The Agora value of this expression.
     @exception errors.AgoraError When something went wrong during the evaluation cycle.
  */
  public AgoraObject untilTrue(Context context,Expression testExp) throws AgoraError
    {
      AgoraObject result = AgoraGlobals.glob.uppedNull;
      do
	{
	  result = this.eval(context);
	} while (!testExp.evalAsBoolean(context));
      return result;
    }
  
  /**
     See untilTrue
     @exception errors.AgoraError When something goes wrong during evaluation.
  */
  public AgoraObject untilFalse(Context context,Expression testExp) throws AgoraError
    {
      AgoraObject result = AgoraGlobals.glob.uppedNull;
      do
	{
	  result = this.eval(context);
	} while (testExp.evalAsBoolean(context));
      return AgoraGlobals.glob.uppedNull;
    }
  
  /**
     Implementation of the COMMENT reifier. Ignores the receiving expression.
     @param context The context of evaluation.
     @return Always returns null.
  */
  public AgoraObject comment(Context context)
    {
      return AgoraGlobals.glob.uppedNull;
    }
  
  /**
     Implementation of the TRY:CATCH: reifier message. The receiver is evaluated, but if it throws
     an AgoraException, this is caught and then the catchcode argument is evaluated in a new context:
     the given context extended with a new binding of formals to the actuals in the 'patter' argument.
     @param context The context of sending of this reifier message.
     @param pattern The formal pattern that this try-catch matches.
     @param catchcode The expression to be evaluated when the handler matches.
     @return The Agora value of this expression.
     @exception errors.AgoraError When something goes wrong, or when the exception does not match.
  */
  public AgoraObject trycatch(Context context,Expression pattern,Expression catchcode) throws AgoraError
    {
      try 
	{
	  return this.eval(context.setException(new AgoraException(catchcode)));
	}
      catch (AgoraException ex)
	{
	  if (pattern instanceof UserPattern)
	    {
	      UserPattern pat = (UserPattern)pattern;
	      AbstractPattern formalPattern = pat.makePattern(context);
	      AbstractPattern actualPattern = ex.getPattern();
	      if (formalPattern.equals(actualPattern))
		{
		  String[] formals = pat.makeFormals(context);
		  Client   actuals = ex.getClient();
		  MethAttribute theAtt = new MethAttribute (formals,catchcode);
		  return theAtt.doAttributeValue(actualPattern,actuals,context);
		}
	      else
		throw ex;
	    }
	  else
	    throw (new ProgramError("TRY:xxx CATCH: pattern is not a valid pattern"));
	}
      catch (AgoraError ex)
	{
	  if (pattern instanceof UserPattern)
	    {
	      UserPattern pat = (UserPattern)pattern;
	      KeywordPattern agoError = new KeywordPattern(1);
	      agoError.atPut(0,"agoraError:");
	      AbstractPattern formalPattern = pat.makePattern(context);
	      if (formalPattern.equals(agoError))
		{
		  String[] formals = pat.makeFormals(context);
		  Object[] args    = new Object[1];
		  args[0] = Up.glob.up(ex);
		  Client   actuals = context.newClient(args);
		  MethAttribute theAtt = new MethAttribute(formals,catchcode);
		  return theAtt.doAttributeValue(agoError,actuals,context);
		}
	      throw ex;
	    }
	  else
	    throw (new ProgramError("TRY:XXX CATCH: pattern is not a valid pattern"));
	}
    }  
  
  /**
     Implements the JAVA reifier. Raises an AgoraException with the receiving pattern as content.
     @param context The context of evaluation when this reifier was sent.
     @return The class object corresponding to the receiving string.
     @exception errors.AgoraError When something goes wrong (e.g. when the class doesn't exist).
  */
  public AgoraObject java(Context context) throws AgoraError
    {
      String res = this.evalAsString(context);
      try
	{
	  return Up.glob.up(Class.forName(res));
	}
      catch(ClassNotFoundException e)
	{
	  throw (new ProgramError("No Such Class : "+res));
	}
    }
  
  /**
     Implements the QUOTE reifier. The receiving expression will be returned as an Agora object.
     @param context The context when this reifier was sent.
     @return The Upped receiving expression (as Agora value)
     @exception errors.AgoraError When something goes wrong.
   */
  public AgoraObject quote(Context context) throws AgoraError
    {
      return Up.glob.up(this);
    }
  
  /**
     Implements the UNQUOTE reifier. The receiving expression is evaluated and the resulting Agora
     object is considered as an expression. This expression is then evaluated again.
     @param context The calling context of this reifier message.
     @return The result of evaluating the receiver denoting an upped expression object.
     @exception errors.AgoraError When something goes wrong during evaluation.
  */
  public AgoraObject unquote(Context context) throws AgoraError
    {
      Object code =  this.eval(context).down();
      if (code instanceof Expression)
	return ((Expression)code).eval(context);
      else
	throw (new ProgramError("UNQUOTE can only be sent to a quoted expression"));
    }
  
  /**
     Implements the UP reifier. Evaluates the receiving expression and returns the result
     as a meta object.
     @param context The context where the reifier was sent.
     @return The receiver evaluated, and then upped as a referable Agora object.
     @exception errors.AgoraError When something goes wrong when evaluating the receiving expression.
  */
  public AgoraObject up(Context context) throws AgoraError
    {
      return Up.glob.up(this.eval(context));
    }
  
  /**
     Implements the DOWN: reifier. The receiving expression is evaluated and considered as
     a meta object (if possible, otherwise an error is thrown). This meta object is brought
     back to the base level.
     @param context The context where the reifier was sent.
     @return The resulting Agora object of downing the evaluated receiving expression.
     @exception errors.AgoraError When something goes wrong (e.g. evaluation error or illegal
     object to down).
  */
  public AgoraObject down(Context context) throws AgoraError
    {
      Object res  = this.eval(context).down();
      if (res instanceof AgoraObject)
	return (AgoraObject)res;
      else
	throw (new ProgramError("DOWN must yield a valid Agora Object"));
    }
  
  /**
     Implements the REIFIER:IS: reifier message. This will install a new reifier method in the
     self of the context.
     @param context The context where this reifier was sent.
     @param contextParameter The formal argument to which the runtime context will be bound.
     @param bodyParameter The body expression of the new reifier.
     @return The return value of the REIFIER:IS: expression (null).
     @exception errors.AgoraError When the reifier cannot be installed or when wrong
     patterns are specified.
  */
  public AgoraObject reifierIs(Context context,
			       Expression contextParameter,
			       Expression bodyparameter) throws  AgoraError
    {
      FormalsAndPattern   leftside= (FormalsAndPattern)this.eval(context.setCat(Category.flags)).down();
      AbstractPattern  thePattern = leftside.pattern;
      int      theCat     = leftside.cat;
      String[] formals    = leftside.formals;
      // Validate Pattern
      if (!(Category.containsLessThan(theCat,Category.local | Category.publik)))
	throw (new ReifierMisused("Illegal Adjectives Used With REIFIER:IS:"));
      if (!(thePattern.isReifier()))
	throw (new ReifierMisused("REIFIER:IS: can only be sent to a reifier pattern."));
      if (!(contextParameter instanceof UserUnaryPattern))
	throw (new ReifierMisused("Context parameter of REIFIER:IS: should be an ordinary identifier"));
      UnaryPattern contextNamePattern 
	= (UnaryPattern)((FormalsAndPattern)contextParameter.eval(context.setCat(Category.flags)).down()).pattern;
      // Fill In Default Values
      if ((!Category.contains(theCat,Category.local))&&
	  (!Category.contains(theCat,Category.publik)))
	theCat=theCat | Category.publik;
      // Install method in the appropriate object part(s)
      Attribute methAtt = new ReifierMethodAttribute(formals,bodyparameter,contextNamePattern);
      if (Category.contains(theCat,Category.publik))
	{
	  context.getPub().installPattern(thePattern,methAtt);
	}
      if (Category.contains(theCat,Category.local))
	{
	  context.getPrivate().installPattern(thePattern,methAtt);
	}
      return AgoraGlobals.glob.uppedNull;
    }
  
  /**
     An auxiliary method used to evaluate the receiving expression as an integer object.
     If not, an AgoraError is thrown.
     @param context The context in which the expression has to be evaluted.
     @return The integer corresponding to the receiving expression.
     @exception errors.AgoraError Is thrown when the receiver does not evaluate to an integer.
  */
  public int evalAsInteger(Context context) throws AgoraError
    {
      AgoraObject resValue = this.eval(context);
      int res;
      if (resValue.down() instanceof Integer)
	res = ((Integer)resValue.down()).intValue();
      else
	throw (new ProgramError("Integer Expected !"));
      return res;
    }
  
  /**
     See evalAsInteger
     @exception errors.AgoraError When the receiver can not be evaluated to a boolean.
  */
  public boolean evalAsBoolean(Context context) throws AgoraError
    {
      AgoraObject resValue = this.eval(context);
      boolean res;
      if (resValue.down() instanceof Boolean)
	res = ((Boolean)resValue.down()).booleanValue();
      else
	throw (new ProgramError("Boolean Expected !"));
      return res;
    }
  
  /**
     See evalAsInteger
     @exception errors.AgoraError When the receiver can not be evaluated to a string.
  */
  public String evalAsString(Context context) throws AgoraError
    {
      AgoraObject resValue = (AgoraObject)this.eval(context);
      if (!(resValue.down() instanceof String))
	throw (new ProgramError("String Expected !"));
      return (String)resValue.down();
    }
}
