package agora.patterns;

import java.util.Objects;

/**
 * Concrete class representing agora.runtime operator agora.patterns. An operator pattern
 * is essentially nothing but a string (containing the operator symbols) with
 * the appropriate methods defined on it.
 *
 * @author Wolfgang De Meuter (Programming Technology Lab).
 * Last change:  E    16 Nov 97    1:50 am
 */
public class OperatorPattern extends Pattern {
    /**
     * String representing the operator.
     */
    protected String operator;

    /**
     * Creates a new operator pattern that is 'non-reifier' by default.
     *
     * @param theOperator The string representation of the new operator pattern.
     */
    public OperatorPattern(String theOperator) {
        super();
        this.operator = theOperator;
    }


    /**
     * Reads the string representing the operator pattern.
     *
     * @return The internal string representation of this operator pattern.
     */
    public String getOperatorPattern() {
        return this.operator;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        OperatorPattern that = (OperatorPattern) o;
        return Objects.equals(operator, that.operator);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), operator);
    }

    @Override
    public String toString() {
        return operator;
    }
}
