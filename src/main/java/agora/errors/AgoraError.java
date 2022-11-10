package agora.errors;

import java.lang.*;
import java.awt.event.*;
import agora.tools.*;
import agora.grammar.*;
import java.awt.*;
import agora.objects.*;
import agora.patterns.*;
import agora.runtime.*;
import java.io.*;

// The majority of this dirty code must be rewritten. It was my first experiment
// with the Java AWT when porting the C++ code of Agora to Java.
// A programming environent must be written!!!!!!!!!!!!!!!!

class DoUnparseCommand implements ActionListener, Serializable
{
  private Dialog d;
  public DoUnparseCommand(Dialog e)
    {
      super();
      this.d = e;
    }
  
  public void actionPerformed(ActionEvent e)
    {
      var unparseD = new Dialog(new Frame(),"Program Text",true);
      var l = new BorderLayout();
      unparseD.setLayout(l);
      var t = new TextArea(((ErrorDialog)d).code.unparse(0),15,20,TextArea.SCROLLBARS_BOTH);
      var b = new Button("Done");
      unparseD.add("Center",t);
      unparseD.add("South",b);
      b.addActionListener(new DoOKCommand(unparseD));
      unparseD.pack();
      unparseD.show();
    }
}
class DoInspectCommand implements ActionListener
{
  private Dialog d;
  public DoInspectCommand(Dialog e)
    {
      super();
      this.d = e;
    }
  
  public void actionPerformed(ActionEvent e)
    {
      var p = new UnaryPattern("inspect");
      var c = new Client(new Object[0],null);
      try
	{
	  Object ignore = ((ErrorDialog)this.d).receiver.send(p,c);
	}
      catch(AgoraError ex)
	{
	  ex.signal();
	}
    }
}

class DoOKCommand implements ActionListener
{
  private Dialog d;
  
  public DoOKCommand(Dialog e)
    {
      super();
      this.d = e;
    }
  
  public void actionPerformed(ActionEvent e)
    {
      this.d.dispose();
    }
}
class ErrorDialog extends Dialog
{
  public String message;
  public Expression code;
  public AgoraObject receiver;
  
  public ErrorDialog(Frame parent,String title, String message,
		    Expression code,AgoraObject receiver)
    {
      super(parent,title,true);
      this.message = message;
      this.code = code;
      this.receiver = receiver;
      var l  = new BorderLayout();
      this.setLayout(l);
      var text =  new TextArea(message,15,20,TextArea.SCROLLBARS_NONE);
      this.add("North", text);
      var p = new Panel();
      p.setLayout(new FlowLayout(FlowLayout.CENTER,15,15));
      if (code != null)
	{
      var b1 = new Button("Program Text");
	  b1.addActionListener(new DoUnparseCommand(this));
	  p.add(b1);
	}
      if (receiver != null)
	{
      var b2 = new Button("Inspect Receiver");
	  b2.addActionListener(new DoInspectCommand(this));
	  p.add(b2);
	}
      var b3 = new Button("Done");
      b3.addActionListener(new DoOKCommand(this));
      p.add(b3);
      this.add("South",p);
    }
}

/**
   This abstract class is the root of the Agora error hierachy.
   @author Wolfgang De Meuter (Programming Technology Lab).
	Last change:  E    16 Nov 97    1:46 am
*/

public abstract class AgoraError extends Throwable
{
  /**
     This variable is the (possible) code tree where the error occured.
  */
  protected Expression code;
  
  /**
     Creates a new error with empty code tree.
  */
  public AgoraError()
    {
      super();
      this.code = null;
    }
  
  /**
     This method signals the error to the user. It is usually called from within the
     programming environemnt. This abstract method must be overriden for each error.
     Its normal implementation is to call 'setUpDialog' with the correct error message,
     and possibly a receiver that can be inspected.
  */
  public abstract void signal();
  
  /**
     If an error is thrown, and the evaluator can figure out in which piece
     of Agora code it occured, it can tell this code to the error and rethrow
     the error.
     @param code The expression that was last evaluated	before the error occured.
  */
  public void setCode(Expression code)
    {
      if (this.code==null)
	this.code = code;
    }
  
  /**
     This internal method sets up an error dialog box with the given string, and allows
     the programmer to inspect the receiver if it is not null.
     @param message The message to be displayed in the dialog box (i.e. the error message).
     @param receiver If this is not null, the programmer is allowed to inspect the receiver.
     Inspecting the receiver when an error occured is extremely important in Agora,
     since everything is a message to an object.
  */
  protected void setUpDialog(String message, AgoraObject receiver)
    {
      var window = new Frame();
      window.setSize(AgoraIO.inspectorX,AgoraIO.inspectorY);
      var dg = new ErrorDialog(window,"ERROR",message,code,receiver);
      window.pack();
      dg.pack();
      dg.show();
    }
}
