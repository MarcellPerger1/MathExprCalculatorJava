package net.marcellperger.mathexpr.util.rs;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class ResultPanicException extends PanicException {
    public ResultPanicException() {
    }

    public ResultPanicException(String message) {
        super(message);
    }

    public ResultPanicException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResultPanicException(Throwable cause) {
        super(cause);
    }

    @SuppressWarnings("unused")  // may be used later
    @Contract(value = " -> new", pure = true)
    public static @NotNull Builder builder() {
        return new Builder();
    }

    public static class Builder extends PanicException.Builder {
        @Override
        public ResultPanicException build() {
            return build(ResultPanicException::new, ResultPanicException::new,
                ResultPanicException::new, ResultPanicException::new);
        }
    }
}
