package net.marcellperger.mathexpr.interactive;

import net.marcellperger.mathexpr.util.Input;
import net.marcellperger.mathexpr.util.InputClosedException;

import java.io.PrintStream;

public class Shell {
    public Input in;
    public PrintStream out;
    ShellCommandParser commandParser;

    public Shell() {
        in = new Input();
        out = System.out;
        commandParser = new ShellCommandParser(this);
    }

    public static void main(String[] args) {
        new Shell().run();
    }

    public void run() {
        displayStartupMessage();
        //noinspection StatementWithEmptyBody
        while (getAndRunCommand()) {}
    }

    public void displayStartupMessage() {
        out.println("MathExpr console v0.1.0-alpha.1 (type \"!exit\" to exit).");
    }

    public /*returns wantMore */boolean getAndRunCommand() {
        String inp;
        try {
            inp = in.getInput(">? ");
        } catch (InputClosedException e) {
            return false;
        }
        // This .strip() is surprisingly important - it allows the .split("\\s") to work properly
        return runCommand(inp.strip());
    }

    public boolean runCommand(String cmd) {
        return commandParser.dispatchCommand(cmd).run(cmd, this);
    }
}
