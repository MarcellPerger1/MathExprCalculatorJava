package net.marcellperger.mathexpr.util.rs;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class OptionPanicException extends PanicException {
    public OptionPanicException() {
    }

    public OptionPanicException(String message) {
        super(message);
    }

    public OptionPanicException(String message, Throwable cause) {
        super(message, cause);
    }

    public OptionPanicException(Throwable cause) {
        super(cause);
    }

    @SuppressWarnings("unused")  // may be used later
    @Contract(value = " -> new", pure = true)
    public static @NotNull Builder builder() {
        return new Builder();
    }

    public static class Builder extends PanicException.Builder {
        @Override
        public OptionPanicException build() {
            return build(OptionPanicException::new, OptionPanicException::new,
                OptionPanicException::new, OptionPanicException::new);
        }
    }
}
