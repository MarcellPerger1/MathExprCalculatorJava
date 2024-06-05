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

    public abstract void setValueFromString(String s);

    public void reset() {
        value = null;
    }
}
