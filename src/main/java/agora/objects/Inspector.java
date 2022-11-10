package agora.objects;

import java.awt.*;
import java.awt.List;
import java.util.*;
import java.awt.event.*;

import agora.attributes.*;
import agora.runtime.*;
import agora.errors.*;
import java.io.*;

/**
   This is really ugly code I'm ashamed of. It was written just to test
   the port of Agora from C++ to Java.
   Somwhere in the near future, a reall programming environment should be written .
   This code is not documented because we plan to rewrite it in the future (where did
   I here this before?). A single method is implemented to which all arguments
   of the object under inspection have to be passed. A new inspector dialog is opened,
   and the object can be recursively inspected.
   @author Wolfgang De Meuter (Programming Technology Lab)
	Last change:  E    17 Nov 97    1:03 am
   */

public class Inspector extends Dialog implements Serializable
{
  
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
		   final Context context)
    {
      super(parent,title,true);
      this.myPart = myPart;
      this.privPart = privPart;
      this.parentPart = parentPart;
      this.methods = methods;
      this.primObject = primObject;
      BorderLayout l  = new BorderLayout();
      this.setLayout(l);
      Panel texts = new Panel();
      texts.setLayout(new BorderLayout());
      final TextArea text = new TextArea("",2,60,TextArea.SCROLLBARS_NONE);
      final TextArea selection = new TextArea("",15,60,TextArea.SCROLLBARS_BOTH);
      this.add("North",text);
      texts.add("South",selection);
      this.add("Center",texts);
      Panel buttons = new Panel();
      buttons.setLayout(new FlowLayout(FlowLayout.CENTER,15,15));
      if (myPart != null)
	{
	  final List li = new List(7,false);
	  texts.add("North",li);
	  
	  Enumeration es = myPart.elements();
	  Enumeration ks = myPart.keys();
	  final Hashtable items = new Hashtable(5);
	  try
	    {
	      while (es.hasMoreElements())
		{
		  String key = ks.nextElement().toString();
		  Object att = es.nextElement();
		  items.put(key,att);
		}
	    }
	  catch(NoSuchElementException ex)
	    {
	      //cannot occur
	    }
	  li.addItemListener(new ItemListener ()
			     {
			       public void itemStateChanged(ItemEvent e)
				 {
				   if (e.getStateChange() == ItemEvent.SELECTED)
				     try
				       {
					 Attribute att = (Attribute)items.get(li.getItem(((Integer)e.getItem()).intValue()));
					 selection.setText(att.inspect(context));
				       }
				   catch(AgoraError ex)
				     {
				       //can never happen
				     }
				 }
			     });
	  ks = myPart.keys();
	  es = myPart.elements();
	  String pat = null;
	  while (ks.hasMoreElements())
	    {
	      try
		{
		  pat =  ks.nextElement().toString();
		}
	      catch(NoSuchElementException ex)
		{
		  // Impossible
		}
	      li.addItem(pat);
	    }
	  //	.. maak een lijst met alle methods en agora.attributes in ..
	}
      if (privPart != null)
	{
	  Button b2 = new Button("Private");
	  b2.addActionListener(new ActionListener()
			       {
				 public void actionPerformed(ActionEvent e)
				   {
				     privPart.inspect(context);
				   }
			       });
	  buttons.add(b2);
	}
      if (parentPart != null)
	{
	  Button b3 = new Button("Parent");
	  b3.addActionListener(new ActionListener ()
			       {
				 public void actionPerformed(ActionEvent e)
				   {
				     try
				       {
					 parentPart.inspect(context);
				       }
				     catch(AgoraError ex)
				       {
					 // Can never happen
				       }
				   }
			       });
	  buttons.add(b3);
	}
      if (primObject != null)
	{
	  String nat = "Native Object: '" + (primObject.toString()) + "'";
	  text.append(nat);
	}
      else
	{
	  text.append("Inspector For " + title);
	}
      
      Button b6 = new Button("Done");
      b6.addActionListener(new ActionListener()
			   {
			     public void actionPerformed(ActionEvent e)
			       {
				 Inspector.this.setVisible(false);
				 Inspector.this.dispose();
			       }
			   });
      buttons.add(b6);
      this.add("South",buttons);
    }
}
