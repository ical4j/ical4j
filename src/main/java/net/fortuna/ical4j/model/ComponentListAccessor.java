package net.fortuna.ical4j.model;

import java.util.List;
import java.util.Optional;

/**
 * An interface for accessing a list of components.
 * This interface provides methods to retrieve components by name or get the entire component list.
 *
 * @param <T> the type of component in the list
 */
public interface ComponentListAccessor<T extends Component> {

    ComponentList<T> getComponentList();

    default <C extends T> List<C> getComponents(final String... name) {
        return getComponentList().get(name);
    }

    default <C extends T> Optional<C> getComponent(final String name) {
        return getComponentList().getFirst(name);
    }
}
