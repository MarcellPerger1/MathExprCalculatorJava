package net.marcellperger.mathexpr.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.NoSuchElementException;
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

    public String getInput() throws InputClosedException {
        try {
            return sc.nextLine();
        } catch (NoSuchElementException e) {
            throw new InputClosedException("Input was closed (Ctrl+C, Ctrl+D, etc.)", e);
        }
    }
    public String getInput(String prompt) throws InputClosedException {
        Objects.requireNonNull(ps, "An OutputStream is required to use Input.getInput(prompt)").print(prompt);
        return getInput();
    }

    public static String input() throws InputClosedException {
        // Don't cache `new Input()` - System.in could be changed
        return new Input().getInput();
    }
    public static String input(@NotNull InputStream iStream) throws InputClosedException {
        return new Input(iStream).getInput();
    }
    public static String input(String prompt) throws InputClosedException {
        return new Input().getInput(prompt);
    }
    public static String input(String prompt, @NotNull OutputStream oStream) throws InputClosedException {
        return new Input(oStream).getInput(prompt);
    }
    public static String input(String prompt, @NotNull InputStream iStream, @NotNull OutputStream oStream) throws InputClosedException {
        return new Input(iStream, oStream).getInput(prompt);
    }
}
