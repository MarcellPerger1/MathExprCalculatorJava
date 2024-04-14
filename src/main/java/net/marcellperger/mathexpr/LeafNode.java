package net.marcellperger.mathexpr;

import org.jetbrains.annotations.Nullable;

public interface LeafNode extends MathSymbol {
    @Override
    default String fmtWithParensIfRequired(@Nullable Integer outerPrecedence, boolean ifEqualPrecedence) {
        return fmt();  // This really shouldn't require parens around this
    }
}
