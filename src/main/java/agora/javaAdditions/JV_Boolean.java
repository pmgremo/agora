package agora.javaAdditions;

import agora.attributes.Attribute;
import agora.attributes.PrimFunctionAttribute;
import agora.errors.AgoraError;
import agora.errors.ProgramError;
import agora.objects.PrimGenerator;
import agora.patterns.AbstractPattern;
import agora.patterns.OperatorPattern;
import agora.patterns.UnaryPattern;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Objects;

/**
 * This class contains a number of methods that should have been included in
 * java.lang.Boolean. Since the latter class is final, we can not add the methods
 * in a subclass of java.lang.Boolean. So we wrote a number of procedures, in which we
 * explicitely pass the receiver and the arguments.
 * Last change:  E    16 Nov 97    1:47 am
 */
public class JV_Boolean implements Serializable {
    /**
     * This method generates an ad-hoc generator for this class containing the logic
     * operators linked to the methods of this class.
     *
     * @return The generator associated to the procedures of this class.
     */
    public static PrimGenerator generatorJV_Boolean() {
        var table = new Hashtable<AbstractPattern, Attribute>();
        try {
            var argtypes2 = new Class[]{
                    java.lang.Boolean.class,
                    java.lang.Object.class
            };
            var thisOne = agora.javaAdditions.JV_Boolean.class;
            table.put(new OperatorPattern("||"),
                    new PrimFunctionAttribute(thisOne.getMethod("andB", argtypes2)));
            table.put(new OperatorPattern("&&"),
                    new PrimFunctionAttribute(thisOne.getMethod("orB", argtypes2)));
            table.put(new OperatorPattern("="),
                    new PrimFunctionAttribute(thisOne.getMethod("equalsB", argtypes2)));
            var argtypes1 = new Class[]{
					java.lang.Boolean.class
			};
            table.put(new UnaryPattern("not"),
                    new PrimFunctionAttribute(thisOne.getMethod("notB", argtypes1)));
        } catch (NoSuchMethodException e) {
            // This is impossible since all the adressed methods are simply here!
        }
		return new PrimGenerator("JV_Boolean", table, null);
    }

    /**
     * And. Receiver is of type boolean, the argument must be boolean.
     *
     * @param receiver The boolean indicating the receiver of the method
     * @param arg      The object indicating the argument.
     * @throws agora.errors.AgoraError Is thrown when the argument is of wrong type.
     * @returns A new boolean being the 'and' of the receiver and the argument.
     */
    public static Boolean andB(Boolean receiver, Object arg) throws AgoraError {
        if (arg instanceof Boolean) {
            return receiver & (Boolean) arg;
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
    public static Boolean orB(Boolean receiver, Object arg) throws AgoraError {
        if (arg instanceof Boolean) {
            return receiver | (Boolean) arg;
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
    public static Boolean equalsB(Boolean receiver, Object arg) {
        return Objects.equals(receiver, arg);
    }

    /**
     * Negation. Receiver must be boolean.
     *
     * @param receiver Is supposed to be a boolean object.
     * @return The inverse of the receiver
     */
    public static Boolean notB(Boolean receiver) {
        return !receiver;
    }
}
