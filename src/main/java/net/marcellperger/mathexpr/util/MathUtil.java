package net.marcellperger.mathexpr.util;

import org.jetbrains.annotations.Range;

public class MathUtil {
    protected MathUtil() {}

    public static double roundToSigFigs(double value, @Range(from=1, to=Integer.MAX_VALUE) int sigFigs) {
        int mostSignificantDigit = (int)Math.floor(Math.log10(value));
        int roundToDigit = mostSignificantDigit - sigFigs + 1;
        return roundToDP(value, roundToDigit);
    }

    public static double roundToNearest(double value, double nearest) {
        return Math.round(value / nearest) * nearest;
    }
    public static double roundToDP(double value, int decimalPlaces) {
        return roundToNearest(value, Math.pow(10, decimalPlaces));
    }
}
