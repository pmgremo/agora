package agora.reflection;

import agora.attributes.*;
import agora.errors.AgoraError;
import agora.javaAdditions.*;
import agora.objects.AgoraObject;
import agora.objects.PrimGenerator;
import agora.objects.PrimIdentityGenerator;
import agora.patterns.*;
import agora.tools.AgoraGlobals;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.LinkedList;

import static java.lang.reflect.Modifier.*;

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
     * This private variable caches the generators created by Up, such that all the work
     * to 'up' a Java implementation level object is done only once. The table maps
     * Java class names onto generators that contain the method tables for the
     * associated class.
     */
    private final Hashtable<String, PrimGenerator> cache = new Hashtable<>(30);

    private final AgoraObject nil;

    public Up() {
        nil = up(JV_Nil.instance);
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
     *                                 a generatorcreator might be invoked that accesses a Java method that does not exist.
     */
    public AgoraObject up(Object o) throws AgoraError {
        if (o == null) return nil;

        var result = o instanceof Class<?> c ?
                new PrimIdentityGenerator(c.getSimpleName(), generatorFor(c, false), o) :
                new PrimIdentityGenerator(Object.class.getSimpleName(), generatorFor(o.getClass(), true), o);
        return result.wrap();
    }

    /**
     * This private procedure generators a generator (i.e. a lookup table that understands 'delegate')
     * for a Java implementation level class. It is called by 'Up' with the class of the object.
     * It checks whether the method table is already in the cache. If so, it is simply returned.
     * Otherwise, the method table is constructed and put in the cache for the next usage. The second
     * argument indicates whether object attributes (apart from class attributes like constructors
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
        if (!isInstance) name += " CLASS";

        var generator = cache.get(name);
        if (generator != null) return generator;

        generator = buildGenerator(type);

        if (generator == null)
            generator = constructGeneratorFor(type, isInstance);

        cache.put(name, generator);

        if (Object.class.equals(type)) {
            generator.setParent(AgoraGlobals.glob.rootIdentity);
            generator.installPattern(
                    new UnaryPattern("primitive"),
                    new VarGetAttribute(new VariableContainer(up(true)))
            );
            return generator;
        }

        generator.setParent(createGeneratorFor(superType, superType.getSuperclass(), isInstance));

        return generator;
    }

    private static PrimGenerator buildGenerator(Class<?> type) {
        var table = new Hashtable<Pattern, Attribute>(5);
        for (var x : type.getDeclaredMethods()) {
            var reified = x.getAnnotation(Reified.class);
            var attribute = isStatic(x.getModifiers()) ? new PrimFunctionAttribute(x) : new PrimMethAttribute(x);
            {
                var annotation = x.getAnnotation(Operator.class);
                if (annotation != null) {
                    for (var name : annotation.value()) {
                        table.put(
                                reified == null ? new OperatorPattern(name) : new OperatorReifierPattern(name),
                                attribute
                        );
                    }
                }
            }
            {
                var annotation = x.getAnnotation(Unary.class);
                if (annotation != null) {
                    for (var name : annotation.value()) {
                        table.put(
                                reified == null ? new UnaryPattern(name) : new UnaryReifierPattern(name),
                                attribute
                        );
                    }
                }
            }
            {
                var words = new LinkedList<String>();
                for (var parameter : x.getParameters()) {
                    var annotation = parameter.getAnnotation(Keyword.class);
                    if (annotation != null) words.add(annotation.value());
                }
                if (words.size() > 0) {
                    table.put(
                            reified == null ? new KeywordPattern(words) : new KeywordReifierPattern(words),
                            attribute
                    );
                }
            }
        }
        if (table.isEmpty()) return null;
        var frame = type.getAnnotation(Frame.class);
        var name = frame != null ? frame.value() : type.getSimpleName();
        return new PrimGenerator(name, table);
    }

    /**
     * This procedure is called by 'createGeneratorFor' that recursively walks through a hierarchy.
     * The procedure creates a generator for a single class. It is called by 'createGeneratorFor'
     * for every class in the hierarchy.
     */
    private PrimGenerator constructGeneratorFor(Class<?> c, boolean isInstance) throws AgoraError {
        var theTable = new Hashtable<Pattern, Attribute>();
        putFieldsInQueue(c, theTable, isInstance);                       // Insert patterns and fields
        putMethodsInQueue(c, theTable, isInstance);                      // Insert patterns and methods
        putConstructorsInQueue(c, theTable, isInstance);                 // Insert patterns and constructors
        return new PrimGenerator(c.getName(), theTable);    // Create a new generator with the members
    }

    /**
     * This procedure reads all the fields (i.e. data members) of a class.
     * It creates appropriate read and write Agora attributes and puts them
     * all in a queue.
     */
    private void putFieldsInQueue(Class<?> c, Hashtable<Pattern, Attribute> q, boolean isInstance) { // Create a pattern and an attribute for every publically accessible field
        for (var field : c.getDeclaredFields()) {
            if (!isPublic(field.getModifiers()) ||
                    isAbstract(field.getModifiers()) ||
                    isInterface(field.getModifiers()) ||
                    !(isInstance | isStatic(field.getModifiers()))) // not isInstance -> isStatic
            {
                continue;
            }
            q.put(createVariableReadPatFor(field), createVariableReadAttFor(field));
            if (!isFinal(field.getModifiers())) { // Only a write pattern if the field is non-final (i.e. not constant)
                q.put(createVariableWritePatFor(field), createVariableWriteAttFor(field));
            }
        }
    }

    /**
     * This procedure reads all the methods in a class. It creates an appropriate
     * Agora attribute and puts a pattern for the methods together with the attribute in
     * a queue.
     */
    private void putMethodsInQueue(Class<?> c, Hashtable<Pattern, Attribute> q, boolean isInstance) { // Create a pattern and a method attribute for every publically accessible method
        for (var method : c.getDeclaredMethods()) {
            if (!isPublic(method.getModifiers()) ||
                    isAbstract(method.getModifiers()) ||
                    isInterface(method.getModifiers()) ||
                    !(isInstance | isStatic(method.getModifiers()))) // not isInstance -> isStatic
            {
                continue;
            }
            q.put(createMethodPatFor(method), createMethodAttFor(method));
        }
    }

    /**
     * This procedure reads all the constructors in a class. For each constructor,
     * a pattern 'new' is created and the appropriate Agora attribute is constructed.
     * All the patterns and the attributes are gathered together in a queue.
     */
    private void putConstructorsInQueue(Class<?> c, Hashtable<Pattern, Attribute> q, boolean isInstance) { // Create a pattern and a cloning method for every publically accessible constructor
        for (var constructor : c.getDeclaredConstructors()) {
            if (!isPublic(constructor.getModifiers()) ||
                    isNative(constructor.getModifiers()) ||
                    isAbstract(constructor.getModifiers()) ||
                    isInterface(constructor.getModifiers()) ||
                    isInstance) {
                continue;
            }
            q.put(createConstructorPatFor(constructor), createConstructorAttFor(constructor));
        }
    }

    /**
     * Creates a Variable Read pattern for a field f.
     */
    private Pattern createVariableReadPatFor(Field f) {
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
    private Pattern createVariableWritePatFor(Field f) {
        return KeywordPattern.keywordPattern(decaps(f.getName()) + ":");
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
    private Pattern createMethodPatFor(Method m) {
        var types = m.getParameterTypes();
        if (types.length == 0) return new UnaryPattern(m.getName());
        var words = new LinkedList<String>();
        words.add(decaps(m.getName()) + typeNameFor(types[0]) + ":");
        for (var j = 1; j < types.length; j++)
            words.add(typeNameFor(types[j]) + ":");
        return new KeywordPattern(words);
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
    private Pattern createConstructorPatFor(Constructor<?> c) {
        var types = c.getParameterTypes();
        if (types.length == 0) return new UnaryPattern("new");
        var words = new LinkedList<String>();
        words.add("new" + typeNameFor(types[0]) + ":");
        for (var j = 1; j < types.length; j++)
            words.add(typeNameFor(types[j]) + ":");
        return new KeywordPattern(words);
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

