package agora.patterns;

import java.io.Serializable;
import java.util.Objects;

/**
 * This class represents agora.runtime unary agora.patterns. A unary pattern is essentially nothing but
 * a string with the appropriate comparision methods defined on it.
 *
 * @author Wolfgang De Meuter (Programming Technology Lab).
 * Last change:  E    16 Nov 97    1:50 am
 */
public class UnaryPattern extends Pattern implements Serializable {
    /**
     * String denoting the pattern value of the unary pattern.
     */
    protected String pattern;

    /**
     * The constructor creates a unary pattern with the given string as pattern.
     * The constructed pattern is 'non-reifier' by default (by calling the super constructor).
     *
     * @param thePattern The string representation of the unary pattern to be created.
     */
    public UnaryPattern(String thePattern) {
        super();
        this.pattern = thePattern;
    }

    /**
     * Creates a keyword pattern with one keyword with the same string but with a colon added.
     * This is used when a variable named x is declared. In that case, a write slot is
     * needed with pattern x:
     * Hence 'makeWritePattern' must be sent to the unary pattern representing x.
     *
     * @return A keyword-write-pattern corresponding to the receiving unary
     * read-pattern.
     */
    public KeywordPattern makeWritePattern() {
        var writePat = new KeywordPattern();
        writePat.atPut(0, pattern.concat(":"));
        return writePat;
    }

    /**
     * Retrieves the unary pattern string stored inside the pattern.
     *
     * @return The internal string representation of the unary pattern.
     */
    public String getUnaryPattern() {
        return this.pattern;
    }

    /**
     * Converts the pattern to a string.
     *
     * @return The unary pattern converted to a string.
     */
    @Override
    public String toString() {
        return this.pattern;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        UnaryPattern that = (UnaryPattern) o;
        return Objects.equals(pattern, that.pattern);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), pattern);
    }
}

