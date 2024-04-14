package net.marcellperger.mathexpr;

// I wish I could put these on 1 line like in Rust like
// import org.jetbrains.annotations.{Contract, NotNull, Nullable}
// This would be especially nice as the import paths are ENORMOUS
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Adds the {@link #left} and {@link #right} fields along with getters (TODO setters?)
 * and a better {@link #fmt()} (using {@link #getInfixInst()} and {@link #getGroupingDirectionInst()})
 */
public abstract class BinaryOperationLeftRight implements BinaryOperation {
    MathSymbol left;
    MathSymbol right;

    public BinaryOperationLeftRight(MathSymbol left_, MathSymbol right_) {
        left = left_;
        right = right_;
    }

    @Override
    public MathSymbol getLeft() {
        return left;
    }

    @Override
    public MathSymbol getRight() {
        return right;
    }

    @Contract(pure = true)
    public @Nullable String getInfixInst() {
        return SymbolInfo.infixFromClass(getClass());
    }

    @Contract(pure = true)
    public @Nullable GroupingDirection getGroupingDirectionInst() {
        return SymbolInfo.groupingDirectionFromClass(getClass());
    }

    @Override
    public String fmt() {
        String infix = getInfixInst();
        if(infix == null) return BinaryOperation.super.fmt(); // fallback
        return "%s %s %s".formatted(
                left.fmtWithParensIfRequired(instPrecedenceInt(), _parensRequiredIfEqual(LeftRight.LEFT)),
                infix,
                right.fmtWithParensIfRequired(instPrecedenceInt(), _parensRequiredIfEqual(LeftRight.RIGHT)));
    }

    protected boolean _parensRequiredIfEqual(@NotNull LeftRight side) {
//        return true;
        // This code be lookin' proper Rust-y
        return switch (getGroupingDirectionInst()) {
            case null -> true;
            case LeftToRight -> side == LeftRight.RIGHT;  // e.g. 1 - 2 + 3 == (1 - 2) + 3 != 1 - (2 + 3)
            case RightToLeft -> side == LeftRight.LEFT;  // e.g. 2**3**4 == 2**(3**4) != (2**3)**4
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BinaryOperationLeftRight that = (BinaryOperationLeftRight) o;
        return Objects.equals(left, that.left) && Objects.equals(right, that.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{left=" + left + ", right=" + right + '}';
    }
}
