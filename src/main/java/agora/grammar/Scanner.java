package agora.grammar;

import agora.tools.*;
import java.io.*;

/**
   This class implements a simple lexical scanner for Agora. At the time
   of writing, no decent parser generator is available.
   @author Wolfgang De Meuter (Programming Technology Lab).
	Last change:  E    16 Nov 97    1:43 am
*/

public class Scanner extends Object implements Serializable
{
  // TOKENS
  
  public static int _LPAR_   = 0;
  public static int _RPAR_   = 1;
  public static int _LBRACK_ = 2;
  public static int _RBRACK_ = 3;
  public static int _RBRACE_ = 4;
  public static int _LBRACE_ = 5;
  public static int _OPERATOR_  = 6;
  public static int _MOPERATOR_ = 7;
  public static int _KEYWORD_   = 8;
  public static int _MKEYWORD_  = 9;
  public static int _UNARY_    = 10;
  public static int _MUNARY_   = 11;
  public static int  _STRING_  = 12;
  public static int _INTEGER_  = 13;
  public static int _REAL_     = 14;
  public static int _CHAR_     = 15;
  public static int _SEMI_     = 16;
  public static int _ERROR_    = 17;
  public static int _EOFTOKEN_ = 18;
  
  // ACCESIBLE RETURN VALUES (After Calleing scan())
  
  public char lastChar;
  
  public String lastRKeyword;
  public String lastUKeyword;
  public String lastROperator;
  public String lastUOperator;
  public String lastRUnary;
  public String lastUUnary;
  public StringLiteral  lastString;
  public IntegerLiteral lastInteger;
  public FloatLiteral   lastFloat;
  public CharLiteral    lastCharacter;
  
  /**
     Creates a new scanner.
  */
  public Scanner()
    {
      this.lastChar = AgoraIO.getChar();
      this.lastRKeyword = null;
      this.lastUKeyword = null;
      this.lastROperator = null;
      this.lastUOperator = null;
      this.lastRUnary = null;
      this.lastUUnary = null;
      this.lastString = null;
      this.lastInteger = null;
      this.lastFloat = null;
      this.lastCharacter= null;
    }
  
  /**
     This protected method get characters from some input device. Its default behavior is
     to access the 'getChar' method from AgoraIO, but this method can be overriden e.g. to get
     characters from a file.
  */
  protected void nextChar()
    {
      this.lastChar = AgoraIO.getChar();
    }
  
  private boolean caps()
    {
      return ((this.lastChar>='A')&&(this.lastChar<='Z'));
    }
  
  private void eatSpaces()
    {
      while ((this.lastChar==' ')||(this.lastChar=='\n')||(this.lastChar=='\r')||
	     (this.lastChar=='\t')||(this.lastChar=='\f')||
	     (this.lastChar==10)||(this.lastChar==13))
	this.nextChar();
    }
  
  private boolean lower()
    {
      return ((this.lastChar>='a')&&(this.lastChar<='z'));
    }
  
  private boolean letter()
    {
      return (this.lower()||this.caps());
    }
  
  private boolean digit()
    {
      return ((this.lastChar>='0') && (this.lastChar<='9'));
    }
  
  private boolean operatorchar()
    {
      return ((this.lastChar == '!')||
	      (this.lastChar == '@')||
	      (this.lastChar == '#')||
	      (this.lastChar == '$')||
	      (this.lastChar == '%')||
	      (this.lastChar == '^')||
	      (this.lastChar == '&')||
	      (this.lastChar == '*')||
	      (this.lastChar == '-')||
	      (this.lastChar == '=')||
	      (this.lastChar == '+')||
	      (this.lastChar == '<')||
	      (this.lastChar == '>')||
	      (this.lastChar == '/')||
	      (this.lastChar == '.')||
	      (this.lastChar == ',')||
	      (this.lastChar == '|')||
	      (this.lastChar == '?'));
    }
  
  private int scan_string() // return value is a token as defined above
    {
      StringBuffer result = new StringBuffer();
      this.nextChar();
      if (this.lastChar==0)
	return _ERROR_;
      while (this.lastChar!='"')
	{
	  if(this.lastChar=='\\')
	    {
	      this.nextChar();
	      if (this.lastChar==0)
		return _ERROR_;
	      if (this.lastChar=='n')
		result.append('\n');
	      if (this.lastChar=='r')
		result.append('\r');
	      if (this.lastChar=='f')
		result.append('\f');
	      if (this.lastChar=='t')
		result.append('\t');
	      if (this.lastChar=='\\')
		result.append('\\');
	      if (this.lastChar=='\'')
		result.append('\'');
	    }
	  else
	    result.append(this.lastChar);
	  this.nextChar();
	  if (this.lastChar==0)
	    return _ERROR_;
	}
      this.nextChar();
      this.lastString = new StringLiteral(result.toString());
      return _STRING_;
    }
  
  private int scan_operator() //return value is a token as defined above
    {
      StringBuffer result = new StringBuffer();
      result.append(this.lastChar);
      this.nextChar();
      while (this.operatorchar())
	{
	  result.append(this.lastChar);
	  this.nextChar();
	}
      this.lastUOperator = result.toString();
      return _OPERATOR_;
    }
  
