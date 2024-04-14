package net.marcellperger.mathexpr;

public class SubOperation extends LTRBinaryOperationLeftRight {
    public SubOperation(MathSymbol left_, MathSymbol right_) {
        super(left_, right_);
    }

    @Override
    public double calculateValue() {
        return left.calculateValue() - right.calculateValue();
    }
}
