package agora.javaAdditions;

import agora.errors.AgoraHalt;
import agora.errors.ProgramError;
import agora.grammar.Parser;
import agora.grammar.Scanner;
import agora.tools.AgoraGlobals;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;

import static java.nio.charset.Charset.defaultCharset;
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

    @Test
    public void shouldHalt() throws IOException {
        AgoraGlobals.glob = new AgoraGlobals(null, null);
        var parser = new Parser(new Scanner(new StringReader("\"Test Halt\" HALT")));
        var expression = parser.parseExpression();
        assertEquals("Test Halt", assertThrows(AgoraHalt.class, expression::defaultEval).getMessage());
    }

    @Test
    public void shouldPrintVariable() throws IOException {
        AgoraGlobals.glob = new AgoraGlobals(null, null);
        var parser = new Parser(new Scanner(new StringReader("""
                [
                    x VARIABLE: "hello";
                    SELF x: "bye";
                    (java . "lang.System") out printString: (SELF x)
                ]
                        """)));
        var expression = parser.parseExpression();
        assertEquals("bye", captureOut(expression::defaultEval));
    }

    public static String captureOut(Runnable statement) {
        var original = System.out;
        try (
                var capture = new ByteArrayOutputStream();
                var stream = new PrintStream(capture, true, defaultCharset())
        ) {
            System.setOut(stream);
            statement.run();
            return capture.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            System.setOut(original);
        }
    }
}
