package net.marcellperger.mathexpr.util.rs;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// Can't make `Throwable`s generic because java...
public class ResultPanicWithValueException extends ResultPanicException {
    protected @Nullable Object value;

    /// To set the cause, use `.builder()`
    @SuppressWarnings("unused")
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

    @SuppressWarnings("unused")
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
        return new Builder().msg(msg).value(value).build();
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
        protected @Nullable Object m_value;

        public Builder() {
            super();
            m_value = null;
        }

        @Override
        public Builder msg(@Nullable String msg) {
            // I wish there was a better way (e.g. a Self) type - could actually do
            // CRTP here but that could get messy for users of the base type
            super.msg(msg);
            return this;
        }

        @Override
        public Builder cause(@Nullable Throwable cause) {
            super.cause(cause);
            return this;
        }

        @Contract("_ -> this")
        public Builder value(Object value) {
            this.m_value = value;
            return this;
        }

        @Override
        public ResultPanicWithValueException build() {
            ResultPanicWithValueException ret = new ResultPanicWithValueException(super.build());
            ret.setValue(m_value);
            return ret;
        }
    }
}
