package agora;

import java.util.Map;

public interface Copyable {
    /**
     * To clone an attribute in a given clone map. The clone map takes care an attribute is copied only
     * once. Must be overriden.
     *
     * @param cache A table of already-copied-things such that nothing is copied twice.
     * @return A cache of the attribute.
     */
    default Object copy(Map<Object, Object> cache) {
        return this;
    }
}
