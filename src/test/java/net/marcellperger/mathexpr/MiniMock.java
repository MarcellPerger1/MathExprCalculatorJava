package net.marcellperger.mathexpr;

import net.marcellperger.mathexpr.util.VoidVal;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.junit.jupiter.api.Assertions;

public class MiniMock {
    enum ReturnStrategy {
        VALUE, FUNC
    }

    public static class BaseMockedCallable<A> {
        List<A> calls;

        protected void handleCall(A args) {
            calls.add(args);
        }

        public void assertCalledOnce() {
            Assertions.assertEquals(1, calls.size(), "Expected function to be called exactly once.");
        }
        public void assertCalledOnceWith(A args) {
            assertCalledOnce();
            Assertions.assertEquals(args, calls.getFirst(), "Expected function to called with these args");
        }

        public void assertNotCalled() {
            Assertions.assertEquals(0, calls.size(), "Expected function not to be called.");
        }

        public void reset() {
            calls.clear();
        }
    }

    // TODO there is SO much shared code between these Mocked* classes
    public static class MockedSupplier<R> extends BaseMockedCallable<VoidVal> implements Supplier<R> {
        // Option<T> would be so useful right now
        R returnValue;
        Supplier<R> returnSupplier;
        ReturnStrategy returnStrategy;

        public MockedSupplier(R returnValue_) {
            calls = new ZST_List();
            returnValue = returnValue_;
            returnStrategy = ReturnStrategy.VALUE;
        }
        public MockedSupplier(Supplier<R> returnSupplier_) {
            calls = new ZST_List();
            returnSupplier = returnSupplier_;
            returnStrategy = ReturnStrategy.FUNC;
        }

        protected R getReturn() {
            return switch (returnStrategy) {
                case VALUE -> returnValue;
                case FUNC -> returnSupplier.get();
            };
        }

        @Override
        public R get() {
            handleCall(VoidVal.val());
            return getReturn();
        }
    }

    public static class MockedFunction<T, R> extends BaseMockedCallable<T> implements Function<T, R> {
        // Option<T> would be so useful right now
        R returnValue;
        Function<? super T, ? extends R> returnFunc;
        ReturnStrategy returnStrategy;

        public MockedFunction(R returnValue_) {
            calls = new ArrayList<>();
            returnValue = returnValue_;
            returnStrategy = ReturnStrategy.VALUE;
        }
        public MockedFunction(Function<? super T, ? extends R> returnSupplier_) {
            calls = new ArrayList<>();
            returnFunc = returnSupplier_;
            returnStrategy = ReturnStrategy.FUNC;
        }

        protected R getReturn(T arg) {
            return switch (returnStrategy) {
                case VALUE -> returnValue;
                case FUNC -> returnFunc.apply(arg);
            };
        }

        @Override
        public R apply(T arg) {
            handleCall(arg);
            return getReturn(arg);
        }
    }

    public static class MockedPredicate<T> extends BaseMockedCallable<T> implements Predicate<T> {
        // Option<T> would be so useful right now
        boolean returnValue;
        Predicate<? super T> returnFunc;
        ReturnStrategy returnStrategy;

        public MockedPredicate(boolean returnValue_) {
            calls = new ArrayList<>();
            returnValue = returnValue_;
            returnStrategy = ReturnStrategy.VALUE;
        }
        public MockedPredicate(Predicate<? super T> returnSupplier_) {
            calls = new ArrayList<>();
            returnFunc = returnSupplier_;
            returnStrategy = ReturnStrategy.FUNC;
        }

        protected boolean getReturn(T arg) {
            return switch (returnStrategy) {
                case VALUE -> returnValue;
                case FUNC -> returnFunc.test(arg);
            };
        }

        @Override
        public boolean test(T arg) {
            handleCall(arg);
            return getReturn(arg);
        }
    }

    public static class MockedConsumer<T> extends BaseMockedCallable<T> implements Consumer<T> {
        public MockedConsumer() {
            calls = new ArrayList<>();
        }

        @Override
        public void accept(T arg) {
            handleCall(arg);
        }
    }

    // oof, this List interface is monstrously big, with very few defaults!
    static class ZST_List implements List<VoidVal> {
        private int m_size = 0;  // I wish this could be long but size() requires int

