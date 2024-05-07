package net.marcellperger.mathexpr.util.rs;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
    void ifThenElse_consumer() {
        MockedConsumer<Integer> intConsumer = new MockedConsumer<>();
        MockedConsumer<String> strConsumer = new MockedConsumer<>();
        {
            getOk().ifThenElse(intConsumer, strConsumer);
            intConsumer.assertCalledOnceWith(314);
            strConsumer.assertNotCalled();
        }
        intConsumer.reset();
        strConsumer.reset();
        {
            getErr().ifThenElse(intConsumer, strConsumer);
            intConsumer.assertNotCalled();
            strConsumer.assertCalledOnceWith("TESTING_ERROR");
        }
    }

    @Test
    void ifThenElse_func() {
        MockedFunction<Integer, String> intFn = new MockedFunction<>("intFn_return");
        MockedFunction<String, String> strFn = new MockedFunction<>("strFn_return");
        {
            assertEquals("intFn_return",  getOk().ifThenElse(intFn, strFn));
            intFn.assertCalledOnceWith(314);
            strFn.assertNotCalled();
        }
        intFn.reset();
        strFn.reset();
        {
            assertEquals("strFn_return", getErr().ifThenElse(intFn, strFn));
            intFn.assertNotCalled();
            strFn.assertCalledOnceWith("TESTING_ERROR");
        }
    }

    @Test
    void inspect() {
        MockedConsumer<Integer> intCons = new MockedConsumer<>();
        assertEquals(getOk(), getOk().inspect(intCons));
        intCons.assertCalledOnceWith(314);
        intCons.reset();
        assertEquals(getErr(), getErr().inspect(intCons));
        intCons.assertNotCalled();
    }

    @Test
    void inspectErr() {
        MockedConsumer<String> strCons = new MockedConsumer<>();
        assertEquals(getErr(), getErr().inspectErr(strCons));
        strCons.assertCalledOnceWith("TESTING_ERROR");
        strCons.reset();
        assertEquals(getOk(), getOk().inspectErr(strCons));
        strCons.assertNotCalled();
    }

    @Test
    void stream() {
        assertEquals(List.of(314), getOk().stream().toList());
        assertEquals(List.of(), getErr().stream().toList());
    }

    @Test
    void iterator() {
        {
            Iterator<Integer> oks = getOk().iterator();
            assertTrue(oks.hasNext());
            assertEquals(314, oks.next());
            assertFalse(oks.hasNext());
        }
        {
            List<Integer> ls = new ArrayList<>();
            getOk().iterator().forEachRemaining(ls::add);
            assertEquals(List.of(314), ls);
        }
        {
            Iterator<Integer> oks = getErr().iterator();
            assertFalse(oks.hasNext());
        }
        {
            List<Integer> ls = new ArrayList<>();
            getErr().iterator().forEachRemaining(ls::add);
            assertEquals(List.of(), ls);
        }
    }

    @Test
    void forEach() {
        MockedConsumer<Integer> intCons = new MockedConsumer<>();
        {
            getOk().forEach(intCons);
            intCons.assertCalledOnceWith(314);
        }
        intCons.reset();
        {
            getErr().forEach(intCons);
            intCons.assertNotCalled();
        }
    }

    @Test
    void unwrap() {
        assertEquals(314, assertDoesNotThrow(() -> getOk().unwrap()));
        {
            Result<Integer, String> err = getErr();
            ResultPanicWithValueException exc = assertThrows(
                ResultPanicWithValueException.class, err::unwrap);
            assertEquals("TESTING_ERROR", exc.getValue());
            assertEquals("unwrap() got Err value: TESTING_ERROR", exc.getMessage());
            assertNull(exc.getCause(), "Expected no cause when Err is a string");
        }
        {
            MyCustomException customExc = new MyCustomException("CUSTOM_ERR_VALUE");
            Result<Integer, MyCustomException> err = Result.newErr(customExc);
            ResultPanicWithValueException exc = assertThrows(
                ResultPanicWithValueException.class, err::unwrap);
            assertEquals(customExc, exc.getValue());
            assertEquals("unwrap() got Err value: " +
                "net.marcellperger.mathexpr.util.rs.ResultTest$MyCustomException:" +
                " CUSTOM_ERR_VALUE", exc.getMessage());
            assertEquals(customExc, exc.getCause());
        }
    }

    @SuppressWarnings("unused")
    static class MyCustomException extends RuntimeException {
        public MyCustomException() {
        }

        public MyCustomException(String message) {
            super(message);
        }

        public MyCustomException(String message, Throwable cause) {
            super(message, cause);
        }

        public MyCustomException(Throwable cause) {
            super(cause);
        }
    }

    @Test
    void expect() {
        assertEquals(314, assertDoesNotThrow(() -> getOk().expect("EXPECT_MSG")));
        {
            Result<Integer, String> err = getErr();
            ResultPanicWithValueException exc = assertThrows(
                ResultPanicWithValueException.class, () -> err.expect("EXPECT_MSG"));
            assertEquals("TESTING_ERROR", exc.getValue());
            assertEquals("EXPECT_MSG: TESTING_ERROR", exc.getMessage());
            assertNull(exc.getCause(), "Expected no cause when Err is a string");
        }
        {
            MyCustomException customExc = new MyCustomException("CUSTOM_ERR_VALUE");
            Result<Integer, MyCustomException> err = Result.newErr(customExc);
            ResultPanicWithValueException exc = assertThrows(
                ResultPanicWithValueException.class, () -> err.expect("EXPECT_MSG"));
            assertEquals(customExc, exc.getValue());
            assertEquals("EXPECT_MSG: " +
                "net.marcellperger.mathexpr.util.rs.ResultTest$MyCustomException:" +
                " CUSTOM_ERR_VALUE", exc.getMessage());
            assertEquals(customExc, exc.getCause());
        }
    }

    @Test
    void unwrap_err() {
        assertEquals("TESTING_ERROR", assertDoesNotThrow(() -> getErr().unwrap_err()));
        {
            Result<Integer, String> err = getOk();
            ResultPanicWithValueException exc = assertThrows(
                ResultPanicWithValueException.class, err::unwrap_err);
            assertEquals(314, exc.getValue());
            assertEquals("unwrap_err() got Ok value: 314", exc.getMessage());
            assertNull(exc.getCause(), "Expected no cause for Ok");
        }
        {
            MyCustomException customExc = new MyCustomException("CUSTOM_ERR_VALUE");
            Result<MyCustomException, Integer> err = Result.newOk(customExc);
            ResultPanicWithValueException exc = assertThrows(
                ResultPanicWithValueException.class, err::unwrap_err);
            assertEquals(customExc, exc.getValue());
            assertEquals("unwrap_err() got Ok value: " +
                "net.marcellperger.mathexpr.util.rs.ResultTest$MyCustomException:" +
                " CUSTOM_ERR_VALUE", exc.getMessage());
            assertNull(exc.getCause(), "Don't set cause for Ok");
        }
    }

    @Test
    void expect_err() {
        assertEquals("TESTING_ERROR", assertDoesNotThrow(() -> getErr().expect_err("EXPECT_ERR_MSG")));
        {
            Result<Integer, String> err = getOk();
            ResultPanicWithValueException exc = assertThrows(
                ResultPanicWithValueException.class, () -> err.expect_err("EXPECT_ERR_MSG"));
            assertEquals(314, exc.getValue());
            assertEquals("EXPECT_ERR_MSG: 314", exc.getMessage());
            assertNull(exc.getCause(), "Expected no cause for Ok");
        }
        {
            MyCustomException customExc = new MyCustomException("CUSTOM_ERR_VALUE");
            Result<MyCustomException, Integer> err = Result.newOk(customExc);
            ResultPanicWithValueException exc = assertThrows(
                ResultPanicWithValueException.class, () -> err.expect_err("EXPECT_ERR_MSG"));
            assertEquals(customExc, exc.getValue());
            assertEquals("EXPECT_ERR_MSG: " +
                "net.marcellperger.mathexpr.util.rs.ResultTest$MyCustomException:" +
                " CUSTOM_ERR_VALUE", exc.getMessage());
            assertNull(exc.getCause(), "Don't set cause for Ok");
        }
    }

    @Test
    void and() {
        assertEquals(Result.newOk(91L), getOk().and(Result.newOk(91L)));
        assertEquals(Result.newErr("ERR_RIGHT"), getOk().and(Result.newErr("ERR_RIGHT")));
        assertEquals(Result.newErr("TESTING_ERROR"), getErr().and(Result.newOk(91L)));
        assertEquals(Result.newErr("TESTING_ERROR"), getErr().and(Result.newErr("ERR_RIGHT")));
    }

    @Test
    void andThen() {
        MockedFunction<Integer, Result<Long, String>> mfOk = new MockedFunction<>(Result.newOk(91L));
        MockedFunction<Integer, Result<Long, String>> mfErr = new MockedFunction<>(Result.newErr("ERR_RIGHT"));
        {
            assertEquals(Result.newOk(91L), getOk().andThen(mfOk));
            mfOk.assertCalledOnceWith(314);
            assertEquals(Result.newErr("ERR_RIGHT"), getOk().andThen(mfErr));
            mfErr.assertCalledOnceWith(314);
        }
        mfOk.reset();
        mfErr.reset();
        {
            assertEquals(Result.newErr("TESTING_ERROR"), getErr().andThen(mfOk));
            mfOk.assertNotCalled();
            assertEquals(Result.newErr("TESTING_ERROR"), getErr().andThen(mfErr));
            mfErr.assertNotCalled();
        }
    }

    @Test
    void or() {
        assertEquals(getOk(), getOk().or(Result.newOk(271)));
        assertEquals(getOk(), getOk().or(Result.newErr("ERR_RIGHT")));
        assertEquals(Result.newOk(271), getErr().or(Result.newOk(271)));
        assertEquals(Result.newErr("ERR_RIGHT"), getErr().or(Result.newErr("ERR_RIGHT")));
    }

    @Test
    void orElse() {
        MockedFunction<String, Result<Integer, String>> mfOk = new MockedFunction<>(Result.newOk(271));
        MockedFunction<String, Result<Integer, String>> mfErr = new MockedFunction<>(Result.newErr("ERR_RIGHT"));
        {
            assertEquals(getOk(), getOk().orElse(mfOk));
            mfOk.assertNotCalled();
            assertEquals(getOk(), getOk().orElse(mfErr));
            mfErr.assertNotCalled();
        }
        mfOk.reset();
        mfErr.reset();
        {
            assertEquals(Result.newOk(271), getErr().orElse(mfOk));
            mfOk.assertCalledOnceWith("TESTING_ERROR");
            assertEquals(Result.newErr("ERR_RIGHT"), getErr().orElse(mfErr));
            mfErr.assertCalledOnceWith("TESTING_ERROR");
        }
    }

    @Test
    void unwrapOr() {
        assertEquals(314, getOk().unwrapOr(-1));
        assertEquals(-1, getErr().unwrapOr(-1));
    }

    @Test
    void unwrapOrElse() {
        MockedFunction<String, Integer> mf = new MockedFunction<>(271);
        {
            assertEquals(314, getOk().unwrapOrElse(mf));
            mf.assertNotCalled();
        }
        mf.reset();
        {
            assertEquals(271, getErr().unwrapOrElse(mf));
            mf.assertCalledOnceWith("TESTING_ERROR");
        }
    }
}