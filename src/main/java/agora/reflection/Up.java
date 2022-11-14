package agora.reflection;

import agora.attributes.*;
import agora.errors.AgoraError;
import agora.javaAdditions.JV_Boolean;
import agora.javaAdditions.JV_Float;
import agora.javaAdditions.JV_Integer;
import agora.javaAdditions.JV_Nil;
import agora.objects.AgoraObject;
import agora.objects.PrimGenerator;
import agora.objects.PrimIdentityGenerator;
import agora.patterns.AbstractPattern;
import agora.patterns.KeywordPattern;
import agora.patterns.OperatorPattern;
import agora.patterns.UnaryPattern;
import agora.tools.AgoraGlobals;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Queue;

import static java.lang.reflect.Modifier.isStatic;

/**
 * This class consists of procedural code. Normally, the Agora design requires that
 * every implementation level object understands the message up(), but since
 * we cannot change the Object of java, we must define Up() as a procedure which
 * we can then !apply! to every implementation level object.
 *
 * @author Wolfgang De Meuter
 * Last change:  E    16 Nov 97    1:58 pm
 */
public class Up implements Serializable {

    /**
     * This is the only instance of this class. The only valid way to
     * instantiate this class is:<p>
     * Up.glob = new Up();
     */
    static public Up glob;

    /**
     * This private variable caches the generators created by Up, such that all the work
     * to 'up' a Java implementation level object is done only once. The table maps
     * Java class names onto generators that contain the method tables for the
     * associated class.
     */
    private final Hashtable<String, PrimGenerator> uptable = new Hashtable<>(30);
    private final VariableContainer primitive = new VariableContainer(null); // temporary variable

    public static PrimGenerator buildGenerator(Class<?> type) {
        var table = new Hashtable<AbstractPattern, Attribute>(5);
        for (var x : type.getDeclaredMethods()) {
            var reified = x.getAnnotation(Reified.class);
            var attribute = isStatic(x.getModifiers()) ? new PrimFunctionAttribute(x) : new PrimMethAttribute(x);
            {
                var annotation = x.getAnnotation(Operator.class);
                if (annotation != null) {
                    for (var name : annotation.value()) {
                        var pattern = new OperatorPattern(name);
                        if (reified != null) pattern.setReifier();
                        table.put(pattern, attribute);
                    }
                }
            }
            {
                var annotation = x.getAnnotation(Unary.class);
                if (annotation != null) {
                    for (var name : annotation.value()) {
                        var pattern = new UnaryPattern(name);
                        if (reified != null) pattern.setReifier();
                        table.put(pattern, attribute);
                    }
                }
            }
            {
                var pattern = new KeywordPattern();
                var parameters = x.getParameters();
                for (int i = 0; i < parameters.length; i++) {
                    var annotation = parameters[i].getAnnotation(Keyword.class);
                    if (annotation != null) pattern.atPut(i, annotation.value());
                }
                if (pattern.size() > 0) {
                    if (reified != null) pattern.setReifier();
                    table.put(pattern, attribute);
                }
            }
        }
        if (table.isEmpty()) return null;
        var frame = type.getAnnotation(Frame.class);
        var name = frame != null ? frame.value() : type.getSimpleName();
        return new PrimGenerator(name, table, null);
    }

    /**
     * This method must be called right after system startup, when the boolean
     * objects have been upped. Somewhere, the up version of the booleans define
     * a method 'primitive' that returns an upped boolean. Hence, when calling
     * Up with a boolean, this upped boolean is not yet known.
     */
    public void fixBooleanCircularity() {
        primitive.write(AgoraGlobals.glob.cachedUppedBoolean(true));
    }

    /**
     * This is a  public procedure.
     * It wraps any Java implementation level object into a corresponding Agora object.
     * It reads the fields, methods and constructors of the class of the object, and turns
     * them into an Agora method table.
     *
     * @param o The object of which the upped Agora version is needed.
     * @return The upped version of the input. This upped version understands
     * send and down.
     * @throws agora.errors.AgoraError When something goes wrong during the upping. For example,
     *                                 a generatorcreator might be invoked that accesses a Java method that does not exists.
     */
    public AgoraObject up(Object o) throws AgoraError {
        if (o == null) // If null object is to be upped, take JV_Nil (reason: in Agora, null is an object!)
        {
            return new PrimIdentityGenerator(
                    Object.class.getSimpleName(),
                    generatorFor(JV_Nil.class, true),
                    new JV_Nil()
            ).wrap();
        } else if (o instanceof Class<?> c) // Dirty type cast because Java has no classes of classes of ...
            return new PrimIdentityGenerator(c.getSimpleName(), generatorFor(c, false), o).wrap();
        else
            return new PrimIdentityGenerator(Object.class.getSimpleName(), generatorFor(o.getClass(), true), o).wrap();
    }

