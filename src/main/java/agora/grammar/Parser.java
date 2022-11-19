package agora.grammar;

import agora.patterns.KeywordReifierPattern;

import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static agora.grammar.Scanner.*;

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
        if (this.lastToken == _ERROR_ ||
                this.lastToken == _EOFTOKEN_)
            return null;
        return this.parse_Rkeywordmessage();
    }

    private void scan() throws IOException {
        this.lastToken = this.s.scan();
    }

    private ReifKeywordPattern makeReifierKeywordPattern(List<String> keys, List<Expression> values) {
        return new ReifKeywordPattern(new KeywordReifierPattern(keys), values);
    }

    private UserKeywordPattern makeUserKeywordPattern(List<String> keys, List<Expression> values) {
        return new UserKeywordPattern(keys, values);
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
        if (lastToken == _MKEYWORD_)
            return this.parse_Rkeywordpattern();
        var opmsg = this.parse_Roperatormessage();
        if (opmsg == null)
            return null;
        if (this.lastToken == _MKEYWORD_) {
            var pat = this.parse_Rkeywordpattern();
            return new ReifierMessage(opmsg, pat);
        } else
            return opmsg;
    }

    private Expression parse_Roperatormessage() throws IOException {
        if (lastToken == _MOPERATOR_)
            return this.parse_Roperatorpattern();
        var unarymsg = this.parse_Runarymessage();
        if (unarymsg == null)
            return null;
        while (this.lastToken == _MOPERATOR_) {
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
        while (this.lastToken == _MUNARY_) {
            var pat = this.parse_Runarypattern();
            if (pat == null)
                return null;
            keywordmsg = new ReifierMessage(keywordmsg, pat);
        }
        return keywordmsg;
    }

    private Expression parse_Keywordmessage() throws IOException {
        if (lastToken == _KEYWORD_) return parse_Keywordpattern();
        var opmsg = parse_Operatormessage();
        if (opmsg == null) return null;
        if (lastToken == _KEYWORD_) return new UserMessage(opmsg, parse_Keywordpattern());
        else return opmsg;
    }

    private Expression parse_Operatormessage() throws IOException {
        if (lastToken == _OPERATOR_) return parse_Operatorpattern();
        var unarymsg = parse_Unarymessage();
        if (unarymsg == null) return null;
        while (lastToken == _OPERATOR_) {
            var pat = this.parse_Operatorpattern();
            if (pat == null) return null;
            unarymsg = new UserMessage(unarymsg, pat);
        }
        return unarymsg;
    }

    private Expression parse_Unarymessage() throws IOException {
        var factor = parse_Factor();
        if (factor == null) return null;
        while (lastToken == _UNARY_) {
            var pat = parse_Unarypattern();
            if (pat == null) return null;
            factor = new UserMessage(factor, pat);
        }
        return factor;
    }

    private Expression parse_Factor() throws IOException {
        if (lastToken == _ERROR_ || lastToken == _EOFTOKEN_)
            return null;
        if (lastToken == _LPAR_) {
            scan();
            if (lastToken == _ERROR_ || lastToken == _EOFTOKEN_)
                return null;
            var exp = parseExpression();
            if (lastToken != _RPAR_)
                return null;
            scan();
            return exp;
        }
        if (lastToken == _LBRACK_ || lastToken == _LBRACE_)
            return parse_Aggregate();
        if (lastToken == _MUNARY_)
            return parse_Runarypattern();
        if (lastToken == _UNARY_)
            return parse_Unarypattern();
        return parse_Literal();
    }

    private Expression parse_Literal() throws IOException {
        if (lastToken == _STRING_) {
            var lit = s.lastString;
            scan();
            return lit;
        } else if (lastToken == _INTEGER_) {
            var lit = s.lastInteger;
            scan();
            return lit;
        } else if (lastToken == _REAL_) {
            var lit = s.lastFloat;
            scan();
            return lit;
        } else if (lastToken == _CHAR_) {
            var lit = s.lastCharacter;
            scan();
            return lit;
        } else {
            return null;
        }
    }

    private Expression parse_Aggregate() throws IOException {
        var begin = lastToken;
        scan();
        if (lastToken == _ERROR_ || lastToken == _EOFTOKEN_)
            return null;
        var exps = parse_Expressionsequence();
        if (exps == null)
            return null;
        if (begin == _LBRACK_ && lastToken != _RBRACK_)
            return null;
        if (begin == _LBRACE_ && lastToken != _RBRACE_)
            return null;
        scan();
        return begin == _LBRACK_ ? makeAggregate('[', ']', exps) : makeAggregate('{', '}', exps);
    }

    private Queue<Object> parse_Expressionsequence() throws IOException {
        var q = new LinkedList<>();
        while (!(this.lastToken == _RBRACE_ ||
                this.lastToken == _RBRACK_)) {
            var exp = parseExpression();
            if (exp == null)
                return null;
            q.offer(exp);
            if (lastToken != _SEMI_) {
                if (lastToken != _RBRACE_ &&
                        lastToken != _RBRACK_)
                    return null;
            } else {
                while (lastToken == _SEMI_)
                    scan();
                if (lastToken == _RBRACE_ ||
                        lastToken == _RBRACK_ ||
                        lastToken == _ERROR_ ||
                        lastToken == _EOFTOKEN_)
                    return null;
            }
        }
        return q;
    }

    private ReifKeywordPattern parse_Rkeywordpattern(List<String> keys, List<Expression> values) throws IOException {
        var key = s.lastRKeyword;
        scan();
        if (lastToken == _ERROR_ || lastToken == _EOFTOKEN_) return null;
        var exp = parse_Roperatormessage();
        if (exp == null) return null;
        keys.add(key);
        values.add(exp);
        return lastToken == _MKEYWORD_ ? parse_Rkeywordpattern(keys, values) : makeReifierKeywordPattern(keys, values);
    }

    private ReifKeywordPattern parse_Rkeywordpattern() throws IOException {
        var keys = new LinkedList<String>();
        var values = new LinkedList<Expression>();
        return parse_Rkeywordpattern(keys, values);
    }

    private ReifUnaryPattern parse_Runarypattern() throws IOException {
        if (this.lastToken != _MUNARY_)
            return null;
        var result = new ReifUnaryPattern(this.s.lastRUnary);
        this.scan();
        return result;
    }

    private ReifOperatorPattern parse_Roperatorpattern() throws IOException {
        if (this.lastToken != _MOPERATOR_)
            return null;
        var result = this.s.lastROperator;
        this.scan();
        var Runarymsg = this.parse_Runarymessage();
        if (Runarymsg == null)
            return null;
        return (new ReifOperatorPattern(result, Runarymsg));
    }

    private UserKeywordPattern parse_Keywordpattern() throws IOException {
        return parse_Keywordpattern(new LinkedList<>(), new LinkedList<>());
    }

    private UserKeywordPattern parse_Keywordpattern(List<String> keys, List<Expression> values) throws IOException {
        keys.add(s.lastUKeyword);
        scan();
        if (lastToken == _ERROR_ || lastToken == _EOFTOKEN_) return null;
        var exp = parse_Operatormessage();
        if (exp == null) return null;
        values.add(exp);
        if (lastToken != _KEYWORD_) return makeUserKeywordPattern(keys, values);
        return parse_Keywordpattern(keys, values);
    }

    private UserUnaryPattern parse_Unarypattern() throws IOException {
        if (this.lastToken != _UNARY_)
            return null;
        var result = new UserUnaryPattern(this.s.lastUUnary);
        this.scan();
        return result;
    }

    private UserOperatorPattern parse_Operatorpattern() throws IOException {
        if (this.lastToken != _OPERATOR_)
            return null;
        var result = this.s.lastUOperator;
        this.scan();
        var unarymsg = parse_Unarymessage();
        if (unarymsg == null)
            return null;
        return (new UserOperatorPattern(result, unarymsg));
    }
}
