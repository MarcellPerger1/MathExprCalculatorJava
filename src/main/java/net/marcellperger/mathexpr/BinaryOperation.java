package net.marcellperger.mathexpr;

public interface BinaryOperation extends MathSymbol {
    MathSymbol getLeft();
    MathSymbol getRight();

    @Override
    default String fmt() {
        return "%s(%s, %s)".formatted(this.getClass().getSimpleName(), getLeft().fmt(), getRight().fmt());
    }

    static MathSymbol construct(MathSymbol left, SymbolInfo op, MathSymbol right) {
        return op.getBiConstructor().construct(left, right);
    }
}
