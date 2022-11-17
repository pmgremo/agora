package agora.grammar;

/**
 * The parse tree node class representing character literals.
 *
 * @author Wolfgang De Meuter
 * Last change:  E    16 Nov 97    1:58 pm
 */
public class CharLiteral extends Literal<Character> {
    /**
     * Creates a new node with c as character.
     *
     * @param c The Java character corresponding to the character literal.
     */
    public CharLiteral(char c) {
        super(c);
    }

    /**
     * Unparses the node to a string. The integer denotes the number of leading blanks.
     *
     * @param hor The number of spaces leading the unparsed representation.
     * @return The literal as string.
     */
    public String unparse(int hor) {
        return " ".repeat(hor) + "'" + (value() == '\n' ? "eoln" : value()) + "'";
    }
}


