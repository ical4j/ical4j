package net.fortuna.ical4j.model;

public interface ComponentContainer<T extends Component> {

    ComponentList<T> getComponents();

    default <C extends T> ComponentList<C> getComponents(final String name) {
        return getComponents().getComponents(name);
    }

    default <C extends T> C getComponent(final String name) {
        return (C) getComponents().getComponent(name);
    }
}
