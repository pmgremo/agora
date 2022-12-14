package agora.grammar;

import java.util.Objects;

/**
 * Abstract class representing Agora message expressions. This class normally has
 * two subclasses representing ordinary messages and reifier messages.
 * Last change:  E    16 Nov 97    2:17 pm
 */
public abstract class Message extends Expression {
    /**
     * Expression representing the receiver of the message.
     */
    protected Expression receiver;
    /**
     * Pattern containing the message pattern and actual arguments.
     */
    protected Pattern pattern;

    /**
     * Create a new message expression given a receiver and a pattern.
     *
     * @param receiver The expression denoting the receiver of the message.
     * @param pattern  The pattern contains the message name and the actual arguments.
     */
    public Message(Expression receiver, Pattern pattern) {
        this.receiver = receiver;
        this.pattern = pattern;
    }

    /**
     * Read receiver expression of the message.
     *
     * @return The receiver expression of this message expression.
     */
    public Expression receiver() {
        return this.receiver;
    }

    /**
     * Read pattern of the message.
     *
     * @return The pattern indicating the message name and actuals, associated to this message
     * expression
     */
    public Pattern pattern() {
        return pattern;
    }

    /**
     * Unparsing the message expression.
     * The hor parameter indicates the number of spaces that have to precede the unparsed
     * message expression.
     *
     * @param hor The number of spaces that must lead the string representation of the message
     *            expression.
     * @return A string representation of this message expression.
     */
    public String unparse(int hor) {
        return " ".repeat(hor) + receiver.unparse(hor) + " " + pattern.unparse(0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message message)) return false;
        return Objects.equals(receiver, message.receiver) && Objects.equals(pattern, message.pattern);
    }

    @Override
    public int hashCode() {
        return Objects.hash(receiver, pattern);
    }

    @Override
    public String toString() {
        return unparse(0);
    }
}
