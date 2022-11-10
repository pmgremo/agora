package agora.patterns;

import java.io.Serializable;

/**
 * This class represents the agora.runtime keyword agora.patterns as being stored in the
 * method tables. Keyword agora.patterns are stored as an array of strings.
 * Each string (i.e. each keyword) must be terminated by a colon.
 *
 * @author Wolfgang De Meuter (Programming technology Lab)
 * Last change:  E    16 Nov 97    1:50 am
 */
public class KeywordPattern extends AbstractPattern implements Serializable {
    /**
     * An array of strings representing the different keywords of
     * the KeywordPattern.
     */
    protected String[] keywords;

    /**
     * Constructor to create a keyword pattern of a given size. The KeywordPattern
     * is not reifier by default (see super constructor). The keywords are set
     * to be 'null' strings. Hence, the must be filled after creation with
     * the atPut method.
     *
     * @param size The initial size of the keyword pattern . This is the number of keywords
     *             the pattern contains.
     */
    public KeywordPattern(int size) {
        super();
        keywords = new String[size];
        for (int i = 0; i < size; i++)
            keywords[i] = null;
    }

    /**
     * Returns the number of keywords in the keyword pattern.
     *
     * @return The number of keywords in the pattern.
     */
    public int size() {
        return keywords.length;
    }

    /**
     * Method to retrieve the i'th keyword from the keywordpattern.
     *
     * @param i The index at which a keyword is to be read.
     * @return The keyword residing at that index.
     */
    public String at(int i) {
        return keywords[i];
    }

    /**
     * Method to store a new keyword at the i'th position of the pattern.
     *
     * @param i       The index at which a keyword must be updated.
     * @param keyword The new value of the keyword at that index.
     */
    public void atPut(int i, String keyword) {
        keywords[i] = keyword;
    }

    /**
     * Converts the keyword pattern to a string by concatenating all the
     * keywords of the pattern.
     *
     * @return The string representation of this keyword pattern.
     */
    public String toString() {
        String result = "";
        for (int i = 0; i < keywords.length; i++)
            result = result.concat(keywords[i]);
        return result;
    }

    /**
     * Internal hashing method.
     *
     * @return The hash value of this pattern.
     */
    protected int doHash() {
        int result = 0;
        String s = null;
        if (keywords.length > 1)
            s = keywords[1];
        else
            s = keywords[0];
        for (int i = 0; i < s.length(); i++)
            result = result + s.charAt(i);
        return result;
    }

    /**
     * Equality testing. If the argument is a keyword pattern, all the keywords
     * are subjected to an equality test. This method overrides the corresponding
     * method in 'java.lang.Object'.
     *
     * @param object An arbitrary object to be compared with this keyword pattern.
     * @return If the argument is also a keyword pattern, and it contains the same keywords
     * and its super.equal is the same, then true is returned. In all other cases, false is returned.
     */
    public boolean equals(Object object) {
        if (object instanceof AbstractPattern)
            if (super.equals(object))
                if (object instanceof KeywordPattern)
                    if (keywords.length == ((KeywordPattern) object).size()) {
                        for (int i = 0; i < keywords.length; i++)
                            if (!(keywords[i].equals(((KeywordPattern) object).at(i))))
                                return false;
                        return true;
                    }
        return false;
    }
}
