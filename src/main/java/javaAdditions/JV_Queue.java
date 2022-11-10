package javaAdditions;

import java.util.*;
import java.io.*;

/**
   The following private class implements nodes for a dynamically
   linked queue. The members of the queue are of type 'Object'
   such that it is generic (cfr void in C).
	Last change:  E    16 Nov 97    1:46 am
*/
class QueueNode implements Serializable
{
  Object obj;
  QueueNode next;
}

/**
   This public class implements queues as a dynamic list of queue nodes.
   The Queue class is generic: it can store any object you wish.
*/
public class JV_Queue implements Serializable
{
  
  private QueueNode first;
  private QueueNode last;
  private int size;
  
  /**
     Create a new Queue. No size is needed as the implementation is dynamic.
  */
  public JV_Queue ()
    {
      this.first = null;
      this.last  = null;
      this.size  = 0;
    }
  
  /** 
      Put the object at the end of the queue.
      @param object The object to be inserted at the end of the queue.
  */
  public void enQueue(Object object)
    {
      QueueNode n = new QueueNode();
      n.obj = object;
      n.next = null;
      this.size++;
      if (this.first == null)
	{
	  this.first = n;
	  this.last = n;
	}
      else
	{
	  this.last.next = n;
	  this.last = n;
	}
    }
  
  /**
     Serve the next object from the queue. If the queue
     is empty, null is returned.
     @return Returns the first element in the queue. Null is returned if the queue is empty.
  */
  public Object deQueue ()
    {
      if(this.first == null)
	return null; // empty queue
      else
	{
	  this.size--;
	  Object obj = this.first.obj;
	  if (this.first == this.last)
	    this.last = null;
	  this.first = this.first.next;
	  return obj;
	}
    }
  
  /**
     Return the size of the queue.
     @return The size of the queue.
  */
  public int size()
    {
      return this.size;
    }
}
