package net.marcellperger.mathexpr.parser;

public class ExprParseRtException extends RuntimeException {
    public ExprParseRtException() {
        super();
    }

    public ExprParseRtException(String message) {
        super(message);
    }

    public ExprParseRtException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExprParseRtException(Throwable cause) {
        super(cause);
    }
}
