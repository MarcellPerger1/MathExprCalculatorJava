package net.marcellperger.first.parser;

import net.marcellperger.first.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {
    void assertInfixParsesTo(String src, int level, MathSymbol expected) {
        // Expects it to be full parse
        Parser p = new Parser(src);
        MathSymbol actual = assertDoesNotThrow(() -> p.parseInfixPrecedenceLevel(level));
        assertEquals(src.substring(0, p.idx), src, "Didn't fully parse the string");  // = isEof() but better msg
        assertTrue(p.isEof());
        assertEquals(expected, actual);
    }

    void assertInfixParsesTo(Pair<MathSymbol, String> exprPair, int level) {
        assertInfixParsesTo(exprPair.right(), level, exprPair.left());
    }

    @Test
    void parseInfixPrecedenceLevel() {
        assertInfixParsesTo("1.0/2.0", 1,
            new DivOperation(new BasicDoubleSymbol(1.0), new BasicDoubleSymbol(2.0)));
        assertInfixParsesTo(".3*6.", 1,
            new MulOperation(new BasicDoubleSymbol(.3), new BasicDoubleSymbol(6.)));
        assertInfixParsesTo("2.1*5.3+1.1", 2,
            new AddOperation(new MulOperation(new BasicDoubleSymbol(2.1), new BasicDoubleSymbol(5.3)), new BasicDoubleSymbol(1.1)));
        assertInfixParsesTo("0.9-2.1/.3", 2,
            new SubOperation(new BasicDoubleSymbol(0.9), new DivOperation(new BasicDoubleSymbol(2.1), new BasicDoubleSymbol(.3))));
        assertInfixParsesTo("(2.2+1.1)+3.7", 2,
            new AddOperation(new AddOperation(new BasicDoubleSymbol(2.2), new BasicDoubleSymbol(1.1)), new BasicDoubleSymbol(3.7)));
        assertInfixParsesTo(CommonData.getBigData1_groupingParens(), 2);
        assertInfixParsesTo(CommonData.getBigData2_groupingParens(), 2);
        assertInfixParsesTo("2.2+1.1+3.7", 2,
            new AddOperation(new AddOperation(new BasicDoubleSymbol(2.2), new BasicDoubleSymbol(1.1)), new BasicDoubleSymbol(3.7)));
        assertInfixParsesTo("2.2+1.1+3.7+0.2", 2,
            new AddOperation(new AddOperation(new AddOperation(new BasicDoubleSymbol(2.2), new BasicDoubleSymbol(1.1)), new BasicDoubleSymbol(3.7)), new BasicDoubleSymbol(0.2)));
        assertInfixParsesTo(CommonData.getBigData1_minimumParens(), 2);
        assertInfixParsesTo(CommonData.getBigData2_minimumParens(), 2);
        assertInfixParsesTo(".9/2./3.3", 1,
            new DivOperation(new DivOperation(new BasicDoubleSymbol(.9), new BasicDoubleSymbol(2.)), new BasicDoubleSymbol(3.3)));
        assertInfixParsesTo(".9/2./3.3", 2,
            new DivOperation(new DivOperation(new BasicDoubleSymbol(.9), new BasicDoubleSymbol(2.)), new BasicDoubleSymbol(3.3)));
    }
}
