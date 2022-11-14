package agora.grammar;

import agora.attributes.*;
import agora.errors.*;
import agora.objects.AgoraObject;
import agora.objects.FormalsAndPattern;
import agora.objects.InternalGenerator;
import agora.patterns.KeywordPattern;
import agora.patterns.UnaryPattern;
import agora.reflection.Keyword;
import agora.reflection.Reified;
import agora.reflection.Unary;
import agora.reflection.Up;
import agora.runtime.Category;
import agora.runtime.Context;
import agora.tools.AgoraGlobals;

import java.io.Serializable;
import java.util.Vector;

/**
 * This is the abstract class representing nodes in Agora parse trees. All other
 * node classes are a subclass of this class.
 *
 * @author Wolfgang De Meuter
 * Last change:  E    16 Nov 97    1:58 pm
 */
abstract public class Expression implements Serializable {
    /**
     * Constructor does nothing but calling the super.
     */
    public Expression() {
        super();
    }

    /**
     * To unparse the expression towards a string. The integer parameter denotes the number of spaces
     * that must precede the expression.
     *
     * @param hor The number of space leading the unparsed expression.
     */
    public abstract String unparse(int hor);

    /**
     * To evaluate the expression in a given context. Might throw an AgoraError.
     *
     * @param context The evaluation context.
     * @return The value associated to the expression.
     * @throws agora.errors.AgoraError Errors occured during evaluation.
     */
    public abstract AgoraObject eval(Context context) throws AgoraError;

    /**
     * To evaluate the expression in the root context.
     *
     * @return The value of the expresssion in the root context.
     * @throws agora.errors.AgoraError Errors occuring during evaluation.
     */
    public AgoraObject defaultEval() throws AgoraError {
        return this.eval(new Context(
                AgoraGlobals.glob.rootIdentity,
                AgoraGlobals.glob.rootPrivate,
                AgoraGlobals.glob.rootIdentity.getMe(),
                Category.emptyCategory,
                AgoraGlobals.glob.rootParent,
                new AgoraException(this)
        ));
    }

    /**
     * Code to evaluate the VARIABLE reifier message in a given context. A new pair of
     * read and write methods will be installed in the self of the context.
     *
     * @param context The context of evaluation when sending the reifier
     * @return The initial value of the variable:null.
     * @throws agora.errors.AgoraError Happens when illegal agora.patterns are given.
     */
    @Unary({"VARIABLE", "VAR"})
    @Reified
    public AgoraObject variable(Context context) throws AgoraError {
        var leftside = (FormalsAndPattern) this.eval(context.setCat(Category.flags)).down();
        var thePattern = leftside.pattern;
        var theCat = leftside.cat;
        // Validate Pattern
        if (!(thePattern instanceof UnaryPattern))
            throw (new ReifierMisused("VARIABLE can only be sent to unary agora.patterns"));
        if (!(Category.containsLessThan(theCat, Category.local | Category.publik)))
            throw (new ReifierMisused("Illegal Adjectives Used With VARIABLE"));
        // Fill In Default Values
        if ((!Category.contains(theCat, Category.local)) &&
                (!Category.contains(theCat, Category.publik)))
            theCat = theCat | Category.publik;
        // Install variable and initial value in the appropriate object part(s)
        var theVarCont = new VariableContainer(Up.glob.up(0));
        var varSetAtt = new VarSetAttribute(theVarCont);
        var varGetAtt = new VarGetAttribute(theVarCont);
        if (Category.contains(theCat, Category.publik)) {
            var getPat = (UnaryPattern) thePattern;
            context.getPub().installPattern(getPat.makeWritePattern(), varSetAtt);
            context.getPub().installPattern(getPat, varGetAtt);
        }
        if (Category.contains(theCat, Category.local)) {
            var getPat = (UnaryPattern) thePattern;
            context.getPrivate().installPattern(getPat.makeWritePattern(), varSetAtt);
            context.getPrivate().installPattern(getPat, varGetAtt);
        }
        return AgoraGlobals.glob.uppedNull;
    }

