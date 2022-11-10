package agora;

import agora.errors.AgoraError;
import agora.errors.PrimException;
import agora.errors.ProgramError;
import agora.grammar.Expression;
import agora.grammar.Parser;
import agora.grammar.Scanner;
import agora.reflection.Up;
import agora.tools.AgoraGlobals;
import agora.tools.AgoraIO;
import agora.tools.SingleRoot;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * This is a very small applet being an interface for the Agora
 * evaluator. It only consists of a textarea in which the programmer
 * can type Agora expressions, and an eval Button with which a selected
 * piece of text is parsed and sent to the evaluator.
 * Last change:  E    17 Nov 97   12:58 pm
 */
public class Agorette extends java.applet.Applet {

    static TextArea textArea;

    private static final int xval = 15; // Text area in which we type Agora code
    private static final int yval = 75; // Text area in which we type Agora code

    private Frame window;

    /**
     * Applet initialization. Creates the text area, the button and returns
     * control the appletviewer.
     */
    public void init() {
        window = new Frame();
        var b1 = new Button("Eval");
        var b2 = new Button("Dump Image");
        var b3 = new Button("Read Image");
        var p = new Panel();
        var f = new FlowLayout();
        p.setLayout(f);
        p.add(b1);
        p.add(b2);
        p.add(b3);
        textArea = new TextArea(xval, yval);
        textArea.setEditable(true);
        var l = new BorderLayout();
        this.setLayout(l);
        this.add("Center", textArea);
        this.add("South", p);
        textArea.append("This is the 'agora' of Agora98!");
        textArea.append("\n\nVersion of Sunday 16 November 1997");
        textArea.append("\n\nWritten by Wolfgang De Meuter");
        textArea.append("\nProgramming Technology Lab (c) - Vrije Universiteit Brussel");
        textArea.append("\nhttp://progwww.vub.ac.be/");
        textArea.append("\n\nIf you find bugs, PLEASE, mail them to wdmeuter@vub.ac.be");
        textArea.append("\n\n\nType in an expression, select it and press the eval button!");
        b1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // FROM HERE TO ....
                String input;
                try   // THIS NORMALLY WORKS!!!!!!!!!!!!!!!!!!!!!!!!!
                {
                    input = textArea.getSelectedText();
                } catch (StringIndexOutOfBoundsException exc) {
                    // THIS WORKS IN THE APPLETVIEWER!!!!!!!!!!!!!!!
                    var str = textArea.getText();
                    var strbuff = new StringBuffer(str);
                    var j = 0;
                    for (var i = 0; i < str.length(); i++) {
                        if ((str.charAt(i) == '\n') | (str.charAt(i) == '\r')) {
                            j++;
                            strbuff.insert((i + j), '\r');
                        }
                    }
                    input = strbuff.toString();
                    input = input.substring(textArea.getSelectionStart(), textArea.getSelectionEnd());
                }
                // ... TO HERE
                // Due to a bug in the JDK Java interpreter and appletviewer.
                AgoraIO.init(input, textArea);
                var selectedExpression = (new Parser(new Scanner())).parseExpression();
                try {
                    if (selectedExpression != null)
                        selectedExpression.defaultEval();
                    else
                        throw (new ProgramError("Parse Error"));
                } catch (AgoraError error) {
                    error.signal();
                }
            }
        });
        b2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                var fd = new FileDialog(new Frame(), "DUMP IMAGE", FileDialog.SAVE);
                fd.show();
                var fileName = fd.getFile();
                if (fileName != null) //User didnot click 'Cancel'
                {
                    try {
                        var oldCursor = window.getCursor();
                        window.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                        var fos = new FileOutputStream(fileName);
                        var gos = new GZIPOutputStream(fos);
                        var os = new ObjectOutputStream(gos);
                        os.writeObject(new SingleRoot(AgoraGlobals.glob, Up.glob));
                        os.flush();
                        os.close();
                        window.setCursor(oldCursor);
                    } catch (IOException error) {
                        (new PrimException(error, "Save Image ActionListener")).signal();
                    }
                }
            }
        });
        b3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                var fd = new FileDialog(new Frame(), "READ IMAGE", FileDialog.LOAD);
                fd.show();
                var fileName = fd.getFile();
                if (fileName != null) //User didnot click 'Cancel'
                {
                    try {
                        var oldCursor = window.getCursor();
                        window.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                        var fis = new FileInputStream(fileName);
                        var gis = new GZIPInputStream(fis);
                        var s = new ObjectInputStream(gis);
                        var newRoot = (SingleRoot) s.readObject();
                        AgoraGlobals.glob = newRoot.globalStructures;
                        Up.glob = newRoot.reflectionWrappers;
                        s.close();
                        AgoraGlobals.glob.updateApplet(Agorette.this);
                        AgoraGlobals.glob.agoraWindow = window;
                        window.setCursor(oldCursor);
                    } catch (IOException error) {
                        (new PrimException(error, "Load Image ActionListener")).signal();
                    } catch (ClassNotFoundException error2) {
                        (new ProgramError("Image of Wrong Version")).signal();
                    }
                }
            }
        });
        try {
            AgoraIO.init("", textArea);
            AgoraGlobals.glob = new AgoraGlobals(this, window);
        } catch (AgoraError ex) // All normal agora.errors are trapped and displayed to
        {                  // Agora programmer.
            ex.signal();
        } catch (IllegalArgumentException e)     //This happens when the evaluator calls a native
        {                                   //method with the wrong arguments. Caanot be
            System.out.print(e.getMessage()); // resolved, because if it happens, this is a bug.
        }
    }

}
