package net.marcellperger.mathexpr;

import net.marcellperger.mathexpr.util.Util;
import net.marcellperger.mathexpr.util.UtilCollectors;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Data for a specific precedence level
 */
public class PrecedenceLevelInfo {
    public Set<SymbolInfo> symbols;
    public Map<String, SymbolInfo> infixToSymbolMap;
    public List<String> sortedInfixes;
    public @Nullable GroupingDirection dirn;
    public int precedence;

    /**
     * @param precedence_ The precedence level (gets data from SymbolInfo)
     */
    public PrecedenceLevelInfo(int precedence_) {
        precedence = precedence_;
        symbols = SymbolInfo.PREC_TO_INFO_MAP.get(precedence);
        // Shouldn't be passed an empty level in the first place
        Util.requireNonEmptyNonNull(symbols, "Cannot create PrecedenceLevelInfo for a level with no items");
        dirn = symbols.stream()
            .map(sm -> sm.groupingDirection).collect(UtilCollectors.singleDistinctItem());
        try {
            infixToSymbolMap = symbols.stream().collect(
                Collectors.toMap(
                    info -> Util.requireNonNull(info.infix, new NullInfixException()),
                    info -> info));
        } catch (NullInfixException e) {
            // a precedence level should really be uniform so if any infix is null,
            //  we set this to null. This may help avoid any subtle bugs later.
            infixToSymbolMap = null;
            sortedInfixes = null;
        }
        if(infixToSymbolMap != null) {
            sortedInfixes = infixToSymbolMap.keySet().stream()
                .sorted(Comparator.comparingInt(String::length).reversed()).toList();
        }
    }

    @Contract("_ -> new")
    public static Map.@NotNull Entry<Integer, PrecedenceLevelInfo> newMapEntry(int precedence) {
        return Util.makeEntry(precedence, new PrecedenceLevelInfo(precedence));
    }

    /** Marker exception that we throw to signal that there should be no infix info at all */
    private static class NullInfixException extends RuntimeException {}
}
