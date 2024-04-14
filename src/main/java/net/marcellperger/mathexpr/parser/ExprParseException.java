package net.marcellperger.mathexpr.parser;


public class ExprParseException extends Exception {
    public ExprParseException() {
        super();
    }

    public ExprParseException(String message) {
        super(message);
    }

    public ExprParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExprParseException(Throwable cause) {
        super(cause);
    }
}
