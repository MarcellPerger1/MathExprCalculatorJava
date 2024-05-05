package net.marcellperger.mathexpr.util.rs;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
    }

    @Test
    void isErrAnd() {
    }

    @Test
    void map() {
    }

    @Test
    void mapOr() {
    }

    @Test
    void mapOrElse() {
    }

    @Test
    void mapErr() {
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