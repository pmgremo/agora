package agora.awt;

import agora.ErrorDialog;
import agora.errors.AgoraError;
import agora.errors.ProgramError;
import agora.grammar.Expression;
import agora.grammar.Parser;
import agora.grammar.Scanner;
import agora.reflection.Up;
import agora.tools.AgoraGlobals;
import agora.tools.SingleRoot;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static java.awt.Cursor.*;
import static java.awt.FileDialog.*;

public class Agorique implements Serializable {

    static TextArea textArea;

    static protected Frame window;

    private static final int xval = 15; // Text area in which we type Agora code
    private static final int yval = 75; // Text area in which we type Agora code

    public static void main(String... argv) {
        window = new Frame("Agorique");
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
        window.setLayout(l);
        window.add("Center", textArea);
        window.add("South", p);
        textArea.append("This is the 'agora' of Agora98!");
        textArea.append("\n\nVersion of Sunday 16 November 1997");
        textArea.append("\n\nWritten by Wolfgang De Meuter");
        textArea.append("\nProgramming Technology Lab (c) - Vrije Universiteit Brussel");
        textArea.append("\nhttp://progwww.vub.ac.be/");
        textArea.append("\n\nIf you find bugs, PLEASE, mail them to wdmeuter@vub.ac.be");
        textArea.append("\n\n\nType in an expression, select it and press the eval button!");
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                window.dispose();
            }
        });
        b1.addActionListener(e -> {
            // FROM HERE TO ....
            String input;
            try   // THIS NORMALLY WORKS!!!!!!!!!!!!!!!!!!!!!!!!!
            {
                input = textArea.getSelectedText();
            } catch (StringIndexOutOfBoundsException exc) {
                // THIS WORKS IN THE APPLETVIEWER!!!!!!!!!!!!!!!
                var str = textArea.getText();
                var strbuff = new StringBuilder(str);
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
            Expression selectedExpression = null;
            try {
                selectedExpression = new Parser(new Scanner(new AwtIo(input, textArea))).parseExpression();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            try {
                if (selectedExpression != null)
                    selectedExpression.defaultEval();
                else
                    throw new ProgramError("Parse Error");
            } catch (AgoraError error) {
                ErrorDialog.setUpErrorDialog(error.getMessage(), error.getCode(), null);
            }
        });
        b2.addActionListener(e -> {
            var fd = new FileDialog(window, "DUMP IMAGE", SAVE);
            fd.setVisible(true);
            var fileName = fd.getFile();
			if (fileName != null) //User did not click 'Cancel'
			{
				try {
                    var oldCursor = window.getCursor();
					window.setCursor(new Cursor(WAIT_CURSOR));
                    var fos = new FileOutputStream(fileName);
                    var gos = new GZIPOutputStream(fos);
                    var os = new ObjectOutputStream(gos);
					os.writeObject(new SingleRoot(AgoraGlobals.glob, Up.glob));
					os.flush();
					os.close();
					window.setCursor(oldCursor);
				} catch (IOException error) {
                    ErrorDialog.setUpErrorDialog("Save Image ActionListener", null, null);
                }
			}
		});
        b3.addActionListener(e -> {
            var fd = new FileDialog(window, "READ IMAGE", LOAD);
			fd.setVisible(true);
            var fileName = fd.getFile();
			if (fileName != null) //User did not click 'Cancel'
			{
				try {
                    var oldCursor = window.getCursor();
					window.setCursor(new Cursor(WAIT_CURSOR));
                    var fis = new FileInputStream(fileName);
                    var gis = new GZIPInputStream(fis);
                    var s = new ObjectInputStream(gis);
                    var newRoot = (SingleRoot) s.readObject();
					AgoraGlobals.glob = newRoot.globalStructures;
					Up.glob = newRoot.reflectionWrappers;
					s.close();
					window.setCursor(oldCursor);
					AgoraGlobals.glob.agoraWindow = window;
				} catch (IOException error) {
                    ErrorDialog.setUpErrorDialog("Load Image ActionListener", null, null);
                } catch (ClassNotFoundException error2) {
                    ErrorDialog.setUpErrorDialog("Image of Wrong Version", null, null);
                }
			}
		});
        try {
            AgoraGlobals.glob = new AgoraGlobals(null, window);
            window.setVisible(true);
        } catch (AgoraError ex) {
            ErrorDialog.setUpErrorDialog(ex.getMessage(), ex.getCode(), null);
        } catch (IllegalArgumentException e) {
            System.err.print(e.getMessage());
        }
    }

}
