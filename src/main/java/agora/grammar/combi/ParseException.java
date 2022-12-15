package agora.grammar.combi;

public class ParseException extends RuntimeException {
    private final Context context;

    public ParseException(Context context, String reason) {
        super(reason);
        this.context = context;
    }

    public Context context() {
        return context;
    }
}
