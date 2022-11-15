package agora.errors;

import agora.grammar.Expression;

// The majority of this dirty code must be rewritten. It was my first experiment
// with the Java AWT when porting the C++ code of Agora to Java.
// A programming environment must be written!!!!!!!!!!!!!!!!

/**
 * This abstract class is the root of the Agora error hierachy.
 *
 * @author Wolfgang De Meuter (Programming Technology Lab).
 * Last change:  E    16 Nov 97    1:46 am
 */

public abstract class AgoraError extends RuntimeException {
    /**
     * This variable is the (possible) code tree where the error occured.
     */
    protected Expression code;

    /**
     * Creates a new error with empty code tree.
     */
    public AgoraError() {
        super();
        this.code = null;
    }

    public AgoraError(String message) {
        super(message);
        this.code = null;
    }

    public AgoraError(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * If an error is thrown, and the evaluator can figure out in which piece
     * of Agora code it occured, it can tell this code to the error and rethrow
     * the error.
     *
     * @param code The expression that was last evaluated	before the error occured.
     */
    public void setCode(Expression code) {
        if (this.code == null) this.code = code;
    }

    public Expression getCode() {
        return code;
    }
}
