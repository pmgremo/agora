package agora.grammar.combi;

public class SkipParser {
    private final Parser<?> skipper;

    public SkipParser(Parser<?> skipper) {
        this.skipper = skipper;
    }

    public <U> Parser<U> then(Parser<U> parser) {
        return x -> {
            var skipped = skipper.parse(x);
            return skipped instanceof Result.Failure<?> f ? skipped.context().failure(f.reason()) : parser.parse(skipped.context());
        };
    }
}
