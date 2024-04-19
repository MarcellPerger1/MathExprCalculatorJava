package net.marcellperger.mathexpr.parser;

import net.marcellperger.mathexpr.*;
import net.marcellperger.mathexpr.util.Util;
import net.marcellperger.mathexpr.util.UtilCollectors;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {
    public static final int POW_PREC = SymbolInfo.POW.precedence;
    public static final int MUL_PREC = SymbolInfo.MUL.precedence;
    public static final int ADD_PREC = SymbolInfo.ADD.precedence;

    boolean nocache = false;

    void assertInfixParsesTo(String src, int level, MathSymbol expected) {
        // Expects it to be full parse
        if (!nocache) assertInfixParsesToInner(src, level, expected);
        else try (WithSuppressingCache ignored = WithSuppressingCache.start()) {
            assertInfixParsesToInner(src, level, expected);
        }
    }

    void assertInfixParsesTo(ObjStringPair exprPair, int level) {
        assertInfixParsesTo(exprPair.str(), level, exprPair.obj());
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

    void assertParsesTo(ObjStringPair exprPair) {
        assertParsesTo(exprPair.str(), exprPair.obj());
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
        assertInfixParsesTo("1.0/2.0", MUL_PREC,
            new DivOperation(new BasicDoubleSymbol(1.0), new BasicDoubleSymbol(2.0)));
        assertInfixParsesTo(".3*6.", MUL_PREC,
            new MulOperation(new BasicDoubleSymbol(.3), new BasicDoubleSymbol(6.)));
        assertInfixParsesTo("2.1*5.3+1.1", ADD_PREC,
            new AddOperation(new MulOperation(new BasicDoubleSymbol(2.1), new BasicDoubleSymbol(5.3)), new BasicDoubleSymbol(1.1)));
        assertInfixParsesTo("0.9-2.1/.3", ADD_PREC,
            new SubOperation(new BasicDoubleSymbol(0.9), new DivOperation(new BasicDoubleSymbol(2.1), new BasicDoubleSymbol(.3))));
        assertInfixParsesTo("(2.2+1.1)+3.7", ADD_PREC,
            new AddOperation(new AddOperation(new BasicDoubleSymbol(2.2), new BasicDoubleSymbol(1.1)), new BasicDoubleSymbol(3.7)));
        assertInfixParsesTo(CommonData.getBigData1_groupingParens(), ADD_PREC);
        assertInfixParsesTo(CommonData.getBigData2_groupingParens(), ADD_PREC);
        assertInfixParsesTo("2.2+1.1+3.7", ADD_PREC,
            new AddOperation(new AddOperation(new BasicDoubleSymbol(2.2), new BasicDoubleSymbol(1.1)), new BasicDoubleSymbol(3.7)));
        assertInfixParsesTo("2.2+1.1+3.7+0.2", ADD_PREC,
            new AddOperation(new AddOperation(new AddOperation(new BasicDoubleSymbol(2.2), new BasicDoubleSymbol(1.1)), new BasicDoubleSymbol(3.7)), new BasicDoubleSymbol(0.2)));
        assertInfixParsesTo(CommonData.getBigData1_minimumParens(), ADD_PREC);
        assertInfixParsesTo(CommonData.getBigData2_minimumParens(), ADD_PREC);
        assertInfixParsesTo(".9/2./3.3", MUL_PREC,
            new DivOperation(new DivOperation(new BasicDoubleSymbol(.9), new BasicDoubleSymbol(2.)), new BasicDoubleSymbol(3.3)));
        assertInfixParsesTo(".9/2./3.3", ADD_PREC,
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

    @Test  // TODO make this not test/inline it
    void parsePrecedenceLevel_pow() {
        assertInfixParsesTo("1.2**9.1", POW_PREC,
            new PowOperation(new BasicDoubleSymbol(1.2), new BasicDoubleSymbol(9.1)));
        assertInfixParsesTo("1.2**9.1**.3", POW_PREC,
            new PowOperation(new BasicDoubleSymbol(1.2), new PowOperation(new BasicDoubleSymbol(9.1), new BasicDoubleSymbol(.3))));
        assertInfixParsesTo("1.2**9.1+.3", ADD_PREC,
            new AddOperation(new PowOperation(new BasicDoubleSymbol(1.2), new BasicDoubleSymbol(9.1)), new BasicDoubleSymbol(.3)));
        assertInfixParsesTo(CommonData.getBigData3Pow_minimumParens(), ADD_PREC);
        assertInfixParsesTo(CommonData.getBigData3Pow_groupingParens(), ADD_PREC);
    }

    @Test
    void parse_pow() {
        assertParsesTo("1.2**9.1",
            new PowOperation(new BasicDoubleSymbol(1.2), new BasicDoubleSymbol(9.1)));
        assertParsesTo("1.2**9.1**.3",
            new PowOperation(new BasicDoubleSymbol(1.2), new PowOperation(new BasicDoubleSymbol(9.1), new BasicDoubleSymbol(.3))));
        assertParsesTo("1.2**9.1+.3",
            new AddOperation(new PowOperation(new BasicDoubleSymbol(1.2), new BasicDoubleSymbol(9.1)), new BasicDoubleSymbol(.3)));
        assertParsesTo(CommonData.getBigData3Pow_minimumParens());
        assertParsesTo(CommonData.getBigData3Pow_groupingParens());
    }

    @ParameterizedTest
    @ValueSource(booleans={true, false})
    void parsePrecedenceLevel_pow_nocache(boolean disableCache) {
        try(var ignored = new WithNocache(this, disableCache)) {
            parsePrecedenceLevel_pow();
        }
    }

    // TODO do more parameterized tests
    @ParameterizedTest
    @ValueSource(booleans={true, false})
    void parse_pow_nocache(boolean disableCache) {
        try(var ignored = new WithNocache(this, disableCache)) {
            parse_pow();
        }
    }

    @ParameterizedTest
    @ValueSource(booleans={true, false})
    void parsePrecedenceLevel_nocache(boolean disableCache) {
        try(var ignored = new WithNocache(this, disableCache)) {
            parsePrecedenceLevel();
        }
    }

    static class WithNocache implements AutoCloseable {
        boolean origNocache;
        boolean doDisable;
        ParserTest inst;

        WithNocache(ParserTest inst_) {
            inst = inst_;
            start();
        }
        WithNocache(ParserTest inst_, boolean doDisable_) {
            inst = inst_;
            doDisable = doDisable_;
            start();
        }
        
        @Contract("->this")
        public WithNocache start() {
            origNocache = inst.nocache;
            inst.nocache = doDisable;
            return this;
        }

        public void close() {
            inst.nocache = origNocache;
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


        Map<SymbolInfo, Optional<BinOpBiConstructor<?>>> origCache = new HashMap<>();
        void clearCache() {
            Field cacheField = getBiConstructorCache();

            origCache = Arrays.stream(SymbolInfo.values()).<Entry<SymbolInfo, Optional<BinOpBiConstructor<?>>>>map(sym -> {
                try {
                    @Nullable BinOpBiConstructor<?> cachedValue = (BinOpBiConstructor<?>)cacheField.get(sym);
                    cacheField.set(sym, null);
                    return Util.makeEntry(sym, Optional.ofNullable(cachedValue));
                } catch (IllegalAccessException e) {
                    throw Util.excToError(e);
                }
            }).collect(UtilCollectors.entriesToUnmodifiableMap());
        }

        void restoreCache() {
            Field cacheField = getBiConstructorCache();
            origCache.forEach((key, value) -> {
                try {
                    cacheField.set(key, value.orElse(null));
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
