package net.marcellperger.mathexpr.cli.minicli;

import net.marcellperger.mathexpr.UIntRange;
import net.marcellperger.mathexpr.util.Pair;
import net.marcellperger.mathexpr.util.Util;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

public class MiniCLI {
    Map<String, CLIOption<?>> options = new HashMap<>();
    Set<CLIOption<?>> optionSet = new HashSet<>();
    UIntRange nPositionalArgs = new UIntRange();
    List<String> positionalArgs = new ArrayList<>();

    public MiniCLI() {

    }

    public void setPositionalArgCount(UIntRange range) {
        nPositionalArgs = range;
    }
    public void setPositionalArgCount(@Nullable Integer min, @Nullable Integer max) {
        setPositionalArgCount(new UIntRange(min, max));
    }
    public void setPositionalArgCount(@Nullable /*null=any*/ Integer value) {
        if(value == null) setPositionalArgCount(new UIntRange());
        else setPositionalArgCount(value, value);
    }

    @Contract("_ -> new")
    public CLIOption<String> addStringOption(String @NotNull ... names) {
        return addOptionFromFactory(names, StringCLIOption::new);
    }
    @Contract("_ -> new")
    public CLIOption<Boolean> addBooleanOption(String @NotNull ... names) {
        return addOptionFromFactory(names, BooleanCLIOption::new);
    }

    protected <T> CLIOption<T> addOptionFromFactory(
            String @NotNull[] names,
            Function<? super List<String>, ? extends CLIOption<T>> optionFactory) {
        // First validate all of them so we don't mutate unless all are valid
        for(String name : names) {
            if (!name.startsWith("-")) throw new IllegalArgumentException("Option name must start with '-'");
            if (options.containsKey(name)) {
                throw new IllegalStateException("Argument '%s' has already been registered".formatted(name));
            }
        }
        CLIOption<T> opt = optionFactory.apply(List.of(names));
        optionSet.add(opt);
        for(String name : names) {
            options.put(name, opt);
        }
        return opt;
    }

    public void reset() {
        positionalArgs.clear();
        optionSet.forEach(CLIOption::reset);
    }

    // TODO better positional arg handling (just dumping an array on the user is a bit meh)
    public List<String> getPositionalArgs() {
        return positionalArgs;
    }

    @Contract("_ -> this")
    public MiniCLI parseArgs(String[] args) {
        return parseArgs(List.of(args));
    }
    @Contract("_ -> this")
    public MiniCLI parseArgs(List<String> args) {
        reset();
        new Parser(args).parse();
        return this;
    }
    private @NotNull CLIOption<?> lookupOption(String opt) {
        CLIOption<?> out = options.get(opt);
        if(out == null) throw new CLIParseException("'%s' is not a valid option".formatted(opt));
        return out;
    }
    private class Parser {
        // NOTE: Do not strip ANYTHING! - the shell does that for us and it also removes quotes
        @Nullable CLIOption<?> prev = null;
        boolean finishedOptions = false;
        List<String> args;

        public Parser(List<String> args) {
            this.args = args;
        }

        public void parse() {
            begin();
            pumpAllArgs();
            finish();
        }

        public void pumpAllArgs() {
            args.forEach(this::pumpSingleArg);
        }

        // Make this public as it could be useful for making stream-ing type stuff
        public void pumpSingleArg(@NotNull String arg) {
            if(finishedOptions) {  // No more options so *must* be positional
                addPositionalArg(arg);
                return;
            }
            if(arg.equals("--")) {
                finishedOptions = true;
                return;
            }
            ArgType argT = ArgType.fromArg(arg);
            if(prev != null && prev.getValueMode() == ValueMode.REQUIRED) {
                // We NEED a value so force it, even if it looks like a flag
                flushPrevWithValue(arg);
                return;
            }
            switch (argT) {
                case NORMAL -> {
                    if (prev != null) flushPrevWithValue(arg);
                    else addPositionalArg(arg);
                }
                case SINGLE -> {
                    if(prev != null) flushPrev();
                    if(!setFromKeyEqualsValue(arg)) {
                        prev = lookupOption(arg);
                        // flush immediately if next one cannot be a value
                        if(!prev.supportsSeparateValueAfterShortForm()) flushPrev();
                    }
                }
                case DOUBLE -> {
                    if(prev != null) flushPrev();
                    if(!setFromKeyEqualsValue(arg)) {
                        lookupOption(arg).setValueFromString(null);
                    }
                }
            }
        }

        public void begin() {
            optionSet.forEach(CLIOption::begin);
        }

        public void finish() {
            if(prev != null) flushPrev();
            int nArgs = positionalArgs.size();
            if(!nPositionalArgs.includes(nArgs))
                throw new CLIParseException("Incorrect number of positional args (required %s, got %d)"
                    .formatted(nPositionalArgs.fancyRepr(), nArgs));
            optionSet.forEach(CLIOption::finish);  // Handle options
        }

        // Makes more logical sense reading the code
        // (`if (![did]setFromKeyEqualValue(arg)) {...}` )
        @SuppressWarnings("BooleanMethodIsAlwaysInverted")
        private boolean /*return success?*/ setFromKeyEqualsValue(String arg) {
            if(!arg.contains("=")) return false;
            Pair<String, String> kv = Pair.ofArray(arg.split("=", 2));
            CLIOption<?> opt = lookupOption(kv.left);
            opt.setValueFromString(kv.right);
            return true;
        }

        private void flushPrev() {
            Util.realAssert(prev != null, "flushPrev must have a `prev` to flush");
            prev.setValueFromString(null);
            prev = null;
        }
        private void flushPrevWithValue(String value) {
            Util.realAssert(prev != null, "flushPrevWithValue must have a `prev` to flush");
            prev.setValueFromString(value);
            prev = null;
        }

        private void addPositionalArg(String arg) {
            int newSize = positionalArgs.size() + 1;
            int maxArgc = nPositionalArgs.getMax();
            if(newSize > maxArgc)
                throw new CLIParseException("Too many positional args (expected max %d, got %d)".formatted(maxArgc, newSize));
            positionalArgs.add(arg);
        }
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
