package net.marcellperger.mathexpr;


import net.marcellperger.mathexpr.util.Util;
import org.jetbrains.annotations.Nullable;

// TODO maybe make it required to have an entry in SymbolInfo
public interface MathSymbol {
    double calculateValue();
    String fmt();

    // Ensure that subclasses don't forget to implement this - needed for tests!!!
    @Override
    boolean equals(Object o);

    /**
     * The {@link Object#getClass()} requires this to be a non-static method
     * ({@code this} is not provided to static methods) so this is
     * just for a nicer syntax when we DO have an instance
     */
    default @Nullable Integer instPrecedenceInt() {
        return Util.chainNulls(SymbolInfo.fromClass(this.getClass()), s -> s.precedence);
    }

    default String fmtAlwaysParens() {
        return "(" + fmt() + ")";
    }

    default String fmtWithParensIfRequired(@Nullable Integer outerPrecedence, boolean ifEqualPrecedence) {
        Integer innerPrecedence = this.instPrecedenceInt();
        if(innerPrecedence == null || outerPrecedence == null) return fmt();
        // if `inner` would be done AFTER `outer` (when no parens),
        //  need to insert parens to ensure `inner` done first THEN `outer`
        // If same precedence, use arg - will depend on the exact piece
        if(innerPrecedence > outerPrecedence || (innerPrecedence.equals(outerPrecedence) && ifEqualPrecedence)) {
            return fmtAlwaysParens();
        }
        return fmt();
    }
    default String fmtWithParensIfRequired(@Nullable Integer outerPrecedence) {
        return fmtWithParensIfRequired(outerPrecedence, true);
    }
}
