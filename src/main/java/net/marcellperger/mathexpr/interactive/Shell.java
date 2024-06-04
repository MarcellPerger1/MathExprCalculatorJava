package net.marcellperger.mathexpr.interactive;

import net.marcellperger.mathexpr.util.Input;
import net.marcellperger.mathexpr.util.InputClosedException;
import net.marcellperger.mathexpr.util.Pair;
import net.marcellperger.mathexpr.util.UnreachableError;
import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;

public class Shell {
    public Input in;
    public PrintStream out;

    public Shell() {
        in = new Input();
        out = System.out;
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

    // TODO should this really be in the Shell class? - SOLID and all that
    /**
     * Splits {@code cmd} into command and arguments
     */
    @NotNull Pair<@NotNull String, @NotNull String> splitCommand(@NotNull String cmd) {
        String[] words = cmd.split("\\s+", 2);
        return switch (words.length) {
            case 0 -> new Pair<>("", "");
            case 1 -> new Pair<>(words[0], "");
            case 2 -> new Pair<>(words[0], words[1]);
            default -> throw new UnreachableError();
        };
    }
    @NotNull String getCommandName(String cmd) {
        return splitCommand(cmd).left;
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
        return getHandler(cmd).run(cmd, this);
    }

    public ShellCommandHandler getHandler(String cmd) {
        // (There will be more...)
        //noinspection SwitchStatementWithTooFewBranches
        return switch (getCommandName(cmd)) {
            case "!exit" -> new ExitCommandHandler();
            default -> new MathCommandHandler();
        };
    }

}
