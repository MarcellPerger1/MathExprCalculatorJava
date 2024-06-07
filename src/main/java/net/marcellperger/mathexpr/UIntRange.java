package net.marcellperger.mathexpr;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class UIntRange extends IntRange {
    protected UIntRange(int min, int max, int ignoredMarker) {
        super(workaround(() -> {
            if(min < 0 || max < 0) throw new IllegalArgumentException();
        }, min), max, ignoredMarker);
    }

    public UIntRange(@Nullable Integer min, @Nullable Integer max) {
        this(Objects.requireNonNullElse(min, 0),
            Objects.requireNonNullElse(max, Integer.MAX_VALUE), /*marker*/0);
    }

    public UIntRange() {
        this(null, null);
    }

    /**
     * Workaround to allow us to run code before super() call.
     * This is required because java insists that super() be this first statement!
     * So not even static stuff can be ran!
     */
    @Contract("_, _ -> param2")
    private static <T> T workaround(@NotNull Runnable r, T returnValue) {
        r.run();
        return returnValue;
    }

}
