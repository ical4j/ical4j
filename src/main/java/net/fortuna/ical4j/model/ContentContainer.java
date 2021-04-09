package net.fortuna.ical4j.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public interface ContentContainer<T extends Content> extends Serializable {

    ContentContainer<T> add(T content);

    ContentContainer<T> addAll(Collection<T> content);

    ContentContainer<T> remove(T content);

    ContentContainer<T> removeAll(String... name);

    ContentContainer<T> replace(T content);

    List<T> getAll();

    @SuppressWarnings("unchecked")
    default <R extends T> List<R> get(String name) {
        return getAll().stream().filter(c -> c.getName().equals(name)).map(c -> (R) c).collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    default <R extends T> Optional<R> getFirst(String name) {
        return (Optional<R>) getAll().stream().filter(c -> c.getName().equals(name)).findFirst();
    }

    @SuppressWarnings("unchecked")
    default <R extends T> R getRequired(String name) throws ConstraintViolationException {
        return (R) getFirst(name).orElseThrow(() -> new ConstraintViolationException(
                String.format("Missing required %s", name)));
    }
}
