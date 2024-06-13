package net.marcellperger.mathexpr.cli.minicli;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class CLIOption<T> {
    List<String> names;
    Class<T> type;
    T value;
    boolean isRequired;
    @Nullable T defaultIfAbsent;

    public CLIOption(Class<T> valueType, List<String> optionNames) {
        names = optionNames;
        type = valueType;
    }

    @Contract("_ -> this")
    public CLIOption<T> setRequired(boolean required) {
        isRequired = required;
        return this;
    }
    @Contract("_ -> this")
    public CLIOption<T> setDefault(T defaultIfAbsent) {
        this.defaultIfAbsent = defaultIfAbsent;
        return this;
    }
    @Contract("_ -> this")
    public CLIOption<T> setDefaultNoValue(T defaultIfNoValue) {
        throw new UnsupportedOperationException("CLIOption<T>.setDefaultValue");
    }

    // TODO default value!

    public List<String> getNames() {
        return names;
    }

    public T getValue() {
        return value;
    }

    public void finish() {
        if(value != null) return;  // All good
        if(isRequired) throw new CLIParseException("This argument is required");  // TODO detail!
        value = defaultIfAbsent;
    }

    public void setValue(@Nullable T value) {
        this.value = value;
    }

    // Nullable because we need a way to distinguish `--foo=''` and `--foo`
    protected abstract void _setValueFromString(@Nullable String s);
    public void setValueFromString(@Nullable String s) {
        getValueMode().validateHasValue(s != null);
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
