package net.marcellperger.mathexpr;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class IntRange {
    int lo, hi;

    protected IntRange(int min, int max, int ignoredMarker) {
        if(min > max) throw new IllegalArgumentException("min must be grater than max");
        lo = min;
        hi = max;
    }
    public IntRange(@Nullable Integer min, @Nullable Integer max) {
        this(Objects.requireNonNullElse(min, Integer.MIN_VALUE),
            Objects.requireNonNullElse(max, Integer.MAX_VALUE), /*marker*/0);
    }
    public IntRange() {
        this(null, null);
    }

    public int getMin() {
        return lo;
    }
    public int getMax() {
        return hi;
    }

    public boolean includes(int v) {
        return lo <= v && v <= hi;
    }
}
