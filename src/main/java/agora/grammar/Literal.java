package agora.grammar;

import agora.errors.AgoraError;
import agora.objects.AgoraObject;
import agora.runtime.Context;
import agora.tools.AgoraGlobals;

import java.util.Objects;

/**
 * This class represents the abstract class for Agora literals. Currently, this class
 * contains no methods.
 *
 * @author Wolfgang De Meuter
 * Last change:  E    16 Nov 97    2:17 pm
 */
abstract public class Literal<T> extends Expression {
    private final T value;

    protected Literal(T value) {
        this.value = value;
    }

    public T value() {
        return value;
    }

    /**
     * Evaluates the node to an Agora object. This is an up-ped version of
     * an instance of the T class.
     *
     * @param context The evaluation context.
     * @return The Agora object represented by the literal.
     * @throws agora.errors.AgoraError When something goes wrong.
     */
    public AgoraObject eval(Context context) throws AgoraError {
        return AgoraGlobals.glob.up.up(value());
    }

    /**
     * Unparses the node to a string. The integer is the number of blanks preceding the string.
     *
     * @param hor The number of spaces leading the unparsed expression.
     * @return The string representation of this floating point literal.
     */
    public String unparse(int hor) {
        return " ".repeat(hor) + value();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Literal<?> literal)) return false;
        return Objects.equals(value, literal.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "value=" + value +
                '}';
    }
}

