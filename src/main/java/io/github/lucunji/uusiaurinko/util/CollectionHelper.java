package io.github.lucunji.uusiaurinko.util;

import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class CollectionHelper {

    /**
     * Iterate through the second collection to check if any of its elements
     * is in the first collection. Their element types must match.
     *
     * @param in      the collection to call {@link Collection#contains} at.
     * @param toCheck the collection to iterate through.
     * @param <T>     the common element type shared by the two collections.
     * @return {@code true} if their intersection is not empty; {@code false} otherwise.
     */
    public static <T> boolean hasAnyOf(Collection<T> in, Collection<T> toCheck) {
        for (T t : toCheck) {
            if (in.contains(t)) return true;
        }
        return false;
    }

    public static <T> Collector<T, Set<T>, Set<T>> toIdentitySet() {
        return new Collector<T, Set<T>, Set<T>>() {
            @Override
            public Supplier<Set<T>> supplier() {
                return Sets::newIdentityHashSet;
            }

            @Override
            public BiConsumer<Set<T>, T> accumulator() {
                return Set::add;
            }

            @Override
            public BinaryOperator<Set<T>> combiner() {
                return (set1, set2) -> {
                    set1.addAll(set2);
                    return set1;
                };
            }

            @Override
            public Function<Set<T>, Set<T>> finisher() {
                return Function.identity();
            }

            @Override
            public Set<Characteristics> characteristics() {
                return Sets.immutableEnumSet(Characteristics.UNORDERED);
            }
        };
    }
}
