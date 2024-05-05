package net.marcellperger.mathexpr.util.rs;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ResultPanicException extends RuntimeException {
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

        public ResultPanicException build() {
            // This complication is required to ensure that initCause can be set later if not set now.
            return msg != null
                ? cause != null ? new ResultPanicException(msg, cause) : new ResultPanicException(msg)
                : cause != null ? new ResultPanicException(cause) : new ResultPanicException();
        }
    }
}
