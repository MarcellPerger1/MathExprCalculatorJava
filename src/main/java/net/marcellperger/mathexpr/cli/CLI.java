package net.marcellperger.mathexpr.cli;

import net.marcellperger.mathexpr.cli.minicli.CLIOption;
import net.marcellperger.mathexpr.cli.minicli.MiniCLI;

import java.util.Arrays;

public class CLI {
    public static void main(String[] args) {
        MiniCLI cli = new MiniCLI();
        CLIOption<Boolean> interactive = cli.addBooleanOption("-i", "--interactive");
        CLIOption<String> rounding = cli.addStringOption("-R", "--round-sf").setDefault("12");
        cli.setPositionalArgCount(0, 1);
        cli.parseArgs(args);
        System.out.println("interactive = " + interactive.getValue());
        System.out.println("rounding = " + rounding.getValue());
        System.out.println("args = " + Arrays.stream(args).map(s -> "`" + s + "`").toList());
    }
}
