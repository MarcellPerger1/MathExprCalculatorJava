package net.marcellperger.mathexpr;


@FunctionalInterface
public interface BinOpBiConstructor {
    // NOTE: Not generic, but implementations will just return `MathSymbol`
    // (which is allowed to be any subclass of MathSymbol) similar to
    // `List<T> of(T... values)` can return any `List` implementation
    MathSymbol construct(MathSymbol left, MathSymbol right);
}
