package agora.errors;

/**
 * This class represents general program errors that occurred during the
 * execution of an Agora program. This can be anything except for the
 * special purpose errors that are in this class hierarchy.
 * Last change:  E    16 Nov 97    1:36 am
 */
public class ProgramError extends AgoraError {
    /**
     * Creates a new program error with the given message.
     *
     * @param msg The error message to be displayed when reporting this error.
     */
    public ProgramError(String msg) {
        super(msg);
    }
}

