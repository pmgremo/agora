package agora.javaAdditions;

import agora.reflection.Operator;

/**
 * This is the implementation level class representing 'package' agora.objects in Agora.
 * Each package abstractly consist of a name (of directories separated by dots).
 * If a package is sent 'more' with a string, there are two possibilities.
 * 1) Either the resulting string denotes a valid class. In this case the
 * class is returned such that an Agora programmer can start making agora.objects
 * of that class.
 * 2) Either the resulting string does not denote a valid class. In that case,
 * the string is returned as a new (longer) package.
 *
 * @author Wolfgang De Meuter (Programming technology Lab).
 * Last change:  E    15 Nov 97   11:52 pm
 */
public class JV_Package {
    protected String packageName;

    /**
     * Creates a new package with the given name.
     *
     * @param packageName The name of the package, e.g. "java.lang"
     */
    public JV_Package(String packageName) {
        this.packageName = packageName;
    }

    /**
     * This method takes a string. It is added to the package string after a dot.
     * If the result is a valid class, the class is returned, otherwise a new
     * 'package' is created with the resulting string.
     *
     * @param s The string to be added to the current string in the package.
     * @return If the result of adding the argument to the package is a class in Java,
     * that class is returned. Otherwise, a new package is created with the concatenation
     * of both strings (with an extra dot in between them).
     */
    @Operator(".")
    public Object more(String s) {
        var newName = this.packageName.concat(".".concat(s)); // Determine new name
        try {
            return Class.forName(newName);                // Try to return the class of that name
        } catch (Throwable e) {
            return new JV_Package(newName);             // If it isn't there, create a new package
        }
    }
}
