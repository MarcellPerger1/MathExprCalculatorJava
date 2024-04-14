package net.marcellperger.mathexpr;


public class AddOperation extends LTRBinaryOperationLeftRight {
    public AddOperation(MathSymbol left_, MathSymbol right_) {
        super(left_, right_);
    }

    @Override
    public double calculateValue() {
        return left.calculateValue() + right.calculateValue();
    }
}
