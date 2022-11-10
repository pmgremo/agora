package agora;

import java.util.Hashtable;

public interface Copyable<T> {
    default T copy(Hashtable copy) {
        return (T) this;
    }
}
