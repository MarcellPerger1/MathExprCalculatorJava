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
    void test_fmt__pow() {
        // TODO decide spacing: `2.2**3.1` or `2.2 ** 3.1`
        assertExprFmt(new ObjStringPair(new PowOperation(new BasicDoubleSymbol(0.2), new BasicDoubleSymbol(5.5)), "0.2 ** 5.5"));
        assertExprFmt(new ObjStringPair(new PowOperation(new PowOperation(new BasicDoubleSymbol(0.2), new BasicDoubleSymbol(5.5)), new BasicDoubleSymbol(3.3)),
            "(0.2 ** 5.5) ** 3.3"));
        assertExprFmt(new ObjStringPair(new PowOperation(new BasicDoubleSymbol(0.2), new PowOperation(new BasicDoubleSymbol(5.5), new BasicDoubleSymbol(3.3))),
            "0.2 ** 5.5 ** 3.3"));
        assertExprFmt(CommonData.getBigData3Pow_minimumParens());
    }

    @Test
    void test_calculateValue() {
        assertExprValue(-31.161, CommonData.getBigData1_minimumParens());
        assertExprValue(-0.0051502812181513195, CommonData.getBigData2_minimumParens());
    }
}
