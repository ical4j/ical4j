package net.fortuna.ical4j.model;

public interface ComponentContainer {

    ComponentList<? extends Component> getComponents();

    default <C extends Component> ComponentList<C> getComponents(final String name) {
        return (ComponentList<C>) getComponents().getComponents(name);
    }

    default <C extends Component> C getComponent(final String name) {
        return (C) getComponents().getComponent(name);
    }
}
