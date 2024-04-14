package net.marcellperger.mathexpr.util;

public class CollectionSizeException extends RuntimeException {
    public CollectionSizeException() {
    }

    public CollectionSizeException(String message) {
        super(message);
    }

    public CollectionSizeException(String message, Throwable cause) {
        super(message, cause);
    }

    public CollectionSizeException(Throwable cause) {
        super(cause);
    }
}
