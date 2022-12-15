package agora.grammar.combi;

import org.junit.jupiter.api.Test;

import static agora.grammar.combi.Parsers.string;
import static org.junit.jupiter.api.Assertions.*;

class ParserTest {
    @Test
    public void shouldAlternate(){
        var parser = string("abc").or(string("123"));
        assertEquals("abc", parser.parse("abc").get());
        assertEquals("123", parser.parse("123").get());
        assertInstanceOf(Result.Failure.class, parser.parse("gghd"));
    }
}