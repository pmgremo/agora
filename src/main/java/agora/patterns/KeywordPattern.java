package agora.patterns;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * This class represents the runtime keyword patterns as being stored in the
 * method tables. Keyword patterns are stored as an array of strings.
 * Each string (i.e. each keyword) must be terminated by a colon.
 *
 * @author Wolfgang De Meuter (Programming technology Lab)
 * Last change:  E    16 Nov 97    1:50 am
 */
public class KeywordPattern extends Pattern {
    /**
     * An array of strings representing the different keywords of
     * the KeywordPattern.
     */
    protected List<String> keywords = new LinkedList<>();

    /**
     * Returns the number of keywords in the keyword pattern.
     *
     * @return The number of keywords in the pattern.
     */
    public int size() {
        return keywords.size();
    }

    /**
     * Method to retrieve the i'th keyword from the keywordpattern.
     *
     * @param i The index at which a keyword is to be read.
     * @return The keyword residing at that index.
     */
    public String at(int i) {
        return keywords.get(i);
    }

    public void add(String keyword) {
        keywords.add(keyword);
    }

    /**
     * Converts the keyword pattern to a string by concatenating all the
     * keywords of the pattern.
     *
     * @return The string representation of this keyword pattern.
     */
    public String toString() {
        return String.join("", keywords);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        KeywordPattern that = (KeywordPattern) o;
        return Objects.equals(keywords, that.keywords);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), keywords);
    }
}
