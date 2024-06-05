package net.marcellperger.mathexpr.cli;

import java.util.Arrays;

public class CLI {
    public static void main(String[] args) {
        System.out.println("args = " + Arrays.stream(args).map(s -> "`" + s + "`").toList());
    }
}
