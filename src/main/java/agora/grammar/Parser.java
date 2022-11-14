package agora.grammar;

import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.Queue;

/**
 * This class implements a simple top-down parser for Agora. At the time
 * of writing, no decent parser generator is available. Its only public
 * methods are the constructor and a method 'parseExpression'.
 * Last change:  E    16 Nov 97    1:38 am
 */

public class Parser implements Serializable {
    private final Scanner s;
    private int lastToken;

    /**
     * In order to construct a parser, a new Scanner must be created. While
     * parsing, the parser will repeatedly call the scanner for a new token.
     *
     * @param scanner A new scanner that will deliver the tokens.
     */
    public Parser(Scanner scanner) throws IOException {
        this.s = scanner;
        this.lastToken = this.s.scan();
    }

    /**
     * This is the parse routine. Either it succeedes, and returns a valid Agora
     * parse tree, or it fails and returns 'null' as parse tree.
     *
     * @return The parsed expression. Parse errors are indicated by null as return value.
     */
    public Expression parseExpression() throws IOException {
        if (this.lastToken == Scanner._ERROR_ ||
                this.lastToken == Scanner._EOFTOKEN_)
            return null;
        return this.parse_Rkeywordmessage();
    }

    private void scan() throws IOException {
        this.lastToken = this.s.scan();
    }

    private ReifKeywordPattern makeReifierKeywordPattern(Queue<Object> contents) {
        var sz = contents.size() / 2;
        int i;
        var result = new ReifKeywordPattern(sz);
        for (i = 0; i < sz; i++)
            result.atPut(i, (String) contents.poll(), (Expression) contents.poll());
        return result;
    }

    private UserKeywordPattern makeUserKeywordPattern(Queue<Object> contents) {
        var sz = contents.size() / 2;
        int i;
        var result = new UserKeywordPattern(sz);
        for (i = 0; i < sz; i++)
            result.atPut(i, (String) contents.poll(), (Expression) contents.poll());
        return result;
    }

    private Aggregate makeAggregate(char l, char r, Queue<Object> contents) {
        var sz = contents.size();
        int i;
        var result = new Aggregate(sz, l, r);
        for (i = 0; i < sz; i++)
            result.atPut(i, (Expression) contents.poll());
        return result;
    }

    private Expression parse_Rkeywordmessage() throws IOException {
        if (lastToken == Scanner._MKEYWORD_)
            return this.parse_Rkeywordpattern();
        var opmsg = this.parse_Roperatormessage();
        if (opmsg == null)
            return null;
        if (this.lastToken == Scanner._MKEYWORD_) {
            var pat = this.parse_Rkeywordpattern();
            return new ReifierMessage(opmsg, pat);
        } else
            return opmsg;
    }

    private Expression parse_Roperatormessage() throws IOException {
        if (lastToken == Scanner._MOPERATOR_)
            return this.parse_Roperatorpattern();
        var unarymsg = this.parse_Runarymessage();
        if (unarymsg == null)
            return null;
        while (this.lastToken == Scanner._MOPERATOR_) {
            var pat = this.parse_Roperatorpattern();
            if (pat == null)
                return null;
            unarymsg = new ReifierMessage(unarymsg, pat);
        }
        return unarymsg;
    }

    private Expression parse_Runarymessage() throws IOException {
        var keywordmsg = this.parse_Keywordmessage();
        if (keywordmsg == null)
            return null;
        while (this.lastToken == Scanner._MUNARY_) {
            var pat = this.parse_Runarypattern();
            if (pat == null)
                return null;
            keywordmsg = new ReifierMessage(keywordmsg, pat);
        }
        return keywordmsg;
    }

    private Expression parse_Keywordmessage() throws IOException {
        if (this.lastToken == Scanner._KEYWORD_)
            return this.parse_Keywordpattern();
        var opmsg = this.parse_Operatormessage();
        if (opmsg == null)
            return null;
        if (lastToken == Scanner._KEYWORD_) {
            var pat = this.parse_Keywordpattern();
            return new UserMessage(opmsg, pat);
        } else
            return opmsg;
    }

    private Expression parse_Operatormessage() throws IOException {
        if (this.lastToken == Scanner._OPERATOR_)
            return this.parse_Operatorpattern();
        var unarymsg = this.parse_Unarymessage();
        if (unarymsg == null)
            return null;
        while (lastToken == Scanner._OPERATOR_) {
            var pat = this.parse_Operatorpattern();
            if (pat == null)
                return null;
            unarymsg = new UserMessage(unarymsg, pat);
        }
        return unarymsg;
    }

    private Expression parse_Unarymessage() throws IOException {
        var factor = this.parse_Factor();
        if (factor == null)
            return null;
        while (this.lastToken == Scanner._UNARY_) {
            var pat = this.parse_Unarypattern();
            if (pat == null)
                return null;
            factor = new UserMessage(factor, pat);
        }
        return factor;
    }

    private Expression parse_Factor() throws IOException {
        if ((this.lastToken == Scanner._ERROR_) ||
                (this.lastToken == Scanner._EOFTOKEN_))
            return null;
        if (this.lastToken == Scanner._LPAR_) {
            this.scan();
            if ((this.lastToken == Scanner._ERROR_) ||
                    (this.lastToken == Scanner._EOFTOKEN_))
                return null;
            var exp = this.parseExpression();
            if (lastToken != Scanner._RPAR_)
                return null;
            this.scan();
            return exp;
        }
        if ((this.lastToken == Scanner._LBRACK_) ||
                (this.lastToken == Scanner._LBRACE_))
            return this.parse_Aggregate();
        if (this.lastToken == Scanner._MUNARY_)
            return this.parse_Runarypattern();
        if (this.lastToken == Scanner._UNARY_)
            return this.parse_Unarypattern();
        return this.parse_Literal();
    }

