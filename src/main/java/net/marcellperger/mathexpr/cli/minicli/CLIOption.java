package net.marcellperger.mathexpr.cli.minicli;

import net.marcellperger.mathexpr.util.Util;
import net.marcellperger.mathexpr.util.rs.Option;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public abstract class CLIOption<T> {
    Class<T> type;
    List<String> names;
    @NotNull Option<ValueMode> valueMode = Option.newNone();
    @NotNull Option<T> defaultIfNoValue = Option.newNone();
    @NotNull Option<T> defaultIfAbsent = Option.newNone();
    T value;
    boolean hasValue;

    public CLIOption(Class<T> valueType, List<String> optionNames) {
        names = optionNames;
        type = valueType;
    }

    public String getDisplayName() {
        return names.isEmpty() ? "<unnamed option>" : String.join("/", names.toArray(String[]::new));
    }
    public CLIParseException fmtNewParseExcWithName(@NotNull String fmt) {
        return new CLIParseException(fmt.formatted(getDisplayName()));
    }

    @Contract(" -> this")
    public CLIOption<T> setRequired() {
        defaultIfAbsent = Option.newNone();
        return this;
    }
    @Contract("_ -> this")
    public CLIOption<T> setDefault(T defaultIfAbsent_) {
        defaultIfAbsent = Option.newSome(defaultIfAbsent_);
        return this;
    }
    @Contract("_ -> this")
    public CLIOption<T> setDefaultIfNoValue(T defaultIfNoValue_) {
        defaultIfNoValue = Option.newSome(defaultIfNoValue_);
        return this;
    }
    public boolean isRequired() {
        return defaultIfAbsent.isNone();
    }

    public List<String> getNames() {
        return names;
    }

    public T getValue() {
        Util.realAssert(hasValue, "value should've been set before getValue() is called");
        return value;
    }

    public void begin() {
        switch (getValueMode()) {
            case OPTIONAL, NONE -> defaultIfNoValue.expect(
                new IllegalStateException("defaultIfNoValue must be provided for OPTIONAL/NONE valueModes"));
            case REQUIRED -> {
                if (defaultIfNoValue.isSome()) {
                    throw new IllegalStateException("defaultIfNoValue should not be specified with a REQUIRED valueMode");
                }
            }
        }
    }
    public void finish() {
        if (!hasValue)
            setValue(defaultIfAbsent.expect(fmtNewParseExcWithName("The %s option is required")));
    }

    public void setValue(T value_) {
        value = value_;
        hasValue = true;
    }

    // Nullable because we need a way to distinguish `--foo=''` and `--foo`
    protected abstract void _setValueFromString(@NotNull String s);
    public void setValueFromString(@Nullable String s) {
        if(s == null) setValueFromNoValue();
        else setValueFromString_hasValue(s);
    }

    public void setValueFromString_hasValue(@NotNull String s) {
        Objects.requireNonNull(s);
        getValueMode().validateHasValue(this, true);
        _setValueFromString(s);
    }
    public void setValueFromNoValue() {
        getValueMode().validateHasValue(this, false);
        setValue(_expectGetDefaultIfNoValue());
    }
    protected T _expectGetDefaultIfNoValue() {
        return defaultIfNoValue.expect(fmtNewParseExcWithName("The %s option requires a value"));
    }

    public ValueMode getDefaultValueMode() {
        return defaultIfNoValue.isSome() ? ValueMode.OPTIONAL : ValueMode.REQUIRED;
    }
    public ValueMode getValueMode() {
        return valueMode.unwrapOr(getDefaultValueMode());
    }
    public Option<ValueMode> getDeclaredValueMode() {
        return valueMode;
    }
    @Contract("_ -> this")
    public CLIOption<T> setValueMode(ValueMode mode) {
        valueMode = Option.newSome(mode);
        return this;
    }

    /**
     * @return True if it supports `-b arg`, False to make `-b arg` into `-b`,`arg`
     */
    public boolean supportsSeparateValueAfterShortForm() {
        return getValueMode() != ValueMode.NONE;
    }

    public void reset() {
        value = null;
        hasValue = false;
    }
}
