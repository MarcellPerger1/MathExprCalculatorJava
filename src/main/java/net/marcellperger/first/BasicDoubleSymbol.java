package net.marcellperger.first;

import java.util.Objects;

public class BasicDoubleSymbol implements LeafNode {
    double value;

    @Override
    public String toString() {
        return "BasicDoubleSymbol{value=" + value + '}';
    }

    public BasicDoubleSymbol(double value) {
        this.value = value;
    }

    @Override
    public double calculateValue() {
        return value;
    }

    @Override
    public String fmt() {
        return String.valueOf(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BasicDoubleSymbol that = (BasicDoubleSymbol) o;
        return Double.compare(value, that.value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
