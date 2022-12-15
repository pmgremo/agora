package agora.grammar.combi;

import java.util.List;

public interface SequenceParser<T> extends Parser<List<T>> {

    default Parser<T> pick(int index) {
        return map(x -> x.get(index));
    }
}
