package net.fortuna.ical4j.model;

import java.util.List;
import java.util.Optional;

public interface ComponentListAccessor<T extends Component> {

    ComponentList<T> getComponentList();

    default <C extends T> List<C> getComponents(final String... name) {
        return getComponentList().get(name);
    }

    default <C extends T> Optional<C> getComponent(final String name) {
        return getComponentList().getFirst(name);
    }
}
