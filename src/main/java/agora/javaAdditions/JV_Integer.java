package agora.javaAdditions;

import agora.errors.AgoraError;
import agora.errors.ProgramError;
import agora.reflection.Operator;
import agora.reflection.Unary;

import static java.lang.Math.pow;

/**
 * An Agora integer will be programmed as an 'up'ed Java integer and all Agora
 * messages on integers will be forwarded to the underlying Java messages.
 * However, java.lang.Integer does not contain messages like +, and it is
 * forbidden to make a subclass of java.lang.Integer to implement +.
 * That's why we implement a number of operators as procedures in
 * this class. When a message is sent to an Agora integer, and it is
 * in the java.lang.Integer class, it will be forwarded to that method.
 * Otherwise, it will be invoked here and its receiver will be passed
 * as a procedure parameter. Pretty dirty, but it's the only way to make
 * it work.
 *
 * @author Wolfgang De Meuter (Programming Technology Lab)
 * Last change:  E    16 Nov 97    1:47 am
 */
public class JV_Integer {
    /**
     * Addition. Receiver is of type integer, the argument can be integer or float.
     *
     * @param receiver The integer indicating the receiver of the method
     * @param arg      The object indicating the argument.
     * @throws agora.errors.AgoraError Is thrown when the argument is of wrong type.
     * @return A new float or int being the sum of the receiver and the argument.
     */
    @Operator("+")
    public static Integer plus(Integer receiver, Number arg) throws AgoraError {
        return receiver + arg.intValue();
    }

    /**
     * Substraction.
     * The receiver is an integer. The argument can be an integer or a float.
     *
     * @param receiver The integer indicating the receiver of the method
     * @param arg      The object indicating the argument.
     * @throws agora.errors.AgoraError Is thrown when the argument is of wrong type.
     * @return A new float or int being the substraction of the receiver and the argument.
     */
    @Operator("-")
    public static Object min(Integer receiver, Object arg) throws AgoraError {
        if (arg instanceof Integer i) {
            return receiver - i;
        } else if (arg instanceof Float f) {
            return receiver - f;
        } else {
            throw new ProgramError("Illegal Argument for -");
        }
    }

    /**
     * Multiplication.
     * The receiver is an integer, the argument can be integer or float.
     *
     * @param receiver The integer indicating the receiver of the method
     * @param arg      The object indicating the argument.
     * @throws agora.errors.AgoraError Is thrown when the argument is of wrong type.
     * @return A new float or int being the multiplication of the receiver and the argument.
     */
    @Operator("*")
    public static Object mult(Integer receiver, Object arg) throws AgoraError {
        if (arg instanceof Integer i) {
            return receiver * i;
        } else if (arg instanceof Float f) {
            return receiver * f;
        } else {
            throw new ProgramError("Illegal Argument for *");
        }
    }

    /**
     * Division.
     * The receiver is an integer, the argument can be an integer or a float.
     *
     * @param receiver The integer indicating the receiver of the method
     * @param arg      The object indicating the argument.
     * @throws agora.errors.AgoraError Is thrown when the argument is of wrong type.
     * @return A new float being the floating division of the receiver and the argument.
     */
    @Operator("/")
    public static Object divide(Integer receiver, Object arg) throws AgoraError {
        if (arg instanceof Integer i) {
            return (float) (receiver / i);
        } else if (arg instanceof Float f) {
            return receiver / f;
        } else {
            throw new ProgramError("Illegal Argument for /");
        }
    }

    /**
     * Integer division.
     * Both the receiver and the arguments must be integers.
     *
     * @param receiver The integer indicating the receiver of the method
     * @param arg      The object indicating the argument.
     * @throws agora.errors.AgoraError Is thrown when the argument is of wrong type.
     * @return A new integer being the integer division of the receiver and the argument.
     */
    @Operator("#")
    public static Object div(Integer receiver, Object arg) throws AgoraError {
        if (arg instanceof Integer i) {
            return receiver / i;
        } else {
            throw new ProgramError("Illegal Argument for #");
        }
    }

    /**
     * Remainder.
     * Both the receiver and the arguments must be integers.
     *
     * @param receiver The integer indicating the receiver of the method
     * @param arg      The object indicating the argument.
     * @throws agora.errors.AgoraError Is thrown when the argument is of wrong type.
     * @return A new integer being the remainder of dividing the receiver and the argument.
     */
    @Operator("%")
    public static Object mod(Integer receiver, Object arg) throws AgoraError {
        if (arg instanceof Integer i) {
            return receiver % i;
        } else {
            throw new ProgramError("Illegal Argument for %");
        }
    }

    /**
     * Power.
     * The receiver is an integer. The exponent can be an integer or a float.
     *
     * @param receiver The int indicating the receiver of the method
     * @param arg      The object indicating the argument.
     * @throws agora.errors.AgoraError Is thrown when the argument is of wrong type.
     * @return A new float or integer being the power of the receiver and the argument.
     */
    @Operator("^")
    public static Object power(Integer receiver, Object arg) throws AgoraError {
        if (arg instanceof Integer i) {
            return (int) pow(receiver, i);
        } else if (arg instanceof Float f) {
            return (float) pow(receiver, f);
        } else {
            throw new ProgramError("Illegal Argument for ^");
        }
    }

    /**
     * Comparision =.
     * The receiver must be integer and the argument can be anything.
     *
     * @param receiver The integer indicating the receiver of the method
     * @param arg      The object indicating the argument.
     * @return A new boolean being the comparision of the receiver and the argument.
     */
    @Operator("=")
    public static Object equalsI(Integer receiver, Object arg) {
        if (arg instanceof Integer i) {
            return receiver.equals(i);
        } else
            return Boolean.FALSE;
    }

