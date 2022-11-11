package agora;

import java.util.Hashtable;

public interface Copyable<T> {
    /**
     To copy an attribute in a given clone map. The clone map takes care an attribute is copied only
     once. Must be overriden.
     @param copy A table of already-copied-things such that nothing is copied twice.
     @return A copy of the attribute.
     */
    default T copy(Hashtable<Object, Object> copy) {
        return (T) this;
    }
}
