package agora.grammar;

import agora.errors.AgoraError;
import agora.objects.AgoraObject;
import agora.runtime.Context;
import agora.awt.AwtIo;
import agora.tools.AgoraGlobals;

/**
 * The parse tree node class representing character literals.
 *
 * @author Wolfgang De Meuter
 * Last change:  E    16 Nov 97    1:58 pm
 */
public class CharLiteral extends Literal {
    /**
     * The internal character value of the parse tree node.
     */
    protected char theChar;

    /**
     * Creates a new node with c as character.
     *
     * @param c The Java character corresponding to the character literal.
     */
    public CharLiteral(char c) {
        this.theChar = c;
    }

    /**
     * Unparses the node to a string. The integer denotes the number of leading blanks.
     *
     * @param hor The number of spaces leading the unparsed representation.
     * @return The literal as string.
     */
    public String unparse(int hor) {
        var msg = AwtIo.makeSpaces(hor);
        if (this.theChar == '\n') {
            msg = msg + "'eoln'";
        } else {
            msg = msg + "'";
            msg = msg + this.theChar;
            msg = msg + "'";
        }
        return msg;
    }

    /**
     * Evaluates the character node to an Agora object. This is an up-ped version of
     * an instance of the java.lang.Character class.
     *
     * @param context The evaluation context.
     * @return The Agora object represented by the literal.
     * @throws agora.errors.AgoraError When something goes wrong.
     */
    public AgoraObject eval(Context context) throws AgoraError {
        return AgoraGlobals.glob.up.up(this.theChar);
    }

    /**
     * Returns the character of this node.
     *
     * @return The actual Java character represented by the literal
     */
    public char getChar() {
        return theChar;
    }
}    


