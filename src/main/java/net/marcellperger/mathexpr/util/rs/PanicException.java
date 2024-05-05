package net.marcellperger.mathexpr.util.rs;

@SuppressWarnings("unused")  // I don't care, this is a **util** package, I'll use it later
public class PanicException extends RuntimeException {
    public PanicException() {
    }

    public PanicException(String message) {
        super(message);
    }

    public PanicException(String message, Throwable cause) {
        super(message, cause);
    }

    public PanicException(Throwable cause) {
        super(cause);
    }
}
