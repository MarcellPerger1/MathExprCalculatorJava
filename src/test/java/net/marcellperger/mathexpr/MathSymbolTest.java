package net.marcellperger.mathexpr;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MathSymbolTest {
    void assertExprFmt(ObjStringPair testcase) {
        assertEquals(/*expected*/testcase.str(), testcase.obj().fmt());
    }
    void assertExprValue(double expected, ObjStringPair testcase) {
        assertEquals(expected, testcase.obj().calculateValue());
    }

    @Test
    void test_fmt() {
        assertExprFmt(CommonData.getBigData1_minimumParens());
        assertExprFmt(CommonData.getBigData2_minimumParens());
    }

    @Test
    void test_calculateValue() {
        assertExprValue(-31.161, CommonData.getBigData1_minimumParens());
        assertExprValue(-0.0051502812181513195, CommonData.getBigData2_minimumParens());
    }
}
