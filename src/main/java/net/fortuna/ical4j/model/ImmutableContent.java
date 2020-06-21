package net.fortuna.ical4j.model;

public interface ImmutableContent {

    default void throwException() {
        throw new UnsupportedOperationException("Cannot modify constant instances");
    }
}
