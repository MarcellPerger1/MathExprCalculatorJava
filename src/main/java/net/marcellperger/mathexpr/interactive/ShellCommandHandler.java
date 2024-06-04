package net.marcellperger.mathexpr.interactive;

@FunctionalInterface
public interface ShellCommandHandler {
    /**
     * @param cmd The command to run as a {@link String}
     * @param sh The {@link Shell} object
     * @return {@code false} to exit, {@code true} to continue
     */
    boolean run(String cmd, Shell sh);
}
