package net.marcellperger.mathexpr.util;

/**
 * The equivalent to Rust's {@code std::ops::ControlFlow::Break}.
 * This is an exception that signals an operation exiting early.
 */
public class ControlFlowBreak extends Throwable {
    public ControlFlowBreak() {}
}