  private int scan_number() // return value is a token as defined above
    {
      int i= 0 ;
      float f = 0;
      int exp=0;
      int len = 0;
      i = (this.lastChar) - '0';
      this.nextChar();
      while (this.digit())
	{
	  i = 10*i + ((this.lastChar) - '0');
	  this.nextChar();
	}
      if (this.lastChar == '.')
	this.nextChar();
      else
	{
	  this.lastInteger = new IntegerLiteral(i);
	  return _INTEGER_;
	}
      if (this.lastChar==0)
	return _ERROR_;
      while (this.digit())
	{
	  len++;
	  f = 10*f + ((this.lastChar) - '0');
	  this.nextChar();
	}
      for (int j=0;j<len;j++)
	f =f /10;
      f = i+f;
      if (this.lastChar=='e')
	{
	  this.nextChar();
	  if ((this.lastChar!='+')&&(this.lastChar!='-'))
	    return _ERROR_;
	  boolean positive = (this.lastChar=='+');
	  this.nextChar();
	  if (!this.digit())
	    return _ERROR_;
	  exp = (this.lastChar) - '0';
	  this.nextChar();
	  if (!this.digit())
	    return _ERROR_;
	  exp = 10*exp + ((this.lastChar) - '0');
	  this.nextChar();
	  for (int j=0;j<exp;j++)
	    if (positive)
	      f = f * 10;
	    else
	      f = f / 10;
	}
      this.lastFloat = new FloatLiteral(f);
      return _REAL_;
    }
  
  private int scan_character() // return value is a token as defined above
    {
      char theChar;
      nextChar();
      if (this.lastChar==0)
	return _ERROR_;
      theChar = this. lastChar;
      if (this.lastChar=='\\')
	{
	  this.nextChar();
	  if (this.lastChar==0)
	    return _ERROR_;
	  if (this.lastChar=='n')
	    theChar = '\n';
	  if (this.lastChar=='r')
	    theChar = '\r';
	  if (this.lastChar=='f')
	    theChar = '\f';
	  if (this.lastChar=='t')
	    theChar = '\t';
	  if (this.lastChar=='\\')
	    theChar = '\\';
	  if (this.lastChar=='\'')
	    theChar = '\'';
	  if (this.lastChar=='\"')
	    theChar = '\"';
	}
      this.nextChar();
      if ((this.lastChar==0)||(this.lastChar!='\''))
	return _ERROR_;
      this.nextChar();
      this.lastCharacter = new CharLiteral(theChar);
      return _CHAR_;
    }
  
  private int scan_identifier() // return value is a token as defined above
    {
      StringBuffer result = new StringBuffer();
      boolean allCaps = this.caps();
      result.append(this.lastChar);
      this.nextChar();
      while (this.letter()||this.digit()||(this.lastChar=='_'))
	{
	  result.append(this.lastChar);
	  allCaps = allCaps && this.caps();
	  this.nextChar();
	}
      boolean keyword = false;
      if ((this.lastChar)== ':')
	{
	  result.append(this.lastChar);
	  this.nextChar();
	  keyword= true;
	}
      String theIdent = result.toString();
      if (allCaps)
	{
	  if (keyword)
	    {
	      lastRKeyword = theIdent;
	      return _MKEYWORD_;
	    }
	  else
	    {
	      lastRUnary = theIdent;
	      return _MUNARY_;
	    }
	}
      else
	{
	  if (keyword)
	    {
	      lastUKeyword = theIdent;
	      return _KEYWORD_;
	    }
	  else
	    {
	      lastUUnary = theIdent;
				return _UNARY_;
	    }
	}
    }
  
  /**
     This public method scans the imput for the next token. The tokens are integer constants defined in this 
     class.
     @return An integer denoting one of the tokens defined in this class.
  */
  public int scan()
    {
      this.eatSpaces();
      if (this.lastChar == 0)
	return _EOFTOKEN_;
      if (this.lastChar=='(')
	{
	  this.nextChar();
	  return _LPAR_;
	}
      if (this.lastChar==')')
	{
	  this.nextChar();
	  return _RPAR_;
	}
      if (this.lastChar=='[')
	{
	  this.nextChar();
	  return _LBRACK_;
	}
      if (this.lastChar==']')
	{
	  this.nextChar();
	  return _RBRACK_;
	}
      if (this.lastChar=='{')
	{
	  this.nextChar();
	  return _LBRACE_;
	}
      if (this.lastChar=='}')
	{
	  this.nextChar();
	  return _RBRACE_;
	}
      if (this.lastChar==';')
	{
	  this.nextChar();
	  return _SEMI_;
	}
      if (this.letter())
	return scan_identifier();
      if (this.digit())
	return scan_number();	
      if (this.lastChar=='"')
	return scan_string();
      if (this.operatorchar())
	return scan_operator();
      if (this.lastChar=='\'')
	return scan_character();
      return _ERROR_;
    }
}

