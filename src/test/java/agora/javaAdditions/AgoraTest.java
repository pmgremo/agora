package agora.javaAdditions;

import agora.errors.ProgramError;
import agora.grammar.Parser;
import agora.grammar.Scanner;
import agora.tools.AgoraGlobals;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AgoraTest {
    @Test
    public void shouldReportIllegalArgument() throws IOException {
        AgoraGlobals.glob = new AgoraGlobals(null, null);
        var parser = new Parser(new Scanner(new StringReader("1 + \"hi\"")));
        var expression = parser.parseExpression();
        assertEquals("Illegal Argument for +", assertThrows(ProgramError.class, expression::defaultEval).getMessage());
    }

    @Test
    public void shouldCondition() throws IOException {
        AgoraGlobals.glob = new AgoraGlobals(null, null);
        var parser = new Parser(new Scanner(new StringReader("1 > 5 IFTRUE: \"hello\" IFFALSE: \"bye\"")));
        var expression = parser.parseExpression();
        assertEquals("bye", expression.defaultEval().down());
    }
}
