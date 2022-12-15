package agora.grammar.combi;

import agora.grammar.combi.Result.Failure;
import agora.grammar.combi.Result.Success;

public record Context(String buffer, int position) {
    public <T> Result<T> success(int location, T result) {
        return new Success<>(new Context(buffer, location), result);
    }

    public <T> Result<T> success(T result){
        return success(position, result);
    }

    public <T> Result<T> failure(String reason) {
        return new Failure<>(new Context(buffer, position), reason);
    }
}
