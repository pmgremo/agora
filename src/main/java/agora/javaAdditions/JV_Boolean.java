package agora.javaAdditions;

import agora.errors.AgoraError;
import agora.errors.ProgramError;
import agora.reflection.Operator;
import agora.reflection.Unary;

import java.util.Objects;

/**
 * This class contains a number of methods that should have been included in
 * java.lang.Boolean. Since the latter class is final, we can not add the methods
 * in a subclass of java.lang.Boolean. So we wrote a number of procedures, in which we
 * explicitely pass the receiver and the arguments.
 * Last change:  E    16 Nov 97    1:47 am
 */
public class JV_Boolean {
    /**
     * And. Receiver is of type boolean, the argument must be boolean.
     *
     * @param receiver The boolean indicating the receiver of the method
     * @param arg      The object indicating the argument.
     * @throws agora.errors.AgoraError Is thrown when the argument is of wrong type.
     * @returns A new boolean being the 'and' of the receiver and the argument.
     */
    @Operator("&&")
    public static Boolean andB(Boolean receiver, Object arg) throws AgoraError {
        if (arg instanceof Boolean b) {
            return receiver & b;
        } else {
            throw new ProgramError("Illegal Argument for &&");
        }
    }

    /**
     * Or. Receiver is of type boolean, the argument must be boolean.
     *
     * @param receiver The boolean indicating the receiver of the method
     * @param arg      The object indicating the argument.
     * @throws agora.errors.AgoraError Is thrown when the argument is of wrong type.
     * @returns A new boolean being the 'or' of the receiver and the argument.
     */
    @Operator("||")
    public static Boolean orB(Boolean receiver, Object arg) throws AgoraError {
        if (arg instanceof Boolean b) {
            return receiver | b;
        } else {
            throw new ProgramError("Illegal Argument for ||");
        }
    }

    /**
     * Comparision =.
     * The receiver must be boolean and the argument can be anything.
     *
     * @param receiver The boolean indicating the receiver of the method
     * @param arg      The object indicating the argument.
     * @returns A new boolean being the comparision of the receiver and the argument.
     */
    @Operator("=")
    public static Boolean equalsB(Boolean receiver, Object arg) {
        return Objects.equals(receiver, arg);
    }

    /**
     * Negation. Receiver must be boolean.
     *
     * @param receiver Is supposed to be a boolean object.
     * @return The inverse of the receiver
     */
    @Unary("not")
    public static Boolean notB(Boolean receiver) {
        return !receiver;
    }
}
