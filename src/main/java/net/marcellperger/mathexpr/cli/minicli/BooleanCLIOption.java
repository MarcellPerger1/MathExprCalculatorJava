package net.marcellperger.mathexpr.cli.minicli;

import java.util.List;

public class BooleanCLIOption extends CLIOption<Boolean> {
    public BooleanCLIOption(List<String> optionNames) {
        super(Boolean.class, optionNames);
    }

    @Override
    public void setValueFromString(String s) {
        setValue(switch (s.strip().toLowerCase()) {
            case "0", "no", "false" -> false;
            case "1", "yes", "true" -> true;
            case String s2 -> throw new IllegalArgumentException("Bad boolean value '" + s2 + "'");  // TODO special CLIParseException
        });
    }
}
