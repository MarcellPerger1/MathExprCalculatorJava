package net.marcellperger.mathexpr.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class UtilCollectors {
    protected UtilCollectors() {}  // prevent instantiation, allow subclassing

    @Contract(" -> new")
    public static <T> @NotNull Collector<T, ?, T> singleItem() {
        return Collectors.collectingAndThen(Collectors.toList(), Util::getOnlyItem);
    }
    public static <T> @NotNull Collector<T, ?, T> singleDistinctItem() {
        return Collectors.collectingAndThen(Collectors.toSet(), Util::getOnlyItem);
    }

    public static <K, V> @NotNull Collector<Entry<K, V>, ?, Map<K, V>> entriesToUnmodifiableMap() {
        return Collectors.toUnmodifiableMap(Entry::getKey, Entry::getValue);
    }
    public static <K, V> @NotNull Collector<Entry<K, V>, ?, Map<K, V>> entriesToMap() {
        return Collectors.toMap(Entry::getKey, Entry::getValue);
    }
}
