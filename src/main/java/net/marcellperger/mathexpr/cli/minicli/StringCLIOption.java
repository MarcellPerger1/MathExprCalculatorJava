package net.marcellperger.mathexpr.cli.minicli;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class StringCLIOption extends CLIOption<String> {
    public StringCLIOption(List<String> keys) {
        super(String.class, keys);
    }

    @Override
    protected void _setValueFromString(@Nullable String s) {
        setValue(s);
    }
}