        public ZST_List() {}
        public ZST_List(int size) { m_size = size; }
        @SuppressWarnings("unused")
        @Contract(pure = true)
        public ZST_List(@NotNull Collection<VoidVal> c) {
            m_size = c.size();
        }


        private void wantInBounds(int index) {
            if(index < 0 || index >= m_size) throw new IndexOutOfBoundsException(index);
        }
        private void wantInBounds_insert(int index) {
            if(index < 0 || index > m_size) throw new IndexOutOfBoundsException(index);
        }

        @Contract(" -> new")
        private @NotNull @Unmodifiable List<VoidVal> immutableImpl() {
            return Collections.nCopies(m_size, VoidVal.val());
        }

        @Override
        public int size() {
            return m_size;
        }

        @Override
        public boolean isEmpty() {
            return m_size == 0;
        }

        @Override
        public boolean contains(Object o) {
            return o == VoidVal.val();  // we contain all VoidVal and nothing else
        }

        @NotNull
        @Override
        public Iterator<VoidVal> iterator() {
            return immutableImpl().iterator();
        }

        @NotNull
        @Override
        public Object @NotNull [] toArray() {
            return immutableImpl().toArray();
        }

        @NotNull
        @Override
        public <T> T @NotNull [] toArray(@NotNull T @NotNull [] a) {
            return immutableImpl().toArray(a);
        }

        @Override
        public boolean add(VoidVal voidVal) {
            ++m_size;
            return true;
        }

        @Override
        public boolean remove(Object o) {
            if(m_size <= 0) return false;
            --m_size;
            return true;
        }

        @Override
        public boolean containsAll(@NotNull Collection<?> c) {
            return m_size != 0 && c.stream().allMatch(o -> o == VoidVal.val());
        }

        @Override
        public boolean addAll(@NotNull Collection<? extends VoidVal> c) {
            if(c.stream().anyMatch(o -> o != VoidVal.val())) throw new ClassCastException("ZST_List can only contain VoidVal");
            m_size += c.size();
            return true;
        }

        @Override
        public boolean addAll(int index, @NotNull Collection<? extends VoidVal> c) {
            wantInBounds_insert(index);
            return addAll(c);
        }

        @Override
        public boolean removeAll(@NotNull Collection<?> c) {
            boolean removeValues = c.contains(VoidVal.val());
            if(removeValues && m_size != 0) {
                m_size = 0;
                return true;
            }
            return false;
        }

        @Override
        public boolean retainAll(@NotNull Collection<?> c) {
            boolean retainValues = c.contains(VoidVal.val());
            if (retainValues || m_size == 0) {
                return false;
            }
            m_size = 0;
            return true;
        }

        @Override
        public void clear() {
            m_size = 0;
        }

        @Override
        public VoidVal get(int index) {
            wantInBounds(index);
            return VoidVal.val();
        }

        @Override
        public VoidVal set(int index, VoidVal element) {
            wantInBounds(index);
            if(element != VoidVal.val()) throw new ClassCastException("ZST_List can only contain VoidVal");
            return VoidVal.val();
        }

        @Override
        public void add(int index, VoidVal element) {
            wantInBounds_insert(index);
            ++m_size;
        }

        @Override
        public VoidVal remove(int index) {
            wantInBounds(index);
            --m_size;
            return VoidVal.val();
        }

        @Override
        public int indexOf(Object o) {
            if(m_size == 0) return -1;
            return o == VoidVal.val() ? 0 : -1;
        }

        @Override
        public int lastIndexOf(Object o) {
            if(m_size == 0) return -1;
            return o == VoidVal.val() ? m_size - 1 : -1;
        }

        // TODO Technically I should make a custom class for this and .iterator()
        //  as modification IS allowed but I'll do that later.
        @NotNull
        @Override
        public ListIterator<VoidVal> listIterator() {
            return immutableImpl().listIterator();
        }
        @NotNull
        @Override
        public ListIterator<VoidVal> listIterator(int index) {
            return immutableImpl().listIterator(index);
        }

        @NotNull
        @Override
        public List<VoidVal> subList(int fromIndex, int toIndex) {
            // We just return a copy, because, for us, there ARE no
            // 'non-structural' changes as only the count matters and there is
            // only one possible value for each place so the only way to modify it is
            return new ZST_List(toIndex - fromIndex);
        }
    }
}
