package agora.grammar;

/**
 * Grammar component representing integer literal parse tree nodes.
 *
 * @author Wolfgang De Meuter
 * Last change:  E    16 Nov 97    2:18 pm
 */
public class IntegerLiteral extends Literal<Integer> {
    /**
     * Constructs a new integer literal parse tree node.
     *
     * @param value The value represented by this literal.
     */
    public IntegerLiteral(int value) {
        super(value);
    }
}
