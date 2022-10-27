package net.fortuna.ical4j.model;

import java.util.Collection;

public interface PropertyContainer extends PropertyListAccessor {

    void setPropertyList(PropertyList properties);

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
     * Add multiple properties to the container.
     * @param properties a collection of properties to add
     * @return a reference to the container to support method chaining
     */
    default PropertyContainer addAll(Collection<Property> properties) {
        setPropertyList((PropertyList) getPropertyList().addAll(properties));
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
