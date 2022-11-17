package agora.javaAdditions;

import agora.reflection.Operator;
import agora.reflection.Unary;

/**
 * An Agora float will be programmed as an up-ed Java float and all Agora
 * messages on floats will be forwarded to the underlying Java messages.
 * However, java.lang.Float does not contain messages like +, and it is
 * forbidden to make a subclass of java.lang.Float to implement +.
 * That's why we implement a number of operators as procedures in
 * this class. When a message is sent to an Agora float, and it is
 * in the java.lang.Float class, it will be forwarded to that method.
 * Otherwise, it will be invoked here and its receiver will be passed
 * as a procedure parameter. Pretty dirty, but it's the only way to make
 * it work.
 * Last change:  E    16 Nov 97    1:47 am
 */
public class JV_Float {
    /**
     * Addition. Receiver is of type float, the argument can be integer or float.
     *
     * @param receiver The float indicating the receiver of the method
     * @param arg      The object indicating the argument.
     * @return A new float being the '+' of the receiver and the argument.
     * @throws agora.errors.AgoraError Is thrown when the argument is of wrong type.
     */
    @Operator("+")
    public static Float plus(Float receiver, Number arg) {
        return receiver + arg.floatValue();
    }

    /**
     * Subtraction.
     * The receiver is a float. The argument can be an integer or a float.
     *
     * @param receiver The float indicating the receiver of the method
     * @param arg      The object indicating the argument.
     * @return A new float being the '-' of the receiver and the argument.
     * @throws agora.errors.AgoraError Is thrown when the argument is of wrong type.
     */
    @Operator("-")
    public static Float min(Float receiver, Number arg) {
        return receiver - arg.floatValue();
    }

    /**
     * Multiplication.
     * The receiver is a float, the argument can be integer or float.
     *
     * @param receiver The float indicating the receiver of the method
     * @param arg      The object indicating the argument.
     * @return A new float being the '*' of the receiver and the argument.
     * @throws agora.errors.AgoraError Is thrown when the argument is of wrong type.
     */
    @Operator("*")
    public static Float multiply(Float receiver, Number arg) {
        return receiver * arg.floatValue();
    }

    /**
     * Division.
     * The receiver is a float, the argument can be an integer or a float.
     *
     * @param receiver The float indicating the receiver of the method
     * @param arg      The object indicating the argument.
     * @return A new float being the '/' of the receiver and the argument.
     * @throws agora.errors.AgoraError Is thrown when the argument is of wrong type.
     */
    @Operator("/")
    public static Float divide(Float receiver, Number arg) {
        return receiver / arg.floatValue();
    }

    /**
     * Power.
     * The receiver is a float. The exponent can be an integer or a float.
     *
     * @param receiver The float indicating the receiver of the method
     * @param arg      The object indicating the argument.
     * @return A new float being the '^' of the receiver and the argument.
     * @throws agora.errors.AgoraError Is thrown when the argument is of wrong type.
     */
    @Operator("^")
    public static Float power(Float receiver, Number arg) {
        return (float) Math.pow(receiver, arg.doubleValue());
    }

    /**
     * Comparison =.
     * The receiver must be a float and the argument can be anything.
     *
     * @param receiver The float indicating the receiver of the method
     * @param arg      The object indicating the argument.
     * @return A new boolean being the comparison of the receiver and the argument.
     * @throws agora.errors.AgoraError Is thrown when the argument is of wrong type.
     */
    @Operator("=")
    public static Boolean equalsF(Float receiver, Number arg) {
        return receiver == arg.floatValue();
    }

    /**
     * Smaller than test.
     * The receiver must be a float, the argument an integer or a float.
     *
     * @param receiver The float indicating the receiver of the method
     * @param arg      The object indicating the argument.
     * @return A new boolean being the comparison of the receiver and the argument.
     * @throws agora.errors.AgoraError Is thrown when the argument is of wrong type.
     */
    @Operator("<")
    public static Boolean smF(Float receiver, Number arg) {
        return receiver.compareTo(arg.floatValue()) < 0;
    }

    /**
     * Greater than test.
     * The receiver must be a float, the argument integer or float.
     *
     * @param receiver The float indicating the receiver of the method
     * @param arg      The object indicating the argument.
     * @return A new boolean being the comparison of the receiver and the argument.
     * @throws agora.errors.AgoraError Is thrown when the argument is of wrong type.
     */
    @Operator(">")
    public static Boolean gtF(Float receiver, Number arg) {
        return receiver.compareTo(arg.floatValue()) > 0;

    }

    /**
     * Smaller than or equal.
     * Receiver must be a float, the argument integer or float.
     *
     * @param receiver The float indicating the receiver of the method
     * @param arg      The object indicating the argument.
     * @return A new boolean being the comparison of the receiver and the argument.
     * @throws agora.errors.AgoraError Is thrown when the argument is of wrong type.
     */
    @Operator("<=")
    public static Boolean smeF(Float receiver, Number arg) {
        return receiver.compareTo(arg.floatValue()) <= 0;
    }

    /**
     * Greater than or equal.
     * The receiver must be a float, the argument integer or float.
     *
     * @param receiver The float indicating the receiver of the method
     * @param arg      The object indicating the argument.
     * @return A new boolean being the comparison of the receiver and the argument.
     * @throws agora.errors.AgoraError Is thrown when the argument is of wrong type.
     */
    @Operator(">=")
    public static Boolean gteF(Float receiver, Number arg) {
        return receiver.compareTo(arg.floatValue()) >= 0;
    }

    /**
     * Absolute value. Receiver is float.
     *
     * @param receiver The receiver of the abs message
     * @return The float indicating the absolute value of the receiver.
     * @throws agora.errors.AgoraError Is never thrown in this case.
     */
    @Unary("abs")
    public static Float abs(Float receiver) {
        return Math.abs(receiver);
    }

    /**
     * Square Root. Receiver is float.
     *
     * @param receiver The receiver of the sqrt message
     * @return The float indicating the square root of the receiver.
     * @throws agora.errors.AgoraError Is never thrown in this case.
     */
    @Unary
    public static Float sqrt(Float receiver) {
        return (float) Math.sqrt(receiver);
    }

    /**
     * Square. Receiver is float.
     *
     * @param receiver The receiver of the sqr message
     * @return The float indicating the square of the receiver.
     * @throws agora.errors.AgoraError Is never thrown in this case.
     */
    @Unary
    public static Float sqr(Float receiver) {
        return receiver * receiver;
    }
}
