package agora.awt;

import agora.attributes.Attribute;
import agora.objects.Generator;
import agora.objects.InternalGenerator;
import agora.objects.MethodsGenerator;
import agora.patterns.Pattern;
import agora.runtime.Context;

import java.awt.*;
import java.io.Serializable;
import java.util.Hashtable;

import static java.awt.FlowLayout.CENTER;
import static java.awt.TextArea.SCROLLBARS_BOTH;
import static java.awt.TextArea.SCROLLBARS_NONE;
import static java.awt.event.ItemEvent.SELECTED;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

/**
 * This is very ugly code I'm ashamed of. It was written just to test
 * the port of Agora from C++ to Java.
 * Somewhere in the near future, a real programming environment should be written .
 * This code is not documented because we plan to rewrite it in the future (where did
 * I here this before?). A single method is implemented to which all arguments
 * of the object under inspection have to be passed. A new inspector dialog is opened,
 * and the object can be recursively inspected.
 *
 * @author Wolfgang De Meuter (Programming Technology Lab)
 * Last change:  E    17 Nov 97    1:03 am
 */

public class Inspector extends Dialog implements Serializable {

    protected Hashtable<Pattern, Attribute> myPart;
    protected InternalGenerator privPart;
    protected Generator parentPart;
    protected MethodsGenerator methods;
    protected Object primObject;

    public Inspector(
            Frame parent,
            String title,
            Hashtable<Pattern, Attribute> myPart,
            InternalGenerator privPart,
            Generator parentPart,
            MethodsGenerator methods,
            Object primObject,
            Context context,
            Generator self
    ) {
        super(parent, title, true);
        this.myPart = myPart;
        this.privPart = privPart;
        this.parentPart = parentPart;
        this.methods = methods;
        this.primObject = primObject;
        var l = new BorderLayout();
        setLayout(l);
        var texts = new Panel();
        texts.setLayout(new BorderLayout());
        var text = new TextArea("", 2, 60, SCROLLBARS_NONE);
        var selection = new TextArea("", 15, 60, SCROLLBARS_BOTH);
        add("North", text);
        texts.add("South", selection);
        add("Center", texts);
        var buttons = new Panel();
        buttons.setLayout(new FlowLayout(CENTER, 15, 15));
        if (myPart != null) {
            var li = new List(7);
            texts.add("North", li);
            var items = myPart.entrySet().stream().collect(toMap(Object::toString, identity()));
            for (var pattern : myPart.keySet()) {
                li.add(pattern.toString());
            }
            li.addItemListener(e -> {
                if (e.getStateChange() != SELECTED) return;
                var att = (Attribute) items.get(li.getItem((Integer) e.getItem()));
                selection.setText(att.inspect(context));
            });
        }
        if (privPart != null) {
            var b2 = new Button("Private");
            b2.addActionListener(e -> privPart.inspect(context));
            buttons.add(b2);
        }
        if (parentPart != null) {
            var b3 = new Button("Parent");
            b3.addActionListener(e -> parentPart.inspect(context));
            buttons.add(b3);
        }
        if (primObject != null) {
            text.append("Native Object: '" + primObject + "'");
        } else {
            text.append("Inspector For " + title);
        }

        var b6 = new Button("Done");
        b6.addActionListener(e -> {
            setVisible(false);
            dispose();
        });
        buttons.add(b6);
        add("South", buttons);
    }
}
