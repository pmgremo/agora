package agora.patterns;

import java.io.Serializable;

/**
 * This class represents agora.runtime unary agora.patterns. A unary pattern is essentially nothing but
 * a string with the appropriate comparision methods defined on it.
 *
 * @author Wolfgang De Meuter (Programming Technology Lab).
 * Last change:  E    16 Nov 97    1:50 am
 */
public class UnaryPattern extends AbstractPattern implements Serializable {
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
        var writePat = new KeywordPattern(1);
        writePat.atPut(0, pattern.concat(":"));
        return writePat;
    }

    /**
     * Internal hashing method.
     *
     * @return The hash value of this unary pattern.
     */
    protected int doHash() {
        var i = 0;
        for (var j = 0; j < pattern.length(); j++)
            i = i + this.pattern.charAt(j);
        return i;
    }

    /**
     * Equality test. If the argument is a unary pattern as well, both strings are compared.
     *
     * @param object An arbitrary Java object.
     * @return If the argument is also a unary pattern, and if it has the same pattern and if
     * super.equals returns true, the result of this method yields true. In all other cases,
     * false is returned.
     */
    public boolean equals(Object object) {
        return object instanceof UnaryPattern && super.equals(object) && pattern.equals(((UnaryPattern) object).getUnaryPattern());
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
    public String toString() {
        return this.pattern;
    }
}

