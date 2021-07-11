package io.github.lucunji.uusiaurinko.util;

import java.util.Collection;

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
}