    private Expression parse_Literal() throws IOException {
        Expression lit;
        if (this.lastToken == Scanner._STRING_) {
            lit = this.s.lastString;
            this.scan();
            return lit;
        }
        if (this.lastToken == Scanner._INTEGER_) {
            lit = this.s.lastInteger;
            this.scan();
            return lit;
        }
        if (this.lastToken == Scanner._REAL_) {
            lit = this.s.lastFloat;
            this.scan();
            return lit;
        }
        if (this.lastToken == Scanner._CHAR_) {
            lit = this.s.lastCharacter;
            this.scan();
            return lit;
        }
        return null;
    }

    private Expression parse_Aggregate() throws IOException {
        var begin = this.lastToken;
        this.scan();
        if ((this.lastToken == Scanner._ERROR_) ||
                (this.lastToken == Scanner._EOFTOKEN_))
            return null;
        var exps = this.parse_Expressionsequence();
        if (exps == null)
            return null;
        if ((begin == Scanner._LBRACK_) &&
                (this.lastToken != Scanner._RBRACK_))
            return null;
        if ((begin == Scanner._LBRACE_) &&
                (this.lastToken != Scanner._RBRACE_))
            return null;
        this.scan();
        if (begin == Scanner._LBRACK_)
            return this.makeAggregate('[', ']', exps);
        else
            return this.makeAggregate('{', '}', exps);
    }

    private Queue<Object> parse_Expressionsequence() throws IOException {
        var q = new LinkedList<>();
        while (!((this.lastToken == Scanner._RBRACE_) ||
                (this.lastToken == Scanner._RBRACK_))) {
            var exp = this.parseExpression();
            if (exp == null)
                return null;
            q.offer(exp);
            if (this.lastToken != Scanner._SEMI_) {
                if ((this.lastToken != Scanner._RBRACE_) &&
                        (this.lastToken != Scanner._RBRACK_))
                    return null;
            } else {
                while (this.lastToken == Scanner._SEMI_)
                    this.scan();
                if ((this.lastToken == Scanner._RBRACE_) ||
                        (this.lastToken == Scanner._RBRACK_) ||
                        (this.lastToken == Scanner._ERROR_) ||
                        (this.lastToken == Scanner._EOFTOKEN_))
                    return null;
            }
        }
        return q;
    }

    private ReifKeywordPattern parse_Rkeywordpattern() throws IOException {
        var q = new LinkedList<>();
        var key = this.s.lastRKeyword;
        this.scan();
        if ((this.lastToken == Scanner._ERROR_) ||
                (this.lastToken == Scanner._EOFTOKEN_))
            return null;
        var exp = this.parse_Roperatormessage();
        if (exp != null) {
            q.offer(key);
            q.offer(exp);
            while (lastToken == Scanner._MKEYWORD_) {
                key = this.s.lastRKeyword;
                this.scan();
                if ((this.lastToken == Scanner._ERROR_) ||
                        (this.lastToken == Scanner._EOFTOKEN_))
                    return null;
                exp = this.parse_Roperatormessage();
                if (exp != null) {
                    q.offer(key);
                    q.offer(exp);
                } else
                    return null;
            }
            return makeReifierKeywordPattern(q);
        } else
            return null;
    }

    private ReifUnaryPattern parse_Runarypattern() throws IOException {
        if (this.lastToken != Scanner._MUNARY_)
            return null;
        var result = new ReifUnaryPattern(this.s.lastRUnary);
        this.scan();
        return result;
    }

    private ReifOperatorPattern parse_Roperatorpattern() throws IOException {
        if (this.lastToken != Scanner._MOPERATOR_)
            return null;
        var result = this.s.lastROperator;
        this.scan();
        var Runarymsg = this.parse_Runarymessage();
        if (Runarymsg == null)
            return null;
        return (new ReifOperatorPattern(result, Runarymsg));
    }

    private UserKeywordPattern parse_Keywordpattern() throws IOException {
        var q = new LinkedList<>();
        var key = this.s.lastUKeyword;
        this.scan();
        if ((this.lastToken == Scanner._ERROR_) ||
                (this.lastToken == Scanner._EOFTOKEN_))
            return null;
        var exp = this.parse_Operatormessage();
        if (exp != null) {
            q.offer(key);
            q.offer(exp);
            while (this.lastToken == Scanner._KEYWORD_) {
                key = this.s.lastUKeyword;
                this.scan();
                if ((this.lastToken == Scanner._ERROR_) ||
                        (this.lastToken == Scanner._EOFTOKEN_))
                    return null;
                exp = this.parse_Operatormessage();
                if (exp != null) {
                    q.offer(key);
                    q.offer(exp);
                } else
                    return null;
            }
            return this.makeUserKeywordPattern(q);
        } else
            return null;
    }

    private UserUnaryPattern parse_Unarypattern() throws IOException {
        if (this.lastToken != Scanner._UNARY_)
            return null;
        var result = new UserUnaryPattern(this.s.lastUUnary);
        this.scan();
        return result;
    }

    private UserOperatorPattern parse_Operatorpattern() throws IOException {
        if (this.lastToken != Scanner._OPERATOR_)
            return null;
        var result = this.s.lastUOperator;
        this.scan();
        var unarymsg = parse_Unarymessage();
        if (unarymsg == null)
            return null;
        return (new UserOperatorPattern(result, unarymsg));
    }
}
