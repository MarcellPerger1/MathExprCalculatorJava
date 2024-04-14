package net.marcellperger.first.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collector;
import java.util.stream.Collectors;

public class UtilCollectors {
    protected UtilCollectors() {}  // prevent instantiation, allow subclassing

    @Contract(" -> new")
    public static <T> @NotNull Collector<T, ?, T> singleItem() {
        return Collectors.collectingAndThen(
            Collectors.toList(),
            items -> Util.expectOrFail(items, items.size() == 1,
                new CollectionSizeException("UtilCollectors.singleItem expected a single item")).getFirst()
        );
    }
}
