package agora;

import agora.grammar.Parser;
import agora.grammar.Scanner;
import agora.tools.AgoraGlobals;
import agora.tools.Io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Agora {
    public static void main(String... args) throws IOException {
        AgoraGlobals.glob = new AgoraGlobals(null, null);
        var input = new ByteArrayInputStream(args[0].getBytes(UTF_8));
        var parser = new Parser(new Scanner(new Io() {
            @Override
            public void print(String s) {
                System.out.println(s);
            }

            @Override
            public InputStream in() {
                return input;
            }
        }));
        parser.parseExpression().defaultEval();
    }
}
