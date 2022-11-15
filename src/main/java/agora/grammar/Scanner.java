package agora.grammar;

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;

/**
 * This class implements a simple lexical scanner for Agora. At the time
 * of writing, no decent parser generator is available.
 *
 * @author Wolfgang De Meuter (Programming Technology Lab).
 * Last change:  E    16 Nov 97    1:43 am
 */

public class Scanner implements Serializable {
    // TOKENS

    public static int _LPAR_ = 0;
    public static int _RPAR_ = 1;
    public static int _LBRACK_ = 2;
    public static int _RBRACK_ = 3;
    public static int _RBRACE_ = 4;
    public static int _LBRACE_ = 5;
    public static int _OPERATOR_ = 6;
    public static int _MOPERATOR_ = 7;
    public static int _KEYWORD_ = 8;
    public static int _MKEYWORD_ = 9;
    public static int _UNARY_ = 10;
    public static int _MUNARY_ = 11;
    public static int _STRING_ = 12;
    public static int _INTEGER_ = 13;
    public static int _REAL_ = 14;
    public static int _CHAR_ = 15;
    public static int _SEMI_ = 16;
    public static int _ERROR_ = 17;
    public static int _EOFTOKEN_ = 18;
    private final Reader in;

    // ACCESIBLE RETURN VALUES (After Calleing scan())

    public char lastChar;

    public String lastRKeyword;
    public String lastUKeyword;
    public String lastROperator;
    public String lastUOperator;
    public String lastRUnary;
    public String lastUUnary;
    public StringLiteral lastString;
    public IntegerLiteral lastInteger;
    public FloatLiteral lastFloat;
    public CharLiteral lastCharacter;

    /**
     * Creates a new scanner.
     */
    public Scanner(Reader in) throws IOException {
        this.in = in;
        lastChar = (char) in.read();
        lastRKeyword = null;
        lastUKeyword = null;
        lastROperator = null;
        lastUOperator = null;
        lastRUnary = null;
        lastUUnary = null;
        lastString = null;
        lastInteger = null;
        lastFloat = null;
        lastCharacter = null;
    }

    /**
     * This protected method get characters from some input device. Its default behavior is
     * to access the 'getChar' method from AgoraIO, but this method can be overriden e.g. to get
     * characters from a file.
     */
    protected void nextChar() throws IOException {
        lastChar = (char) in.read();
    }

    private boolean caps() {
        return lastChar >= 'A' && lastChar <= 'Z';
    }

    private void eatSpaces() throws IOException {
        while (lastChar == ' ' || lastChar == '\n' || lastChar == '\r' || lastChar == '\t' || lastChar == '\f')
            nextChar();
    }

    private boolean lower() {
        return lastChar >= 'a' && lastChar <= 'z';
    }

    private boolean letter() {
        return (lower() || caps());
    }

    private boolean digit() {
        return lastChar >= '0' && lastChar <= '9';
    }

    private boolean operatorchar() {
        return lastChar == '!' ||
                lastChar == '@' ||
                lastChar == '#' ||
                lastChar == '$' ||
                lastChar == '%' ||
                lastChar == '^' ||
                lastChar == '&' ||
                lastChar == '*' ||
                lastChar == '-' ||
                lastChar == '=' ||
                lastChar == '+' ||
                lastChar == '<' ||
                lastChar == '>' ||
                lastChar == '/' ||
                lastChar == '.' ||
                lastChar == ',' ||
                lastChar == '|' ||
                lastChar == '?';
    }

    private int scan_string() throws IOException // return value is a token as defined above
    {
        var result = new StringBuilder();
        nextChar();
        if (lastChar == 0) return _ERROR_;
        while (lastChar != '"') {
            if (lastChar == '\\') {
                nextChar();
                if (lastChar == 0)
                    return _ERROR_;
                if (lastChar == 'n')
                    result.append('\n');
                if (lastChar == 'r')
                    result.append('\r');
                if (lastChar == 'f')
                    result.append('\f');
                if (lastChar == 't')
                    result.append('\t');
                if (lastChar == '\\')
                    result.append('\\');
                if (lastChar == '\'')
                    result.append('\'');
            } else
                result.append(lastChar);
            nextChar();
            if (lastChar == 0)
                return _ERROR_;
        }
        nextChar();
        lastString = new StringLiteral(result.toString());
        return _STRING_;
    }

