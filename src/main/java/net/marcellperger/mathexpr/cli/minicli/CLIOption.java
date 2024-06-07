package net.marcellperger.mathexpr.cli.minicli;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class CLIOption<T> {
    List<String> names;
    Class<T> type;
    @Nullable T value;

    public CLIOption(Class<T> valueType, List<String> optionNames) {
        names = optionNames;
        type = valueType;
    }

    public List<String> getNames() {
        return names;
    }

    public @Nullable T getValue() {
        return value;
    }
    public void setValue(@Nullable T value) {
        this.value = value;
    }

    // Nullable because we need a way to distinguish `--foo=''` and `--foo`
    protected abstract void _setValueFromString(@Nullable String s);
    public void setValueFromString(@Nullable String s) {
        getValueMode().validateHasValue(s == null);
        _setValueFromString(s);
    }

    public OptionValueMode getValueMode() {
        return OptionValueMode.REQUIRED;
    }

    /**
     * @return True if it supports `-b arg`, False to make `-b arg` into `-b`,`arg`
     */
    public boolean supportsSeparateValueAfterShortForm() {
        return getValueMode() != OptionValueMode.NONE;
    }

    public void reset() {
        value = null;
    }
}
