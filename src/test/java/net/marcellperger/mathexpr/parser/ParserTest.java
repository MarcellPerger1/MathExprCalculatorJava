package net.marcellperger.mathexpr.parser;

import net.marcellperger.mathexpr.*;
import net.marcellperger.mathexpr.util.Util;
import net.marcellperger.mathexpr.util.UtilCollectors;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {
    public static final int POW_PREC = SymbolInfo.POW.precedence;
    public static final int MUL_PREC = SymbolInfo.MUL.precedence;
    public static final int ADD_PREC = SymbolInfo.ADD.precedence;

    boolean nocache = false;

    void assertInfixParsesTo(String src, int level, MathSymbol expected) {
        // Expects it to be full parse
        try (var ignored = applyNocacheAttr()) {
            assertInfixParsesTo_inner(src, level, expected);
        }
    }
    void assertInfixParsesTo(ObjStringPair exprPair, int level) {
        assertInfixParsesTo(exprPair.str(), level, exprPair.obj());
    }
    void assertInfixParsesTo_inner(String src, int level, MathSymbol expected) {
        // Expects it to be full parse
        Parser p = new Parser(src);
        MathSymbol actual = assertDoesNotThrow(() -> p.parseInfixPrecedenceLevel(level));
        assertEquals('"' + src + '"', '"' + src.substring(0, p.idx) + '"',
            "Didn't fully parse the string");  // = isEof() but better msg
        assertTrue(p.isEof());
        assertEquals(expected, actual);
    }

    void assertParsesTo(String src, MathSymbol expected) {
        // Expects it to be full parse
        try (var ignored = applyNocacheAttr()) {
            assertParsesTo_inner(src, expected);
        }
    }
    void assertParsesTo(ObjStringPair exprPair) {
        assertParsesTo(exprPair.str(), exprPair.obj());
    }
    void assertParsesTo_inner(String src, MathSymbol expected) {
        // Expects it to be full parse
        Parser p = new Parser(src);
        MathSymbol actual = assertDoesNotThrow(p::parse);
        // No need to check EOF as parse() checks that it consumed the whole string
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(booleans={true, false})
    void parseInfixPrecedenceLevel(boolean disableCache) {
        try(var ignored = setNocacheAttr(disableCache)) {
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
    }

    @ParameterizedTest
    @ValueSource(booleans={true, false})
    void parse(boolean disableCache) {
        try(var ignored = setNocacheAttr(disableCache)) {
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
    }

    @ParameterizedTest
    @ValueSource(booleans={true, false})
    void parsePrecedenceLevel_pow(boolean disableCache) {
        try(var ignored = setNocacheAttr(disableCache)) {
            assertInfixParsesTo("1.2**9.1", POW_PREC,
                new PowOperation(new BasicDoubleSymbol(1.2), new BasicDoubleSymbol(9.1)));
            assertInfixParsesTo("1.2**9.1**.3", POW_PREC,
                new PowOperation(new BasicDoubleSymbol(1.2), new PowOperation(new BasicDoubleSymbol(9.1), new BasicDoubleSymbol(.3))));
            assertInfixParsesTo("1.2**9.1+.3", ADD_PREC,
                new AddOperation(new PowOperation(new BasicDoubleSymbol(1.2), new BasicDoubleSymbol(9.1)), new BasicDoubleSymbol(.3)));
            assertInfixParsesTo(CommonData.getBigData3Pow_minimumParens(), ADD_PREC);
            assertInfixParsesTo(CommonData.getBigData3Pow_groupingParens(), ADD_PREC);
        }
    }

    @ParameterizedTest
    @ValueSource(booleans={true, false})
    void parse_pow(boolean disableCache) {
        try(var ignored = setNocacheAttr(disableCache)) {
            assertParsesTo("1.2**9.1",
                new PowOperation(new BasicDoubleSymbol(1.2), new BasicDoubleSymbol(9.1)));
            assertParsesTo("1.2**9.1**.3",
                new PowOperation(new BasicDoubleSymbol(1.2), new PowOperation(new BasicDoubleSymbol(9.1), new BasicDoubleSymbol(.3))));
            assertParsesTo("1.2**9.1+.3",
                new AddOperation(new PowOperation(new BasicDoubleSymbol(1.2), new BasicDoubleSymbol(9.1)), new BasicDoubleSymbol(.3)));
            assertParsesTo(CommonData.getBigData3Pow_minimumParens());
            assertParsesTo(CommonData.getBigData3Pow_groupingParens());
        }
    }

    @Disabled("[SKIP] Failing, will fix in a later PR")
    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void parensWhitespaceBug(boolean disableCache) {
        try(var ignored = setNocacheAttr(disableCache)) {
            // These 3 are fine
            assertInfixParsesTo("( 1.2 )", MUL_PREC, new BasicDoubleSymbol(1.2));
            assertParsesTo("( 1.2 )", new BasicDoubleSymbol(1.2));
            assertParsesTo("  1.2  ", new BasicDoubleSymbol(1.2));
            // This is not - could be a bug in the future
            assertInfixParsesTo("  1.2  ", 0, new BasicDoubleSymbol(1.2));
        }
    }

    protected WithNocache setNocacheAttr(boolean disableCache) {
        return new WithNocache(this, disableCache);
    }

    static class WithNocache implements AutoCloseable {
        boolean origNocache;
        boolean doDisable;
        ParserTest inst;

        public WithNocache(ParserTest inst_) {
            this(inst_, true);
        }
        public WithNocache(ParserTest inst_, boolean doDisable_) {
            inst = inst_;
            doDisable = doDisable_;
            _start();
        }

        private void _start() {
            origNocache = inst.nocache;
            inst.nocache = doDisable;
        }

        public void close() {
            inst.nocache = origNocache;
        }
    }

    protected WithSuppressingCache applyNocacheAttr() {
        return WithSuppressingCache.start(nocache);
    }

    static class WithSuppressingCache implements AutoCloseable {
        @Contract("_->new")
        public static @NotNull WithSuppressingCache start(boolean doStart) {
            WithSuppressingCache self = new WithSuppressingCache();
            if(doStart) self.clearCache();
            return self;
        }
        @Contract(" -> new")
        public static @NotNull WithSuppressingCache start() {
            return start(true);
        }

        @Override
        public void close() {
            restoreCache();
        }

        // region implementation details (ctor, clearCache, restoreCache
        private WithSuppressingCache() {}

        private Map<SymbolInfo, Optional<BinOpBiConstructor<?>>> origCache = null;

        protected void clearCache() {
            Field cacheField = getBiConstructorCache();
            origCache = Arrays.stream(SymbolInfo.values()).map(sym -> {
                try {
                    @Nullable BinOpBiConstructor<?> cachedValue = (BinOpBiConstructor<?>)cacheField.get(sym);
                    cacheField.set(sym, null);
                    return Util.makeEntry(sym, Optional.<BinOpBiConstructor<?>>ofNullable(cachedValue));
                } catch (IllegalAccessException e) {
                    throw Util.excToError(e);
                }
            }).collect(UtilCollectors.entriesToUnmodifiableMap());
        }

        protected void restoreCache() {
            if(origCache == null) return;  // didn't cache so nothing to restore
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
        // endregion
    }
}
