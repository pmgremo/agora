package agora;

import agora.errors.AgoraHalt;
import agora.errors.MessageNotUnderstood;
import agora.errors.ProgramError;
import agora.grammar.Expression;
import agora.grammar.Parser;
import agora.grammar.Scanner;
import agora.tools.AgoraGlobals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;

import static java.nio.charset.Charset.defaultCharset;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AgoraTest {

    @BeforeEach
    public void setUp() {
        AgoraGlobals.glob = new AgoraGlobals(null, null);
    }

    public static Expression compile(String code) throws IOException {
        var parser = new Parser(new Scanner(new StringReader(code)));
        return parser.parseExpression();
    }

    @Test
    public void shouldPrintHelloWorld() throws IOException {
        var expression = compile("(java . \"lang\" . \"System\") out printString: \"Hello World!\"");
        assertEquals("Hello World!", captureOut(expression::defaultEval));
    }

    @Test
    public void shouldExpressionAsArgument() throws IOException {
        var expression = compile("(java . \"lang\" . \"System\") out printString: ((5 + 9 * 10) toString)");
        assertEquals("140", captureOut(expression::defaultEval));
    }

    @Test
    public void shouldConstructJavaObject() throws IOException {
        var expression = compile("(\"java.awt.Label\" JAVA) newString: \"Hello World!\"");
        assertEquals("Hello World!", expression.defaultEval().<Label>down().getText());
    }

    @Test
    public void shouldReportIllegalArgument() throws IOException {
        var expression = compile("1 + \"hi\"");
        assertEquals("Illegal Argument for +", assertThrows(ProgramError.class, expression::defaultEval).getMessage());
    }

    @Test
    public void shouldErrorAccessingLocalVariableOfAnotherObject() throws IOException {
        var expression = compile("""
                [
                    account PUBLIC VARIABLE: [
                                                amount PUBLIC VARIABLE: 0;
                                                password LOCAL VARIABLE: "007"
                                             ];
                    SELF account password:"008"
                ]
                """);
        assertEquals("password:", assertThrows(MessageNotUnderstood.class, expression::defaultEval).getPattern().toString());
    }

    @Test
    public void shouldCondition() throws IOException {
        var expression = compile("1 > 5 IFTRUE: \"hello\" IFFALSE: \"bye\"");
        assertEquals("bye", expression.defaultEval().down());
    }

    @Test
    public void shouldHalt() throws IOException {
        var expression = compile("\"Test Halt\" HALT");
        assertEquals("Test Halt", assertThrows(AgoraHalt.class, expression::defaultEval).getMessage());
    }

    @Test
    public void shouldPrintVariable() throws IOException {
        var expression = compile("""
                [
                    x VARIABLE: "hello";
                    SELF x: "bye";
                    (java . "lang.System") out printString: (SELF x)
                ]
                        """);
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
