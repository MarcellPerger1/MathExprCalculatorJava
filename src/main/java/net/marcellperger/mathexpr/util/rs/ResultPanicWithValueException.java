package net.marcellperger.mathexpr.util.rs;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// Can't make `Throwable`s generic because java...
public class ResultPanicWithValueException extends ResultPanicException {
    protected @Nullable Object value;

    /// To set the cause, use `.builder()`
    public ResultPanicWithValueException() {
    }

    public ResultPanicWithValueException(String message) {
        super(message);
    }

    public ResultPanicWithValueException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResultPanicWithValueException(String message, Throwable cause, @Nullable Object value) {
        super(message, cause);
        this.value = value;
    }

    public ResultPanicWithValueException(Throwable cause) {
        super(cause);
    }

    protected ResultPanicWithValueException(@NotNull ResultPanicException parent) {
        this(parent.getMessage(), parent.getCause());
    }

    public static @NotNull ResultPanicWithValueException fromMaybeExcValue(@Nullable Object value, String msg) {
        return switch (value) {
            case null -> new ResultPanicWithValueException(msg);
            case Throwable excValue -> new ResultPanicWithValueException(msg, excValue, excValue);
            default -> new ResultPanicWithValueException(msg, null, value);
        };
    }

    public static @NotNull ResultPanicWithValueException fromPlainValue(@Nullable Object value, String msg) {
        ResultPanicWithValueException res = new ResultPanicWithValueException(msg);
        res.setValue(value);
        return res;
    }

    @Override
    public String getMessage() {
        return switch (super.getMessage()) {
            case null -> value != null ? "Panic value: " + value : "";
            case "" -> value != null ? "Panic value: " + value : "";
            case String msg -> msg + (value == null ? "" : ": " + value);
        };
    }

    public @Nullable Object getValue() {
        return value;
    }
    public void setValue(@Nullable Object value) {
        this.value = value;
    }

    public static class Builder extends ResultPanicException.Builder {
        protected @Nullable Object value;

        public Builder() {
            super();
            value = null;
        }

        @Contract("_ -> this")
        public Builder value(Object value) {
            this.value = value;
            return this;
        }

        @Override
        public ResultPanicWithValueException build() {
            ResultPanicWithValueException ret = new ResultPanicWithValueException(super.build());
            ret.setValue(value);
            return ret;
        }
    }
}
