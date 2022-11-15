package agora.grammar;

import agora.errors.AgoraError;
import agora.objects.AgoraObject;
import agora.runtime.Context;
import agora.awt.AwtIo;
import agora.tools.AgoraGlobals;


/**
 * Grammar component representing integer literal parse tree nodes.
 *
 * @author Wolfgang De Meuter
 * Last change:  E    16 Nov 97    2:18 pm
 */
public class IntegerLiteral extends Literal {
    /**
     * Internal value of the literal as denoted in the Agora program.
     */
    protected int theInt;

    /**
     * Constructs a new integer literal parse tree node.
     *
     * @param value The value represented by this literal.
     */
    public IntegerLiteral(int value) {
        this.theInt = value;
    }

    /**
     * Unparse the component to a string. The integer parameter denoted the number of leading spaces.
     *
     * @param hor The nunber of spaces leading the string representation of the literal.
     * @return The String representation of the literal.
     */
    public String unparse(int hor) {
        return AwtIo.makeSpaces(hor) + theInt;
    }

    /**
     * Evaluates the integer literal in the given context. This creates an up-ped instance
     * of the java.lang.Integer class.
     *
     * @param context The context in which the literal must be evaluated. Of course, this
     *                is not used for evaluating literals.
     * @return The Agora Object represented by this literal.
     * @throws agora.errors.AgoraError When something goes wrong during evaluation.
     */
    public AgoraObject eval(Context context) throws AgoraError {
        return AgoraGlobals.glob.up.up(this.theInt);
    }

    /**
     * Returns the internal value of the integer literal.
     *
     * @return The actual integer represented by this literal.
     */
    public int getInt() {
        return theInt;
    }
}