    /**
     * Code to evaluate the VARIABLE: message in a given context. The expression denotes
     * the initial value. It will be evaluated. The new variable
     * will install a read and write method in the self of the context.
     *
     * @param context The context where the reifier was sent.
     * @param value   The expression indicating the initial value.
     * @return The agora object associated to this reifier invocation.
     * @throws agora.errors.AgoraError When something goes wrong.
     */
    @Reified
    public AgoraObject variableColon(Context context, @Keyword("VARIABLE:") Expression value) throws AgoraError {
        var leftside = (FormalsAndPattern) this.eval(context.setCat(Category.flags)).down();
        var thePattern = leftside.pattern;
        var theCat = leftside.cat;
        // Validate Pattern
        if (!(thePattern instanceof UnaryPattern))
            throw (new ReifierMisused("VARIABLE can only be sent to unary pattern"));
        if (!(Category.containsLessThan(theCat, Category.local | Category.publik)))
            throw (new ReifierMisused("Illegal Adjectives Used With VARIABLE"));
        // Fill In Default Values
        if ((!Category.contains(theCat, Category.local)) &&
                (!Category.contains(theCat, Category.publik)))
            theCat = theCat | Category.publik;
        //Install variable and initial value
        var initValue = value.eval(context);
        var theVarCont = new VariableContainer(initValue);
        var varSetAtt = new VarSetAttribute(theVarCont);
        var varGetAtt = new VarGetAttribute(theVarCont);
        if (Category.contains(theCat, Category.publik)) {
            var getPat = (UnaryPattern) thePattern;
            context.getPub().installPattern(getPat.makeWritePattern(), varSetAtt);
            context.getPub().installPattern(getPat, varGetAtt);
        }
        if (Category.contains(theCat, Category.local)) {
            var getPat = (UnaryPattern) thePattern;
            context.getPrivate().installPattern(getPat.makeWritePattern(), varSetAtt);
            context.getPrivate().installPattern(getPat, varGetAtt);
        }
        return initValue;
    }

    /**
     * Code to evaluate the CONST: message in a given context. The expression denotes
     * the initial value. It will be evaluated. The new variable
     * will install a read method in the self of the context.
     *
     * @param context The context of invocation of this reifier.
     * @param value   The expression denoting the value of the constant.
     * @return The agoraobject being the value of this reifier expression.
     * @throws agora.errors.AgoraError When something goes wrong.
     */
    @Reified
    public AgoraObject constColon(Context context, @Keyword("CONST:") Expression value) throws AgoraError {
        var leftside = (FormalsAndPattern) this.eval(context.setCat(Category.flags)).down();
        var thePattern = leftside.pattern;
        var theCat = leftside.cat;
        // Validate Pattern
        if (!(thePattern instanceof UnaryPattern))
            throw (new ReifierMisused("CONST can only be sent to unary agora.patterns"));
        if (!(Category.containsLessThan(theCat, Category.local | Category.publik)))
            throw (new ReifierMisused("Illegal Adjectives Used With CONST"));
        // Fill In Default Values
        if ((!Category.contains(theCat, Category.local)) &&
                (!Category.contains(theCat, Category.publik)))
            theCat = theCat | Category.publik;
        // Install variable and initial value in the appropriate object part(s)
        var initValue = value.eval(context);
        var theVarCont = new VariableContainer(initValue);
        var varGetAtt = new VarGetAttribute(theVarCont);
        if (Category.contains(theCat, Category.publik)) {
            var getPat = (UnaryPattern) thePattern;
            context.getPub().installPattern(getPat, varGetAtt);
        }
        if (Category.contains(theCat, Category.local)) {
            var getPat = (UnaryPattern) thePattern;
            context.getPrivate().installPattern(getPat, varGetAtt);
        }
        return initValue;
    }

    /**
     * Code to evaluate the ARRAY reifier message.
     *
     * @param context The context of invocation of this reifier message.
     * @return The agora object representing the array.
     * @throws agora.errors.AgoraError When something goes wrong.
     */
    @Unary("ARRAY")
    @Reified
    public AgoraObject array(Context context) throws AgoraError {
        var size = this.evalAsInteger(context);
        var theArray = new Vector<String>(size);
        try {
            for (var i = 0; i < size; i++)
                theArray.insertElementAt("empty", i);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw (new PrimException(e, "Expression::array"));
            // impossible because we created it large enough
        }
        return Up.glob.up(theArray);
    }

    /**
     * Code to evaluate the ARRAY: reifier message. The expression argument denotes the initial value
     * each array location will contain. It will be evaluated for each array location.
     *
     * @param context The calling context of this reifier message.
     * @param value   The expression denoting the initial values of the array.
     * @return The agora object representing the newly created array.
     * @throws agora.errors.AgoraError When something goes wrong.
     */
    @Reified
    public AgoraObject arrayColon(Context context, @Keyword("ARRAY:") Expression value) throws AgoraError {
        var size = this.evalAsInteger(context);
        var theArray = new Vector<>(size);
        try {
            for (var i = 0; i < size; i++)
                theArray.insertElementAt(value.eval(context).down(), i);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw (new PrimException(e, "Expression::arrayColon"));
            // impossible because we created it large enough
        }
        return Up.glob.up(theArray);
    }

