package net.marcellperger.mathexpr.cli.minicli;

public class CLIParseException extends IllegalArgumentException {
    public CLIParseException() {
    }

    public CLIParseException(String s) {
        super(s);
    }

    public CLIParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public CLIParseException(Throwable cause) {
        super(cause);
    }
}
