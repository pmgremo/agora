package agora.patterns;

/**
 * Concrete class representing runtime operator agora.patterns. An operator pattern
 * is essentially nothing but a string (containing the operator symbols) with
 * the appropriate methods defined on it.
 *
 * @param operator String representing the operator.
 * @author Wolfgang De Meuter (Programming Technology Lab).
 * Last change:  E    16 Nov 97    1:50 am
 */
public record OperatorPattern(String operator) implements Pattern {
    @Override
    public String toString() {
        return operator;
    }
}