    /**
     * Code that implements the METHOD: reifier message in a given context. This will install a new method
     * in the self of the context. The expression argument
     * denotes the body of the method.
     *
     * @param context The calling context of this reifier.
     * @param body    The body expression of the method.
     * @return The return value of installing this method.
     * @throws agora.errors.AgoraError When something goes wrong during installing the method.
     */
    @Reified
    public AgoraObject methodColon(Context context, @Keyword("METHOD:") Expression body) throws AgoraError {
        var leftside = (FormalsAndPattern) this.eval(context.setCat(Category.flags)).down();
        var thePattern = leftside.pattern;
        var theCat = leftside.cat;
        var formals = leftside.formals;
        // Validate Pattern
        if (!(Category.containsLessThan(theCat, Category.local | Category.publik)))
            throw (new ReifierMisused("Illegal Adjectives Used With METHOD"));
        // Fill In Default Values
        if ((!Category.contains(theCat, Category.local)) &&
                (!Category.contains(theCat, Category.publik)))
            theCat = theCat | Category.publik;
        Attribute methAtt = new MethAttribute(formals, body);
        if (Category.contains(theCat, Category.publik)) {
            context.getPub().installPattern(thePattern, methAtt);
        }
        if (Category.contains(theCat, Category.local)) {
            context.getPrivate().installPattern(thePattern, methAtt);
        }
        return AgoraGlobals.glob.uppedNull;
    }

    /**
     * Code to evaluate the MIXIN: reifier message in a given context. This will install a new mixin
     * method in the self of the context with the expression argument as body.
     *
     * @param context The calling context of this reifier message.
     * @param body    The body expression of the mixin.
     * @return The return value of installing this mixin.
     * @throws agora.errors.AgoraError When something goes wrong during installing the mixin.
     */
    @Reified
    public AgoraObject mixinColon(Context context, @Keyword("MIXIN:") Expression body) throws AgoraError {
        var leftside = (FormalsAndPattern) this.eval(context.setCat(Category.flags)).down();
        var thePattern = leftside.pattern;
        var theCat = leftside.cat;
        var formals = leftside.formals;
        // Validate Pattern
        if (!(Category.containsLessThan(theCat, Category.local |
                Category.publik)))
            throw (new ReifierMisused("Illegal Adjectives Used With MIXIN"));
        // Fill In Default Values
        if ((!Category.contains(theCat, Category.local)) &&
                (!Category.contains(theCat, Category.publik)))
            theCat = theCat | Category.publik;
        // Install method in the appropriate object part(s)
        Attribute methAtt = new MixinAttribute(formals, body);
        if (Category.contains(theCat, Category.publik)) {
            context.getPub().installPattern(thePattern, methAtt);
        }
        if (Category.contains(theCat, Category.local)) {
            context.getPrivate().installPattern(thePattern, methAtt);
        }
        return AgoraGlobals.glob.uppedNull;
    }

    /**
     * Code to evaluate the VIEW: reifier message in a given context. This will install a new mixin
     * method in the self of the context with the expression argument as body.
     *
     * @param context The calling context of this reifier message.
     * @param body    The body expression of the view.
     * @return The return value of installing the new view.
     * @throws agora.errors.AgoraError When something goes wrong when installing the new view.
     */
    @Reified
    public AgoraObject viewColon(Context context, @Keyword("VIEW:") Expression body) throws AgoraError {
        var leftside = (FormalsAndPattern) this.eval(context.setCat(Category.flags)).down();
        var thePattern = leftside.pattern;
        var theCat = leftside.cat;
        var formals = leftside.formals;
        // Validate Pattern
        if (!(Category.containsLessThan(theCat, Category.local | Category.publik)))
            throw (new ReifierMisused("Illegal Adjectives Used With VIEW"));
        // Fill In Default Values
        if ((!Category.contains(theCat, Category.local)) &&
                (!Category.contains(theCat, Category.publik)))
            theCat = theCat | Category.publik;
        // Install method in the appropriate object part(s)
        Attribute methAtt = new ViewAttribute(formals, body);
        if (Category.contains(theCat, Category.publik)) {
            context.getPub().installPattern(thePattern, methAtt);
        }
        if (Category.contains(theCat, Category.local)) {
            context.getPrivate().installPattern(thePattern, methAtt);
        }
        return AgoraGlobals.glob.uppedNull;
    }

