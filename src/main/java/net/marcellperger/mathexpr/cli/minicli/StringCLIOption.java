package net.marcellperger.mathexpr.cli.minicli;

import java.util.List;

public class StringCLIOption extends CLIOption<String> {
    public StringCLIOption(List<String> keys) {
        super(String.class, keys);
    }

    @Override
    public void setValueFromString(String s) {
        setValue(s);
    }
}
