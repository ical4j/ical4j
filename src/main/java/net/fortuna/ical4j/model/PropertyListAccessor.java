package net.fortuna.ical4j.model;

import java.util.List;
import java.util.Optional;

public interface PropertyListAccessor {

    PropertyList getPropertyList();

    default <T extends Property> List<T> getProperties(final String... name) {
        return getPropertyList().get(name);
    }

    default <T extends Property> Optional<T> getProperty(final String name) {
        return getPropertyList().getFirst(name);
    }

    default <T extends Property> Optional<T> getProperty(final Enum<?> name) {
        return getProperty(name.toString());
    }

    /**
     * Convenience method for retrieving a required named property.
     *
     * @param name name of the property to retrieve
     * @return the first matching property in the property list with the specified name
     * @throws ConstraintViolationException when a property is not found
     */
    default <T extends Property> T getRequiredProperty(String name) throws ConstraintViolationException {
        return getPropertyList().getRequired(name);
    }

    default <T extends Property> T getRequiredProperty(Enum<?> name) throws ConstraintViolationException {
        return getRequiredProperty(name.toString());
    }
}
