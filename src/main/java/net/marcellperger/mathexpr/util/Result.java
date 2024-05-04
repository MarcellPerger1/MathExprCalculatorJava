package net.marcellperger.mathexpr.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

// TODO maybe separate (sub-)package?


/**
 * An implementation of Rust's immutable {@code Result} type that stores either:
 * <ul><li>A value of type {@link T} representing success</li>
 * <li>An error value of type {@link E} representing an error</li></ul>
 * No restrictions are placed on any of these types for convenience and flexibility.
 * @see <a href=https://doc.rust-lang.org/std/result/enum.Result.html#>Rust's <code>std::result::Result</code></a>
 * @param <T> Type of the {@link Ok} value
 * @param <E> Type of the {@link Err} error value
 */
@SuppressWarnings({"unused", "InnerClassOfInterface"})  // should be an abstract class but then the `record Ok/Err` cannot extend it
public sealed interface Result<T, E> extends Iterable<T> {
    record Ok<T, E>(T value) implements Result<T, E> {
        public<E2> Ok<T, E2> cast() { return new Ok<>(value); }
    }

    record Err<T, E>(E exc) implements Result<T, E> {
        public<R2> Err<R2, E> cast() { return new Err<>(exc); }
    }

    default @Nullable Ok<T, E> ok() {
        return switch (this) {
            case Ok<T, E> ok -> ok;
            case Err<T, E> _e -> null;
        };
    }
    default @Nullable Err<T, E> err() {
        return switch (this) {
            case Ok<T, E> _ok -> null;
            case Err<T, E> err -> err;
        };
    }

    default Optional<Ok<T, E>> okOpt() { return Optional.ofNullable(ok()); }
    default Optional<Err<T, E>> errOpt() { return Optional.ofNullable(err()); }


    default boolean isOk() { return ok() != null; }
    default boolean isErr() { return err() != null; }

    default boolean isOkAnd(Predicate<? super T> f) {
        return switch (this) {
            case Ok(T value) -> f.test(value);
            case Err<T, E> e -> false;
        };
    }
    default boolean isErrAnd(Predicate<? super E> f)  {
        return switch (this) {
            case Ok<T, E> o -> false;
            case Err(E err) -> f.test(err);
        };
    }

    // TODO: once Option is done, do proper ok() / err() -> Option<T / E>

    default <U> Result<U, E> map(Function<? super T, ? extends U> op) {
        return switch (this) {
            case Ok(T value) -> new Ok<>(op.apply(value));
            case Err<T, E> e -> e.cast();
        };
    }
    // TODO some sort of Consumer<> variant of these or a
    //  (IMO more logically named) valueOrElse / tryElse / ifLetElse / andThenElse
    default <U> U mapOr(U default_, Function<? super T, ? extends U> f) {
        return mapOrElse((_e) -> default_, f);
    }
    default <U> U mapOrElse(Function<? super E, ? extends U> defaultFn, Function<? super T, ? extends U> f) {
        return switch (this) {
            case Ok(T value) -> f.apply(value);
            case Err(E err) -> defaultFn.apply(err);
        };
    }
    default <E2> Result<T, E2> mapErr(Function<? super E, ? extends E2> op) {
        return switch (this) {
            case Ok<T, E> o -> o.cast();
            case Err(E err) -> new Err<>(op.apply(err));
        };
    }

    // TODO toString

    default Result<T, E> inspect(Consumer<? super T> f) {
        return map(Util.consumerToIdentityFunc(f));
    }
    default Result<T, E> inspectErr(Consumer<? super E> f) {
        return mapErr(Util.consumerToIdentityFunc(f));
    }

    // .iter()-esque methods: why does Java have SO MANY - one is enough.
    default Stream<T> stream() {
        return okOpt().map(Ok::value).stream();
    }
    // Iterable automatically implements `spliterator()` for us
    @NotNull
    @Override
    default Iterator<T> iterator() {
        return switch (this) {
            case Ok(T value) -> Util.singleItemIterator(value);
            case Err(E _err) -> Collections.emptyIterator();
        };
    }
    @Override
    default void forEach(Consumer<? super T> action) {
        map(Util.consumerToFunction(action));
    }

