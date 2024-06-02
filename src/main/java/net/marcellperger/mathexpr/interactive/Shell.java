package net.marcellperger.mathexpr.interactive;

import net.marcellperger.mathexpr.MathSymbol;
import net.marcellperger.mathexpr.parser.ExprParseException;
import net.marcellperger.mathexpr.parser.Parser;
import net.marcellperger.mathexpr.util.Input;
import net.marcellperger.mathexpr.util.MathUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.Arrays;

import static net.marcellperger.mathexpr.util.Input.input;

public class Shell {
    Input in;
    PrintStream out;

    public Shell() {
        in = new Input();
        out = System.out;
    }

    public static void main(String[] args) {
        new Shell().run();
    }

    // TODO run() until exit better - parse exit command/Ctrl+C
    // TODO fails badly with unchecked exception on parsing `12 + (`

    public void run() {
        //noinspection InfiniteLoopStatement
        while (true) getAndRunCommand();
    }

    public void getAndRunCommand() {
        runCommand(in.getInput(">? "));
    }
    public void runCommand(String cmd) {
        @Nullable MathSymbol sym = parseCmdOrPrintError(cmd);
        if(sym == null) return;
        double value = sym.calculateValue();
        out.println(MathUtil.roundToSigFigs(value, 10));
    }
    public @Nullable MathSymbol parseCmdOrPrintError(String cmd) {
        // No special commands so pass straight to parser
        Parser p = new Parser(cmd);
        try {
            return p.parse();
        } catch (ExprParseException exc) {
            out.println(exc);
            return null;
        }
    }
}