    /**
     * Smaller than test.
     * The receiver must be an integer, the argument an integer or a float.
     *
     * @param receiver The integer indicating the receiver of the method
     * @param arg      The object indicating the argument.
     * @throws agora.errors.AgoraError Is thrown when the argument is of wrong type.
     * @return A new boolean being the comparision of the receiver and the argument.
     */
    @Operator("<")
    public static Object smI(Integer receiver, Object arg) throws AgoraError {
        if (arg instanceof Integer i) {
            return receiver < i;
        } else if (arg instanceof Float f) {
            return receiver < f;
        } else {
            throw new ProgramError("Illegal Argument for <");
        }
    }

    /**
     * Greater than test.
     * The receiver must be integer, the argument integer or float.
     *
     * @param receiver The integer indicating the receiver of the method
     * @param arg      The object indicating the argument.
     * @throws agora.errors.AgoraError Is thrown when the argument is of wrong type.
     * @return A new boolean being the comparision of the receiver and the argument.
     */
    @Operator(">")
    public static Object gtI(Integer receiver, Object arg) throws AgoraError {
        if (arg instanceof Integer i) {
            return receiver > i;
        } else if (arg instanceof Float f) {
            return receiver > f;
        } else {
            throw new ProgramError("Illegal Argument for >");
        }
    }

    /**
     * Smaller than or equal.
     * Receiver must be integer, the argument integer or float.
     *
     * @param receiver The integer indicating the receiver of the method
     * @param arg      The object indicating the argument.
     * @throws agora.errors.AgoraError Is thrown when the argument is of wrong type.
     * @return A new boolean being the comparision of the receiver and the argument.
     */
    @Operator("<=")
    public static Object smeI(Integer receiver, Object arg) throws AgoraError {
        if (arg instanceof Integer i) {
            return receiver <= i;
        } else if (arg instanceof Float f) {
            return receiver <= f;
        } else {
            throw new ProgramError("Illegal Argument for <=");
        }
    }

    /**
     * Greater than or equal.
     * The receiver must be integer, the argument integer or float.
     *
     * @param receiver The integer indicating the receiver of the method
     * @param arg      The object indicating the argument.
     * @throws agora.errors.AgoraError Is thrown when the argument is of wrong type.
     * @return A new boolean being the comparision of the receiver and the argument.
     */
    @Operator(">=")
    public static Object gteI(Integer receiver, Object arg) throws AgoraError {
        if (arg instanceof Integer i) {
            return receiver >= i;
        } else if (arg instanceof Float f) {
            return receiver >= f;
        } else {
            throw new ProgramError("Illegal Argument for +");
        }
    }

    /**
     * Bitwise or.
     * Both receiver and argument must be integers.
     *
     * @param receiver The integer indicating the receiver of the method
     * @param arg      The object indicating the argument.
     * @throws agora.errors.AgoraError Is thrown when the argument is of wrong type.
     * @return A new boolean being the comparision of the receiver and the argument.
     */
    @Operator("|")
    public static Object orI(Integer receiver, Object arg) throws AgoraError {
        if (arg instanceof Integer i) {
            return receiver | i;
        } else {
            throw new ProgramError("Illegal Argument for |");
        }
    }

    /**
     * Bitwise and.
     * Both receiver and argument must be integers.
     *
     * @param receiver The integer indicating the receiver of the method
     * @param arg      The object indicating the argument.
     * @throws agora.errors.AgoraError Is thrown when the argument is of wrong type.
     * @return A new boolean being the comparision of the receiver and the argument.
     */
    @Operator("&")
    public static Object andI(Integer receiver, Object arg) throws AgoraError {
        if (arg instanceof Integer i) {
            return receiver & i;
        } else {
            throw new ProgramError("Illegal Argument for &");
        }
    }

    /**
     * Absolute value. Receiver is integer.
     *
     * @param receiver The receiver of the message
     * @return The integer indicating the absolute value of the receiver.
     */
    @Unary("abs")
    public static Object abs(Integer receiver) {
        return Math.abs(receiver);
    }

    /**
     * Increment. Receicer is integer.
     *
     * @param receiver The receiver of the message
     * @return The integer indicating the increment of the receiver.
     */
    @Unary("inc")
    public static Object inc(Integer receiver) {
        return receiver + 1;
    }

    /**
     * Decrement. Receicer is integer.
     *
     * @param receiver The receiver of the message
     * @return The integer indicating the decrement of the receiver.
     */
    @Unary("dev")
    public static Object dec(Integer receiver) {
        return receiver - 1;
    }

    /**
     * Square Root. Receicer is integer.
     *
     * @param receiver The receiver of the message
     * @return The float indicating the square root of the receiver.
     */
    @Unary("sqrt")
    public static Object sqrt(Integer receiver) {
        return (float) Math.sqrt(receiver);
    }

    /**
     * Square. Receiver is integer.
     *
     * @param receiver The receiver of the message
     * @return The integer indicating the square of the receiver.
     */
    @Unary("sqr")
    public static Object sqr(Integer receiver) {
        return receiver * receiver;
    }

    /**
     * Bitwise not. Receiver is integer.
     *
     * @param receiver The receiver of the message
     * @return The integer indicating the bitwise not of the receiver.
     * @throws agora.errors.AgoraError Is never thrown in this case.
     */
    @Unary("not")
    public static Object not(Integer receiver) throws AgoraError {
        return ~receiver;
    }
}