    /**
     * This private procedure generators a generator (i.e. a lookup table that understands 'delegate')
     * for a Java implementation level class. It is called by 'Up' with the class of the object.
     * It checks whether the method table is already in the cache. If so, it is simply returned.
     * Otherwise, the method table is constructed and put in the cache for the next usage. The second
     * argument indicates whether object agora.attributes (apart from class agora.attributes like constructors
     * and statics) should also be considered
     */
    private PrimGenerator generatorFor(Class<?> c, boolean isInstance) throws AgoraError {
        var type = c;
        var superType = c.getSuperclass();
        if (Integer.class.equals(c)) {
            type = JV_Integer.class;
            superType = c;
        } else if (Float.class.equals(c)) {
            type = JV_Float.class;
            superType = c;
        } else if (Boolean.class.equals(c)) {
            type = JV_Boolean.class;
            superType = c;
        }
        return createGeneratorFor(type, superType, isInstance);
    }

    /**
     * This procedure really creates the generator for the class by recursively
     * traversing the class hierarchy and creating (or looking up in the cache)
     * the generator for the subclasses. The generator for 'java.lang.Object' is linked
     * to the root of the Agora system.
     */
    private PrimGenerator createGeneratorFor(Class<?> type, Class<?> superType, boolean isInstance) throws AgoraError {
        var name = type.getName();
        if (!isInstance) name = name + "CLASS";

        var methodTableOfc = uptable.get(name);
        if (methodTableOfc != null) return methodTableOfc;

        methodTableOfc = buildGenerator(type);

        if (methodTableOfc == null)
            methodTableOfc = constructGeneratorFor(type, isInstance);

        if (Object.class.equals(type)) {
            methodTableOfc.setParent(AgoraGlobals.glob.rootIdentity);
            methodTableOfc.installPattern(
                    new UnaryPattern("primitive"),
                    new VarGetAttribute(primitive)
            );
            return methodTableOfc;
        }

        methodTableOfc.setParent(createGeneratorFor(superType, superType.getSuperclass(), isInstance));

        uptable.put(name, methodTableOfc);

        return methodTableOfc;
    }

    /**
     * This procedure is called by 'createGeneratorFor' that recursively walks through a hierarchy.
     * The procedure creates a generator for a single class. It is called by 'createGeneratorFor'
     * for every class in the hierarchy.
     */
    private PrimGenerator constructGeneratorFor(Class<?> c, boolean isInstance) throws AgoraError {
        var q = new LinkedList<>();  // Make a new Queue
        putFieldsInQueue(c, q, isInstance);        // Insert patterns and fields
        putMethodsInQueue(c, q, isInstance);       // Insert patterns and methods
        putConstructorsInQueue(c, q, isInstance);  // Insert patterns and constructors
        var sz = q.size() / 2;        // queue = (pat,att)(pat,att)....(pat,att)
        var theTable = new Hashtable<AbstractPattern, Attribute>(sz + 1);//The size may not be zero, so add one
        for (var j = 0; j < sz; j++) {
            var pattern = q.poll(); // These statements are really necessary because
            var attrib = q.poll(); // if we would simply write put(q.deQueue(),q.deQeueue)
            theTable.put((AbstractPattern) pattern, (Attribute) attrib); // the wrong order could be used (if Java does it right to left)
        }
        return new PrimGenerator(c.getName(), theTable, null);    // Create a new generator with the members
    }

    /**
     * This procedure reads all the fields (i.e. data members) of a class.
     * It creates appropriate read and write Agora agora.attributes and puts them
     * all in a queue.
     */
    private void putFieldsInQueue(Class<?> c, Queue<Object> q, boolean isInstance) { // Create a pattern and an attribute for every publically accessible field
        var fields = c.getFields();
        for (var field : fields) {
            if (Modifier.isPublic(field.getModifiers()) &&
                    (!Modifier.isAbstract(field.getModifiers())) &&
                    (!Modifier.isPrivate(field.getModifiers())) &&
                    (!Modifier.isProtected(field.getModifiers())) &&
                    (!Modifier.isInterface(field.getModifiers())) &&
                    (field.getDeclaringClass().equals(c)) &&
                    (isInstance | (Modifier.isStatic(field.getModifiers())))) // not isInstance -> isStatic
            {
                q.offer(createVariableReadPatFor(field));
                q.offer(createVariableReadAttFor(field));
                if (!(Modifier.isFinal(field.getModifiers()))) { // Only a write pattern if the field is non-final (i.e. not constant)
                    q.offer(createVariableWritePatFor(field));
                    q.offer(createVariableWriteAttFor(field));
                }
            }
        }
    }

    /**
     * This procedure reads all the methods in a class. It creates an appropriate
     * Agora attribute and puts a pattern for the methods together with the attribute in
     * a queue.
     */
    private void putMethodsInQueue(Class<?> c, Queue<Object> q, boolean isInstance) { // Create a pattern and a method attribute for every publically accessible method
        var methods = c.getMethods();
        for (var method : methods) {
            if (Modifier.isPublic(method.getModifiers()) &&
                    !Modifier.isAbstract(method.getModifiers()) &&
                    !Modifier.isPrivate(method.getModifiers()) &&
                    !Modifier.isProtected(method.getModifiers()) &&
                    !Modifier.isInterface(method.getModifiers()) &&
                    method.getDeclaringClass().equals(c) &&
                    isInstance | Modifier.isStatic(method.getModifiers())) // not isInstance -> isStatic
            {
                q.offer(createMethodPatFor(method));
                q.offer(createMethodAttFor(method));
            }
        }
    }

