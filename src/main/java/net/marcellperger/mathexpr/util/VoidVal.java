package net.marcellperger.mathexpr.util;

import org.jetbrains.annotations.NotNull;


/** The equivalent of Rust's {@code ()} or Python's {@code NoneType}/{@code None}
 * or a {@code void} type in Java but can be passed as a type arg
 * when e.g. you don't actually want to return anything but {@code Function} interface
 * requires it to return instances of a type. Because 'no value' is a type that has
 * EXACTLY ONE instance: nothing ({@code None} / {@code ()} / {@code undefined})*/
public final class VoidVal {
    static final @NotNull VoidVal INST = new VoidVal();

    @SuppressWarnings("ConstantValue")  // This is actually called to initialize it to non-null
    private VoidVal() {
        if(INST != null) throw new AssertionError(
            "new VoidVal() should only be called once to create VoidVal.INST");
    }

    public static VoidVal val() {
        return INST;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;  // there can only be one.
    }
}
