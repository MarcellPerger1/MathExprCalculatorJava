package net.marcellperger.mathexpr.util.rs;

import net.marcellperger.mathexpr.util.Util;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;


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
    // hashCode / equals is automatically implemented for these record types, but we customize toString
    record Ok<T, E>(T value) implements Result<T, E> {
        public<E2> Ok<T, E2> cast() { return new Ok<>(value); }

        @Override
        public String toString() {
            return "Ok(" + value + ')';
        }
    }

    record Err<T, E>(E exc) implements Result<T, E> {
        public<R2> Err<R2, E> cast() { return new Err<>(exc); }

        @Override
        public String toString() {
            return "Err("  + exc + ')';
        }
    }

    // same as new Ok/Err but return Option
    static <T, E> Result<T, E> newOk(T value) {
        return new Ok<>(value);
    }
    static <T, E> Result<T, E> newErr(E err) {
        return new Err<>(err);
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

    // not a Rust function. Similar to mapOrElse but reversed arguments
    default void ifThenElse_void(Consumer<? super T> okFn, Consumer<? super E> errFn) {
        // This _void variant exists (instead of an overload) to avoid Java complaining
        // about ambiguous arguments when passing method references
        mapOrElse(Util.consumerToFunction(errFn), Util.consumerToFunction(okFn));
    }
    default <U> U ifThenElse(Function<? super T, ? extends U> okFn, Function<? super E, ? extends U> errFn) {
        return mapOrElse(errFn, okFn);
    }

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
        return unwrapOrElse((err) -> {
            throw ResultPanicWithValueException.fromMaybeExcValue(err, msg);
        });
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
    default E expectErr(String msg) {
        return ifThenElse(ok -> {
            throw ResultPanicWithValueException.fromPlainValue(ok, msg);
        }, Function.identity());
    }
    default E unwrapErr() {
        return expectErr("unwrapErr() got Ok value");
    }
    // We can't implement into_ok/into_err as Java doesn't have that
    // kind of infallible type and I don't think Java can be that rigorous about
    // infallibility (Java's type system in proven to be unsound so...)
    // or can't even reason about it in the first place.

    default <U> Result<U, E> and(Result<U, E> other) {
        return andThen((_ok) -> other);
    }
    default <U> Result<U, E> andThen(Function<? super T, ? extends Result<U, E>> then) {
        return switch (this) {
            case Ok(T value) -> then.apply(value);  // everything normal with `this`, do next
            case Err<T, E> err -> err.cast();
        };
    }

    default <E2> Result<T, E2> or(Result<T, E2> other) {
        return orElse((_err) -> other);
    }
    default <E2> Result<T, E2> orElse(Function<? super E, ? extends Result<T, E2>> elseFn) {
        return switch (this) {
            case Ok<T, E> ok -> ok.cast();
            case Err(E err) -> elseFn.apply(err);  // `this` failed so try `elseFn` - "You're my only hope"
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
}
