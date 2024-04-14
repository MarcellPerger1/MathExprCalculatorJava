package net.marcellperger.first.util;


/**
 * A class for a general unchecked exception returned by {@link Util#}
 */
public class UncheckedException extends RuntimeException {
    public UncheckedException() {
    }

    public UncheckedException(String message) {
        super(message);
    }

    public UncheckedException(String message, Throwable cause) {
        super(message, cause);
    }

    public UncheckedException(Throwable cause) {
        super(cause);
    }
}
