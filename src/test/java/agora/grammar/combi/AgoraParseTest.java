package agora.grammar.combi;

import agora.grammar.*;
import agora.patterns.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AgoraParseTest {
    @Test
    public void shouldParseLiteral() {
        assertEquals(new StringLiteral("hello\n"), AgoraParser.parser.parse("\"hello\n\"").get());
        assertEquals(new CharLiteral('J'), AgoraParser.parser.parse("'J'").get());
        assertEquals(new IntegerLiteral(4432), AgoraParser.parser.parse("4432").get());
        assertEquals(new FloatLiteral(3.14f), AgoraParser.parser.parse("3.14").get());
    }

    @Test
    public void shouldParseBlock() {
        assertEquals(new Block(List.of(new StringLiteral("hello\n"), new CharLiteral('J'))), AgoraParser.parser.parse("{\t\"hello\n\";\n\t'J'}").get());
    }

    @Test
    public void shouldParseOperator() {
        assertEquals(
                new UserMessage(new IntegerLiteral(6), new UserOperatorPattern(new OperatorPattern("+"), new IntegerLiteral(43))),
                AgoraParser.parser.parse("6 + 43").get()
        );
    }

    @Test
    public void shouldParseUserUnary() {
        assertEquals(
                new UserUnaryPattern(new UnaryPattern("blah")),
                AgoraParser.parser.parse("blah").get()
        );
    }

    @Test
    public void shouldParseUserKeyword() {
        assertEquals(
                new UserMessage(
                        new UserUnaryPattern(new UnaryPattern("hello")),
                        new UserKeywordPattern(new KeywordPattern(List.of("a:", "b:")), List.of(new IntegerLiteral(123), new StringLiteral("hi")))
                ),
                AgoraParser.parser.parse("hello a:123 b:\"hi\"").get()
        );
    }

    @Test
    public void shouldParseReifiedUnary() {
        assertEquals(
                new ReifUnaryPattern(new UnaryReifierPattern("BLAH")),
                AgoraParser.parser.parse("BLAH").get()
        );
    }

    @Test
    public void shouldParseReifiedKeyword() {
        assertEquals(
                new ReifierMessage(
                        new UserUnaryPattern(new UnaryPattern("hello")),
                        new ReifKeywordPattern(new KeywordReifierPattern(List.of("A:", "B:")), List.of(new IntegerLiteral(123), new StringLiteral("hi")))
                ),
                AgoraParser.parser.parse("hello A:123 B:\"hi\"").get()
        );
    }

    @Test
    public void shouldParseParens() {
        assertEquals(
                new UserMessage(
                        new UserMessage(new IntegerLiteral(2), new UserOperatorPattern(new OperatorPattern("+"), new IntegerLiteral(3))),
                        new UserOperatorPattern(new OperatorPattern("*"), new IntegerLiteral(4))
                ),
                AgoraParser.parser.parse("(2 + 3) * 4").get()
        );
    }

}
