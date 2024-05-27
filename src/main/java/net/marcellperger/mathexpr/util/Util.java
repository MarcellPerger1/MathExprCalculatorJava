package net.marcellperger.mathexpr.util;


import org.intellij.lang.annotations.Flow;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

@SuppressWarnings("unused")
public class Util {
    protected Util() {}

    public static <R, T> @Nullable R chainNulls(@Nullable T value, Function<? super T, ? extends R> func) {
        return value == null ? null : func.apply(value);
    }

    public static <R, T1, T0> @Nullable R chainNulls(@Nullable T0 value,
                                                     Function<? super T0, ? extends T1> f0,
                                                     Function<? super T1, ? extends R> f1) {
        // could optimise this `return v0 == null ? null : (v1=f0.apply(v0)) == null ? null : v1`
        return chainNulls(chainNulls(value, f0), f1);
    }

    public static <R, T2, T1, T0> @Nullable R chainNulls(@Nullable T0 value,
                                                         Function<? super T0, ? extends T1> f0,
                                                         Function<? super T1, ? extends T2> f1,
                                                         Function<? super T2, ? extends R> f2) {
        return chainNulls(chainNulls(value, f0, f1), f2);
    }

    public static <R, T3, T2, T1, T0> @Nullable R chainNulls(@Nullable T0 value,
                                                             Function<? super T0, ? extends T1> f0,
                                                             Function<? super T1, ? extends T2> f1,
                                                             Function<? super T2, ? extends T3> f2,
                                                             Function<? super T3, ? extends R> f3) {
        return chainNulls(chainNulls(value, f0, f1, f2), f3);
    }

    public static <R, T4, T3, T2, T1, T0> @Nullable R chainNulls(@Nullable T0 value,
                                                                 Function<? super T0, ? extends T1> f0,
                                                                 Function<? super T1, ? extends T2> f1,
                                                                 Function<? super T2, ? extends T3> f2,
                                                                 Function<? super T3, ? extends T4> f3,
                                                                 Function<? super T4, ? extends R> f4) {
        return chainNulls(chainNulls(value, f0, f1, f2, f3), f4);
    }

    /** This exists solely to be able to provide a custom error type */
    @Contract(value = "null, _ -> fail; !null, _ -> param1", pure = true)
    public static <T> @NotNull T requireNonNull(T obj, RuntimeException exc) {
        if(obj == null) throw exc;
        return obj;
    }

    @Contract("_ -> param1")
    public static <C extends Collection<?>> @NotNull C requireNonEmpty(@NotNull C collection) {
        if(collection.isEmpty()) throw new CollectionSizeException("Argument must not be empty");
        return collection;
    }
    @Contract("null -> fail; _ -> param1")
    public static <C extends Collection<?>> @NotNull C requireNonEmptyNonNull(C collection) {
        return requireNonEmpty(Objects.requireNonNull(collection));
    }
    @Contract("_,_ -> param1")
    public static <C extends Collection<?>> @NotNull C requireNonEmpty(@NotNull C collection, String msg) {
        if(collection.isEmpty()) throw new CollectionSizeException(msg);
        return collection;
    }
    @Contract("null, _ -> fail; _, _ -> param1")
    public static <C extends Collection<?>> @NotNull C requireNonEmptyNonNull(C collection, String msg) {
        return requireNonEmpty(Objects.requireNonNull(collection), msg);
    }
    @Contract("_,_ -> param1")
    public static <C extends Collection<?>> @NotNull C requireNonEmpty(@NotNull C collection, RuntimeException exc) {
        if(collection.isEmpty()) throw exc;
        return collection;
    }
    @Contract("null, _ -> fail; _, _ -> param1")
    public static <C extends Collection<?>> @NotNull C requireNonEmptyNonNull(C collection, RuntimeException exc) {
        return requireNonEmpty(Objects.requireNonNull(collection), exc);
    }

    @Contract("_, _ -> param1")
    public static<T> T expectOrFail(T value, @NotNull Predicate<T> predicate) {
        if(!predicate.test(value)) throw new AssertionError("Assertion failed in expectOrFail");
        return value;
    }
    @Contract("_, true -> param1; _, false -> fail")
    public static<T> T expectOrFail(T value, boolean cond) {
        if(!cond) throw new AssertionError("Assertion failed in expectOrFail");
        return value;
    }
    @Contract("_, true, _ -> param1; _, false, _ -> fail")
    public static<T> T expectOrFail(T value, boolean cond, RuntimeException exc) {
        if(!cond) throw exc;
        return value;
    }

    @Contract("true -> _; false -> fail")
    public static void expectOrFail(boolean cond) {
        if(!cond) throw new AssertionError("Assertion failed in expectOrFail");
    }

//    @Contract("_, _ -> param1")
//    public static<T> T expectOrFail(T value, @NotNull Predicate<T> predicate, ) {
//        if(!predicate.test(value)) throw new AssertionError("Assertion failed in expectOrFail");
//        return value;
//    }
//    @Contract("_, true -> param1; _, false -> fail")
//    public static<T> T expectOrFail(T value, boolean cond) {
//        if(!cond) throw new AssertionError("Assertion failed in expectOrFail");
//        return value;
//    }

    @Contract("_, _ -> param1")
    public static<T extends Throwable> @NotNull T withCause(@NotNull T exc, @Nullable Throwable cause) {
        if(cause != null) exc.initCause(cause);
        return exc;
    }

