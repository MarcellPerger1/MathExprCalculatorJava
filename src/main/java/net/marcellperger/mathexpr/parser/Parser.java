package net.marcellperger.mathexpr.parser;

import net.marcellperger.mathexpr.*;
import net.marcellperger.mathexpr.util.Pair;
import net.marcellperger.mathexpr.util.Util;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.regex.MatchResult;
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
    public @NotNull MathSymbol parseDoubleLiteral() throws ExprParseException {
        discardWhitespace();
        String s = matchNextRegexString(DOUBLE_RE, "Invalid number (double)");
        try {
            return new BasicDoubleSymbol(Double.parseDouble(s));
        } catch (NumberFormatException exc) {
            throw new AssertionError("There is a problem with the regex," +
                " this should've been rejected earlier", exc);
        }
    }

    public @NotNull MathSymbol parseParensOrLiteral() throws ExprParseException {
        discardWhitespace();
        return peekExpect() == '(' ? parseParens() : parseDoubleLiteral();
    }

    public MathSymbol parseParens() throws ExprParseException {
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

    public boolean notEof() {
        return idx < src.length();
    }
    public boolean isEof() {
        return idx >= src.length();
    }

    protected char peekAssert() {
        return src.charAt(idx);
    }
    protected char peekExpect() throws ExprParseEofException {
        if(isEof()) throw new ExprParseEofException("Unexpected end of input");
        return src.charAt(idx);
    }
    protected char advanceAssert() {
        return src.charAt(idx++);
    }
    protected char advanceExpect() throws ExprParseEofException {
        if(isEof()) throw new ExprParseEofException("Unexpected end of input");
        return src.charAt(idx++);
    }
    @SuppressWarnings("unused")
    protected boolean advanceIf(@NotNull Function<Character, Boolean> predicate) throws ExprParseEofException {
        boolean doAdvance = predicate.apply(peekExpect());
        if(doAdvance) ++idx;
        return doAdvance;
    }
    protected boolean advanceIfAssert(@NotNull Function<Character, Boolean> predicate) {
        boolean doAdvance = predicate.apply(peekAssert());
        if(doAdvance) ++idx;
        return doAdvance;
    }

    /**
     * @param predicate Keep advancing while this returns true
     * @return Amount of spaces advanced
     */
    protected int advanceWhile(@NotNull Function<Character, Boolean> predicate) {
        int n = 0;
        while (notEof() && advanceIfAssert(predicate)) ++n;
        return n;
    }

    protected void discardN(@Range(from = 0, to = Integer.MAX_VALUE) int n) {
        idx += n;
    }

    protected void discardWhitespace() {
        advanceWhile(Character::isWhitespace);
    }

    protected void advanceExpectNext(char expected) throws ExprParseException {
        char actual = advanceExpect();
        if(actual != expected) throw new ExprParseException("Expected '%c', got '%c'".formatted(expected, actual));
    }
    protected void advanceExpectNext_ignoreWs(char expected) throws ExprParseException {
        discardWhitespace();
        advanceExpectNext(expected);
    }

    protected MatchResult matchNextRegexResult(@NotNull Pattern pat, ExprParseException exc) throws ExprParseException {
        Matcher m = pat.matcher(strFromHere());
        if (!m.lookingAt()) throw exc;
        String s = m.group();
        idx += s.length();
        return m.toMatchResult();
    }
    protected MatchResult matchNextRegexResult(@NotNull Pattern pat, String exc) throws ExprParseException {
        return matchNextRegexResult(pat, new ExprParseException(exc));
    }
    protected MatchResult matchNextRegexResult(@NotNull Pattern pat) throws ExprParseException {
        return matchNextRegexResult(pat, "Regex should've been matched");
    }
    @SuppressWarnings("unused")
    protected String matchNextRegexString(@NotNull Pattern pat, ExprParseException exc) throws ExprParseException {
        return matchNextRegexResult(pat, exc).group();
    }
    @SuppressWarnings("SameParameterValue")
    protected String matchNextRegexString(@NotNull Pattern pat, String msg) throws ExprParseException {
        return matchNextRegexResult(pat, msg).group();
    }
    @SuppressWarnings("unused")
    protected String matchNextRegexString(@NotNull Pattern pat) throws ExprParseException {
        return matchNextRegexResult(pat).group();
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
