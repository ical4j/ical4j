package net.fortuna.ical4j.model;

import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Optional;

/**
 * Interface for accessing a list of properties.
 * Provides methods to retrieve properties by name, including convenience methods for required properties.
 *
 * @see PropertyList
 */
public interface PropertyListAccessor {

    PropertyList getPropertyList();

    default <T extends Property> List<T> getProperties(final String... name) {
        return getPropertyList().get(name);
    }

    default <T extends Property> Optional<T> getProperty(final String name) {
        return getPropertyList().getFirst(name);
    }

    default <T extends Property> Optional<T> getProperty(@NonNull final Enum<?> name) {
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

    default <T extends Property> T getRequiredProperty(@NonNull Enum<?> name) throws ConstraintViolationException {
        return getRequiredProperty(name.toString());
    }
}
