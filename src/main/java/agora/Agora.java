package agora;

import agora.grammar.Parser;
import agora.grammar.Scanner;
import agora.tools.AgoraGlobals;

import java.io.IOException;
import java.io.StringReader;

public class Agora {
    public static void main(String... args) throws IOException {
        AgoraGlobals.glob = new AgoraGlobals(null, null);
        var input = new StringReader(args[0]);
        var parser = new Parser(new Scanner(input));
        var result = parser.parseExpression().defaultEval();
        System.out.println();
        System.out.println(result.down());
    }
}
