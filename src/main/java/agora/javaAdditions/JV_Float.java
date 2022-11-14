package agora.javaAdditions;

import agora.errors.AgoraError;
import agora.errors.ProgramError;
import agora.reflection.Operator;
import agora.reflection.Unary;

import static java.lang.Boolean.FALSE;

/**
 * An Agora float will be programmed as an 'up'ed Java float and all Agora
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
     * @throws agora.errors.AgoraError Is thrown when the argument is of wrong type.
     * @returns A new float being the '+' of the receiver and the argument.
     */
    @Operator("+")
    public static Object plus(Float receiver, Object arg) throws AgoraError {
        if (arg instanceof Integer i) {
            return receiver + i;
        } else if (arg instanceof Float f) {
            return receiver + f;
        } else {
            throw new ProgramError("Illegal Argument for +");
        }
    }

    /**
     * Substraction.
     * The receiver is a float. The argument can be an integer or a float.
     *
     * @param receiver The float indicating the receiver of the method
     * @param arg      The object indicating the argument.
     * @throws agora.errors.AgoraError Is thrown when the argument is of wrong type.
     * @returns A new float being the '-' of the receiver and the argument.
     */
    @Operator("-")
    public static Object min(Float receiver, Object arg) throws AgoraError {
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
     * The receiver is a float, the argument can be integer or float.
     *
     * @param receiver The float indicating the receiver of the method
     * @param arg      The object indicating the argument.
     * @throws agora.errors.AgoraError Is thrown when the argument is of wrong type.
     * @returns A new float being the '*' of the receiver and the argument.
     */
    @Operator("*")
    public static Object mult(Float receiver, Object arg) throws AgoraError {
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
     * The receiver is a float, the argument can be an integer or a float.
     *
     * @param receiver The float indicating the receiver of the method
     * @param arg      The object indicating the argument.
     * @throws agora.errors.AgoraError Is thrown when the argument is of wrong type.
     * @returns A new float being the '/' of the receiver and the argument.
     */
    @Operator("/")
    public static Object divide(Float receiver, Object arg) throws AgoraError {
        if (arg instanceof Integer i) {
            return receiver / i;
        } else if (arg instanceof Float f) {
            return receiver / f;
        } else {
            throw new ProgramError("Illegal Argument for /");
        }
    }

    /**
     * Power.
     * The receiver is a float. The exponent can be an integer or a float.
     *
     * @param receiver The float indicating the receiver of the method
     * @param arg      The object indicating the argument.
     * @throws agora.errors.AgoraError Is thrown when the argument is of wrong type.
     * @returns A new float being the '^' of the receiver and the argument.
     */
    @Operator("^")
    public static Object power(Float receiver, Object arg) throws AgoraError {
        if (arg instanceof Integer i) {
            return (float) (int) Math.pow(receiver, i);
        } else if (arg instanceof Float f) {
            return (float) Math.pow(receiver, f);
        } else {
            throw new ProgramError("Illegal Argument for ^");
        }
    }

    /**
     * Comparision =.
     * The receiver must be float and the argument can be anything.
     *
     * @param receiver The float indicating the receiver of the method
     * @param arg      The object indicating the argument.
     * @throws agora.errors.AgoraError Is thrown when the argument is of wrong type.
     * @returns A new boolean being the comparision of the receiver and the argument.
     */
    @Operator("=")
    public static Object equalsF(Float receiver, Object arg) throws AgoraError {
        if (arg instanceof Number n) {
            return receiver == n.intValue();
        } else
            return FALSE;
    }

    /**
     * Smaller than test.
     * The receiver must be a float, the argument an integer or a float.
     *
     * @param receiver The float indicating the receiver of the method
     * @param arg      The object indicating the argument.
     * @throws agora.errors.AgoraError Is thrown when the argument is of wrong type.
     * @returns A new boolean being the comparision of the receiver and the argument.
     */
    @Operator("<")
    public static Object smF(Float receiver, Object arg) throws AgoraError {
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
     * The receiver must be float, the argument integer or float.
     *
     * @param receiver The float indicating the receiver of the method
     * @param arg      The object indicating the argument.
     * @throws agora.errors.AgoraError Is thrown when the argument is of wrong type.
     * @returns A new boolean being the comparision of the receiver and the argument.
     */
    @Operator(">")
    public static Object gtF(Float receiver, Object arg) throws AgoraError {
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
     * Receiver must be float, the argument integer or float.
     *
     * @param receiver The float indicating the receiver of the method
     * @param arg      The object indicating the argument.
     * @throws agora.errors.AgoraError Is thrown when the argument is of wrong type.
     * @returns A new boolean being the comparision of the receiver and the argument.
     */
    @Operator("<=")
    public static Object smeF(Float receiver, Object arg) throws AgoraError {
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
     * The receiver must be float, the argument integer or float.
     *
     * @param receiver The float indicating the receiver of the method
     * @param arg      The object indicating the argument.
     * @throws agora.errors.AgoraError Is thrown when the argument is of wrong type.
     * @returns A new boolean being the comparision of the receiver and the argument.
     */
    @Operator(">=")
    public static Object gteF(Float receiver, Object arg) throws AgoraError {
        if (arg instanceof Integer i) {
            return receiver >= i;
        } else if (arg instanceof Float f) {
            return receiver >= f;
        } else {
            throw new ProgramError("Illegal Argument for >=");
        }
    }

    /**
     * Absolute value. Receiver is floatr.
     *
     * @param receiver The receiver of the abs message
     * @return The float indicating the absolute value of the receiver.
     * @throws agora.errors.AgoraError Is never thrown in this case.
     */
    @Unary("abs")
    public static Object abs(Float receiver) throws AgoraError {
        return Math.abs(receiver);
    }

    /**
     * Square Root. Receicer is float.
     *
     * @param receiver The receiver of the sqrt message
     * @return The float indicating the square root of the receiver.
     * @throws agora.errors.AgoraError Is never thrown in this case.
     */
    @Unary
    public static Object sqrt(Float receiver) throws AgoraError {
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
    public static Object sqr(Float receiver) throws AgoraError {
        return receiver * receiver;
    }

}
