package agora.attributes;

import agora.Copyable;
import agora.objects.AgoraObject;

import java.io.Serializable;
import java.util.Hashtable;

/**
 * When a variable is declared, a read and write slot are created. These two agora.attributes must point to
 * the same structure such that they stay consistent. That's why they both point to a container like this.
 * Last change:  E    16 Nov 97    1:57 am
 */
public class VariableContainer implements Serializable, Copyable<VariableContainer> {
    protected AgoraObject variableValue;

    /**
     * Create a new container with the argument as initial value. This value must always be a valid
     * AgoraObject.
     *
     * @param value The AgoraObject with which the variable container is initialised.
     */
    public VariableContainer(AgoraObject value) {
        this.variableValue = value;
    }

    /**
     * Method to write the variable container.
     *
     * @param value The new value of the container.
     */
    public void write(AgoraObject value) {
        this.variableValue = value;
    }

    /**
     * Method to read the variable container.
     *
     * @return The value held by the container.
     */
    public AgoraObject read() {
        return (this.variableValue);
    }

    /**
     * Copying the variable container consists of looking it up in the cloning map.
     * If it is not there, it is really copied. Otherwise the copy is returned.
     *
     * @param cache A table of already-copied-things such that nothing gets copied twice.
     * @return A copy of this attribute.
     */
    public VariableContainer copy(Hashtable<Object, Object> cache) {
        var exist = (VariableContainer) cache.get(this);
        if (exist != null) return exist;
        var result = new VariableContainer(this.variableValue);
        cache.put(this, result);
        return result;
    }

}
