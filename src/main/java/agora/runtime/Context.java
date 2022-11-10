package agora.runtime;

import agora.objects.*;
import agora.errors.*;
import java.io.*;

/**
  This class represents the contexts in which Agora expressions are evaluated.
  Contexts can be compared with the environment parameter in languages like Scheme.
  @author Wolfgang De Meuter (Programming Technology Lab).
  @see agora.runtime.Client
  @see agora.runtime.ReifierClient
	Last change:  E    16 Nov 97    1:51 am
  */
public class Context extends Object implements Serializable
{
  /**
    The 'current' self, i.e. the object identity of the object in which an expression
    is evaluated.
	Last change:  E     8 Nov 97    7:49 am
    */
  protected IdentityGenerator currentSelf;

  /**
    The 'current' private, i.e. the private of the object in which the expression
    is evaluated.
    */
  protected InternalGenerator currentPriv;
  /**
    The 'current' public, i.e. the public of the object in which the expression is
    evaluated.
    */
  protected MethodsGenerator currentPub;

  /**
    The 'current' parent.
    */
  protected AbstractGenerator currentParent;

  /**
    The evaluation category in which evaluation is 'currently' happening.
    */
  protected int currentCategory;

  /**
    The exception (catch code) that was encountered last (and needs to be checked
    first when it is raised).
    */
  protected AgoraException currentException;

  /**
    Creates a new context. This constructor should never be used, because there is a contract
    between contexts and clients (see Client). Contexts and clients must create each other,
    so that this constructor can only be called by the methods 'makeContext' defined on
    Clients and ReifierClients. The only exception to this rule is at the very beginning
    of the evaluation cycle, when a new context is created to evaluate an expression
    in the context of the root of the Agora system.<p>
    Normally, contexts are created right after send occurs. Immediatly, the client of the
    send creates a new context in which all information of the receiver is present.
    @param self The self of the object in which	the message arrived.
    @param prive The private part of that object.
    @param pub The public part of that object.
    @param cat The evaluation category in which the method corresponding to the
    arrived message must be evaluated.
    @param parent The parent object.
    @param except The exception that resides in the client that created this context.
    */
  public Context (IdentityGenerator self,
		  InternalGenerator priv,
		  MethodsGenerator pub,
		  int cat,
		  AbstractGenerator parent,
		  AgoraException except)
    {
      super();
      this.currentException = except;
      this.currentParent= parent;
      this.currentPriv= priv;
      this.currentSelf= self;
      this.currentPub=pub;
      this.currentCategory= cat;
    }

  /**
    This is something like a copy constructor.
    @param c A given context to be copied.
    @return A copy of the specified argument
    */
  public Context newContext(Context c)
    {
      return (new Context(c.currentSelf,
			  c.currentPriv,
			  c.currentPub,
			  c.currentCategory,
			  c.currentParent,
			  c.currentException));
    }

  /**
    This is a method that supports the contract between contexts and clients. Never create
    a new client, always ask the context at hand.
    @param actuals The actual arguments beloning to the message that is sent making use
    of the new client.
    @return A new Client object with the given actuals.
    */
  public Client  newClient (Object[] actuals)
    {
      return new Client(actuals,this.currentException);
    }

  /**
    Same as above, but now a reifier client instead of an ordinary client is created.
    The difference is that the receiving context (this) is copied into the reifier client,
    such that when this reifier client is asked for a new context, this context (the receiver
    is simply returned.
    @param actuals The actual parameters of the reifier message that will use the newly
    created reifier client.
    @return A new ReifierClient with the given actuals.
    */
  public ReifierClient newReifierClient (Object[] actuals)
    {
      return new ReifierClient(actuals,this,currentException);
    }

  /**
    Copies the receiver and changes the self to the specified parameter.
    @param self The updated self to be stored in the new context.
    @return A new context in which everything is the same, but the given argument.
    */
  public Context setSelf(IdentityGenerator self)
    {
      Context newC = this.newContext(this);
      newC.currentSelf = self;
      return newC;
    }

  /**
    Copies the receiving context and stores a new category in this copy.
    @param cat The updated category to be stored in the new context.
    @return A new context in which everything is the same, but the given argument.
    */
  public Context setCat (int cat)
    {
      Context newC = this.newContext(this);
      newC.currentCategory=cat;
      return newC;
    }
  
  /**
    Copies the receiving context and stores a new public in this copy.
    @param pub The updated public part to be stored in the new context.
    @return A new context in which everything is the same, but the given argument.
    */
  public Context setPub (MethodsGenerator pub)
    {
      Context newC = this.newContext(this);
      newC.currentPub = pub;
      return newC;
    }
  
  /**
    Copies the receiving context and stores a new parent in this copy.
    @param parent The updated parent to be stored in the new context.
    @return A new context in which everything is the same, but the given argument.
    */
  public Context setParent (AbstractGenerator parent)
    {
      Context newC =this.newContext(this);
      newC.currentParent=parent;
      return newC;
    }
  
  /**
    Copies the receiving context and stores a new private in this copy.
    @param priv The updated private part to be stored in the new context.
    @return A new context in which everything is the same, but the given argument.
    */
  public Context setPrivate(InternalGenerator priv)
    {
      Context newC = this.newContext(this);
      newC.currentPriv=priv;
      return newC;
    }
  
  /**
    Copies the receiving context and stores a new exception in this copy.
    @param except The updated exception to be stored in the new context.
    @return A new context in which everything is the same, but the given argument.
    */
  public Context setException(AgoraException except)
    {
      Context newC = this.newContext(this);
      newC.currentException=except;
      return newC;
    }