    /**
     * This method implements the CLONING: reifier message. The method will install a new cloning
     * method in the self that is inside the context object. The extra expression argument will be used
     * as the body of the cloning method.
     *
     * @param context The context of sending this reifier message.
     * @param body    The body expression of the new cloning method.
     * @return The return value of installing the new method.
     * @throws agora.errors.AgoraError When something goes wrong during installation of the new method.
     */
    @Reified
    public AgoraObject cloningColon(Context context, @Keyword("CLONING:") Expression body) throws AgoraError {
        var leftside = (FormalsAndPattern) this.eval(context.setCat(Category.flags)).down();
        var thePattern = leftside.pattern;
        var theCat = leftside.cat;
        var formals = leftside.formals;
        // Validate Pattern
        if (!(Category.containsLessThan(theCat, Category.local | Category.publik)))
            throw (new ReifierMisused("Illegal Adjectives Used With CLONING"));
        // Fill In Default Values
        if ((!Category.contains(theCat, Category.local)) &&
                (!Category.contains(theCat, Category.publik)))
            theCat = theCat | Category.publik;
        // Install method in the appropriate object part(s)
        Attribute methAtt = new CloningAttribute(formals, body);
        if (Category.contains(theCat, Category.publik)) {
            context.getPub().installPattern(thePattern, methAtt);
        }
        if (Category.contains(theCat, Category.local)) {
            context.getPrivate().installPattern(thePattern, methAtt);
        }
        return AgoraGlobals.glob.uppedNull;
    }

    /**
     * Implements the PUBLIC reifier as sent to the receiving expression.
     *
     * @param context The context where the reifier was sent.
     * @return The FormalsAndPattern where the public bit is set to 1.
     * @throws agora.errors.AgoraError Perhaps a wrong pattern was there.
     */
    @Unary({"PUBLIC", "PUB"})
    @Reified
    public AgoraObject publik(Context context) throws AgoraError {
        var leftside = (FormalsAndPattern) this.eval(context).down();
        leftside.cat = leftside.cat | Category.publik;
        return Up.glob.up(leftside);
    }

    /**
     * Implements the LOCAL reifier sent to the receiving expression.
     *
     * @param context The context of the place the reifier was sent.
     * @return The FormalsAndPattern(upped!) where the local bit is set to 1.
     * @throws agora.errors.AgoraError Perhaps a wrong pattern is the receiver.
     */
    @Unary({"LOCAL", "LOC"})
    @Reified
    public AgoraObject local(Context context) throws AgoraError {
        var leftside = (FormalsAndPattern) this.eval(context).down();
        leftside.cat = leftside.cat | Category.local;
        return Up.glob.up(leftside);
    }

    /**
     * Implements the FOR:TO:DO: reifier message. The receiving expression must be a unary pattern.
     * The 'from' expression must evaluate to a number, as well as the 'to' parameter.
     * The third expression argument is the block that will be iterated.
     *
     * @param context The context the reifier occurs in.
     * @param from    The expression indicating the lower bound. Must evaluate to an integer.
     * @param to      The expression indicating the upper bound. Must evaluate to an integer.
     * @param doblock The expression that will be iterated.
     * @return The value of the FOR: expression.
     * @throws agora.errors.AgoraError When something goes wrong during evaluation.
     */
    @Reified
    public AgoraObject fortodo(Context context,
                               @Keyword("FOR:") Expression from,
                               @Keyword("TO:") Expression to,
                               @Keyword("DO:") Expression doblock) throws AgoraError {
        var init = from.evalAsInteger(context);
        var term = to.evalAsInteger(context);
        // Determine unary receiverless pattern
        var leftside = (FormalsAndPattern) this.eval(context.setCat(Category.flags)).down();
        var thePattern = leftside.pattern;
        // Validate Pattern
        if (!(thePattern instanceof UnaryPattern))
            throw (new ReifierMisused("FOR:TO:DO: can only be sent to unary agora.patterns"));
        // Extend Private Temporarily and install variable and initial value in the new private object part
        var newPriv = (InternalGenerator) context.getPrivate().funcAddLayer("FOR:TO:DO: scope");
        newPriv.setPrivate(newPriv);
        var theVarCont = new VariableContainer(Up.glob.up(init));
        var varGetAtt = new VarGetAttribute(theVarCont);
        var getPat = new UnaryPattern(((UnaryPattern) thePattern).getUnaryPattern());
        newPriv.installPattern(getPat, varGetAtt);
        // Do the Looping
        var locCont = context.setPrivate(newPriv);
        var result = AgoraGlobals.glob.uppedNull;
        for (var current = init; current <= term; current++) {
            theVarCont.write(Up.glob.up(current));
            result = doblock.eval(locCont);
        }
        return result;
    }

