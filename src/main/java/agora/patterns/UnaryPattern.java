package agora.patterns;

import java.util.List;

/**
 * This class represents runtime unary agora.patterns. A unary pattern is essentially nothing but
 * a string with the appropriate comparision methods defined on it.
 *
 * @param pattern String denoting the pattern value of the unary pattern.
 * @author Wolfgang De Meuter (Programming Technology Lab).
 * Last change:  E    16 Nov 97    1:50 am
 */
public record UnaryPattern(String pattern) implements Pattern {
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
        return new KeywordPattern(List.of(new String[]{pattern + ":"}));
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
}