    /**
     * This procedure reads all the constructors in a class. For each constructor,
     * a pattern 'new' is created and the appropriate Agora attribute is constructed.
     * All the patterns and the attributes are gathered together in a queue.
     */
    private void putConstructorsInQueue(Class<?> c, Queue<Object> q, boolean isInstance) { // Create a pattern and a cloning method for every publically accessible constructor
        var constructors = c.getConstructors();
        for (var constructor : constructors) {
            if (Modifier.isPublic(constructor.getModifiers()) &&
                    !Modifier.isNative(constructor.getModifiers()) &&
                    !Modifier.isAbstract(constructor.getModifiers()) &&
                    !Modifier.isPrivate(constructor.getModifiers()) &&
                    !Modifier.isProtected(constructor.getModifiers()) &&
                    !Modifier.isInterface(constructor.getModifiers()) &&
                    constructor.getDeclaringClass().equals(c) &&
                    !isInstance) {
                q.offer(createConstructorPatFor(constructor));
                q.offer(createConstructorAttFor(constructor));
            }
        }
    }

    /**
     * Creates a Variable Read pattern for a field f.
     */
    private AbstractPattern createVariableReadPatFor(Field f) {
        return new UnaryPattern(decaps(f.getName()));
    }

    /**
     * Creates a variable read attribute for f.
     */
    private Attribute createVariableReadAttFor(Field f) {
        return new PrimVarGetAttribute(f);
    }

    /**
     * Creates a variable write pattern for a field f, i.e. the name of f with a colon.
     */
    private AbstractPattern createVariableWritePatFor(Field f) {
        var writePat = new KeywordPattern();
        writePat.add(decaps(f.getName()) + ":");
        return writePat;
    }

    /**
     * Creates a variable write attribute for a field f. This is only called when
     * the field is not final.
     */
    private Attribute createVariableWriteAttFor(Field f) {
        return new PrimVarSetAttribute(f);
    }

    /**
     * Creates a method pattern for a java method. Depending on the
     * signature of the Java method, a unary, an operator or a
     * keyword pattern is created.
     */
    private AbstractPattern createMethodPatFor(Method m) {
        var types = m.getParameterTypes();
        if (types.length == 0)
            return new UnaryPattern(m.getName());
        else {
            var pat = new KeywordPattern();
            pat.add(decaps(m.getName()) + typeNameFor(types[0]) + ":");
            for (var j = 1; j < types.length; j++)
                pat.add(typeNameFor(types[j]) + ":");
            return pat;
        }
    }

    /**
     * This procedure creates a method attribute for the given
     * Java implementation level method.
     */
    private Attribute createMethodAttFor(Method m) {
        return new PrimMethAttribute(m);
    }

    /**
     * This procedure creates a cloning method pattern for a given constructor.
     * Depending on the signature of the constructor, a unary pattern or a keyword
     * pattern is created.
     */
    private AbstractPattern createConstructorPatFor(Constructor<?> c) {
        var types = c.getParameterTypes();
        if (types.length == 0)
            return new UnaryPattern("new");
        else if (types.length == 1) {
            var pat = new KeywordPattern();
            pat.atPut(0, "new" + typeNameFor(types[0]) + ":");
            return pat;
        } else {
            var pat = new KeywordPattern();
            pat.atPut(0, "new" + typeNameFor(types[0]) + ":");
            for (var j = 1; j < types.length; j++)
                pat.atPut(j, typeNameFor(types[j]) + ":");
            return pat;
        }
    }

    /**
     * Creates a cloning method for the given constructor c.
     */
    private Attribute createConstructorAttFor(Constructor<?> c) {
        return new PrimCloningAttribute(c);
    }

    /**
     * Determined the unqualified, decapitalized name of a class. Array information
     * is also removed. Hence, the 'real' type name results.
     */
    private String typeNameFor(Class<?> s) {
        return decaps(unArrayed(s)); // Remove [], remove package structure, remove capitals
    }

    /**
     * Removes all the array information from a class. For example,
     * the type Vector[][] is transformed into VectorAA.
     */
    private String unArrayed(Class<?> c) {
        return c.isArray() ? unArrayed(c.getComponentType()) + "A" : c.getSimpleName();
    }

    /**
     * Transforms a string into its fully decapitalized version.
     */
    private String decaps(String s) {
        var arr = s.toCharArray();
        var allcaps = true;
        for (char c : arr) allcaps = allcaps & (c >= 'A' & c <= 'Z' | (c == '_'));
        return allcaps ? "j" + s : s;
    }
}

