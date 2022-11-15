package agora.javaAdditions;

import agora.reflection.Frame;
import agora.reflection.Operator;

/**
 * When the Java 'null' object is wrapped into the Agora 'null' object,
 * this will not work since 'null' has no class in Java, and hence
 * it has no methods. That's why the Agora object 'null' has a method
 * table, whose methods (only one, the = method) have to be somewhere.
 * Hence, this class contains static methods (i.e. procedures)
 * that contain the code of the methods defined on Agora null.
 * Dirty, we know, but it's the only way to make it work!
 *
 * @author Wolfgang De Meuter (Programming Technology Lab).
 * Last change:  E    16 Nov 97    1:46 am
 */
@Frame("Public")
public class JV_Nil {

    public static JV_Nil instance = new JV_Nil();

    private JV_Nil() {
    }

    /**
     * Overrides the same method in 'java.lang.Object'. If its argument is of type
     * JV_Nil, true is returned because there is only one Null object in the
     * entire Agora system.
     *
     * @param arg The argument of the equality can be of any type.
     * @return Returns whether the argument is also the 'null' object.
     */
    @Operator("equals")
    public boolean equals(Object arg) {
        return arg instanceof JV_Nil;
    }

    @Override
    public String toString() {
        return "Nil";
    }
}
