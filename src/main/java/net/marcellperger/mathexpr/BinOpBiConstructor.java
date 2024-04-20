package net.marcellperger.mathexpr;


// TODO: this is stupid - it should not be generic!!! It should return a MathSymbol always
//  (not a specific subclass but exactly a `impl MathSymbol` that MUST BE CAST)
//  i.e. we want this to be like `MathSymbol construct(MathSymbol left, MathSymbol right)`
//  similar to `List<T> of(T... values)` is NOT `(? extends List<T>) of(T... values)`
//  (or maybe could be generic on method? but probably bad idea)
//  This is NOT rust, you don't need to make it explicit `impl MathSymbol`
//  i.e. currently:
//    trait BinOpBinConstructor<R: MathSymbol> {
//        fn construct(MS left, MS right) -> R;
//    }
//  and we want:
//    trait BinOpBiConstructor {
//        fn construct(MS left, MS right) -> (dyn?) impl MS;
//    }
@FunctionalInterface
public interface BinOpBiConstructor<R extends MathSymbol> {
    R construct(MathSymbol left, MathSymbol right);
}
