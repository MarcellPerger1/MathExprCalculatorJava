package net.marcellperger.mathexpr.cli.minicli;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

class StringCLIOption extends CLIOption<String> {
    @Nullable String defaultIfNoValue;  // TODO should this really be here - makes more sense in CLIOption

    public StringCLIOption(List<String> keys, @Nullable String defaultIfNoValue) {
        super(String.class, keys);
        this.defaultIfNoValue = defaultIfNoValue;
    }
    public StringCLIOption(List<String> keys) {
        this(keys, null);
    }

    @Override
    public CLIOption<String> setDefaultNoValue(String defaultIfNoValue) {
        this.defaultIfNoValue = defaultIfNoValue;
        return this;
    }

    @Override
    protected void _setValueFromString(@Nullable String s) {
        setValue(s == null ? getDefaultIfNoValue() : s);
    }

    @Override
    public OptionValueMode getValueMode() {
        return defaultIfNoValue == null ? OptionValueMode.REQUIRED : OptionValueMode.OPTIONAL;
    }

    protected @NotNull String getDefaultIfNoValue() {
        if (defaultIfNoValue == null) {  // TODO valueMode should handle this?
            throw new CLIParseException("This argument requires a value");  // TODO some way of specifying which to use a display?
        }
        return defaultIfNoValue;
    }
}
