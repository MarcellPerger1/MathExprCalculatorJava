package net.marcellperger.mathexpr.interactive;

import net.marcellperger.mathexpr.MathSymbol;
import net.marcellperger.mathexpr.parser.ExprParseException;
import net.marcellperger.mathexpr.parser.Parser;
import net.marcellperger.mathexpr.util.Input;
import net.marcellperger.mathexpr.util.MathUtil;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.NoSuchElementException;

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

    // TODO run() until exit better - parse exit command, more robust/extensible command handling system

    public void run() {
        //noinspection StatementWithEmptyBody
        while (getAndRunCommand()) {}
    }

    public /*returns wantMore */boolean getAndRunCommand() {
        String inp;
        try {
            inp = in.getInput(">? ");
        } catch (NoSuchElementException e) {
            return false;
        }
        runCommand(inp);
        return true;
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
