package agora.grammar.combi;

import agora.grammar.*;
import agora.patterns.OperatorPattern;
import agora.patterns.UnaryPattern;

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

    private static final Parser<String> numbers = digit().plus().flatten();

    private static final Parser<Expression> stringLiteral = skip(character('\"')).then(anyCharacter.star().flatten()).skip(character('\"'))
            .map(StringLiteral::new);
    private static final Parser<Expression> characterLiteral = seq(character('\''), anyCharacter, character('\'')).pick(1)
            .map(CharLiteral::new);
    private static final Parser<Expression> floatLiteral = seq(numbers, string("."), numbers).flatten()
            .map(Float::parseFloat)
            .map(FloatLiteral::new);
    private static final Parser<Expression> integerLiteral = numbers
            .map(Integer::parseInt)
            .map(IntegerLiteral::new);

    private static final Parser<Expression> expression = stringLiteral
            .or(characterLiteral)
            .or(floatLiteral)
            .or(integerLiteral)
            .or(() -> AgoraParser.userOperator)
            .or(() -> AgoraParser.userKeyword)
            .or(() -> AgoraParser.userUnary)
            .or(() -> AgoraParser.aggregate)
            .or(() -> AgoraParser.block)
            .trim();
    private static final Parser<Expression> userOperator = any("!@#$%^&*-=+<>/.,|?").plus().flatten().map(OperatorPattern::new).then(expression)
            .map(x -> new UserOperatorPattern(x.first(), x.second()));

    private static final Parser<Expression> userUnary = seq(character(Character::isLetter, "expected java letter").flatten(), word().star().flatten()).flatten()
            .map(UnaryPattern::new)
            .map(UserUnaryPattern::new);

    private static final Parser<Expression> userKeyword = seq(
            character(Character::isLetter, "expected java letter").flatten(),
            word().star().flatten(),
            character(':').flatten()
    ).flatten()
            .then(expression).plus()
            .map(x -> new UserKeywordPattern(x.stream().map(Cell::first).toList(), x.stream().map(Cell::second).toList()));
    private static final SequenceParser<Expression> expressions = expression.plus(character(';'));
    private static final Parser<Expression> aggregate = skip(character('[')).then(expressions).skip(character(']'))
            .map(Aggregate::new);
    private static final Parser<Expression> block = skip(character('{')).then(expressions).skip(character('}'))
            .map(Block::new);

    public static final Parser<Expression> parser = expression.end();
}
