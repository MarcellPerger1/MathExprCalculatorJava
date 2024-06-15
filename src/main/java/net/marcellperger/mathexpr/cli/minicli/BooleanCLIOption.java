package net.marcellperger.mathexpr.cli.minicli;

import org.jetbrains.annotations.NotNull;

import java.util.List;

class BooleanCLIOption extends CLIOption<Boolean> {
    public BooleanCLIOption(List<String> optionNames) {
        super(Boolean.class, optionNames);
        setDefault(false);
        setDefaultIfNoValue(true);
        setValueMode(ValueMode.OPTIONAL);
    }

    @Override
    protected void _setValueFromString(@NotNull String s) {
        setValue(switch (s.strip().toLowerCase()) {
            case "0", "no", "false" -> false;
            case "1", "yes", "true" -> true;
            case String s2 -> throw new CLIParseException("Bad boolean value '" + s2 + "'");
        });
    }

    @Override
    public boolean supportsSeparateValueAfterShortForm() {
        return false;  // cannot have `foo -r no` (use `-r=no` / `--long-form=no`
    }
}
