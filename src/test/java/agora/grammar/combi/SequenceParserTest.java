package agora.grammar.combi;

import org.junit.jupiter.api.Test;

import static agora.grammar.combi.Parsers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SequenceParserTest {
    @Test
    public void shouldParse() {
        var parser = seq(character(' ').flatten(), string("1234")).pick(1);
        assertEquals("1234", parser.parse(" 1234").get());
    }

    @Test
    public void shouldParseCharacterLiteral() {
        var parser = seq(character('\''), word(), character('\'')).pick(1);
        assertEquals('g', parser.parse("'g''").get());
    }
}