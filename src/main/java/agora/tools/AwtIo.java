package agora.tools;

import java.awt.*;

/**
 * This class is an abstract layer for all Agora IO that occurs during the execution
 * of an Agora program. If we would implement all the methods in this class
 * as instance methods, we would have to instantiate only one instance
 * of this class because all IO has to be consistent. That's why all the
 * methods in this class are static. This is a bit faster.
 *
 * @author Wolfgang De Meuter (Programming Technology Lab).
 * Last change:  E    15 Nov 97   11:46 pm
 */
public class AwtIo implements Io {
    /**
     * The input string used to initialise AgoraIO.
     */
    protected static String theInput;
    /**
     * The textArea to which all output will occur.
     */
    protected static TextArea theOutput;
    /**
     * Indicates the current character in the input string.
     */
    protected static int count;

    /**
     * The size of an inspector.
     */
    public static int inspectorX = 10;
    public static int inspectorY = 10;

    /**
     * Initialises AgoraIO with a given string and a textarea.
     * (While writing this comment (remember, this used to be C++ code), I don't remember
     * using this method. I guess it is no longer used).
     *
     * @param i The input String
     * @param o The output text area.
     */
    public AwtIo(String i, TextArea o) {
        theInput = i;
        theOutput = o;
        count = 0;
    }

    /**
     * Method to check whether the user has pressed an interuption key (control-C) for example.
     * Currently not implemented. Its intended implementation is to break the evaluation loop by
     * throwing an exception.
     * This comes from the original C++ code and is currenly not used.
     */
    public static void checkEscape() {
    }

    /**
     * Returns a string consisting of spaces whose length is indicated by the integer
     * parameter.	This method is heavily used by the unparser to do indentation properly.
     *
     * @param i Integer value denoting the number of spaces required.
     * @return A string consisting of so many spaces as indicated by the input.
     */
    public static String makeSpaces(int i) {
      return " " + (i == 0 ? "" : makeSpaces(i - 1));
    }

    /**
     * Dumps the given string onto the textarea associated with AgoraIO.
     *
     * @param s The string to be printed.
     */
    @Override
    public void print(String s) {
        theOutput.append(s);
    }

    /**
     * Reads the next character from the input and moves the 'current' one
     * step further.
     *
     * @return The next character from the input.
     */
    @Override
    public char getChar() {
        if (count >= theInput.length())
            return (char) 0;
        else {
            var ret = theInput.charAt(count);
            count++;
            return ret;
        }
    }
}