    @Contract("_ -> new")
    public static @NotNull IllegalAccessError excToError(@NotNull IllegalAccessException exc) {
        return withCause(new IllegalAccessError(exc.getMessage()), exc.getCause());
    }
    @Contract("_ -> new")
    public static @NotNull InstantiationError excToError(@NotNull InstantiationException exc) {
        return withCause(new InstantiationError(exc.getMessage()), exc.getCause());
    }
    @Contract("_ -> new")
    public static @NotNull NoSuchMethodError excToError(@NotNull NoSuchMethodException exc) {
        return withCause(new NoSuchMethodError(exc.getMessage()), exc.getCause());
    }
    @Contract("_ -> new")
    public static @NotNull NoSuchFieldError excToError(@NotNull NoSuchFieldException exc) {
        return withCause(new NoSuchFieldError(exc.getMessage()), exc.getCause());
    }

    @Contract("_ -> new")
    public static @NotNull UncheckedException intoUnchecked(Exception exc) {
        return new UncheckedException(exc);
    }
    @Contract("_, _ -> new")
    public static @NotNull UncheckedException intoUnchecked(Exception exc, String msg) {
        return new UncheckedException(msg, exc);
    }

    @Contract(value = "false -> fail", pure = true)
    public static void realAssert(boolean b) {
        if(!b) throw new AssertionError("Assertion failed");
    }
    @Contract(value = "false, _ -> fail", pure = true)
    public static void realAssert(boolean b, String msg) {
        if(!b) throw new AssertionError(msg);
    }
    @Contract(value = "false, _ -> fail", pure = true)
    public static void realAssert(boolean b, Throwable cause) {
        if(!b) throw new AssertionError(cause);
    }
    @Contract(value = "false, _, _ -> fail", pure = true)
    public static void realAssert(boolean b, String msg, Throwable cause) {
        if(!b) throw new AssertionError(msg, cause);
    }

    @Contract(value = "_, _ -> new", pure = true)
    public static @NotNull <K, V> Map.Entry<K, V> makeEntryMut(K k, V v) {
        return new AbstractMap.SimpleEntry<>(k, v);
    }
    @Contract(value = "_, _ -> new", pure = true)
    public static @NotNull <K, V> Map.Entry<K, V> makeEntry(K k, V v) {
        return new AbstractMap.SimpleImmutableEntry<>(k, v);
    }

    @Contract(pure = true)
    public static <K, V, R> @NotNull Function<Map.Entry<K, V>, Map.Entry<K, R>> valueMapper(Function<V, R> mapper) {
        return en -> makeEntry(en.getKey(), mapper.apply(en.getValue()));
    }

    public static<T> T getOnlyItem(@Flow(sourceIsContainer = true) @NotNull Collection<T> c) {
        if(c.size() != 1) throw new CollectionSizeException("Expected collection to have 1 item");
        return c.iterator().next();
    }
    public static<T> T getOnlyItem(@Flow(sourceIsContainer = true) @NotNull SequencedCollection<T> c) {
        if(c.size() != 1) throw new CollectionSizeException("Expected collection to have 1 item");
        return c.getFirst();
    }

    public static <K, V> @NotNull V getNotNull(@NotNull Map<K, V> map, K key) {
        return Objects.requireNonNull(map.get(key));
    }

    @Contract(value = "_ -> new", pure = true)
    public static <T> @NotNull Iterator<T> singleItemIterator(T value) {
        return new SingleItemIterator<>(value);
    }

    protected static class SingleItemIterator<T> implements Iterator<T> {
        T value;
        boolean isAtEnd;

        public SingleItemIterator(T value) {
            this.value = value;
            isAtEnd = false;
        }

        @Override
        public boolean hasNext() {
            return !isAtEnd;
        }

        @Override
        public T next() {
            if(isAtEnd) throw new NoSuchElementException("End of SingleItemIterator reached");
            return _moveToEndAndPop();
        }

        protected T _moveToEndAndPop() {
            isAtEnd = true;
            T val = value;
            // Don't hold on to our copy - we don't need it anymore
            //  as iterators cannot go backwards and this lets JVM free it earlier.
            value = null;
            return val;
        }

        @Override
        public void forEachRemaining(Consumer<? super T> action) {
            if(isAtEnd) return;
            action.accept(_moveToEndAndPop());
        }
    }

    @Contract(pure = true)
    public static <T> @NotNull Function<? super T, VoidVal> consumerToFunction(Consumer<? super T> consumer) {
        return v -> {
            consumer.accept(v);
            return VoidVal.val();
        };
    }
    @Contract(pure = true)
    public static <T> @NotNull UnaryOperator<T> consumerToIdentityFunc(Consumer<? super T> consumer) {
        return v -> {
            consumer.accept(v);
            return v;
        };
    }

    public static @NotNull Supplier<VoidVal> runnableToSupplier(Runnable runnable) {
        return () -> {
            runnable.run();
            return VoidVal.val();
        };
    }

    // These 2 trick Java into throwing checked exceptions in an unchecked way
    @Contract("_ -> fail")
    public static <T> T throwAsUnchecked(Throwable exc) {
        throwAs(exc);  // <RuntimeException> is inferred from no `throws` clause
        // Java doesn't know that this always throws so this lets us
        // do `return/throw throwAsUnchecked()` to make Java's flow control analyser happy
        throw new AssertionError("Unreachable");
    }
    @SuppressWarnings("unchecked")
    @Contract("_ -> fail")
    public static <E extends Throwable> void throwAs(Throwable exc) throws E {
        // We do a little type erasure hack to trick Java:
        //  - E will be type-erased to Throwable so this will become
        //       throw (Throwable)exc;
        //     but exc is already Throwable due to the param type so
        //     this is like a runtime no-op.
        //  - But javac will see that we're throwing an E which is allowed!
        //  - The reason we need a separate method is so that there is a
        //     generic for javac to type-erase (otherwise JVM would check that
        //     it's actually that concrete type when it is thrown and this way
        //     it only checks for the base condition)
        throw (E)exc;
    }
}
