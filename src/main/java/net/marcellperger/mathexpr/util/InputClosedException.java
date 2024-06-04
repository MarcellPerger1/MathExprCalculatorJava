package net.marcellperger.mathexpr.util;

import java.util.NoSuchElementException;

public class InputClosedException extends NoSuchElementException {
    public InputClosedException() {
    }

    public InputClosedException(String s, Throwable cause) {
        super(s, cause);
    }

    public InputClosedException(Throwable cause) {
        super(cause);
    }

    public InputClosedException(String s) {
        super(s);
    }
}
