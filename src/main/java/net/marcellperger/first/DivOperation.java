package net.marcellperger.first;


public class DivOperation extends LTRBinaryOperationLeftRight {
    public DivOperation(MathSymbol left_, MathSymbol right_) {
        super(left_, right_);
    }

    @Override
    public double calculateValue() {
        return left.calculateValue() / right.calculateValue();
    }
}
