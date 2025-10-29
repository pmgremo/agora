package agora.grammar.combi;


import agora.grammar.combi.Result.Failure;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import static agora.grammar.combi.Parsers.whiteSpace;
import static java.util.stream.Collectors.joining;

@FunctionalInterface
public interface Parser<T> {

    Result<T> parse(Context context);

    default Result<T> parse(String input) {
        return parse(new Context(input, 0));
    }

    default <B> Parser<B> map(Function<T, B> function) {
        return x -> {
            var result = parse(x);
            var context = result.context();
            return result instanceof Failure<T> f ? context.failure(f.reason()) : context.success(function.apply(result.get()));
        };
    }

    default Parser<T> or(Parser<T> other) {
        return or(() -> other);
    }

    default Parser<T> or(Supplier<Parser<T>> other) {
        return x -> {
            var result = parse(x);
            return result instanceof Failure<T> ? other.get().parse(x) : result;
        };
    }

    default SequenceParser<T> star(Parser<?> separator) {
        return x -> {
            var result = new LinkedList<T>();
            var next = parse(x);
            if (next instanceof Result.Failure<T> f) return f.context().success(result);
            result.add(next.get());
            while (true) {
                var sep = separator.parse(next.context());
                if (sep instanceof Result.Failure<?>) return sep.context().success(result);
                next = parse(sep.context());
                if (next instanceof Result.Failure<T> f) return next.context().failure(f.reason());
                result.add(next.get());
            }
        };
    }

    default SequenceParser<T> star() {
        return x -> {
            var current = x;
            var result = new LinkedList<T>();
            Result<T> next;
            while (true) {
                next = parse(current);
                if (next instanceof Result.Failure<T>) break;
                result.add(next.get());
                current = next.context();
            }
            return next.context().success(result);
        };
    }

    default SequenceParser<T> plus(Parser<?> separator) {
        return x -> {
            var result = new LinkedList<T>();
            var next = parse(x);
            if (next instanceof Failure<T>(Context context, String reason)) return context.failure(reason);
            result.add(next.get());
            while (true) {
                var sep = separator.parse(next.context());
                if (sep instanceof Result.Failure<?>) return sep.context().success(result);
                next = parse(sep.context());
                if (next instanceof Result.Failure<T> f) return next.context().failure(f.reason());
                result.add(next.get());
            }
        };
    }

    default SequenceParser<T> plus() {
        return x -> {
            var result = new LinkedList<T>();
            var next = parse(x);
            if (next instanceof Failure<T>(Context context, String reason)) return context.failure(reason);
            result.add(next.get());
            while (true) {
                next = parse(next.context());
                if (next instanceof Result.Failure<T>) break;
                result.add(next.get());
            }
            return next.context().success(result);
        };
    }

    @SuppressWarnings("unchecked")
    default SequenceParser<T> seq(Parser<T> parser, Parser<T>... others) {
        var seq = Parsers.seq(parser, others);
        return x -> {
            var current = parse(x);
            return current instanceof Result.Failure<T> f ? x.failure(f.reason()) : seq.parse(current.context());
        };
    }

    default Parser<T> skip(Parser<?> skipper) {
        return x -> {
            var value = parse(x);
            if (value instanceof Result.Failure<T>) return value;
            var skipped = skipper.parse(value.context());
            var context = skipped.context();
            return skipped instanceof Result.Failure<?> f ? context.failure(f.reason()) : context.success(value.get());
        };
    }

    default Parser<T> end() {
        return Parsers.seq(this, Parsers.end()).pick(0);
    }

    default Parser<String> flatten() {
        return map(Parser::flatten);
    }

    private static String flatten(Object x) {
        return switch (x) {
            case Collection<?> i -> i.stream().map(Parser::flatten).collect(joining());
            case Cell<?, ?> c -> flatten(c.first()) + flatten(c.second());
            case String s -> s;
            default -> Objects.toString(x, null);
        };
    }

    default Parser<T> not() {
        return x -> parse(x) instanceof Result.Failure<T> ?
                x.success(null) :
                x.failure("undefined message");
    }

    default Parser<T> trim() {
        var trim = whiteSpace().star();
        return Parsers.skip(trim).then(this).skip(trim);
    }

    default <U> Parser<Cell<T, U>> then(Parser<U> other) {
        return x -> {
            var a = parse(x);
            if (a instanceof Result.Failure<T> fa) return a.context().failure(fa.reason());
            var b = other.parse(a.context());
            if (b instanceof Result.Failure<U> fb) return b.context().failure(fb.reason());
            return b.context().success(new Cell<>(a.get(), b.get()));
        };
    }

    default Parser<T> optional(){
        return x -> parse(x) instanceof Result.Success<T> s ? s : x.success(null);
    }
}
