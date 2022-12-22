package agora.grammar.combi;

import agora.grammar.*;
import agora.patterns.*;

import java.util.List;
import java.util.Map;

import static agora.grammar.combi.Parsers.*;

public class AgoraParser {
    private static final Map<Character, Character> table = Map.of(
            '\\', '\\',
            '/', '/',
            '"', '"',
            'b', '\b',
            'f', '\f',
            'n', '\n',
            'r', '\r',
            't', '\t'
    );
    public static Parser<Character> anyCharacter = choice(
            seq(character('\''), character(table::containsKey, "expected escape character")).pick(1).map(table::get),
            any("\"\\").not().seq(character(x -> true, "")).pick(0)
    );
    private static final Parser<Expression> stringLiteral = skip(character('\"')).then(anyCharacter.star().flatten()).skip(character('\"'))
            .map(StringLiteral::new);

    private static final Parser<Expression> characterLiteral = seq(character('\''), anyCharacter, character('\'')).pick(1)
            .map(CharLiteral::new);
    private static final Parser<String> numbers = digit().plus().flatten();
    private static final Parser<Expression> floatLiteral = seq(numbers, string("."), numbers).flatten()
            .map(Float::parseFloat)
            .map(FloatLiteral::new);
    private static final Parser<Expression> integerLiteral = numbers
            .map(Integer::parseInt)
            .map(IntegerLiteral::new);
    private static final Parser<Expression> literal = choice(stringLiteral, characterLiteral, floatLiteral, integerLiteral);
    private static final Parser<Expression> expression = parser(() -> AgoraParser.reifiedMessage)
            .or(() -> AgoraParser.userMessage)
            .or(() -> AgoraParser.factor)
            .trim();
    private static final Parser<Expression> factor = parser(() -> AgoraParser.parenthesized)
            .or(() -> AgoraParser.aggregate)
            .or(() -> AgoraParser.block)
            .or(() -> AgoraParser.reifiedUnary)
            .or(() -> AgoraParser.userUnary)
            .or(() -> AgoraParser.literal)
            .trim();
    private static final Parser<Cell<Character, List<Character>>> userIdentifier = character(Character::isLetter, "expected java letter")
            .then(word().star());
    private static final Parser<Expression> userUnary = userIdentifier
            .flatten()
            .map(UnaryPattern::new)
            .map(UserUnaryPattern::new);
    private static final Parser<Expression> userOperator = any("!@#$%^&*-=+<>/.,|?").plus().flatten()
            .map(OperatorPattern::new)
            .then(choice(factor, userMessage(userUnary)))
            .map(x -> new UserOperatorPattern(x.first(), x.second()));

    private static Parser<Expression> userMessage(Parser<Expression> parser) {
        return factor.then(parser).map(x -> new UserMessage(x.first(), (UserPattern) x.second()));
    }

    private static final Parser<Expression> userKeyword = userIdentifier.then(character(':')).flatten()
            .then(choice(factor, userMessage(userOperator), userMessage(userUnary))).plus()
            .map(x -> new UserKeywordPattern(new KeywordPattern(x.stream().map(Cell::first).toList()), x.stream().map(Cell::second).toList()));

    private static final Parser<Expression> userMessage = choice(
            userMessage(userOperator),
            userMessage(userKeyword),
            userMessage(userUnary)
    );
    private static final Parser<Character> upperCase = character(Character::isUpperCase, "expected upper case");
    private static final Parser<Cell<Character, List<Character>>> reifiedIdentifier = upperCase.then(upperCase.or(digit()).star());
    private static final Parser<Expression> reifiedUnary = reifiedIdentifier
            .flatten()
            .map(UnaryReifierPattern::new)
            .map(ReifUnaryPattern::new);
    private static final Parser<Expression> reifiedKeyword = reifiedIdentifier.then(character(':')).flatten()
            .then(choice(factor, reifiedMessage(reifiedUnary))).plus()
            .map(x -> new ReifKeywordPattern(new KeywordReifierPattern(x.stream().map(Cell::first).toList()), x.stream().map(Cell::second).toList()));

    private static Parser<Expression> reifiedMessage(Parser<Expression> parser) {
        return factor.then(parser).map(x -> new ReifierMessage(x.first(), (ReifPattern) x.second()));
    }

    private static final Parser<Expression> reifiedMessage = choice(
            reifiedMessage(reifiedKeyword),
            reifiedMessage(reifiedUnary)
    );

    private static final SequenceParser<Expression> expressions = expression.plus(character(';'));
    private static final Parser<Expression> aggregate = skip(character('[')).then(expressions).skip(character(']'))
            .map(Aggregate::new);
    private static final Parser<Expression> block = skip(character('{')).then(expressions).skip(character('}'))
            .map(Block::new);
    private static final Parser<Expression> parenthesized = skip(character('(')).then(expression).skip(character(')'));
    public static final Parser<Expression> parser = expression.end();
}
