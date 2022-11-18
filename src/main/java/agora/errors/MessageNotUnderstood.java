package agora.errors;

import agora.objects.AgoraObject;
import agora.patterns.Pattern;
import agora.patterns.Reifier;

/**
 * This is the most occuring Agora error to indicate that a 'send'
 * has been done with an undefined pattern.
 *
 * @author Wolfgang De Meuter (Programming Technology Lab)
 * Last change:  E    16 Nov 97    1:36 am
 */
public class MessageNotUnderstood extends AgoraError {
    /**
     * The message that was sent.
     */
    protected Pattern msg;
    /**
     * The object to which the message was sent.
     */
    protected AgoraObject receiver;

    /**
     * Create a new message not understood error with the given pattern
     * as the message (which was not understood) and the given object
     * as the receiver in which it was not understood.
     *
     * @param msg      The message which was not understood by the object that threw this error.
     * @param receiver If this is not null, the error reporting facility will allow the
     *                 receiver to be inspected. If it is null, no 'Inspect Receiver' button will be
     *                 visible in the dialog box.
     */
    public MessageNotUnderstood(Pattern msg, AgoraObject receiver) {
        super();
        this.msg = msg;
        this.receiver = receiver;
    }

    /**
     * To access the pattern that was not understood.
     *
     * @return The pattern determining the message that was not understood.
     */
    public Pattern getPattern() {
        return this.msg;
    }

    /**
     * To access the object that did not understand the message.
     *
     * @return The receiver that did not understand the message. If this is null,
     * it probably concerns a receiverless message.
     */
    public AgoraObject getReceiver() {
        return this.receiver;
    }

    @Override
    public String getMessage() {
        var x = "";
        if (msg instanceof Reifier) x = "Reifier ";
        return x + "Message Not Understood: \n\n" + msg.toString();
    }
}
