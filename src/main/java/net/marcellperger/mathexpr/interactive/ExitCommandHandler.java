package net.marcellperger.mathexpr.interactive;

public class ExitCommandHandler implements ShellCommandHandler {
    @Override
    public boolean run(String cmd, Shell sh) {
        return false;  // Do not continue
    }
}
