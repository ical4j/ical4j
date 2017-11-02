package net.fortuna.ical4j.util;

import java.util.NoSuchElementException;

public class Optional<T> {

    private static final Optional<?> EMPTY = new Optional<>();

    private final T value;

    private Optional() {
        this.value = null;
    }

    private Optional(T value) {
        this.value = value;
    }

    public T get() {
        if (value == null) {
            throw new NoSuchElementException("No value present");
        }
        return value;
    }

    public T orElse(T other) {
        return value != null ? value : other;
    }

    public boolean isPresent() {
        return value != null;
    }

    public static <T> Optional<T> of(T value) {
        return new Optional<>(value);
    }

    public static <T> Optional<T> empty() {
        return (Optional<T>) EMPTY;
    }
}

