package agora.errors;

/**
 * When the Agora programmer sends the HALT reifier to a string,
 * an exception is generated that will terminate the execution of
 * the program. The string to which the HALT reifier is sent
 * will be displayed in a dialogbox.
 *
 * @author Wolfgang De Meuter (Programming Technology Lab)
 * Last change:  E    16 Nov 97    1:36 am
 */
public class AgoraHalt extends AgoraError {
    /**
     * The string to which HALT was sent.
     */
    protected String theString;

    /**
     * To create a new AgoraHalt error with the given string.
     *
     * @param haltString This is the string to which HALT was sent. It must be filled in
     *                   by the HALT reifier upon creation of the AgoraHalt exception. It will be displayed
     *                   upon termination of the Agora program.
     */
    public AgoraHalt(String haltString) {
        super(haltString);
        theString = haltString;
    }

    /**
     * To access the string to which HALT was sent.
     *
     * @return The halt message as specified by the HALT that gave rise to this exception.
     */
    public String getHaltMessage() {
        return theString;
    }
}


