package agora.grammar.combi;

public record SkipParser(Parser<?> skipper) {

    public <U> Parser<U> then(Parser<U> parser) {
        return x -> {
            var skipped = skipper.parse(x);
            return skipped instanceof Result.Failure<?> f ? skipped.context().failure(f.reason()) : parser.parse(skipped.context());
        };
    }
}
