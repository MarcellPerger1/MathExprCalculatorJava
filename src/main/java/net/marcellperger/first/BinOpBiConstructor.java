package net.marcellperger.first;


@FunctionalInterface
public interface BinOpBiConstructor<R extends MathSymbol> {
    R construct(MathSymbol left, MathSymbol right);
}
