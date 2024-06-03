package net.marcellperger.mathexpr.parser;

public class ExprParseEofException extends ExprParseException {
    public ExprParseEofException() {
    }

    public ExprParseEofException(String message) {
        super(message);
    }

    public ExprParseEofException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExprParseEofException(Throwable cause) {
        super(cause);
    }
}
