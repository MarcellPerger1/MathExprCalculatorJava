package net.marcellperger.first;

public interface BinaryOperation extends MathSymbol {
    MathSymbol getLeft();
    MathSymbol getRight();

    @Override
    default String fmt() {
        return "%s(%s, %s)".formatted(this.getClass().getSimpleName(), getLeft().fmt(), getRight().fmt());
    }
}
