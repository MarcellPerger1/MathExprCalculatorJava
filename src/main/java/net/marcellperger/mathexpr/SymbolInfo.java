package net.marcellperger.mathexpr;

import net.marcellperger.mathexpr.util.Util;
import net.marcellperger.mathexpr.util.UtilCollectors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;



public enum SymbolInfo {
    // Let's say that precedence 0 is for (parens) OR literals - TODO add a class?? but it wouldn't actually be used !
    POW(PowOperation.class, 1, GroupingDirection.RightToLeft, "**", "", PowOperation::new),

    MUL(MulOperation.class, 2, GroupingDirection.LeftToRight, "*", " ", MulOperation::new),
    DIV(DivOperation.class, 2, GroupingDirection.LeftToRight, "/", " ", DivOperation::new),

    ADD(AddOperation.class, 3, GroupingDirection.LeftToRight, "+", " ", AddOperation::new),
    SUB(SubOperation.class, 3, GroupingDirection.LeftToRight, "-", " ", SubOperation::new),
    ;

    public static final Map<Class<? extends MathSymbol>, SymbolInfo> CLS_TO_INFO_MAP;
    public static final Map<Integer, Set<SymbolInfo>> PREC_TO_INFO_MAP;
    public static final List<Entry<Integer, Set<SymbolInfo>>> PREC_SORTED_INFO;
    public static final int MAX_PRECEDENCE;
    public static final Map<Integer, PrecedenceLevelInfo> PREC_LEVELS_INFO;

    public final int precedence;  // TODO make this Integer
    public final Class<? extends MathSymbol> cls;
    public final GroupingDirection groupingDirection;
    public final String infix;
    public final @Nullable String spacesAroundInfix;

    @SuppressWarnings("unused")  // useful later
    SymbolInfo(Class<? extends MathSymbol> cls, int precedence,
               GroupingDirection groupingDirection, @Nullable String spacesAroundInfix, @Nullable String infix) {
        this.precedence = precedence;
        this.cls = cls;
        this.groupingDirection = groupingDirection;
        this.infix = infix;
        this.spacesAroundInfix = spacesAroundInfix;
    }
    SymbolInfo(Class<? extends BinaryOperationLeftRight> cls, int precedence,
               GroupingDirection groupingDirection, @Nullable String infix, @Nullable String spacesAroundInfix,
               BinOpBiConstructor biConstructor) {
        this.precedence = precedence;
        this.cls = cls;  // TODO: private + getters?
        this.groupingDirection = groupingDirection;
        this.infix = infix;
        this.spacesAroundInfix = spacesAroundInfix;
        this.biConstructorCache = biConstructor;
    }

    public static @Nullable SymbolInfo fromClass(Class<? extends MathSymbol> cls) {
        return CLS_TO_INFO_MAP.get(cls);
    }

    // These aren't strictly necessary but make it much more elegant to handle null / non-existent SymbolInfo for a class
    public static @Nullable Integer precedenceFromClass(Class<? extends MathSymbol> cls) {
        return Util.chainNulls(fromClass(cls), x -> x.precedence);
    }
    public static @Nullable GroupingDirection groupingDirectionFromClass(Class<? extends MathSymbol> cls) {
        return Util.chainNulls(fromClass(cls), x -> x.groupingDirection);
    }
    public static @Nullable String infixFromClass(Class<? extends MathSymbol> cls) {
        return Util.chainNulls(fromClass(cls), x -> x.infix);
    }
    public static @Nullable String spacesAroundInfixFromClass(Class<? extends MathSymbol> cls) {
        return Util.chainNulls(fromClass(cls), s -> s.spacesAroundInfix);
    }

    private BinOpBiConstructor biConstructorCache = null;
    public @NotNull BinOpBiConstructor getBiConstructor() {
        if(biConstructorCache != null) return biConstructorCache;
        assert BinaryOperationLeftRight.class.isAssignableFrom(cls);

        Constructor<? extends BinaryOperationLeftRight> ctor;
        try {
            ctor = cls.asSubclass(BinaryOperationLeftRight.class).getConstructor(MathSymbol.class, MathSymbol.class);
        } catch (NoSuchMethodException e) {
            throw Util.excToError(e);
        }
        // Basic sanity checks so that the errors are thrown now, not later when the BiFunction<> is ran.
        try {
            ctor.newInstance(new BasicDoubleSymbol(1.0), new BasicDoubleSymbol(2.0));
        } catch (IllegalAccessException e) {
            throw Util.withCause(new IllegalAccessError("getBiConsumer() expects cls to have a public 2-arg constructor"), e);
        } catch (InstantiationException e) {
            throw Util.excToError(e);
        } catch (InvocationTargetException ignored) {}

        return biConstructorCache = (left, right) -> {
            try {
                return ctor.newInstance(left, right);
            } catch (InvocationTargetException e) {
                throw Util.intoUnchecked(e);
            } catch (IllegalAccessException | InstantiationException e) {
                throw new AssertionError("Once public, the constructor shall remain public", e);
            }
        };
    }


    static {
        CLS_TO_INFO_MAP = Arrays.stream(values()).collect(Collectors.toUnmodifiableMap(p -> p.cls, p -> p));
        PREC_TO_INFO_MAP = Arrays.stream(values()).collect(Collectors.groupingBy(s -> s.precedence, Collectors.toUnmodifiableSet()));
        PREC_SORTED_INFO = PREC_TO_INFO_MAP.entrySet().stream().sorted(Comparator.comparingInt(Entry::getKey)).toList();
        MAX_PRECEDENCE = PREC_SORTED_INFO.getLast().getKey();
        PREC_LEVELS_INFO = PREC_TO_INFO_MAP.keySet().stream().map(PrecedenceLevelInfo::newMapEntry).collect(UtilCollectors.entriesToMap());
    }
}
