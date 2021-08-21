package net.fortuna.ical4j.model;

import java.util.List;
import java.util.Optional;

public interface ComponentContainer<T extends Component> {

    ComponentList<T> getComponents();

    void setComponents(ComponentList<T> components);

    default <C extends T> List<C> getComponents(final String name) {
        return getComponents().get(name);
    }

    default <C extends T> Optional<C> getComponent(final String name) {
        return getComponents().getFirst(name);
    }

    /**
     * Add a subcomponent to this component.
     * @param component the subcomponent to add
     * @return a reference to this component to support method chaining
     */
    default ComponentContainer<T> add(T component) {
        setComponents((ComponentList<T>) getComponents().add(component));
        return this;
    }

    /**
     * Remove a subcomponent from this component.
     * @param component the subcomponent to remove
     * @return a reference to this component to support method chaining
     */
    default ComponentContainer<T> remove(T component) {
        setComponents((ComponentList<T>)  getComponents().remove(component));
        return this;
    }

    /**
     * Add a subcomponent to this component whilst removing all other subcomponents with the same component name.
     * @param component the subcomponent to add
     * @return a reference to the component to support method chaining
     */
    default ComponentContainer<T> replace(T component) {
        setComponents((ComponentList<T>)  getComponents().replace(component));
        return this;
    }
}
