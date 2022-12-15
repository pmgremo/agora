package agora.grammar.combi;

public interface Result<T> {
    Context context();

    T get();

    record Success<T>(Context context, T get) implements Result<T> {

    }

    record Failure<T>(Context context, String reason) implements Result<T> {

        @Override
        public T get() {
            throw new ParseException(context, reason);
        }

    }
}