  /**
    Destructively changes the self in the context.
    @param self The new self.
    */
  public void impSetSelf  (IdentityGenerator self)
    {
      this.currentSelf = self;
    }

    /**
    Destructively changes the category in the context.
    @param cat The new Category.
    */
  public void impSetCat(int cat)
    {
      this.currentCategory=cat;
    }

    /**
    Destructively changes the parent in the context.
    @param parent The new parent part.
    */
  public void impSetParent(AbstractGenerator parent)
    {
      this.currentParent=parent;
    }

    /**
    Destructively changes the private part of the context.
    @param priv The new private part.
    */
  public void impSetPrivate (InternalGenerator priv)
    {
      this.currentPriv=priv;
    }

    /**
    Destructively changes the public part in the context.
    @param pub The new public part.
    */
  public void impSetPublic (MethodsGenerator pub)
    {
      this.currentPub=pub;
    }

    /**
    Makes a copy of the receiving context, and updates the given arguments in this copy.
    @param self The new self.
    @param priv An updated private part.
    @param parent An updates parent part.
    @return A new Context in which everything is the same as in the original context,
    but the specified parameters.
    */
  public Context setMultiple (IdentityGenerator self,
			      InternalGenerator priv,
			      AbstractGenerator parent)
    {
      Context newC = this.newContext(this);
      newC.currentParent=parent;
      newC.currentPriv=priv;
      newC.currentSelf=self;
      return newC;
    }
  
    /**
    Makes a copy of the receiving context, and updates the given argument in this copy.
    @param self The new self.
    @param cat An updated category.
    @return A new Context in which everything is the same as in the original context,
    but the specified parameters.
    */
  public Context setMultiple (IdentityGenerator self,
			      int cat)
    {
      Context newC = this.newContext(this);
      newC.currentSelf = self;
      newC.currentCategory=cat;
      return newC;
    }
  
    /**
    Makes a copy of the receiving context, and updates the given arguments in this copy.
    @param priv An updated private part.
    @param cat A new category.
    @param parent An updated parent part.
    @return A new Context in which everything is the same as in the original context,
    but the specified parameters.
    */
  public Context setMultiple (InternalGenerator priv,
			      int cat,
			      AbstractGenerator parent)
    {
      Context newC = this.newContext(this);
      newC.currentPriv=priv;
      newC.currentParent=parent;
      newC.currentCategory = cat;
      return newC;
    }
  
    /**
    Makes a copy of the receiving context, and updates the given arguments in this copy.
    @param self The new self.
    @param priv An updated private part.
    @param parent An updated parent part.
    @param pub An updated public part.
    @param cat A new category.
    @return A new Context in which everything is the same as in the original context,
    but the specified parameters.
    */
  public Context setMultiple  (IdentityGenerator self,
			       InternalGenerator priv,
			       MethodsGenerator pub,
			       int cat,
			       AbstractGenerator parent)
    {
      Context newC = this.newContext(this);
      newC.currentSelf = self;
      newC.currentPriv=priv;
      newC.currentPub = pub;
      newC.currentCategory=cat;
      newC.currentParent=parent;
      return newC;
    }

    /**
    Read the parent part from the context.
    @return The parent part that resides in this context.
    */
  public AbstractGenerator  getParent ()
    {
      return currentParent;
    }

    /**
    Read the private part from the context.
    @return The private part that resides in this context.
    */
  public InternalGenerator getPrivate()
    {
      return currentPriv;
    }

    /**
    Read the public part from the context.
    @return The public part carried around in this context.
    */
  public MethodsGenerator  getPub()
    {
      return currentPub;
    }

    /**
    Read the object identity from the context.
    @return The object identity that resides in the current evaluation context.
    */
  public IdentityGenerator getSelf()
    {
      return currentSelf;
    }

    /**
    Read the evaluation category from the context.
    @return An integer representing the bitset as the evaluation category that
    resides in the current evaluation context.
    */
  public int getCategory()
    {
      return currentCategory;
    }

    /**
    Read the current exception (i.e. the last encountered catch-code) from the context.
    @return The last encountered catch-code that can be thrown by the RAISE reifier.
    */
  public AgoraException getException()
    {
      return currentException;
    }

    /**
    This is the implementation of the SELF reifier. Since SELF is a receiverless reifier,
    it is sent to the evaluation context.
    @param context This is the context where SELF occurs. It is from that context that the
    self part (disguised as an AgoraObject) must be extracted. This is not a special trick:
    the first argument of every reifier is the context of invocation: SELF just happens to have no
    other expression arguments like, to name only one, FOR:TO:DO:.
    @return An Object being an AgoraObject indicating the SELF of the context in which
    the SELF reifier occurs.
    */
  public Object Self(Context context)
    { 
      return context.currentSelf.wrap(); 
    }

    /**
    This IS a hack: normally, every object should understand 'inspect', but in the case
    of primitive agora.objects (java agora.objects), this message is not implemented in the API's, and
    we cannot add it because java.lang.Object cannot be adjusted. Hence, we implemented
    inspectPrimitive as a procedure on contexts that can read the self from the context,
    and inspect it. This way *all* agora.objects can be inspected.
    @exception agora.errors.AgoraError Is thrown when something goes wrong during inspection.
    (If this happens, it is a bug in the implementation).
    */
  public void inspectPrimitive() throws AgoraError
    {
      this.currentSelf.inspect(this);
    }
  
}
