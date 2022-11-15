package agora.awt;

import agora.errors.AgoraError;
import agora.errors.MessageNotUnderstood;
import agora.grammar.Expression;
import agora.objects.AgoraObject;
import agora.patterns.UnaryPattern;
import agora.runtime.Client;

import java.awt.*;

import static java.awt.FlowLayout.CENTER;
import static java.awt.TextArea.SCROLLBARS_BOTH;
import static java.awt.TextArea.SCROLLBARS_NONE;

public class ErrorDialog extends Dialog {
    public String message;
    public Expression code;
    public AgoraObject receiver;

    public ErrorDialog(
            Frame parent,
            String title,
            String message,
            Expression code,
            AgoraObject receiver
    ) {
        super(parent, title, true);
        this.message = message;
        this.code = code;
        this.receiver = receiver;
        var l = new BorderLayout();
        setLayout(l);
        var text = new TextArea(message, 15, 20, SCROLLBARS_NONE);
        add("North", text);
        var p = new Panel();
        p.setLayout(new FlowLayout(CENTER, 15, 15));
        if (code != null) {
            var b1 = new Button("Program Text");
            b1.addActionListener(e -> {
                var unparseD = new Dialog(new Frame(), "Program Text", true);
                var l1 = new BorderLayout();
                unparseD.setLayout(l1);
                var t = new TextArea(code.unparse(0), 15, 20, SCROLLBARS_BOTH);
                var b = new Button("Done");
                unparseD.add("Center", t);
                unparseD.add("South", b);
                b.addActionListener(e1 -> dispose());
                unparseD.pack();
                unparseD.setVisible(true);
            });
            p.add(b1);
        }
        if (receiver != null) {
            var b2 = new Button("Inspect Receiver");
            b2.addActionListener(e -> {
                var p1 = new UnaryPattern("inspect");
                var c = new Client(new Object[0], null);
                try {
                    receiver.send(p1, c);
                }catch(MessageNotUnderstood ex){
                    setUpErrorDialog(ex.getMessage(), ex.getCode(), ex.getReceiver());
                } catch (AgoraError ex) {
                    setUpErrorDialog(ex.getMessage(), ex.getCode(), null);
                }
            });
            p.add(b2);
        }
        var b3 = new Button("Done");
        b3.addActionListener(e -> dispose());
        p.add(b3);
        add("South", p);
    }

    /**
     * This internal method sets up an error dialog box with the given string, and allows
     * the programmer to inspect the receiver if it is not null.
     *
     * @param message  The message to be displayed in the dialog box (i.e. the error message).
     * @param receiver If this is not null, the programmer is allowed to inspect the receiver.
     *                 Inspecting the receiver when an error occured is extremely important in Agora,
     *                 since everything is a message to an object.
     */
    public static void setUpErrorDialog(String message, Expression code, AgoraObject receiver) {
        var window = new Frame();
        window.setSize(AwtIo.inspectorX, AwtIo.inspectorY);
        var dg = new ErrorDialog(window, "ERROR", message, code, receiver);
        window.pack();
        dg.pack();
        dg.setVisible(true);
    }
}
