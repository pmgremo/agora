package agora.runtime;

import java.io.Serializable;

/**
 * This class defines a number of constants representing evaluation categories.
 * Each category is represented by a bit. A complete evaluation category is an
 * integer in which a number of bits are activated. Evaluation categories conduct the
 * evaluator to be in a special mode. For example, when a cloning method is activated, a
 * clone is to be created and its body is to be evaluated in the context of that clone.
 * However, if a cloning method is evaluated in the 'dontClone' category, the method is
 * simply evaluated without cloning. This situation happens when a cloning method does
 * a super call. In that case, only one clone must be made and the super cloning
 * method is to be evaluated in the 'dontClone' category. Each category conducts
 * the evaluator in a different way.
 * <p>
 * The Category class contains a number of public static methods (hence, procedures, yuck!),
 * but this is much more efficient that if we would be representing evaluation categories
 * by objects with methods defined on them. This is so because the evaluator
 * switches categories all the time.
 *
 * @author Wolfgang De Meuter (Programming Technology Lab).
 * Last change:  E    16 Nov 97    1:52 am
 */
public class Category implements Serializable {
    /**
     * The empty category (= ordinary evaluation)
     */
    static public final int emptyCategory = 0;
    static public final int mixin = 4;
    /**
     * The flags category. This is used when evaluating a pattern such that the pattern must
     * return itself instead of looking it up in some object. Hence, this is used when
     * evaluating the receiver of things like VARIABLE:
     */
    static public final int flags = 8;
    /**
     * This is used as part of the return value of evaluating PUBLIC. The return value of
     * this reifier is a formal pattern and a suite of bits to know the modifiers.
     */
    static public final int publik = 16;
    /**
     * Same as publik
     */
    static public final int local = 64;
    /**
     * When evaluating a cloning method, all it's super sends have to evaluated in the
     * dontClone category otherwise each cloning method in the inheritance chain would
     * create its own copy of the receiver.
     */
    static public final int dontClone = 256;

    /**
     * Tests whether the query bits contains more than or the same bits that the given
     * bitses.
     *
     * @param query The query bitset.
     * @param bits  The given bitset that must be contained in the query bitset.
     * @return Returns whether the query at least contains all bits as the fiven bitset.
     */
    static public boolean contains(int query, int bits) {
        return (query & bits) == bits;
    }

    /**
     * Tests whether the query bits contains not a single more bit than the given bitset,
     * in other words, the method yields true if all bits in the query are also in the
     * given bitset.
     *
     * @param query The query bitset.
     * @param bits  The given bitset that must be more or equal than the query
     * @return Returns true if all bits of the query are at least also in the bitset.
     */
    static public boolean containsLessThan(int query, int bits) {
        return (~query | bits) == ~0;
    }

    /**
     * Converts a category to a string.
     */
    static public String toString(int cat) {
        return "";
    }
}
