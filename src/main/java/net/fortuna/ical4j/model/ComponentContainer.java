package net.fortuna.ical4j.model;

import java.util.List;

public interface ComponentContainer<T extends Component> {

    ComponentList<T> getComponents();

    default <C extends T> List<C> getComponents(final String name) {
        return getComponents().getComponents(name);
    }

    default <C extends T> C getComponent(final String name) {
        return (C) getComponents().getComponent(name);
    }
}