    /**
     * See fortodo.
     *
     * @throws agora.errors.AgoraError When something goes wrong during evaluation.
     */
    @Reified
    public AgoraObject fordowntodo(Context context,
                                   @Keyword("FOR:") Expression from,
                                   @Keyword("DOWNTO:") Expression downto,
                                   @Keyword("DO:") Expression doblock) throws AgoraError {
        var init = from.evalAsInteger(context);
        var term = downto.evalAsInteger(context);
        // Determine unary receiverless pattern
        var leftside = (FormalsAndPattern) this.eval(context.setCat(Category.flags)).down();
        var thePattern = leftside.pattern;
        // Validate Pattern
        if (!(thePattern instanceof UnaryPattern))
            throw (new ReifierMisused("FOR:DOWNTO:DO: can only be sent to unary agora.patterns"));
        // Extend Private Temporarily and install variable and initial value in the new private object part
        var newPriv = (InternalGenerator) context.getPrivate().funcAddLayer("FOR:DOWNTO:DO: scope");
        newPriv.setPrivate(newPriv);
        var theVarCont = new VariableContainer(Up.glob.up(init));
        var varGetAtt = new VarGetAttribute(theVarCont);
        var getPat
                = new UnaryPattern(((UnaryPattern) thePattern).getUnaryPattern());
        newPriv.installPattern(getPat, varGetAtt);
        // Do the Looping
        var locCont = context.setPrivate(newPriv);
        var result = AgoraGlobals.glob.uppedNull;
        for (var current = init; current >= term; current--) {
            theVarCont.write(Up.glob.up(current));
            result = doblock.eval(locCont);
        }
        return result;
    }

    /**
     * See fortodo. The extra 'by' argument must evaluate to an integer.
     *
     * @param context The context the reifier occurs in.
     * @param from    The expression indicating the lower bound. Must evaluate to an integer.
     * @param to      The expression indicating the upper bound. Must evaluate to an integer.
     * @param doblock The expression that will be iterated.
     * @param by      The expression denoting the step size. Must evaluate to an integer.
     * @return The value of the FOR: expression.
     * @throws agora.errors.AgoraError When something goes wrong during evaluation.
     */
    @Reified
    public AgoraObject fortobydo(Context context,
                                 @Keyword("FOR:") Expression from,
                                 @Keyword("TO:") Expression to,
                                 @Keyword("BY:") Expression by,
                                 @Keyword("DO:") Expression doblock) throws AgoraError {
        var init = from.evalAsInteger(context);
        var term = to.evalAsInteger(context);
        var step = by.evalAsInteger(context);
        // Determine unary receiverless pattern
        var leftside = (FormalsAndPattern) this.eval(context.setCat(Category.flags)).down();
        var thePattern = leftside.pattern;
        // Validate Pattern
        if (!(thePattern instanceof UnaryPattern))
            throw (new ReifierMisused("FOR:TO:BY:DO: can only be sent to unary agora.patterns"));
        // Extend Private Temporarily and Install variable and initial value in the new private object part
        var newPriv = (InternalGenerator) context.getPrivate().funcAddLayer("FOR:TO:BY:DO: scope");
        newPriv.setPrivate(newPriv);
        var theVarCont = new VariableContainer(Up.glob.up(init));
        var varGetAtt = new VarGetAttribute(theVarCont);
        var getPat = new UnaryPattern(((UnaryPattern) thePattern).getUnaryPattern());
        newPriv.installPattern(getPat, varGetAtt);
        // Do the Looping
        var locCont = context.setPrivate(newPriv);
        var result = AgoraGlobals.glob.uppedNull;
        for (var current = init; current <= term; current = current + step) {
            theVarCont.write(Up.glob.up(current));
            result = doblock.eval(locCont);
        }
        return result;
    }

    /**
     * See fortobydo
     *
     * @throws agora.errors.AgoraError When something goes wrong during evaluation.
     */
    @Reified
    public AgoraObject fordowntobydo(Context context,
                                     @Keyword("FOR:") Expression from,
                                     @Keyword("DOWNTO:") Expression downto,
                                     @Keyword("BY:") Expression by,
                                     @Keyword("DO:") Expression doblock) throws AgoraError {
        var init = from.evalAsInteger(context);
        var term = downto.evalAsInteger(context);
        var step = by.evalAsInteger(context);
        // Determine unary receiverless pattern
        var leftside = (FormalsAndPattern) this.eval(context.setCat(Category.flags)).down();
        var thePattern = leftside.pattern;
        // Validate Pattern
        if (!(thePattern instanceof UnaryPattern))
            throw (new ReifierMisused("FOR:DOWNTO:BY:DO: can only be sent to unary agora.patterns"));
        // Install variable and initial value in the new private object part
        var newPriv = (InternalGenerator) context.getPrivate().funcAddLayer("FOR:DOWNTO:BY:DO: scope");
        newPriv.setPrivate(newPriv);
        var theVarCont = new VariableContainer(Up.glob.up(init));
        var varGetAtt = new VarGetAttribute(theVarCont);
        var getPat = new UnaryPattern(((UnaryPattern) thePattern).getUnaryPattern());
        newPriv.installPattern(getPat, varGetAtt);
        // Do the Looping
        var locCont = context.setPrivate(newPriv);
        var result = AgoraGlobals.glob.uppedNull;
        for (var current = init; current >= term; current = current - step) {
            theVarCont.write(Up.glob.up(current));
            result = doblock.eval(locCont);
        }
        return result;
    }

