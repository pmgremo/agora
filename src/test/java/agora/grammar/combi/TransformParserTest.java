package agora.grammar.combi;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


class TransformParserTest {
    @Test
    public void shouldMatch() {
        var result = Parsers.digit().plus().flatten().map(Integer::parseInt).parse("1234  ");
        assertEquals(1234, result.get());
    }
}