package net.fortuna.ical4j.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Implementors of this interface support the immutable collection contract specified by this interface.
 * The contract states that any mutation function will not modify the underlying collection, but rather
 * will return a copy of the collection with the applied mutation.
 *
 * @param <T>
 */
public interface ContentCollection<T extends Content, E extends ContentCollection<T, E>> extends Serializable {

    E add(T content);

    E addAll(Collection<T> content);

    E remove(T content);

    E removeAll(String... name);

    E removeIf(Predicate<T> filter);

    E replace(T content);

    List<T> getAll();

    /**
     * Return a list of elements filtered by name. If no names are specified return all elements.
     * @param names a list of zero or more names to match
     * @param <R> content type
     * @return a list of elements less than or equal to the elements in this collection
     */
    @SuppressWarnings("unchecked")
    default <R extends T> List<R> get(String... names) {
        if (names.length > 0) {
            List<String> filter = Arrays.stream(names).map(String::toUpperCase).collect(Collectors.toList());
            // sort according to the order of names specified in the filter
            return getAll().stream().filter(c -> filter.contains(c.getName())).map(c -> (R) c).sorted((a, b) -> {
                int idxA = filter.indexOf(a.getName().toUpperCase());
                int idxB = filter.indexOf(b.getName().toUpperCase());
                return Integer.compare(idxA, idxB);
            }).collect(Collectors.toList());
        } else {
            return (List<R>) getAll();
        }
    }

    @SuppressWarnings("unchecked")
    default <R extends T> Optional<R> getFirst(String name) {
        return (Optional<R>) getAll().stream().filter(c -> c.getName().equalsIgnoreCase(name)).findFirst();
    }

    @SuppressWarnings("unchecked")
    default <R extends T> R getRequired(String name) throws ConstraintViolationException {
        return (R) getFirst(name).orElseThrow(() -> new ConstraintViolationException(
                String.format("Missing required %s", name)));
    }
}
