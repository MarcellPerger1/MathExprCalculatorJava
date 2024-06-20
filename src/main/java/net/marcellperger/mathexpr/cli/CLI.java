package net.marcellperger.mathexpr.cli;

import net.marcellperger.mathexpr.MathSymbol;
import net.marcellperger.mathexpr.cli.minicli.CLIOption;
import net.marcellperger.mathexpr.cli.minicli.CLIParseException;
import net.marcellperger.mathexpr.cli.minicli.MiniCLI;
import net.marcellperger.mathexpr.interactive.Shell;
import net.marcellperger.mathexpr.parser.ExprParseException;
import net.marcellperger.mathexpr.parser.Parser;
import net.marcellperger.mathexpr.util.MathUtil;
import net.marcellperger.mathexpr.util.Util;


public class CLI {
    MiniCLI cli;
    CLIOption<Boolean> interactive;
    CLIOption<String> rounding;
    Integer roundSf;

    public CLI() {
        cli = new MiniCLI();
        interactive = cli.addBooleanOption("-i", "--interactive");
        rounding = cli.addStringOption("-R", "--round-sf").setDefault("12");
        cli.setPositionalArgCount(0, 1);
    }

    public void run(String[] args) {
        try {
            _run(args);
        } catch (CLIParseException exc) {
            System.err.println("Invalid CLI arguments: " + exc);
        }
    }
    protected void _run(String[] args) {
        parseArgs(args);
        if(interactive.getValue() || cli.getPositionalArgs().isEmpty()) runInteractive(roundSf);
        else runEvaluateExpr(roundSf);
    }

    private void parseArgs(String[] args) {
        cli.parseArgs(args);
        try {
            roundSf = Integer.parseInt(rounding.getValue());
        } catch (NumberFormatException e) {
            throw new CLIParseException(e);
        }
    }

    private void runEvaluateExpr(int roundSf) {
        if(cli.getPositionalArgs().isEmpty())
            throw new CLIParseException("1 argument expected without -i/--interactive");
        MathSymbol sym;
        try {
            sym = new Parser(cli.getPositionalArgs().getFirst()).parse();
        } catch (ExprParseException exc) {
            // TODO this is a bit meh solution here
            Util.throwAsUnchecked(exc);  // Trick Java but I want call-site checked exceptions
            return;
        }
        System.out.println(MathUtil.roundToSigFigs(sym.calculateValue(), roundSf));
    }

    private void runInteractive(int roundSf) {
        if(!cli.getPositionalArgs().isEmpty())
            throw new CLIParseException("No argument expected with -i/--interactive");
        new Shell(roundSf).run();
    }

    public static void main(String[] args) {
        new CLI().run(args);
    }
}
