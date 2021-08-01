package net.fortuna.ical4j.model;

public interface PropertyContainer {

    PropertyList<Property> getProperties();

    <T extends Property> PropertyList<T> getProperties(final String name);

    <T extends Property> T getProperty(final String name);
}
