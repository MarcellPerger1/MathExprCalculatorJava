package net.marcellperger.first.parser;

import net.marcellperger.first.*;
import net.marcellperger.first.util.Util;
import net.marcellperger.first.util.UtilCollectors;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {
    boolean nocache = false;

    void assertInfixParsesTo(String src, int level, MathSymbol expected) {
        // Expects it to be full parse
        if (!nocache) assertInfixParsesToInner(src, level, expected);
        else try (WithSuppressingCache ignored = WithSuppressingCache.start()) {
            assertInfixParsesToInner(src, level, expected);
        }
    }

    void assertInfixParsesTo(Pair<MathSymbol, String> exprPair, int level) {
        assertInfixParsesTo(exprPair.right(), level, exprPair.left());
    }

    void assertParsesToInner(String src, MathSymbol expected) {
        // Expects it to be full parse
        Parser p = new Parser(src);
        MathSymbol actual = assertDoesNotThrow(p::parse);
        // No need to check EOF as parse() checks that it consumed the whole string
        assertEquals(expected, actual);
    }

    void assertParsesTo(String src, MathSymbol expected) {
        // Expects it to be full parse
        if (!nocache) assertParsesToInner(src, expected);
        else try (WithSuppressingCache ignored = WithSuppressingCache.start()) {
            assertParsesToInner(src, expected);
        }
    }

    void assertParsesTo(Pair<MathSymbol, String> exprPair) {
        assertParsesTo(exprPair.right(), exprPair.left());
    }

    void assertInfixParsesToInner(String src, int level, MathSymbol expected) {
        // Expects it to be full parse
        Parser p = new Parser(src);
        MathSymbol actual = assertDoesNotThrow(() -> p.parseInfixPrecedenceLevel(level));
        assertEquals(src.substring(0, p.idx), src, "Didn't fully parse the string");  // = isEof() but better msg
        assertTrue(p.isEof());
        assertEquals(expected, actual);
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

    @Test
    void parseInfixPrecedenceLevel_nocache() {
        boolean origNocache = nocache;
        nocache = true;
        try {
            parseInfixPrecedenceLevel();
        } finally {
            nocache = origNocache;
        }
    }

    @Test
    void parsePrecedenceLevel() {
        assertParsesTo("1.0/2.0",
            new DivOperation(new BasicDoubleSymbol(1.0), new BasicDoubleSymbol(2.0)));
        assertParsesTo(".3*6.",
            new MulOperation(new BasicDoubleSymbol(.3), new BasicDoubleSymbol(6.)));
        assertParsesTo("2.1*5.3+1.1",
            new AddOperation(new MulOperation(new BasicDoubleSymbol(2.1), new BasicDoubleSymbol(5.3)), new BasicDoubleSymbol(1.1)));
        assertParsesTo("0.9-2.1/.3",
            new SubOperation(new BasicDoubleSymbol(0.9), new DivOperation(new BasicDoubleSymbol(2.1), new BasicDoubleSymbol(.3))));
        assertParsesTo("(2.2+1.1)+3.7",
            new AddOperation(new AddOperation(new BasicDoubleSymbol(2.2), new BasicDoubleSymbol(1.1)), new BasicDoubleSymbol(3.7)));
        assertParsesTo(CommonData.getBigData1_groupingParens());
        assertParsesTo(CommonData.getBigData2_groupingParens());
        assertParsesTo("2.2+1.1+3.7",
            new AddOperation(new AddOperation(new BasicDoubleSymbol(2.2), new BasicDoubleSymbol(1.1)), new BasicDoubleSymbol(3.7)));
        assertParsesTo("2.2+1.1+3.7+0.2",
            new AddOperation(new AddOperation(new AddOperation(new BasicDoubleSymbol(2.2), new BasicDoubleSymbol(1.1)), new BasicDoubleSymbol(3.7)), new BasicDoubleSymbol(0.2)));
        assertParsesTo(CommonData.getBigData1_minimumParens());
        assertParsesTo(CommonData.getBigData2_minimumParens());
        assertParsesTo(".9/2./3.3",
            new DivOperation(new DivOperation(new BasicDoubleSymbol(.9), new BasicDoubleSymbol(2.)), new BasicDoubleSymbol(3.3)));
        assertParsesTo(".9/2./3.3",
            new DivOperation(new DivOperation(new BasicDoubleSymbol(.9), new BasicDoubleSymbol(2.)), new BasicDoubleSymbol(3.3)));
    }

    @Test
    void parsePrecedenceLevel_nocache() {
        boolean origNocache = nocache;
        nocache = true;
        try {
            parsePrecedenceLevel();
        } finally {
            nocache = origNocache;
        }
    }

    static class WithSuppressingCache implements AutoCloseable {
        public static WithSuppressingCache getInstance() {
            return INSTANCE;
        }
        protected static WithSuppressingCache INSTANCE = new WithSuppressingCache();
        protected WithSuppressingCache() {}

        WithSuppressingCache startInstance() {
            clearCache();
            return this;
        }
        static WithSuppressingCache start() {
            return getInstance().startInstance();
        }
        WithSuppressingCache stop() {
            restoreCache();
            return this;
        }

        @Override
        public void close() {
            restoreCache();
        }


        Map<SymbolInfo, BinOpBiConstructor<?>> origCache = new HashMap<>();
        void clearCache() {
            Field cacheField = getBiConstructorCache();

            origCache = Arrays.stream(SymbolInfo.values()).<Entry<SymbolInfo, BinOpBiConstructor<?>>>map(sym -> {
                try {
                    BinOpBiConstructor<?> cachedValue = (BinOpBiConstructor<?>)cacheField.get(sym);
                    cacheField.set(sym, null);
                    return Util.makeEntry(sym, cachedValue);
                } catch (IllegalAccessException e) {
                    throw Util.excToError(e);
                }
            }).collect(UtilCollectors.entriesToUnmodifiableMap());
        }

        void restoreCache() {
            Field cacheField = getBiConstructorCache();
            origCache.forEach((key, value) -> {
                try {
                    cacheField.set(key, value);
                } catch (IllegalAccessException ex) {
                    throw Util.excToError(ex);
                }
            });
        }

        @NotNull
        private static Field getBiConstructorCache() {
            Field cacheField;
            try {
                cacheField = SymbolInfo.class.getDeclaredField("biConstructorCache");
            } catch (NoSuchFieldException e) {
                throw Util.excToError(e);
            }
            cacheField.setAccessible(true);
            return cacheField;
        }
    }
}
