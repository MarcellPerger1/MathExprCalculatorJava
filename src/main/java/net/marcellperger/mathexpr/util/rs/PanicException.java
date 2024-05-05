package net.marcellperger.mathexpr.util.rs;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

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

    @SuppressWarnings("unused")  // may be used later
    @Contract(value = " -> new", pure = true)
    public static @NotNull Builder builder() {
        return new Builder();
    }

    public static class Builder {
        protected @Nullable String msg;
        protected @Nullable Throwable cause;

        protected Builder() {
            msg = null;
            cause = null;
        }

        @Contract("_ -> this")
        public Builder msg(@Nullable String msg) {
            this.msg = msg;
            return this;
        }

        @Contract("_ -> this")
        public Builder cause(@Nullable Throwable cause) {
            this.cause = cause;
            return this;
        }

        public PanicException build() {
            return build(PanicException::new, PanicException::new, PanicException::new, PanicException::new);
        }

        public <T extends PanicException> T build(Supplier<T> ctor0, Function<String, T> ctor1str,
                                                  Function<? super Throwable, T> ctor1exc,
                                                  BiFunction<String, ? super Throwable, T> ctor2) {
            return msg != null
                ? cause != null ? ctor2.apply(msg, cause) : ctor1str.apply(msg)
                : cause != null ? ctor1exc.apply(cause) : ctor0.get();
        }
    }
}
