package agora.grammar;

/**
 * Parse tree nodes for floating point literals.
 *
 * @author Wolfgang De Meuter
 * Last change:  E    16 Nov 97    2:18 pm
 */
public class FloatLiteral extends Literal<Float> {
    /**
     * Constructs a new floating point parse tree node.
     *
     * @param value The floating point value represented by this literal.
     */
    public FloatLiteral(float value) {
        super(value);
    }
}
