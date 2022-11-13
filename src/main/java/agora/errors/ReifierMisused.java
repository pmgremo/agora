package agora.errors;

/**
 * This error is thrown whenever the programmer misuses a reifier, e.g. when she
 * sends SUPER to a number or something like that.
 *
 * @author Wolfgang De Meuter(Programming Technology Lab)
 * Last change:  E    16 Nov 97    1:36 am
 */
public class ReifierMisused extends AgoraError {

    /**
     * Creates a new ReifierMisused error with the given error message.
     *
     * @param message The error message to be displayed when reporting this error.
     */
    public ReifierMisused(String message) {
        super("Misused Reifier: \n\n" + message + "\n\n");
    }
}

