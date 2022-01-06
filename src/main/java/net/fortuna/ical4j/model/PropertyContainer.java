package net.fortuna.ical4j.model;

import java.util.List;
import java.util.Optional;

public interface PropertyContainer {

    PropertyList getPropertyList();

    void setPropertyList(PropertyList properties);

    default <T extends Property> List<T> getProperties(final String... name) {
        return getPropertyList().get(name);
    }

    default <T extends Property> Optional<T> getProperty(final String name) {
        return getPropertyList().getFirst(name);
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

    /**
     * Add a property to the container.
     * @param property the property to add
     * @return a reference to the container to support method chaining
     */
    default PropertyContainer add(Property property) {
        setPropertyList((PropertyList) getPropertyList().add(property));
        return this;
    }

    /**
     * Remove a property from the container.
     * @param property the property to remove
     * @return a reference to the container to support method chaining
     */
    default PropertyContainer remove(Property property) {
        setPropertyList((PropertyList) getPropertyList().remove(property));
        return this;
    }

    /**
     * Remove all properties with the matching name.
     * @param name name of the properties to remove
     * @return a reference to the container to support method chaining
     */
    default PropertyContainer removeAll(String... name) {
        setPropertyList((PropertyList) getPropertyList().removeAll(name));
        return this;
    }

    /**
     * Add a property to the container whilst removing all other properties with the same property name.
     * @param property the property to add
     * @return a reference to the container to support method chaining
     */
    default PropertyContainer replace(Property property) {
        setPropertyList((PropertyList) getPropertyList().replace(property));
        return this;
    }
}
