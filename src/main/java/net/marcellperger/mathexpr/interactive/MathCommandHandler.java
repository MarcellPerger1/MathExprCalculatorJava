package net.marcellperger.mathexpr.interactive;


import net.marcellperger.mathexpr.MathSymbol;
import net.marcellperger.mathexpr.parser.ExprParseException;
import net.marcellperger.mathexpr.parser.Parser;
import net.marcellperger.mathexpr.util.ControlFlowBreak;
import net.marcellperger.mathexpr.util.MathUtil;

public class MathCommandHandler implements ShellCommandHandler {
    @Override
    public boolean run(String cmd, Shell sh) {
        try {
            MathSymbol sym = parseOrPrintError(cmd, sh);
            sh.out.println(MathUtil.roundToSigFigs(sym.calculateValue(), sh.roundToSf));
        } catch (ControlFlowBreak _parseErrorAlreadyPrinted) {}
        return true;  // Always continue
    }

    MathSymbol parseOrPrintError(String cmd, Shell sh) throws ControlFlowBreak {
        try {
            return new Parser(cmd).parse();
        } catch (ExprParseException e) {
            sh.out.println(e);
            throw new ControlFlowBreak();
        }
    }
}
