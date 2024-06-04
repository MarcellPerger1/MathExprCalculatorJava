package net.marcellperger.mathexpr.interactive;

import net.marcellperger.mathexpr.util.Pair;
import net.marcellperger.mathexpr.util.UnreachableError;
import org.jetbrains.annotations.NotNull;

public class ShellCommandParser {
    Shell sh;

    public ShellCommandParser(Shell shell) {
        sh = shell;
    }

    public ShellCommandHandler dispatchCommand(String cmd) {
        // (There will be more...)
        //noinspection SwitchStatementWithTooFewBranches
        return switch (getCommandName(cmd)) {
            case "!exit" -> new ExitCommandHandler();
            default -> new MathCommandHandler();
        };
    }

    protected @NotNull String getCommandName(String cmd) {
        return splitCommand(cmd).left;
    }

    /**
     * Splits {@code cmd} into command and arguments
     */
    protected @NotNull Pair<@NotNull String, @NotNull String> splitCommand(@NotNull String cmd) {
        String[] words = cmd.split("\\s+", 2);
        return switch (words.length) {
            case 0 -> new Pair<>("", "");
            case 1 -> new Pair<>(words[0], "");
            case 2 -> new Pair<>(words[0], words[1]);
            default -> throw new UnreachableError();
        };
    }
}
