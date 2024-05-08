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

    @Contract("_ -> new")
    static <T> @NotNull Some<T> newSome(T value) {
        return new Some<>(value);
    }

    @Contract(" -> new")
    static <T> @NotNull None<T> newNone() {
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
    default Option<T> mapErr(Runnable noneFn) {  // there is nothing to map from or to (None -> None)!
        if(isNone()) noneFn.run();
        return this;
    }

    // not Rust functions, but provides more logical argument order
    // (if/else vs else/if) than mapOrElse
    default void ifThenElse(Consumer<? super T> someFn, Runnable noneFn) {
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
}
