package net.marcellperger.mathexpr.util.rs;

import org.junit.jupiter.api.Test;

import static net.marcellperger.mathexpr.MiniMock.*;
import static org.junit.jupiter.api.Assertions.*;

// Some of these tests are somewhat based on Rust's tests for `core::result::Result`
//  with some additions for all the methods that aren't tested in Rust's tests
//  so that we test all the non-trivial methods
class ResultTest {
    Result<Integer, String> getOk() {
        return Result.newOk(314);
    }
    Result<Integer, String> getErr() {
        return Result.newErr("TESTING_ERROR");
    }

    @Test
    void isOk() {
        assertTrue(getOk().isOk());
        assertFalse(getErr().isOk());
    }

    @Test
    void isErr() {
        assertFalse(getOk().isErr());
        assertTrue(getErr().isErr());
    }

    @Test
    void isOkAnd() {
        MockedPredicate<Integer> mfFalse = new MockedPredicate<>(false);
        MockedPredicate<Integer> mfTrue = new MockedPredicate<>(true);
        {
            assertTrue(getOk().isOkAnd(mfTrue));
            mfTrue.assertCalledOnceWith(314);
            assertFalse(getOk().isOkAnd(mfFalse));
            mfFalse.assertCalledOnceWith(314);
        }
        mfTrue.reset();
        mfFalse.reset();
        {
            assertFalse(getErr().isOkAnd(mfTrue));
            mfTrue.assertNotCalled();
            assertFalse(getErr().isOkAnd(mfFalse));
            mfFalse.assertNotCalled();
        }
    }

    @Test
    void isErrAnd() {
        MockedPredicate<String> mfFalse = new MockedPredicate<>(false);
        MockedPredicate<String> mfTrue = new MockedPredicate<>(true);
        {
            assertTrue(getErr().isErrAnd(mfTrue));
            mfTrue.assertCalledOnceWith("TESTING_ERROR");
            assertFalse(getErr().isErrAnd(mfFalse));
            mfFalse.assertCalledOnceWith("TESTING_ERROR");
        }
        mfTrue.reset();
        mfFalse.reset();
        {
            assertFalse(getOk().isErrAnd(mfTrue));
            mfTrue.assertNotCalled();
            assertFalse(getOk().isErrAnd(mfFalse));
            mfFalse.assertNotCalled();
        }
    }

    @Test
    void map() {
        MockedFunction<Integer, Integer> mfAdd1 = new MockedFunction<>(i -> i + 1);
        assertEquals(Result.newOk(7), Result.<Integer, Integer>newOk(6).map(mfAdd1));
        mfAdd1.assertCalledOnceWith(6);
        mfAdd1.reset();
        assertEquals(Result.newErr(6), Result.<Integer, Integer>newErr(6).map(mfAdd1));
        mfAdd1.assertNotCalled();
    }

    @Test
    void mapOr() {
        MockedFunction<Integer, Integer> mfAdd1 = new MockedFunction<>(i -> i + 1);
        assertEquals(7, Result.<Integer, Integer>newOk(6).mapOr(-1, mfAdd1));
        mfAdd1.assertCalledOnceWith(6);
        mfAdd1.reset();
        assertEquals(-1, Result.<Integer, Integer>newErr(6).mapOr(-1, mfAdd1));
        mfAdd1.assertNotCalled();
    }

    @Test
    void mapOrElse() {
        MockedFunction<Integer, Integer> mfAdd1 = new MockedFunction<>(i -> i + 1);
        MockedFunction<Integer, Integer> mfAdd25 = new MockedFunction<>(i -> i + 25);
        {
            assertEquals(7, Result.<Integer, Integer>newOk(6).mapOrElse(mfAdd25, mfAdd1));
            mfAdd1.assertCalledOnceWith(6);
            mfAdd25.assertNotCalled();
        }
        mfAdd1.reset();
        mfAdd25.reset();
        {
            assertEquals(31, Result.<Integer, Integer>newErr(6).mapOrElse(mfAdd25, mfAdd1));
            mfAdd1.assertNotCalled();
            mfAdd25.assertCalledOnceWith(6);
        }
    }

    @Test
    void mapErr() {
        MockedFunction<Integer, Integer> mfAdd1 = new MockedFunction<>(i -> i + 1);
        assertEquals(Result.newErr(7), Result.<Integer, Integer>newErr(6).mapErr(mfAdd1));
        mfAdd1.assertCalledOnceWith(6);
        mfAdd1.reset();
        assertEquals(Result.newOk(6), Result.<Integer, Integer>newOk(6).mapErr(mfAdd1));
        mfAdd1.assertNotCalled();
    }

    @Test
    void ifThenElse() {
    }

    @Test
    void testIfThenElse() {
    }

    @Test
    void inspect() {
    }

    @Test
    void inspectErr() {
    }

    @Test
    void stream() {
    }

    @Test
    void iterator() {
    }

    @Test
    void forEach() {
    }

    @Test
    void unwrap() {
    }

    @Test
    void expect() {
    }

    @Test
    void expect_err() {
    }

    @Test
    void unwrap_err() {
    }

    @Test
    void and() {
    }

    @Test
    void andThen() {
    }

    @Test
    void or() {
    }

    @Test
    void orElse() {
    }

    @Test
    void unwrapOr() {
    }

    @Test
    void unwrapOrElse() {
    }
}