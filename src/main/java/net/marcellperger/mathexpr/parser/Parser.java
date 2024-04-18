package net.marcellperger.mathexpr.parser;

import net.marcellperger.mathexpr.*;
import net.marcellperger.mathexpr.util.Util;
import net.marcellperger.mathexpr.util.UtilCollectors;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.nio.CharBuffer;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;



public class Parser {
    protected String src;
    /** Next index to parse */
    protected int idx;

    public Parser(String src_) {
        src = src_;
        idx = 0;
    }

    public MathSymbol parse() throws ExprParseException {
        MathSymbol sym = parseInfixPrecedenceLevel(3);  // TODO compute max prec level
        discardWhitespace();
        if(notEof()) throw new ExprParseException("Syntax error: didn't reach end of input");
        return sym;
    }

    // https://regex101.com/r/2EogTA/1
    protected static final Pattern DOUBLE_RE = Pattern.compile("^([+-]?)(\\d*\\.\\d+|\\d+\\.?)(?:[eE]([+-]?\\d+))?");
    public @NotNull MathSymbol parseDoubleLiteral_exc() throws ExprParseException {
        return switch (parseDoubleLiteral_null()) {
            case null -> throw new ExprParseException("Couldn't parse double in expression");
            case MathSymbol sym -> sym;
        };
    }
    public @Nullable MathSymbol parseDoubleLiteral_null() {
        discardWhitespace();
        Matcher m = DOUBLE_RE.matcher(strFromHere());
        if (!m.lookingAt()) return null;
        String s = m.group();
        idx += s.length();
        double value;
        try {
            value = Double.parseDouble(s);
        } catch (NumberFormatException _exc) {
            // Technically this should never happen - assuming I've got that regex right
            assert false: "There is a problem with the regex, this should've been rejected earlier";
            return null;
        }
        return new BasicDoubleSymbol(value);
    }

    public @Nullable MathSymbol parseParensOrLiteral() throws ExprParseException {
        discardWhitespace();
        if(peek() == '(') return parseParens();
        // TODO add a better error handling system - don't want to maintain 2 versions of each function
        //  returning null: +easier to do unions, +no need for verbose try/catch, -no info about errors
        return parseDoubleLiteral_null();
    }

    private static final int PREC_IN_PARENS = 3;  // TODO compute max prec level
    public @Nullable MathSymbol parseParens() throws ExprParseException {
        advanceExpectNext('(');
        MathSymbol sym = parseInfixPrecedenceLevel(PREC_IN_PARENS);  // TODO use .parse() when it is completed
        advanceExpectNext(')');
        return sym;
    }

    public MathSymbol parseInfixPrecedenceLevel(int level) throws ExprParseException {
        discardWhitespace();
        if(level == 0) {
            return parseParensOrLiteral();
        }
        // TODO remove this next-line:
        if(SymbolInfo.PREC_TO_INFO_MAP.get(level) == null) { return parseInfixPrecedenceLevel(level - 1); }
        Set<SymbolInfo> symbols = Util.requireNonEmptyNonNull(SymbolInfo.PREC_TO_INFO_MAP.get(level));
        @Nullable GroupingDirection dirn = symbols.stream()
            .map(sm -> sm.groupingDirection).distinct().collect(UtilCollectors.singleItem());
        assert dirn == GroupingDirection.LeftToRight: "RTL/unknown operators not implemented yet"; // TODO
        Map<String, SymbolInfo> infixToSymbolInfo = symbols.stream().collect(  // TODO pre-compute/cache these
            Collectors.toUnmodifiableMap(
                si -> Objects.requireNonNull(si.infix, "null infix not allowed for parseInfixPrecedenceLevel"),
                Function.identity()));
        String[] infixesToFind = sortByLength(infixToSymbolInfo.keySet().toArray(String[]::new));
        MathSymbol left = parseInfixPrecedenceLevel(level - 1);
        String op;

        discardWhitespace();
        while((op = discardMatchesNextAny_optionsSorted(infixesToFind)) != null) {
            SymbolInfo opInfo = Objects.requireNonNull(infixToSymbolInfo.get(op));
            MathSymbol right = parseInfixPrecedenceLevel(level - 1);
            BinOpBiConstructor<?> ctor = opInfo.getBiConstructor();
            left = ctor.construct(left, right);
            discardWhitespace();
        }
        return left;
        // TODO what if dirn == null? Maybe just disallow the ambiguous case of > 2 operands in same level
    }

    // region utils
    protected CharSequence strFromHere() {
        return CharBuffer.wrap(src, idx, src.length());
    }

    boolean notEof() {
        return idx < src.length();
    }
    boolean isEof() {
        return idx >= src.length();
    }

    char peek() {
        return src.charAt(idx);
    }
    char advance() {
        return src.charAt(idx++);
    }
    boolean advanceIf(@NotNull Function<Character, Boolean> predicate) {
        boolean doAdvance = predicate.apply(peek());
        if(doAdvance) ++idx;
        return doAdvance;
    }

    /**
     * @param predicate Keep advancing while this returns true
     * @return Amount of spaces advanced
     */
    @SuppressWarnings("UnusedReturnValue")
    int advanceWhile(@NotNull Function<Character, Boolean> predicate) {
        int n = 0;
        while (notEof() && advanceIf(predicate)) ++n;
        return n;
    }

    protected void discardN(@Range(from = 0, to = Integer.MAX_VALUE) int n) {
        idx += n;
    }

    protected void discardWhitespace() {
        advanceWhile(Character::isWhitespace);
    }

    protected void advanceExpectNext(char expected) {
        char actual = advance();
        if(actual != expected) throw new ExprParseRtException("Expected '%c', got '%c'".formatted(expected, actual));
    }

    protected boolean matchesNext(@NotNull String expected) {
        return src.startsWith(expected, /*start*/idx);
    }
    
    private @NotNull String @NotNull [] sortByLength(@NotNull String @NotNull[] arr) {
        return Arrays.stream(arr).sorted(Comparator.comparingInt(String::length).reversed()).toArray(String[]::new);
    }
    private @Nullable String matchesNextAny_optionsSorted(@NotNull String... expected) {
        return Arrays.stream(expected).filter(this::matchesNext).findFirst().orElse(null);
    }
    private @Nullable String discardMatchesNextAny_optionsSorted(@NotNull String... expected) {
        String s = Arrays.stream(expected).filter(this::matchesNext).findFirst().orElse(null);
        if(s != null) discardN(s.length());
        return s;
    }
    protected @Nullable String matchesNextAny(@NotNull String... expected) {
        // Try to longer ones first, then shorter ones.
        return matchesNextAny_optionsSorted(
            Arrays.stream(expected).sorted(Comparator.comparingInt(String::length).reversed()).toArray(String[]::new));
    }
    // endregion

    @Contract(pure = true)
    private static<K, V, R> @NotNull Function<Entry<K, V>, Entry<K, R>> _valueTransformer(Function<V, R> fn) {
        return e -> _makeEntry(e.getKey(), fn.apply(e.getValue()));
    }
    @Contract(value = "_, _ -> new", pure = true)
    private static @NotNull <K, V> Entry<K, V> _makeEntry(K k, V v) {
        return new SimpleEntry<>(k, v);
    }
}
