package net.marcellperger.mathexpr.util.rs;

import net.marcellperger.mathexpr.util.Util;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public sealed interface Option<T> extends Iterable<T> {
    // hashCode, equals are automatically implemented for `record`s
    record Some<T>(T value) implements Option<T> {
        @Contract(pure = true)
        @Override
        public @NotNull String toString() {
            return "Some(" + value + ')';
        }
    }

    record None<T>() implements Option<T> {
        @Contract(" -> new")
        public <U> @NotNull None<U> cast() {
            return new None<>();
        }

        @Contract(pure = true)
        @Override
        public @NotNull String toString() {
            return "None";
        }
    }

    // Same as new Some/None but return Option
    @Contract("_ -> new")
    static <T> @NotNull Option<T> newSome(T value) {
        return new Some<>(value);
    }

    @Contract(" -> new")
    static <T> @NotNull Option<T> newNone() {
        return new None<>();
    }

    default boolean isSome() {
        return this instanceof Some<T>;
    }

    default boolean isNone() {
        return this instanceof None<T>;
    }

    default boolean isSomeAnd(Predicate<? super T> predicate) {
        return switch (this) {
            case None() -> false;
            case Some(T value) -> predicate.test(value);
        };
    }
    // I don't think we need as_slice because .stream().toList() / .collect()
    //  and there are already easy ways of Option -> Collection using .map etc.

    default <U> Option<U> map(Function<? super T, ? extends U> fn) {
        return switch (this) {
            case Some(T value) -> new Some<>(fn.apply(value));
            case None<T> n -> n.cast();
        };
    }
    default <U> U mapOr(U default_, Function<? super T, ? extends U> fn) {
        return mapOrElse(() -> default_, fn);
    }
    default <U> U mapOrElse(Supplier<? extends U> defaultFn, Function<? super T, ? extends U> fn) {
        return switch (this) {
            case Some(T value) -> fn.apply(value);
            case None() -> defaultFn.get();
        };
    }
    // there is nothing to map from or to (None -> None)! so this is Runnable
    // so this is the same as inspectErr. It's here for consistency with Result
    default Option<T> mapErr(Runnable noneFn) {
        if(isNone()) noneFn.run();
        return this;
    }

    // not Rust functions, but provides more logical argument order
    // (if/else vs else/if) than mapOrElse
    default void ifThenElse_void(Consumer<? super T> someFn, Runnable noneFn) {
        mapOrElse(Util.runnableToSupplier(noneFn), Util.consumerToFunction(someFn));
    }
    default <U> U ifThenElse(Function<? super T, ? extends U> someFn, Supplier<? extends U> noneFn) {
        return mapOrElse(noneFn, someFn);
    }

    default Option<T> inspect(Consumer<? super T> fn) {
        return map(Util.consumerToIdentityFunc(fn));
    }
    // not in Rust but could be useful (same as mapErr)
    default Option<T> inspectErr(Runnable noneFn) {
        if(isNone()) noneFn.run();
        return this;
    }

    // .iter()-esque methods: why does Java have SO MANY - one is enough.
    default Stream<T> stream() {
        return mapOrElse(Stream::empty, Stream::of);
    }
    // Iterable automatically implements `spliterator()` for us
    @NotNull
    @Override
    default Iterator<T> iterator() {
        return switch (this) {
            case Some(T value) -> Util.singleItemIterator(value);
            case None() -> Collections.emptyIterator();
        };
    }
    @Override
    default void forEach(Consumer<? super T> action) {
        map(Util.consumerToFunction(action));
    }

    default T unwrapOr(T default_) {
        return unwrapOrElse(() -> default_);
    }
    default T unwrapOrElse(Supplier<? extends T> ifNone) {
        return mapOrElse(ifNone, Function.identity());
    }
    // no unwrap_or_default (see Result.java for big explanation)
    default T expect(String msg) {
        return unwrapOrElse(() -> {
            throw new OptionPanicException(msg);
        });
    }
    default T unwrap() {
        return expect("Option.unwrap() got None value");
    }

    default <E> Result<T, E> okOr(E err) {
        return ifThenElse(Result::newOk, () -> Result.newErr(err));
    }
    default <E> Result<T, E> okOrElse(Supplier<? extends E> errSupplier) {
        return ifThenElse(Result::newOk, () -> Result.newErr(errSupplier.get()));
    }

    default <U> Option<U> and(Option<U> right) {
        return andThen((_v) -> right);
    }
    default <U> Option<U> andThen(Function<? super T, ? extends Option<U>> fn) {
        return switch (this) {
            case Some(T value) -> fn.apply(value);
            case None<T> n -> n.cast();
        };
    }

    default Option<T> or(Option<T> right) {
        return orElse(() -> right);
    }
    default Option<T> orElse(Supplier<? extends Option<T>> orFn) {
        return switch (this) {
            case Some<T> s -> s;
            case None() -> orFn.get();
        };
    }

    default Option<T> filter(Predicate<? super T> predicate) {
        return switch (this) {
            case Some(T value) -> predicate.test(value) ? this : newNone();
            case None<T> n -> n;
        };
    }

    default Option<T> xor(Option<T> right) {
        return switch (this) {  // I wish this implementation was more elegant
            case Some<T> left -> right.isNone() ? left : newNone();
            case None() -> right;
        };
    }

    // no insert / get_or_insert(_*) / take(_if) / replace
    //  (as record is immutable in Java)
    // no (un)zip(_with) because no tuples  in Java (will do if needed tho)
    // rest are `impl`s on compound types which Java can't do
    //  (see Result.java for detailed explanation), can do static method if we need it so much
}
