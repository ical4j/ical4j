package net.fortuna.ical4j.model;

public interface PropertyContainer {

    PropertyList<Property> getProperties();

    default <T extends Property> PropertyList<T> getProperties(final String name) {
        return getProperties().getProperties(name);
    }

    default <T extends Property> T getProperty(final String name) {
        return getProperties().getProperty(name);
    }
}
