package agora;

import agora.grammar.Parser;
import agora.grammar.Scanner;
import agora.tools.AgoraGlobals;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Agora {
    public static void main(String... args) throws IOException {
        AgoraGlobals.glob = new AgoraGlobals(null, null);
        var input = new ByteArrayInputStream(args[0].getBytes(UTF_8));
        var parser = new Parser(new Scanner(() -> input));
        var x = parser.parseExpression().defaultEval();
        System.out.println();
        System.out.println(x.down());
    }
}
