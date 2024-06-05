package net.marcellperger.mathexpr.cli.minicli;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        return this;
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
