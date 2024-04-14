package net.marcellperger.mathexpr;

public class MulOperation extends LTRBinaryOperationLeftRight {
    public MulOperation(MathSymbol left_, MathSymbol right_) {
        super(left_, right_);
    }

    @Override
    public double calculateValue() {
        return left.calculateValue() * right.calculateValue();
    }
}
