package net.marcellperger.mathexpr.util.rs;

import net.marcellperger.mathexpr.MiniMock.*;
import net.marcellperger.mathexpr.util.rs.Option.Some;
import net.marcellperger.mathexpr.util.rs.Option.None;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OptionTest {
    Some<Integer> getSome() {
        return Option.newSome(314);
    }
    None<Integer> getNone() {
        return Option.newNone();
    }

    @Test
    void isSome() {
        assertTrue(getSome().isSome());
        assertFalse(getNone().isSome());
    }

    @Test
    void isNone() {
        assertFalse(getSome().isNone());
        assertTrue(getNone().isNone());
    }

    @Test
    void isSomeAnd() {
        MockedPredicate<Integer> mfFalse = new MockedPredicate<>(false);
        MockedPredicate<Integer> mfTrue = new MockedPredicate<>(true);
        {
            assertTrue(getSome().isSomeAnd(mfTrue));
            mfTrue.assertCalledOnceWith(314);
            assertFalse(getSome().isSomeAnd(mfFalse));
            mfFalse.assertCalledOnceWith(314);
        }
        mfTrue.reset();
        mfFalse.reset();
        {
            assertFalse(getNone().isSomeAnd(mfTrue));
            mfTrue.assertNotCalled();
            assertFalse(getNone().isSomeAnd(mfFalse));
            mfFalse.assertNotCalled();
        }
    }

    @Test
    void map() {
        MockedFunction<Integer, Integer> mfAdd1 = new MockedFunction<>(i -> i + 1);
        assertEquals(Option.newSome(7), Option.newSome(6).map(mfAdd1));
        mfAdd1.assertCalledOnceWith(6);
        mfAdd1.reset();
        assertEquals(Option.newNone(), Option.<Integer>newNone().map(mfAdd1));
        mfAdd1.assertNotCalled();
    }

    @Test
    void mapOr() {
        MockedFunction<Integer, Integer> mfAdd1 = new MockedFunction<>(i -> i + 1);
        assertEquals(7, Option.newSome(6).mapOr(-1, mfAdd1));
        mfAdd1.assertCalledOnceWith(6);
        mfAdd1.reset();
        assertEquals(-1, Option.<Integer>newNone().mapOr(-1, mfAdd1));
        mfAdd1.assertNotCalled();
    }

    @Test
    void mapOrElse() {
        MockedSupplier<Integer> mSupplier = new MockedSupplier<>(-6);
        MockedFunction<Integer, Integer> mfAdd1 = new MockedFunction<>(i -> i + 1);
        {
            assertEquals(7, Option.newSome(6).mapOrElse(mSupplier, mfAdd1));
            mfAdd1.assertCalledOnceWith(6);
            mSupplier.assertNotCalled();
        }
        mfAdd1.reset();
        mSupplier.reset();
        {
            assertEquals(-6, Option.<Integer>newNone().mapOrElse(mSupplier, mfAdd1));
            mfAdd1.assertNotCalled();
            mSupplier.assertCalledOnce();
        }
    }

    @Test
    void mapErr() {
        MockedRunnable mRunnable = new MockedRunnable();
        assertEquals(getNone(), getNone().mapErr(mRunnable));
        mRunnable.assertCalledOnce();
        mRunnable.reset();
        assertEquals(getSome(), getSome().mapErr(mRunnable));
        mRunnable.assertNotCalled();
    }

    @Test
    void ifThenElse() {
        MockedSupplier<Integer> mSupplier = new MockedSupplier<>(-6);
        MockedFunction<Integer, Integer> mfAdd1 = new MockedFunction<>(i -> i + 1);
        {
            assertEquals(7, Option.newSome(6).ifThenElse(mfAdd1, mSupplier));
            mfAdd1.assertCalledOnceWith(6);
            mSupplier.assertNotCalled();
        }
        mfAdd1.reset();
        mSupplier.reset();
        {
            assertEquals(-6, Option.<Integer>newNone().ifThenElse(mfAdd1, mSupplier));
            mfAdd1.assertNotCalled();
            mSupplier.assertCalledOnce();
        }
    }

    @Test
    void ifThenElse_void() {
        MockedConsumer<Integer> mCons = new MockedConsumer<>();
        MockedRunnable mRunnable = new MockedRunnable();
        {
            getSome().ifThenElse_void(mCons, mRunnable);
            mCons.assertCalledOnceWith(314);
            mRunnable.assertNotCalled();
        }
        mCons.reset();
        mRunnable.reset();
        {
            getNone().ifThenElse_void(mCons, mRunnable);
            mCons.assertNotCalled();
            mRunnable.assertCalledOnce();
        }
    }

    @Test
    void inspect() {
        MockedConsumer<Integer> intCons = new MockedConsumer<>();
        assertEquals(getSome(), getSome().inspect(intCons));
        intCons.assertCalledOnceWith(314);
        intCons.reset();
        assertEquals(getNone(), getNone().inspect(intCons));
        intCons.assertNotCalled();
    }

    @Test
    void inspectErr() {
        MockedRunnable mRunnable = new MockedRunnable();
        assertEquals(getNone(), getNone().inspectErr(mRunnable));
        mRunnable.assertCalledOnce();
        mRunnable.reset();
        assertEquals(getSome(), getSome().inspectErr(mRunnable));
        mRunnable.assertNotCalled();
    }

    @Test
    void stream() {
        assertEquals(List.of(314), getSome().stream().toList());
        assertEquals(List.of(), getNone().stream().toList());
    }

    @Test
    void iterator() {
        {
            Iterator<Integer> oks = getSome().iterator();
            assertTrue(oks.hasNext());
            assertEquals(314, oks.next());
            assertFalse(oks.hasNext());
        }
        {
            List<Integer> ls = new ArrayList<>();
            getSome().iterator().forEachRemaining(ls::add);
            assertEquals(List.of(314), ls);
        }
        {
            Iterator<Integer> oks = getNone().iterator();
            assertFalse(oks.hasNext());
        }
        {
            List<Integer> ls = new ArrayList<>();
            getNone().iterator().forEachRemaining(ls::add);
            assertEquals(List.of(), ls);
        }
    }

    @Test
    void forEach() {
        MockedConsumer<Integer> intCons = new MockedConsumer<>();
        {
            getSome().forEach(intCons);
            intCons.assertCalledOnceWith(314);
        }
        intCons.reset();
        {
            getNone().forEach(intCons);
            intCons.assertNotCalled();
        }
    }

    @Test
    void unwrapOr() {
        assertEquals(314, getSome().unwrapOr(-1));
        assertEquals(-1, getNone().unwrapOr(-1));
    }

    @Test
    void unwrapOrElse() {
        MockedSupplier<Integer> ms = new MockedSupplier<>(271);
        {
            assertEquals(314, getSome().unwrapOrElse(ms));
            ms.assertNotCalled();
        }
        ms.reset();
        {
            assertEquals(271, getNone().unwrapOrElse(ms));
            ms.assertCalledOnce();
        }
    }

    @Test
    void expect() {
        assertEquals(314, assertDoesNotThrow(() -> getSome().expect("EXPECT_MSG")));
        Option<Integer> n = getNone();
        OptionPanicException exc = assertThrows(OptionPanicException.class, () -> n.expect("EXPECT_MSG"));
        assertEquals("EXPECT_MSG", exc.getMessage());
        assertNull(exc.getCause(), "Expected no cause for Option.expect()");
    }

    @Test
    void unwrap() {
        assertEquals(314, assertDoesNotThrow(() -> getSome().unwrap()));
        OptionPanicException exc = assertThrows(OptionPanicException.class, getNone()::unwrap);
        assertEquals("Option.unwrap() got None value", exc.getMessage());
        assertNull(exc.getCause(), "Expected no cause for Option.unwrap()");
    }

    @Test
    void okOr() {
        assertEquals(Result.newOk(314), getSome().okOr("ERR_ARG"));
        assertEquals(Result.newErr("ERR_ARG"), getNone().okOr("ERR_ARG"));
    }

    @Test
    void okOrElse() {
        MockedSupplier<String> ms = new MockedSupplier<>("ERR_FN_RETURN");
        {
            assertEquals(Result.newOk(314), getSome().okOrElse(ms));
            ms.assertNotCalled();
        }
        ms.reset();
        {
            assertEquals(Result.newErr("ERR_FN_RETURN"), getNone().okOrElse(ms));
            ms.assertCalledOnce();
        }
    }

    @Test
    void and() {
        assertEquals(Option.newSome(91L), getSome().and(Option.newSome(91L)));
        assertEquals(Option.newNone(), getSome().and(Option.newNone()));
        assertEquals(Option.newNone(), getNone().and(Option.newSome(91L)));
        assertEquals(Option.newNone(), getNone().and(getNone()));
    }

    @Test
    void andThen() {
        MockedFunction<Integer, Option<Long>> mfOk = new MockedFunction<>(Option.newSome(91L));
        MockedFunction<Integer, Option<Long>> mfErr = new MockedFunction<>(Option.newNone());
        {
            assertEquals(Option.newSome(91L), getSome().andThen(mfOk));
            mfOk.assertCalledOnceWith(314);
            assertEquals(Option.newNone(), getSome().andThen(mfErr));
            mfErr.assertCalledOnceWith(314);
        }
        mfOk.reset();
        mfErr.reset();
        {
            assertEquals(Option.newNone(), getNone().andThen(mfOk));
            mfOk.assertNotCalled();
            assertEquals(Option.newNone(), getNone().andThen(mfErr));
            mfErr.assertNotCalled();
        }
    }

    @Test
    void filter() {
        MockedPredicate<Integer> mTrue = new MockedPredicate<>(true);
        MockedPredicate<Integer> mFalse = new MockedPredicate<>(false);
        {
            assertEquals(getSome(), getSome().filter(mTrue));
            mTrue.assertCalledOnceWith(314);
            assertEquals(getNone(), getSome().filter(mFalse));
            mFalse.assertCalledOnceWith(314);
        }
        mTrue.reset();
        mFalse.reset();
        {
            assertEquals(getNone(), getNone().filter(mTrue));
            mTrue.assertNotCalled();
            assertEquals(getNone(), getNone().filter(mFalse));
            mFalse.assertNotCalled();
        }
    }

    @Test
    void or() {
        assertEquals(getSome(), getSome().or(Option.newSome(271)));
        assertEquals(getSome(), getSome().or(getNone()));
        assertEquals(getSome(), getNone().or(getSome()));
        assertEquals(getNone(), getNone().or(getNone()));
    }

    @Test
    void orElse() {
        MockedSupplier<Option<Integer>> mSome = new MockedSupplier<>(Option.newSome(271));
        MockedSupplier<Option<Integer>> mNone = new MockedSupplier<>(Option.newNone());
        {
            assertEquals(getSome(), getSome().orElse(mSome));
            mSome.assertNotCalled();
            assertEquals(getSome(), getSome().orElse(mNone));
            mNone.assertNotCalled();
        }
        mSome.reset();
        mNone.reset();
        {
            assertEquals(Option.newSome(271), getNone().orElse(mSome));
            mSome.assertCalledOnce();
            assertEquals(Option.newNone(), getNone().orElse(mNone));
            mNone.assertCalledOnce();
        }
    }

    @Test
    void xor() {
        assertEquals(getNone(), getSome().xor(Option.newSome(271)));
        assertEquals(getSome(), getSome().xor(getNone()));
        assertEquals(getSome(), getNone().xor(getSome()));
        assertEquals(getNone(), getNone().xor(getNone()));
    }

    @Test
    void test_toString() {
        assertEquals("Some(314)", getSome().toString());
        assertEquals("None", getNone().toString());
    }
}