    default T unwrap() {
        return expect("unwrap() got Err value");
    }
    default T expect(String msg) {
        return okOpt().orElseThrow(() -> ResultPanicWithValueException.fromMaybeExcValue(err(), msg)).value;
    }
    // We CANNOT do unwrap_or_default because Java: <rant>
    //  - Static methods cannot be overloaded properly and cannot even be part of an interface
    //     (static methods are very much NOT first-class things in Java - unlike Rust)
    //      so the language that is the 'standard' Object-Oriented language cannot
    //      even do polymorphism for static methods! (without resorting to
    //      runtime non-compile-time-checked reflection)
    //  - My (very many) attempts to solve it using C++-style CRTP have all failed
    //     because Java handles generics using type erasure, making 1 general
    //     type-erased version of a method so any attempts to call methods on the passed subtype
    //     just results on the method being called on the type declared extended in the `<T extends ...>`.
    //     Another problematic consequence of (I think) type erasure is that a class hierarchy
    //     cannot implement the same interface with 2 different types so something
    //     cannot be both Iterable<String> and Iterable<Long>.
    //     IMO, handling generics using monomorphisation (like C++ and Rust) would solve these issues and would
    //     allow much greater flexibility and could open paths to a SFINAE-type
    //     system which would solve the first BP.
    //  - I really don't want to use reflection, because in strongly typed languages,
    //     my philosophy is "Check as much as possible at compile-time to reduce runtime failure". </rant>
    default E expect_err(String msg) {
        return errOpt().orElseThrow(() -> ResultPanicWithValueException.fromPlainValue(ok(), msg)).exc;
    }
    default E unwrap_err() {
        return expect_err("unwrap_err() got Ok value");
    }
    // We can't implement into_ok/into_err as Java doesn't have that
    // kind of infallible type and I don't think Java can be that rigorous about
    // infallibility (Java's type system in proven to be unsound so...)
    // or can't even reason about it in the first place.

    default <U> Result<U, E> and(Result<U, E> other) {
        return andThen(() -> other);
    }
    default <U> Result<U, E> andThen(Supplier<? extends Result<U, E>> then) {
        return switch (this) {
            case Ok<T, E> _ok -> then.get();  // everything normal with `this`, do next
            case Err<T, E> err -> err.cast();
        };
    }

    default <E2> Result<T, E2> or(Result<T, E2> other) {
        return orElse(() -> other);
    }
    default <E2> Result<T, E2> orElse(Supplier<? extends Result<T, E2>> elseFn) {
        return switch (this) {
            case Ok<T, E> ok -> ok.cast();
            case Err<T, E> _err -> elseFn.get();  // `this` failed so try `elseFn` - "You're my only hope"
        };
    }

    default T unwrapOr(T defaultV) {
        return mapOr(defaultV, Function.identity());
    }
    default T unwrapOrElse(Function<? super E, ? extends T> ifErr) {
        return mapOrElse(ifErr, Function.identity());
    }
    // no unwrap_unchecked/unwrap_err_unchecked for obvious reasons (usually Java != UB/unsafe)

    // Cannot have implementations for Result<Result<T, E>, E> and similar because Java
    //  has nothing that allows us to enable methods if certain conditions are met
    //  and no multiple implementation blocks with possibly-different conditions on the type parameters.

    // TODO maybe these exceptions deserve their own file...
    class ResultPanicException extends RuntimeException {
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
                return msg != null
                    ? cause != null ? new ResultPanicException(msg, cause) : new ResultPanicException(msg)
                    : cause != null ? new ResultPanicException(cause) : new ResultPanicException();
            }
        }
    }

    // Can't make `Throwable`s generic because java...
    class ResultPanicWithValueException extends ResultPanicException {
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

        protected ResultPanicWithValueException(@NotNull ResultPanicException parent) {
            this(parent.getMessage(), parent.getCause());
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
}