    /**
     * Implements the IFTRUE: reifier message in the given context. The expression argument
     * is evaluated if the receiving expression evaluates to the true object.
     *
     * @param context  The context where the reifier was sent.
     * @param thenPart The expression to be evaluated when the receiving expression
     *                 evaluates to true.
     * @return The return value of this expression
     * @throws agora.errors.AgoraError When something goes wrong during evaluation.
     */
    @Reified
    public AgoraObject ifTrue(Context context, @Keyword("IFTRUE:") Expression thenPart) throws AgoraError {
        if (this.evalAsBoolean(context))
            return thenPart.eval(context);
        else
            return AgoraGlobals.glob.uppedNull;
    }

    /**
     * Implements the IFFALSE: reifier message. The argument expression is evaluated in the given context
     * whenever the receiving expression evaluates to 'false'.
     *
     * @param context  The context where the reifier was sent.
     * @param thenPart The expression to be evaluated when the receiver evaluates to false.
     * @return The value of this agora expression.
     * @throws agora.errors.AgoraError When something goes wrong during evaluation.
     */
    @Reified
    public AgoraObject ifFalse(Context context, @Keyword("IFFALSE:") Expression thenPart) throws AgoraError {
        if (!this.evalAsBoolean(context))
            return thenPart.eval(context);
        else
            return AgoraGlobals.glob.uppedNull;
    }

    /**
     * See ifTrue and ifFalse
     *
     * @throws agora.errors.AgoraError When something goes wrong during evaluation.
     */
    @Reified
    public AgoraObject ifTrueifFalse(
            Context context,
            @Keyword("IFTRUE:") Expression thenPart,
            @Keyword("IFFALSE:") Expression elsePart
    ) throws AgoraError {
        if (evalAsBoolean(context))
            return thenPart.eval(context);
        else
            return elsePart.eval(context);
    }

    /**
     * See ifTrue and ifFalse
     *
     * @throws agora.errors.AgoraError When something goes wrong during evaluation.
     */
    @Reified
    public AgoraObject ifFalseifTrue(
            Context context,
            @Keyword("IFFALSE:") Expression thenPart,
            @Keyword("IFTRUE:") Expression elsePart
    ) throws AgoraError {
        if (!this.evalAsBoolean(context))
            return thenPart.eval(context);
        else
            return elsePart.eval(context);
    }

    /**
     * Contains the code for the WHILETRUE: reifier message. The receiving expression
     * will be evaluated as long as its evaluation yields the true object. Whenever this is
     * the case, the argument expression is evaluated in the given context.
     *
     * @param context The context where the reifier was sent.
     * @param body    the expression to be evaluated.
     * @throws agora.errors.AgoraError When something goes wrong during evaluation.
     */
    @Reified
    public AgoraObject whileTrue(Context context, @Keyword("WHILETRUE:") Expression body) throws AgoraError {
        var result = AgoraGlobals.glob.uppedNull;
        while (this.evalAsBoolean(context))
            result = body.eval(context);
        return result;
    }

    /**
     * See whiletrue
     *
     * @throws agora.errors.AgoraError When something goes wrong during evaluation.
     */
    @Reified
    public AgoraObject whileFalse(Context context, @Keyword("WHILEFALSE:") Expression body) throws AgoraError {
        var result = AgoraGlobals.glob.uppedNull;
        while (!this.evalAsBoolean(context))
            result = body.eval(context);
        return result;
    }

    /**
     * The receiving expression will be evaluated until the evaluation of the argument expression
     * yields the true object.
     *
     * @param context The context where this reifier was sent.
     * @param testExp The expression that will evaluate to a boolean value.
     * @return The Agora value of this expression.
     * @throws agora.errors.AgoraError When something went wrong during the evaluation cycle.
     */
    @Reified
    public AgoraObject untilTrue(Context context, @Keyword("UNTILTRUE:") Expression testExp) throws AgoraError {
        var result = AgoraGlobals.glob.uppedNull;
        do {
            result = this.eval(context);
        } while (!testExp.evalAsBoolean(context));
        return result;
    }

