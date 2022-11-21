package agora.grammar;

import agora.patterns.*;

import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

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
     * This is the parse routine. Either it succeeds, and returns a valid Agora
     * parse tree, or it fails and returns 'null' as parse tree.
     *
     * @return The parsed expression. Parse errors are indicated by null as return value.
     */
    public Expression parseExpression() throws IOException {
        return lastToken == _ERROR_ || lastToken == _EOFTOKEN_ ? null : parse_Rkeywordmessage();
    }

    private void scan() throws IOException {
        lastToken = s.scan();
    }

    private Expression parse_Rkeywordmessage() throws IOException {
        if (lastToken == _MKEYWORD_) return parse_Rkeywordpattern();
        var opmsg = parse_Roperatormessage();
        if (opmsg == null) return null;
        if (lastToken != _MKEYWORD_) return opmsg;
        return new ReifierMessage(opmsg, parse_Rkeywordpattern());
    }

    private Expression parse_Roperatormessage() throws IOException {
        if (lastToken == _MOPERATOR_) return parse_Roperatorpattern();
        var unarymsg = parse_Runarymessage();
        if (unarymsg == null) return null;
        while (lastToken == _MOPERATOR_) {
            var pat = parse_Roperatorpattern();
            if (pat == null) return null;
            unarymsg = new ReifierMessage(unarymsg, pat);
        }
        return unarymsg;
    }

    private Expression parse_Runarymessage() throws IOException {
        var keywordmsg = parse_Keywordmessage();
        if (keywordmsg == null) return null;
        while (lastToken == _MUNARY_) {
            var pat = parse_Runarypattern();
            if (pat == null) return null;
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
        var exps = parse_ExpressionSequence();
        if (exps == null)
            return null;
        if (begin == _LBRACK_ && lastToken != _RBRACK_)
            return null;
        if (begin == _LBRACE_ && lastToken != _RBRACE_)
            return null;
        scan();
        return begin == _LBRACK_ ?
                new Aggregate(exps, '[', ']') :
                new Aggregate(exps, '{', '}');
    }

    private List<Expression> parse_ExpressionSequence() throws IOException {
        var q = new LinkedList<Expression>();
        while (!(this.lastToken == _RBRACE_ ||
                this.lastToken == _RBRACK_)) {
            var exp = parseExpression();
            if (exp == null) return null;
            q.offer(exp);
            if (lastToken == _SEMI_) {
                while (lastToken == _SEMI_)
                    scan();
                if (lastToken == _RBRACE_ ||
                        lastToken == _RBRACK_ ||
                        lastToken == _ERROR_ ||
                        lastToken == _EOFTOKEN_)
                    return null;
            } else {
                if (lastToken != _RBRACE_ &&
                        lastToken != _RBRACK_)
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
        return lastToken == _MKEYWORD_ ? parse_Rkeywordpattern(keys, values) : new ReifKeywordPattern(new KeywordReifierPattern(keys), values);
    }

    private ReifKeywordPattern parse_Rkeywordpattern() throws IOException {
        var keys = new LinkedList<String>();
        var values = new LinkedList<Expression>();
        return parse_Rkeywordpattern(keys, values);
    }

    private ReifUnaryPattern parse_Runarypattern() throws IOException {
        if (lastToken != _MUNARY_) return null;
        var result = new ReifUnaryPattern(new UnaryReifierPattern(s.lastRUnary));
        scan();
        return result;
    }

    private ReifOperatorPattern parse_Roperatorpattern() throws IOException {
        if (lastToken != _MOPERATOR_) return null;
        var result = s.lastROperator;
        scan();
        var runarymsg = parse_Runarymessage();
        if (runarymsg == null) return null;
        return new ReifOperatorPattern(new OperatorReifierPattern(result), runarymsg);
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
        if (lastToken != _KEYWORD_) return new UserKeywordPattern(keys, values);
        return parse_Keywordpattern(keys, values);
    }

    private UserUnaryPattern parse_Unarypattern() throws IOException {
        if (lastToken != _UNARY_) return null;
        var result = new UserUnaryPattern(new UnaryPattern(s.lastUUnary));
        scan();
        return result;
    }

    private UserOperatorPattern parse_Operatorpattern() throws IOException {
        if (lastToken != _OPERATOR_) return null;
        var result = s.lastUOperator;
        scan();
        var unarymsg = parse_Unarymessage();
        if (unarymsg == null) return null;
        return new UserOperatorPattern(new OperatorPattern(result), unarymsg);
    }
}
