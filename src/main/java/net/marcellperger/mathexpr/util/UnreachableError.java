package net.marcellperger.mathexpr.util;

public class UnreachableError extends AssertionError {
    public UnreachableError() {
        super("Unreachable code reached");
    }

    public UnreachableError(String message, Throwable cause) {
        super(message, cause);
    }

    public UnreachableError(Object detailMessage) {
        super(detailMessage);
    }
}
