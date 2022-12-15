package agora.grammar.combi;

import agora.grammar.*;
import agora.patterns.OperatorPattern;
import agora.patterns.UnaryPattern;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChoiceParseTest {
    @Test
    public void shouldParExpression() {
        assertEquals(new Block(List.of(new StringLiteral("hello\n"),new CharLiteral('J'))), AgoraParser.parser.parse("{\t\"hello\n\";\n\t'J'}").get());
        assertEquals(new StringLiteral("hello\n"), AgoraParser.parser.parse("\"hello\n\"").get());
        assertEquals(new CharLiteral('J'), AgoraParser.parser.parse("'J'").get());
        assertEquals(new IntegerLiteral(4432), AgoraParser.parser.parse("4432").get());
        assertEquals(new FloatLiteral(3.14f), AgoraParser.parser.parse("3.14").get());
    }

    @Test
    public void shouldParseOperator(){
        assertEquals(
                new UserOperatorPattern(new OperatorPattern("+"), new IntegerLiteral(43)),
                AgoraParser.parser.parse("+ 43").get()
        );
    }

    @Test
    public void shouldParseUserUnary(){
        assertEquals(
                new UserUnaryPattern(new UnaryPattern("blah")),
                AgoraParser.parser.parse("blah").get()
        );
    }

    @Test
    public void shouldParseUserKeyword(){
        assertEquals(
                new UserKeywordPattern(List.of("a:", "b:"), List.of(new IntegerLiteral(123), new StringLiteral("hi"))),
                AgoraParser.parser.parse("a:123 b:\"hi\"").get()
        );
    }

}
