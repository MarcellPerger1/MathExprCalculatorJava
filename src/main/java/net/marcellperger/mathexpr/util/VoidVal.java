package net.marcellperger.mathexpr.util;

import org.jetbrains.annotations.NotNull;


/** The equivalent of Rust's {@code ()} or Python's {@code NoneType}/{@code None}
 * or a {@code void} type in Java but can be passed as a type arg
 * when e.g. you don't actually want to return anything but {@code Function} interface
 * requires it to return instances of a type. Because 'no value' is a type that has
 * EXACTLY ONE instance: nothing ({@code None} / {@code ()} / {@code undefined})*/
@SuppressWarnings("InstantiationOfUtilityClass")  // not a so-called 'utility class'; Pycharm just thinks it is
public final class VoidVal {
    static @NotNull VoidVal INST = new VoidVal();

    private VoidVal() {}

    public static VoidVal val() {
        return INST;
    }
}