    private int scan_operator() throws IOException //return value is a token as defined above
    {
        var result = new StringBuilder();
        result.append(lastChar);
        nextChar();
        while (operatorchar()) {
            result.append(lastChar);
            nextChar();
        }
        lastUOperator = result.toString();
        return _OPERATOR_;
    }

    private int scan_number() throws IOException // return value is a token as defined above
    {
        var i = 0;
        float f = 0;
        var exp = 0;
        var len = 0;
        i = (lastChar) - '0';
        nextChar();
        while (digit()) {
            i = 10 * i + ((lastChar) - '0');
            nextChar();
        }
        if (lastChar == '.')
            nextChar();
        else {
            lastInteger = new IntegerLiteral(i);
            return _INTEGER_;
        }
        if (lastChar == 0)
            return _ERROR_;
        while (digit()) {
            len++;
            f = 10 * f + ((lastChar) - '0');
            nextChar();
        }
        for (var j = 0; j < len; j++)
            f = f / 10;
        f = i + f;
        if (lastChar == 'e') {
            nextChar();
            if (lastChar != '+' && lastChar != '-')
                return _ERROR_;
            var positive = lastChar == '+';
            nextChar();
            if (!digit())
                return _ERROR_;
            exp = (lastChar) - '0';
            nextChar();
            if (!digit())
                return _ERROR_;
            exp = 10 * exp + ((lastChar) - '0');
            nextChar();
            for (var j = 0; j < exp; j++)
                if (positive)
                    f = f * 10;
                else
                    f = f / 10;
        }
        lastFloat = new FloatLiteral(f);
        return _REAL_;
    }

    private int scan_character() throws IOException // return value is a token as defined above
    {
        char theChar;
        nextChar();
        if (lastChar == 0)
            return _ERROR_;
        theChar = lastChar;
        if (lastChar == '\\') {
            nextChar();
            if (lastChar == 0)
                return _ERROR_;
            if (lastChar == 'n')
                theChar = '\n';
            if (lastChar == 'r')
                theChar = '\r';
            if (lastChar == 'f')
                theChar = '\f';
            if (lastChar == 't')
                theChar = '\t';
            if (lastChar == '\\')
                theChar = '\\';
            if (lastChar == '\'')
                theChar = '\'';
            if (lastChar == '\"')
                theChar = '\"';
        }
        nextChar();
        if ((lastChar == 0) || (lastChar != '\''))
            return _ERROR_;
        nextChar();
        lastCharacter = new CharLiteral(theChar);
        return _CHAR_;
    }

    private int scan_identifier() throws IOException // return value is a token as defined above
    {
        var result = new StringBuilder();
        var allCaps = caps();
        result.append(lastChar);
        nextChar();
        while (letter() || digit() || (lastChar == '_')) {
            result.append(lastChar);
            allCaps = allCaps && caps();
            nextChar();
        }
        var keyword = false;
        if ((lastChar) == ':') {
            result.append(lastChar);
            nextChar();
            keyword = true;
        }
        var theIdent = result.toString();
        if (allCaps) {
            if (keyword) {
                lastRKeyword = theIdent;
                return _MKEYWORD_;
            } else {
                lastRUnary = theIdent;
                return _MUNARY_;
            }
        } else {
            if (keyword) {
                lastUKeyword = theIdent;
                return _KEYWORD_;
            } else {
                lastUUnary = theIdent;
                return _UNARY_;
            }
        }
    }

    /**
     * This public method scans the imput for the next token. The tokens are integer constants defined in this
     * class.
     *
     * @return An integer denoting one of the tokens defined in this class.
     */
    public int scan() throws IOException {
        eatSpaces();
        if (lastChar == 0)
            return _EOFTOKEN_;
        if (lastChar == '(') {
            nextChar();
            return _LPAR_;
        }
        if (lastChar == ')') {
            nextChar();
            return _RPAR_;
        }
        if (lastChar == '[') {
            nextChar();
            return _LBRACK_;
        }
        if (lastChar == ']') {
            nextChar();
            return _RBRACK_;
        }
        if (lastChar == '{') {
            nextChar();
            return _LBRACE_;
        }
        if (lastChar == '}') {
            nextChar();
            return _RBRACE_;
        }
        if (lastChar == ';') {
            nextChar();
            return _SEMI_;
        }
        if (letter())
            return scan_identifier();
        if (digit())
            return scan_number();
        if (lastChar == '"')
            return scan_string();
        if (operatorchar())
            return scan_operator();
        if (lastChar == '\'')
            return scan_character();
        return _ERROR_;
    }
}

