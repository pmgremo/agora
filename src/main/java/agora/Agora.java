package agora;

import agora.grammar.Parser;
import agora.grammar.Scanner;
import agora.tools.AgoraGlobals;
import agora.tools.Io;

import java.io.IOException;
import java.io.StringReader;

public class Agora {
    public static void main(String... args) {
        AgoraGlobals.glob = new AgoraGlobals(null, null);
        var input = new StringReader(args[0]);
        var expression = new Parser(new Scanner(new Io() {
            @Override
            public void print(String s) {
                System.out.println(s);
            }

            @Override
            public char getChar() {
                try {
                    return (char) input.read();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }));
        expression.parseExpression().defaultEval();
    }
}
