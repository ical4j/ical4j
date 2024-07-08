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
 *
 * The contract states that any mutation function will not modify the underlying collection, but rather
 * will return a copy of the collection with the applied mutation.
 *
 * @param <T>
 */
public interface ContentCollection<T extends Content> extends Serializable {

    ContentCollection<T> add(T content);

    ContentCollection<T> addAll(Collection<T> content);

    ContentCollection<T> remove(T content);

    ContentCollection<T> removeAll(String... name);

    ContentCollection<T> removeIf(Predicate<T> filter);

    ContentCollection<T> replace(T content);

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
            return getAll().stream().filter(c -> filter.contains(c.getName())).map(c -> (R) c).collect(Collectors.toList());
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
