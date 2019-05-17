public class RuntimeError extends RuntimeException {
    final String context;
    final int line;

    RuntimeError(String message, String context) {
        super(message);
        this.context = context;
        this.line = -1;
    }

    RuntimeError(String message, String context, int line) {
        super(message);
        this.context = context;
        this.line = line;
    }
}
