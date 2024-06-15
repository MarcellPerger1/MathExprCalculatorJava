package net.marcellperger.mathexpr.cli.minicli;

import org.jetbrains.annotations.NotNull;

import java.util.List;

class StringCLIOption extends CLIOption<String> {
    public StringCLIOption(List<String> keys) {
        super(String.class, keys);
    }

    @Override
    protected void _setValueFromString(@NotNull String s) {
        setValue(s);
    }

    @Override
    public ValueMode getDefaultValueMode() {
        return defaultIfNoValue.isNone() ? ValueMode.REQUIRED : ValueMode.OPTIONAL;
    }
}
