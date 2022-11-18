package agora.patterns;

import java.util.Arrays;
import java.util.List;

/**
 * This class represents the runtime keyword patterns as being stored in the
 * method tables. Keyword patterns are stored as an array of strings.
 * Each string (i.e. each keyword) must be terminated by a colon.
 *
 * @param keywords An array of strings representing the different keywords of
 *                 the KeywordPattern.
 * @author Wolfgang De Meuter (Programming technology Lab)
 * Last change:  E    16 Nov 97    1:50 am
 */
public record KeywordReifierPattern(List<String> keywords) implements Pattern {

    public static KeywordReifierPattern keywordReifierPattern(String... words) {
        return new KeywordReifierPattern(Arrays.asList(words));
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
}