    /**
     * See untilTrue
     *
     * @throws agora.errors.AgoraError When something goes wrong during evaluation.
     */
    @Reified
    public AgoraObject untilFalse(Context context, @Keyword("UNTILFALSE") Expression testExp) throws AgoraError {
        do {
            this.eval(context);
        } while (testExp.evalAsBoolean(context));
        return AgoraGlobals.glob.uppedNull;
    }

    /**
     * Implementation of the COMMENT reifier. Ignores the receiving expression.
     *
     * @param context The context of evaluation.
     * @return Always returns null.
     */
    @Unary("COMMENT")
    @Reified
    public AgoraObject comment(Context context) {
        return AgoraGlobals.glob.uppedNull;
    }

    /**
     * Implementation of the TRY:CATCH: reifier message. The receiver is evaluated, but if it throws
     * an AgoraException, this is caught and then the catchcode argument is evaluated in a new context:
     * the given context extended with a new binding of formals to the actuals in the 'patter' argument.
     *
     * @param context   The context of sending of this reifier message.
     * @param pattern   The formal pattern that this try-catch matches.
     * @param catchcode The expression to be evaluated when the handler matches.
     * @return The Agora value of this expression.
     * @throws agora.errors.AgoraError When something goes wrong, or when the exception does not match.
     */
    @Reified
    public AgoraObject trycatch(
            Context context,
            @Keyword("TRY:") Expression pattern,
            @Keyword("CATCH:") Expression catchcode
    ) throws AgoraError {
        try {
            return this.eval(context.setException(new AgoraException(catchcode)));
        } catch (AgoraException ex) {
            if (pattern instanceof UserPattern pat) {
                var formalPattern = pat.makePattern(context);
                var actualPattern = ex.getPattern();
                if (formalPattern.equals(actualPattern)) {
                    var formals = pat.makeFormals(context);
                    var actuals = ex.getClient();
                    var theAtt = new MethAttribute(formals, catchcode);
                    return theAtt.doAttributeValue(actualPattern, actuals, context);
                } else
                    throw ex;
            } else
                throw (new ProgramError("TRY:xxx CATCH: pattern is not a valid pattern"));
        } catch (AgoraError ex) {
            if (pattern instanceof UserPattern pat) {
                var agoError = new KeywordPattern();
                agoError.atPut(0, "agoraError:");
                var formalPattern = pat.makePattern(context);
                if (formalPattern.equals(agoError)) {
                    var formals = pat.makeFormals(context);
                    var args = new Object[1];
                    args[0] = Up.glob.up(ex);
                    var actuals = context.newClient(args);
                    var theAtt = new MethAttribute(formals, catchcode);
                    return theAtt.doAttributeValue(agoError, actuals, context);
                }
                throw ex;
            } else
                throw (new ProgramError("TRY:XXX CATCH: pattern is not a valid pattern"));
        }
    }

    /**
     * Implements the JAVA reifier. Raises an AgoraException with the receiving pattern as content.
     *
     * @param context The context of evaluation when this reifier was sent.
     * @return The class object corresponding to the receiving string.
     * @throws agora.errors.AgoraError When something goes wrong (e.g. when the class doesn't exist).
     */
    @Unary("JAVA")
    @Reified
    public AgoraObject java(Context context) throws AgoraError {
        var res = this.evalAsString(context);
        try {
            return Up.glob.up(Class.forName(res));
        } catch (ClassNotFoundException e) {
            throw (new ProgramError("No Such Class : " + res));
        }
    }

    /**
     * Implements the QUOTE reifier. The receiving expression will be returned as an Agora object.
     *
     * @param context The context when this reifier was sent.
     * @return The Upped receiving expression (as Agora value)
     * @throws agora.errors.AgoraError When something goes wrong.
     */
    @Unary("QUOTE")
    @Reified
    public AgoraObject quote(Context context) throws AgoraError {
        return Up.glob.up(this);
    }

    /**
     * Implements the UNQUOTE reifier. The receiving expression is evaluated and the resulting Agora
     * object is considered as an expression. This expression is then evaluated again.
     *
     * @param context The calling context of this reifier message.
     * @return The result of evaluating the receiver denoting an upped expression object.
     * @throws agora.errors.AgoraError When something goes wrong during evaluation.
     */
    @Unary("UNQUOTE")
    @Reified
    public AgoraObject unquote(Context context) throws AgoraError {
        var code = this.eval(context).down();
        if (code instanceof Expression)
            return ((Expression) code).eval(context);
        else
            throw (new ProgramError("UNQUOTE can only be sent to a quoted expression"));
    }

