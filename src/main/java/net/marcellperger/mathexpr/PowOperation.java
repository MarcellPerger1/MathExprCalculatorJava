package net.marcellperger.mathexpr;

public class PowOperation extends BinaryOperationLeftRight {
    public PowOperation(MathSymbol left_, MathSymbol right_) {
        super(left_, right_);
    }

    @Override
    public double calculateValue() {
        return Math.pow(left.calculateValue(), right.calculateValue());
    }
}
