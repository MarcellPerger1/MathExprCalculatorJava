package net.marcellperger.mathexpr;

import org.jetbrains.annotations.Nullable;

public abstract class LTRBinaryOperationLeftRight extends BinaryOperationLeftRight {
    public LTRBinaryOperationLeftRight(MathSymbol left_, MathSymbol right_) {
        super(left_, right_);
    }

    @Override
    public @Nullable GroupingDirection getGroupingDirectionInst() {
        return GroupingDirection.LeftToRight;  // (((2 + 3) + 4) + 5)
    }
}