    /**
     * Implements the UP reifier. Evaluates the receiving expression and returns the result
     * as a meta object.
     *
     * @param context The context where the reifier was sent.
     * @return The receiver evaluated, and then upped as a referable Agora object.
     * @throws agora.errors.AgoraError When something goes wrong when evaluating the receiving expression.
     */
    @Unary("UP")
    @Reified
    public AgoraObject up(Context context) throws AgoraError {
        return Up.glob.up(this.eval(context));
    }

    /**
     * Implements the DOWN: reifier. The receiving expression is evaluated and considered as
     * a meta object (if possible, otherwise an error is thrown). This meta object is brought
     * back to the base level.
     *
     * @param context The context where the reifier was sent.
     * @return The resulting Agora object of downing the evaluated receiving expression.
     * @throws agora.errors.AgoraError When something goes wrong (e.g. evaluation error or illegal
     *                                 object to down).
     */
    @Unary("down")
    @Reified
    public AgoraObject down(Context context) throws AgoraError {
        var res = this.eval(context).down();
        if (res instanceof AgoraObject)
            return (AgoraObject) res;
        else
            throw (new ProgramError("DOWN must yield a valid Agora Object"));
    }

    /**
     * Implements the REIFIER:IS: reifier message. This will install a new reifier method in the
     * self of the context.
     *
     * @param context          The context where this reifier was sent.
     * @param contextParameter The formal argument to which the agora.runtime context will be bound.
     * @param bodyparameter    The body expression of the new reifier.
     * @return The return value of the REIFIER:IS: expression (null).
     * @throws agora.errors.AgoraError When the reifier cannot be installed or when wrong
     *                                 agora.patterns are specified.
     */
    @Reified
    public AgoraObject reifierIs(Context context,
                                 @Keyword("REIFIER:") Expression contextParameter,
                                 @Keyword("IS:") Expression bodyparameter) throws AgoraError {
        var leftside = (FormalsAndPattern) this.eval(context.setCat(Category.flags)).down();
        var thePattern = leftside.pattern;
        var theCat = leftside.cat;
        var formals = leftside.formals;
        // Validate Pattern
        if (!(Category.containsLessThan(theCat, Category.local | Category.publik)))
            throw (new ReifierMisused("Illegal Adjectives Used With REIFIER:IS:"));
        if (!(thePattern.isReifier()))
            throw (new ReifierMisused("REIFIER:IS: can only be sent to a reifier pattern."));
        if (!(contextParameter instanceof UserUnaryPattern))
            throw (new ReifierMisused("Context parameter of REIFIER:IS: should be an ordinary identifier"));
        var contextNamePattern
                = (UnaryPattern) ((FormalsAndPattern) contextParameter.eval(context.setCat(Category.flags)).down()).pattern;
        // Fill In Default Values
        if ((!Category.contains(theCat, Category.local)) &&
                (!Category.contains(theCat, Category.publik)))
            theCat = theCat | Category.publik;
        // Install method in the appropriate object part(s)
        Attribute methAtt = new ReifierMethodAttribute(formals, bodyparameter, contextNamePattern);
        if (Category.contains(theCat, Category.publik)) {
            context.getPub().installPattern(thePattern, methAtt);
        }
        if (Category.contains(theCat, Category.local)) {
            context.getPrivate().installPattern(thePattern, methAtt);
        }
        return AgoraGlobals.glob.uppedNull;
    }

    /**
     * An auxiliary method used to evaluate the receiving expression as an integer object.
     * If not, an AgoraError is thrown.
     *
     * @param context The context in which the expression has to be evaluted.
     * @return The integer corresponding to the receiving expression.
     * @throws agora.errors.AgoraError Is thrown when the receiver does not evaluate to an integer.
     */
    public int evalAsInteger(Context context) throws AgoraError {
        var resValue = this.eval(context);
        int res;
        if (resValue.down() instanceof Integer)
            res = (Integer) resValue.down();
        else
            throw (new ProgramError("Integer Expected !"));
        return res;
    }

    /**
     * See evalAsInteger
     *
     * @throws agora.errors.AgoraError When the receiver can not be evaluated to a boolean.
     */
    public boolean evalAsBoolean(Context context) throws AgoraError {
        var resValue = this.eval(context);
        boolean res;
        if (resValue.down() instanceof Boolean)
            res = (Boolean) resValue.down();
        else
            throw (new ProgramError("Boolean Expected !"));
        return res;
    }

    /**
     * See evalAsInteger
     *
     * @throws agora.errors.AgoraError When the receiver can not be evaluated to a string.
     */
    public String evalAsString(Context context) throws AgoraError {
        var resValue = (AgoraObject) this.eval(context);
        if (!(resValue.down() instanceof String))
            throw (new ProgramError("String Expected !"));
        return (String) resValue.down();
    }
}
