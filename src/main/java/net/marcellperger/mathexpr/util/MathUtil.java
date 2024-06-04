package net.marcellperger.mathexpr.util;

import org.jetbrains.annotations.Range;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MathUtil {
    protected MathUtil() {}

    public static double roundToSigFigs(double value, @Range(from=1, to=Integer.MAX_VALUE) int sigFigs) {
        if(value == 0) return 0;  // log10 would give Infinity so special-case it
        int mostSignificantDigit = (int)Math.floor(Math.log10(Math.abs(value)));
        int roundToDigit = mostSignificantDigit - sigFigs + 1;
        return roundToDP(value, roundToDigit);
    }

    public static double roundToDP(double value, int decimalPlaces) {
        return roundToNearest(value, Math.pow(10, decimalPlaces));
    }

    public static double roundToNearest(double value, double nearest) {
        if(Double.isNaN(nearest) || Double.isNaN(value)) return Double.NaN;
        // 0==0*n and inf==inf*n => 0 and 0 are multiples of everything
        if(value == 0.0 || Double.isInfinite(value)) return value;
        // The nearest infinity if the one with the same sign
        if(Double.isInfinite(nearest)) return Math.copySign(nearest, value);
        return roundRealToNearest(value, nearest);
    }
    protected static double roundRealToNearest(double value, double nearest) {
        BigDecimal nearestD = BigDecimal.valueOf(nearest);
        return BigDecimal.valueOf(value).divide(nearestD, RoundingMode.HALF_UP)
            .setScale(0, RoundingMode.HALF_UP).multiply(nearestD).doubleValue();
    }
}
