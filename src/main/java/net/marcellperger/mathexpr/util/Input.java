package net.marcellperger.mathexpr.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Objects;
import java.util.Scanner;

public class Input {
    @NotNull Scanner sc;
    @Nullable PrintStream ps;

    public Input() {
        this(System.in, System.out);
    }
    public Input(@NotNull InputStream iStream) {
        this(new Scanner(iStream));
    }
    public Input(Readable readable) {
        this(new Scanner(readable));
    }
    public Input(String s) {
        this(new Scanner(s));
    }
    public Input(@NotNull Scanner scanner) {
        this(scanner, null);
    }
    public Input(InputStream iStream, @Nullable OutputStream oStream) {
        this(new Scanner(iStream), oStream);
    }
    public Input(@Nullable OutputStream oStream) {
        this(System.in, oStream);
    }
    public Input(@NotNull Scanner scanner, @Nullable OutputStream oStream) {
        this(scanner, oStream == null ? null : new PrintStream(oStream));
    }
    public Input(@NotNull Scanner scanner, @Nullable PrintStream pStream) {
        sc = scanner;
        ps = pStream;
    }

    public String getInput() {
        return sc.nextLine();
    }
    public String getInput(String prompt) {
        Objects.requireNonNull(ps, "An OutputStream is required to use Input.getInput(prompt)").print(prompt);
        return sc.nextLine();
    }

    public static String input() {
        // Don't cache `new Input()` - System.in could be changed
        return new Input().getInput();
    }
    public static String input(@NotNull InputStream iStream) {
        return new Input(iStream).getInput();
    }
    public static String input(String prompt) {
        return new Input().getInput(prompt);
    }
    public static String input(String prompt, @NotNull OutputStream oStream) {
        return new Input(oStream).getInput(prompt);
    }
    public static String input(String prompt, @NotNull InputStream iStream, @NotNull OutputStream oStream) {
        return new Input(iStream, oStream).getInput(prompt);
    }
}
