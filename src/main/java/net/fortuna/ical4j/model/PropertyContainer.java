package net.fortuna.ical4j.model;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.function.BiFunction;

public interface PropertyContainer extends PropertyListAccessor {
    BiFunction<PropertyContainer, Property, PropertyContainer> ADD_IF_NOT_PRESENT = (c, p) -> {
        if (!c.getProperty(p.getName()).isPresent()) {
            c.add(p);
        }
        return c;
    };

    void setPropertyList(PropertyList properties);

    /**
     * Add a property to the container.
     * @param property the property to add
     * @return a reference to the container to support method chaining
     */
    default <T extends PropertyContainer> T add(@NotNull Property property) {
        setPropertyList((PropertyList) getPropertyList().add(property));
        return (T) this;
    }

    /**
     * Add multiple properties to the container.
     * @param properties a collection of properties to add
     * @return a reference to the container to support method chaining
     */
    default <T extends PropertyContainer> T addAll(@NotNull Collection<Property> properties) {
        setPropertyList((PropertyList) getPropertyList().addAll(properties));
        return (T) this;
    }

    /**
     * Remove a property from the container.
     * @param property the property to remove
     * @return a reference to the container to support method chaining
     */
    default <T extends PropertyContainer> T remove(Property property) {
        setPropertyList((PropertyList) getPropertyList().remove(property));
        return (T) this;
    }

    /**
     * Remove all properties with the matching name.
     * @param name name of the properties to remove
     * @return a reference to the container to support method chaining
     */
    default <T extends PropertyContainer> T removeAll(String... name) {
        setPropertyList((PropertyList) getPropertyList().removeAll(name));
        return (T) this;
    }

    /**
     * Add a property to the container whilst removing all other properties with the same property name.
     * @param property the property to add
     * @return a reference to the container to support method chaining
     */
    default <T extends PropertyContainer> T replace(Property property) {
        setPropertyList((PropertyList) getPropertyList().replace(property));
        return (T) this;
    }

    /**
     * A functional method used to apply a property to a container in an undefined way.
     *
     * For example, a null check can be introduced as follows:
     *
     *  container.with((c, p) -> if (p != null) c.add(p); return c;)
     * @param f
     * @param p
     * @return
     * @param <T>
     */
    default <T extends PropertyContainer, P> T with(BiFunction<T, P, T> f, P p) {
        return f.apply((T) this, p);
    }
}
