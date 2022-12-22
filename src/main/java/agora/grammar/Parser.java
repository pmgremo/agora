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
        scan();
    }

    /**
     * This is the parse routine. Either it succeeds, and returns a valid Agora
     * parse tree, or it fails and returns 'null' as parse tree.
     *
     * @return The parsed expression. Parse errors are indicated by null as return value.
     */
    public Expression parseExpression() throws IOException {
        return lastToken == _ERROR_ || lastToken == _EOFTOKEN_ ? null : parseRkeywordMessage();
    }

    private void scan() throws IOException {
        lastToken = s.scan();
    }

    private Expression parseRkeywordMessage() throws IOException {
        if (lastToken == _MKEYWORD_) return parseRkeywordPattern();
        var opmsg = parseRoperatorMessage();
        if (opmsg == null) return null;
        if (lastToken != _MKEYWORD_) return opmsg;
        return new ReifierMessage(opmsg, parseRkeywordPattern());
    }

    private Expression parseRoperatorMessage() throws IOException {
        if (lastToken == _MOPERATOR_) return parseRoperatorPattern();
        var unarymsg = parseRunaryMessage();
        if (unarymsg == null) return null;
        while (lastToken == _MOPERATOR_) {
            var pat = parseRoperatorPattern();
            if (pat == null) return null;
            unarymsg = new ReifierMessage(unarymsg, pat);
        }
        return unarymsg;
    }

    private Expression parseRunaryMessage() throws IOException {
        var keywordmsg = parseKeywordMessage();
        if (keywordmsg == null) return null;
        while (lastToken == _MUNARY_) {
            var pat = parseRunaryPattern();
            if (pat == null) return null;
            keywordmsg = new ReifierMessage(keywordmsg, pat);
        }
        return keywordmsg;
    }

    private Expression parseKeywordMessage() throws IOException {
        if (lastToken == _KEYWORD_) return parseKeywordPattern();
        var opmsg = parseOperatorMessage();
        if (opmsg == null) return null;
        if (lastToken == _KEYWORD_) return new UserMessage(opmsg, parseKeywordPattern());
        else return opmsg;
    }

    private Expression parseOperatorMessage() throws IOException {
        if (lastToken == _OPERATOR_) return parseOperatorPattern();
        var unarymsg = parseUnaryMessage();
        if (unarymsg == null) return null;
        while (lastToken == _OPERATOR_) {
            var pat = this.parseOperatorPattern();
            if (pat == null) return null;
            unarymsg = new UserMessage(unarymsg, pat);
        }
        return unarymsg;
    }

    private Expression parseUnaryMessage() throws IOException {
        var factor = parseFactor();
        if (factor == null) return null;
        while (lastToken == _UNARY_) {
            var pat = parseUnaryPattern();
            if (pat == null) return null;
            factor = new UserMessage(factor, pat);
        }
        return factor;
    }

    private Expression parseFactor() throws IOException {
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
            return parseAggregate();
        if (lastToken == _MUNARY_)
            return parseRunaryPattern();
        if (lastToken == _UNARY_)
            return parseUnaryPattern();
        return parseLiteral();
    }

    private Expression parseLiteral() throws IOException {
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

    private Expression parseAggregate() throws IOException {
        var begin = lastToken;
        scan();
        if (lastToken == _ERROR_ || lastToken == _EOFTOKEN_)
            return null;
        var exps = parseExpressionSequence();
        if (exps == null)
            return null;
        if (begin == _LBRACK_ && lastToken != _RBRACK_)
            return null;
        if (begin == _LBRACE_ && lastToken != _RBRACE_)
            return null;
        scan();
        return begin == _LBRACK_ ? new Aggregate(exps) : new Block(exps);
    }

    private List<Expression> parseExpressionSequence() throws IOException {
        var q = new LinkedList<Expression>();
        while (!(lastToken == _RBRACE_ ||
                lastToken == _RBRACK_)) {
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

    private ReifKeywordPattern parseRkeywordPattern(List<String> keys, List<Expression> values) throws IOException {
        var key = s.lastRKeyword;
        scan();
        if (lastToken == _ERROR_ || lastToken == _EOFTOKEN_) return null;
        var exp = parseRoperatorMessage();
        if (exp == null) return null;
        keys.add(key);
        values.add(exp);
        return lastToken == _MKEYWORD_ ? parseRkeywordPattern(keys, values) : new ReifKeywordPattern(new KeywordReifierPattern(keys), values);
    }

    private ReifKeywordPattern parseRkeywordPattern() throws IOException {
        var keys = new LinkedList<String>();
        var values = new LinkedList<Expression>();
        return parseRkeywordPattern(keys, values);
    }

    private ReifUnaryPattern parseRunaryPattern() throws IOException {
        if (lastToken != _MUNARY_) return null;
        var result = new ReifUnaryPattern(new UnaryReifierPattern(s.lastRUnary));
        scan();
        return result;
    }

    private ReifOperatorPattern parseRoperatorPattern() throws IOException {
        if (lastToken != _MOPERATOR_) return null;
        var result = s.lastROperator;
        scan();
        var runarymsg = parseRunaryMessage();
        if (runarymsg == null) return null;
        return new ReifOperatorPattern(new OperatorReifierPattern(result), runarymsg);
    }

    private UserKeywordPattern parseKeywordPattern() throws IOException {
        return parseKeywordPattern(new LinkedList<>(), new LinkedList<>());
    }

    private UserKeywordPattern parseKeywordPattern(List<String> keys, List<Expression> values) throws IOException {
        keys.add(s.lastUKeyword);
        scan();
        if (lastToken == _ERROR_ || lastToken == _EOFTOKEN_) return null;
        var exp = parseOperatorMessage();
        if (exp == null) return null;
        values.add(exp);
        if (lastToken != _KEYWORD_) return new UserKeywordPattern(new KeywordPattern(keys), values);
        return parseKeywordPattern(keys, values);
    }

    private UserUnaryPattern parseUnaryPattern() throws IOException {
        if (lastToken != _UNARY_) return null;
        var result = new UserUnaryPattern(new UnaryPattern(s.lastUUnary));
        scan();
        return result;
    }

    private UserOperatorPattern parseOperatorPattern() throws IOException {
        if (lastToken != _OPERATOR_) return null;
        var result = s.lastUOperator;
        scan();
        var unarymsg = parseUnaryMessage();
        if (unarymsg == null) return null;
        return new UserOperatorPattern(new OperatorPattern(result), unarymsg);
    }
}
