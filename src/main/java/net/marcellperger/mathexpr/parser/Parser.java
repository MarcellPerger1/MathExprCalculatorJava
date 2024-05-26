package net.marcellperger.mathexpr.parser;

import net.marcellperger.mathexpr.*;
import net.marcellperger.mathexpr.util.Pair;
import net.marcellperger.mathexpr.util.Util;
import net.marcellperger.mathexpr.util.rs.Result;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Parser {
    protected String src;
    /** Next index to parse */
    protected int idx;

    public Parser(String src_) {
        src = src_;
        idx = 0;
    }

    public MathSymbol parse() throws ExprParseException {
        MathSymbol sym = parseExpr();
        discardWhitespace();
        if(notEof()) throw new ExprParseException("Syntax error: didn't reach end of input");
        return sym;
    }

    public MathSymbol parseExpr() throws ExprParseException {
        return parseInfixPrecedenceLevel(SymbolInfo.MAX_PRECEDENCE);
    }

    // https://regex101.com/r/2EogTA/1
    protected static final Pattern DOUBLE_RE = Pattern.compile("^([+-]?)(\\d*\\.\\d+|\\d+\\.?)(?:[eE]([+-]?\\d+))?");
    @SuppressWarnings("unused")  // ignore for now - will fix later with the better error handling
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
    public @NotNull Result<MathSymbol, Throwable> parseDoubleLiteral_result() {
        discardWhitespace();
        Matcher m = DOUBLE_RE.matcher(strFromHere());
        if (!m.lookingAt()) return Result.fromExc(new ExprParseException("Invalid number"));
        String s = m.group();
        idx += s.length();
        double value;
        try {
            value = Double.parseDouble(s);
        } catch (NumberFormatException exc) {
            // Technically this should never happen - assuming I've got that regex right
            throw new AssertionError(
                "There is a problem with the regex, this should've been rejected earlier", exc);
        }
        return Result.newOk(new BasicDoubleSymbol(value));
    }

    public @Nullable MathSymbol parseParensOrLiteral() throws ExprParseException {
        discardWhitespace();
        if(peek() == '(') return parseParens();
        // TODO add a better error handling system - don't want to maintain 2 versions of each function
        //  returning null: +easier to do unions, +no need for verbose try/catch, -no info about errors
        return parseDoubleLiteral_null();
    }

    public @Nullable MathSymbol parseParens() throws ExprParseException {
        advanceExpectNext_ignoreWs('(');
        MathSymbol sym = parseExpr();
        advanceExpectNext_ignoreWs(')');
        return sym;
    }

    public MathSymbol parseInfixPrecedenceLevel(int level) throws ExprParseException {
        discardWhitespace();
        if(level == 0) return parseParensOrLiteral();
        PrecedenceLevelInfo precInfo = SymbolInfo.PREC_LEVELS_INFO.get(level);
        if (precInfo == null) return parseInfixPrecedenceLevel(level - 1);
        MathSymbol left = parseInfixPrecedenceLevel(level - 1);
        return switch (precInfo.dirn) {
            case LeftToRight -> parseInfixPrecedenceLevel_LTR(left, precInfo);
            case RightToLeft -> parseInfixPrecedenceLevel_RTL(left, precInfo);
            case null -> parseInfixPrecedenceLevel_noDirn(left, precInfo);
        };
    }

    private MathSymbol parseInfixPrecedenceLevel_RTL(MathSymbol left, PrecedenceLevelInfo precInfo) throws ExprParseException {
        String op;
        List<Pair<SymbolInfo, MathSymbol>> otherOps = new ArrayList<>();
        while((op = discardMatchesNextAny_optionsSorted_removeWs(precInfo.sortedInfixes)) != null) {
            otherOps.add(new Pair<>(
                Util.getNotNull(precInfo.infixToSymbolMap, op),
                parseInfixPrecedenceLevel(precInfo.precedence - 1)));
        }
        return otherOps.reversed().stream().reduce((rightpair, leftpair) ->
            leftpair.asVars((preOp, argL) ->
                new Pair<>(preOp, rightpair.asVars((midOp, argR) -> BinaryOperation.construct(argL, midOp, argR))))
        ).map(p -> p.asVars((midOp, argR) -> BinaryOperation.construct(left, midOp, argR))).orElse(left);
    }

    private MathSymbol parseInfixPrecedenceLevel_LTR(MathSymbol left, PrecedenceLevelInfo precInfo) throws ExprParseException {
        String op;
        while((op = discardMatchesNextAny_optionsSorted_removeWs(precInfo.sortedInfixes)) != null) {
            left = BinaryOperation.construct(
                left, Util.getNotNull(precInfo.infixToSymbolMap, op), parseInfixPrecedenceLevel(precInfo.precedence - 1));
        }
        return left;
    }

    private MathSymbol parseInfixPrecedenceLevel_noDirn(MathSymbol left, PrecedenceLevelInfo precInfo) throws ExprParseException {
        String op;
        if((op = discardMatchesNextAny_optionsSorted_removeWs(precInfo.sortedInfixes)) == null) return left;
        MathSymbol result = BinaryOperation.construct(
            left, Util.getNotNull(precInfo.infixToSymbolMap, op), parseInfixPrecedenceLevel(precInfo.precedence - 1));
        if(matchesNextAny_optionsSorted_removeWs(precInfo.sortedInfixes) != null) {
            throw new ExprParseException("Error: parens are required for precedence levels without a GroupingDirection");
        }
        return result;
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
    protected int advanceWhile(@NotNull Function<Character, Boolean> predicate) {
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
    protected void advanceExpectNext_ignoreWs(char expected) {
        discardWhitespace();
        advanceExpectNext(expected);
    }

    protected boolean matchesNext(@NotNull String expected) {
        return src.startsWith(expected, /*start*/idx);
    }
    
    private @NotNull List<@NotNull String> sortedByLength(@NotNull List<@NotNull String> arr) {
        return arr.stream().sorted(Comparator.comparingInt(String::length).reversed()).toList();
    }
    private @Nullable String matchesNextAny_optionsSorted(@NotNull List<@NotNull String> expected) {
        return expected.stream().filter(this::matchesNext).findFirst().orElse(null);
    }
    private @Nullable String matchesNextAny_optionsSorted_removeWs(@NotNull List<@NotNull String> expected) {
        discardWhitespace();
        return matchesNextAny_optionsSorted(expected);
    }
    private @Nullable String discardMatchesNextAny_optionsSorted(@NotNull List<@NotNull String> expected) {
        String s = matchesNextAny_optionsSorted(expected);
        if(s != null) discardN(s.length());
        return s;
    }
    private @Nullable String discardMatchesNextAny_optionsSorted_removeWs(@NotNull List<@NotNull String> expected) {
        discardWhitespace();
        return discardMatchesNextAny_optionsSorted(expected);
    }
    @SuppressWarnings("unused")
    protected @Nullable String matchesNextAny(@NotNull List<@NotNull String> expected) {
        // Try to longer ones first, then shorter ones.
        return matchesNextAny_optionsSorted(sortedByLength(expected));
    }
    @SuppressWarnings("unused")
    protected @Nullable String discardMatchesNextAny(@NotNull List<@NotNull String> expected) {
        // Try to longer ones first, then shorter ones.
        return discardMatchesNextAny_optionsSorted(sortedByLength(expected));
    }
    // endregion
}
