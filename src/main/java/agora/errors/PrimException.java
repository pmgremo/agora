package agora.errors;

/**
 * This class represents a wrapper for a primitive exception. That is, when
 * underlying Java generates an exception, this class represents the Agora
 * variant for it. The original exception is stored inside the wrapper.
 *
 * @author Wolfgang De Meuter (Programming Technology Lab).
 * Last change:  E    16 Nov 97    1:36 am
 */
public class PrimException extends AgoraError {

    /**
     * Create a new primitive exception with the given Java exception and the given
     * name of the Java method in which the exception occured.
     *
     * @param exception  The primitive exception that was thrown.
     * @param javaMethod The name of the Java method that threw the primitive exception.
     */
    public PrimException(Throwable exception, String javaMethod) {
        super("Native Java Exception: " + exception.toString() + "\nIn the method :" + javaMethod, exception);
    }
}
