package net.marcellperger.mathexpr.cli.minicli;

import net.marcellperger.mathexpr.util.Pair;
import net.marcellperger.mathexpr.util.Util;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MiniCLI {
    Map<String, CLIOption<?>> options = new HashMap<>();
    List<String> positionalArgs = new ArrayList<>();

    public MiniCLI() {

    }

    @Contract("_ -> new")
    public CLIOption<String> addStringOption(String @NotNull ... names) {
        // First validate all of them so we don't mutate unless all are valid
        for(String name : names) {
            if (!name.startsWith("-")) throw new IllegalArgumentException("Option name must start with '-'");
            if (options.containsKey(name)) {
                throw new IllegalStateException("Argument '%s' has already been registered".formatted(name));
            }
        }
        CLIOption<String> opt = new StringCLIOption(List.of(names));
        for(String name : names) {
            options.put(name, opt);
        }
        return opt;
    }


    @Contract("_ -> this")
    public MiniCLI parseArgs(String[] args) {
        return parseArgs(List.of(args));
    }
    @Contract("_ -> this")
    public MiniCLI parseArgs(List<String> args) {
        new Parser(args).parse();
        return this;
    }
    private @NotNull CLIOption<?> lookupOption(String opt) {
        CLIOption<?> out = options.get(opt);
        if(out == null) throw new CLIParseException("'%s' is not a valid option".formatted(opt));
        return out;
    }
    private class Parser {
        @Nullable CLIOption<?> prev = null;
        List<String> args;

        public Parser(List<String> args) {
            this.args = args;
        }

        public void parse() {
            args.forEach(this::pumpSingleArg);
            // TODO: finalize: check prev, handle if not null
        }

        // Make this public as it could be useful for making stream-ing type stuff
        public void pumpSingleArg(String arg) {
            ArgType argT = ArgType.fromArg(arg);
            if(prev != null && prev.getValueMode() == OptionValueMode.REQUIRED) {
                // We NEED a value so force it, even if it looks like a flag
                flushPrevWithValue(arg);
                return;
            }
            switch (argT) {
                case NORMAL -> {
                    if (prev == null) positionalArgs.add(arg);
                    else flushPrevWithValue(arg);
                }
                case SINGLE -> {
                    if(prev != null) flushPrev();
                    if(arg.contains("=")) {
                        Pair<String, String> kv = Pair.ofArray(arg.split("=", 2));
                        CLIOption<?> opt = lookupOption(kv.left);
                        opt.setValueFromString(kv.right);
                    } else {
                        prev = lookupOption(arg);
                        // flush immediately if next one cannot be a value
                        if(!prev.supportsSeparateValueAfterShortForm()) flushPrev();
                    }
                }
                case DOUBLE -> {
                    if(prev != null) flushPrev();

                    // TODO lookup here
                    prev = null;
                }
            }
        }

        private void flushPrev() {
            Util.realAssert(prev != null, "flushPrev must have a `prev` to flush");
            prev = null;
        }
        private void flushPrevWithValue(String value) {
            Util.realAssert(prev != null, "flushPrevWithValue must have a `prev` to flush");
            prev.setValueFromString(value);
            prev = null;
        }
    }
    private record PrevState(CLIOption<?> option, @Nullable String value) {

    }
    private enum ArgType {
        NORMAL, SINGLE, DOUBLE;
        public static ArgType fromArg(String arg) {
            if(arg.startsWith("--")) return DOUBLE;
            if(arg.startsWith("-")) return SINGLE;
            return NORMAL;
        }
    }
}
