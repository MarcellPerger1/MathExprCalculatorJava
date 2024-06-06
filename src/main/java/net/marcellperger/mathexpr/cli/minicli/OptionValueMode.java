package net.marcellperger.mathexpr.cli.minicli;

/**
 * Controls if a value is required for an option
 */
public enum OptionValueMode {
    /**
     * Specifying a value is not allowed (e.g. {@code --foo} is allowed, {@code --foo=76} is not)
     */
    NONE,
    /**
     * Specifying a value is optional (e.g. {@code --foo} and {@code --foo=76} are both allowed)
     */
    OPTIONAL,
    /**
     * Specifying a value is required (e.g. {@code --foo=76} is allowed, {@code --foo} is not)
     */
    REQUIRED,
    ;
    public void validateHasValue(boolean hasValue) {
        switch (this) {
            case NONE -> {
                if(hasValue) throw new CLIParseException("Specifying a value for this option is not allowed");
            }
            case OPTIONAL -> {}
            case REQUIRED -> {
                if(!hasValue) throw new CLIParseException("This option requires a value to be passed");
            }
        }
    }
}
