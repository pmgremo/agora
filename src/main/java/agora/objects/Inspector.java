package agora.objects;

import agora.attributes.Attribute;
import agora.errors.AgoraError;
import agora.runtime.Context;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.NoSuchElementException;

/**
 * This is really ugly code I'm ashamed of. It was written just to test
 * the port of Agora from C++ to Java.
 * Somwhere in the near future, a reall programming environment should be written .
 * This code is not documented because we plan to rewrite it in the future (where did
 * I here this before?). A single method is implemented to which all arguments
 * of the object under inspection have to be passed. A new inspector dialog is opened,
 * and the object can be recursively inspected.
 *
 * @author Wolfgang De Meuter (Programming Technology Lab)
 * Last change:  E    17 Nov 97    1:03 am
 */

public class Inspector extends Dialog implements Serializable {

    protected Hashtable myPart;
    protected InternalGenerator privPart;
    protected AbstractGenerator parentPart;
    protected MethodsGenerator methods;
    protected Object primObject;

    public Inspector(Frame parent,
                     String title,
                     Hashtable myPart,
                     final InternalGenerator privPart,
                     final AbstractGenerator parentPart,
                     MethodsGenerator methods,
                     Object primObject,
                     final Context context) {
        super(parent, title, true);
        this.myPart = myPart;
        this.privPart = privPart;
        this.parentPart = parentPart;
        this.methods = methods;
        this.primObject = primObject;
        var l = new BorderLayout();
        this.setLayout(l);
        var texts = new Panel();
        texts.setLayout(new BorderLayout());
        final var text = new TextArea("", 2, 60, TextArea.SCROLLBARS_NONE);
        final var selection = new TextArea("", 15, 60, TextArea.SCROLLBARS_BOTH);
        this.add("North", text);
        texts.add("South", selection);
        this.add("Center", texts);
        var buttons = new Panel();
        buttons.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 15));
        if (myPart != null) {
            final var li = new List(7, false);
            texts.add("North", li);

            var es = myPart.elements();
            var ks = myPart.keys();
            final var items = new Hashtable(5);
            try {
                while (es.hasMoreElements()) {
                    var key = ks.nextElement().toString();
                    var att = es.nextElement();
                    items.put(key, att);
                }
            } catch (NoSuchElementException ex) {
                //cannot occur
            }
            li.addItemListener(e -> {
				if (e.getStateChange() == ItemEvent.SELECTED)
					try {
						var att = (Attribute) items.get(li.getItem((Integer) e.getItem()));
						selection.setText(att.inspect(context));
					} catch (AgoraError ex) {
						//can never happen
					}
			});
            ks = myPart.keys();
            es = myPart.elements();
            String pat = null;
            while (ks.hasMoreElements()) {
                try {
                    pat = ks.nextElement().toString();
                } catch (NoSuchElementException ex) {
                    // Impossible
                }
                li.addItem(pat);
            }
            //	.. maak een lijst met alle methods en agora.attributes in ..
        }
        if (privPart != null) {
            var b2 = new Button("Private");
            b2.addActionListener(e -> privPart.inspect(context));
            buttons.add(b2);
        }
        if (parentPart != null) {
            var b3 = new Button("Parent");
            b3.addActionListener(e -> {
				try {
					parentPart.inspect(context);
				} catch (AgoraError ex) {
					// Can never happen
				}
			});
            buttons.add(b3);
        }
        if (primObject != null) {
            var nat = "Native Object: '" + (primObject.toString()) + "'";
            text.append(nat);
        } else {
            text.append("Inspector For " + title);
        }

        var b6 = new Button("Done");
        b6.addActionListener(e -> {
            Inspector.this.setVisible(false);
            Inspector.this.dispose();
        });
        buttons.add(b6);
        this.add("South", buttons);
    }
}
