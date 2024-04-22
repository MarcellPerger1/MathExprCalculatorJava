package net.marcellperger.mathexpr.util;

import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;

public class Pair<T, U> {
    public T left;
    public U right;

    public Pair(T left, U right) {
        this.left = left;
        this.right = right;
    }

    /**
     * This is a workaround for Java not having parameter unpacking/similar.
     * So anything that uses the unpacked variables goes inside, this is
     * very Haskell-y ({@code let ... in ...})
     * <p>
     * Java: <pre>{@code pair.asVars((myLeft, myRight) -> {<function body>})}</pre>
     * Python: <pre>{@code
     * myLeft, myRight = pair
     * <function body>
     * }</pre>
     * Haskell: <pre>{@code
     * let (myLeft, myRight)=pair in <function body>
     * }</pre>
     */
    public <R> R asVars(@NotNull BiFunction<? super T, ? super U, ? extends R> fn) {
        return fn.apply(left, right);
    }
}
