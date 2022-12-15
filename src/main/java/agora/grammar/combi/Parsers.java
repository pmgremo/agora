package agora.grammar.combi;

import java.util.LinkedList;
import java.util.Objects;
import java.util.function.Predicate;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;

public class Parsers {
    public static SkipParser skip(Parser<?> parser) {
        return new SkipParser(parser);
    }

    public static Parser<Character> whiteSpace() {
        return character(Character::isWhitespace, "white space expected");
    }

    public static Parser<Character> digit() {
        return character(Character::isDigit, "digit expected");
    }

    public static Parser<Character> word() {
        return character(Character::isLetterOrDigit, "letter or digit expected");
    }

    public static Parser<Character> character(Character character) {
        return character(x -> Objects.equals(x, character), character + " expected");
    }

    public static Parser<Character> character(Predicate<Character> predicate, String message) {
        return x -> {
            var position = x.position();
            var buffer = x.buffer();
            if (position >= buffer.length()) return x.failure(message);
            var target = buffer.charAt(position);
            return predicate.test(target) ? x.success(position + 1, target) : x.failure(message);
        };
    }

    public static Parser<String> string(String string) {
        return string(string, string + " expected");
    }

    public static Parser<String> string(String string, String message) {
        return x -> {
            var start = x.position();
            var buffer = x.buffer();
            var end = start + string.length();
            if (end > buffer.length()) return x.failure(message);
            var target = buffer.substring(start, end);
            return string.equals(target) ? x.success(end, target) : x.failure(message);
        };
    }

    public static Parser<Character> any(String chars) {
        return character(x -> chars.indexOf(x) > -1, "one of [" + chars + "] expected");
    }

    @SafeVarargs
    public static <T> SequenceParser<T> seq(Parser<T> parser, Parser<T>... others) {
        var parsers = new LinkedList<Parser<T>>();
        parsers.add(parser);
        parsers.addAll(asList(others));
        return x -> {
            var results = new LinkedList<T>();
            var current = x;
            for (var p : parsers) {
                var result = p.parse(current);
                if (result instanceof Result.Failure<T> f) return current.failure(f.reason());
                current = result.context();
                results.add(result.get());
            }
            return current.success(results);
        };
    }

    @SafeVarargs
    public static <T> Parser<T> choice(Parser<T> parser, Parser<T>... others) {
        return stream(others).reduce(parser, Parser::or);
    }

    public static <T> Parser<T> end() {
        return x -> x.position() < x.buffer().length() ? x.failure("end of input expected") : x.success(null);
    }
}
