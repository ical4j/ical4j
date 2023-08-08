package net.fortuna.ical4j.model;

public interface ComponentContainer<C extends Component> extends ComponentListAccessor<C> {

    void setComponentList(ComponentList<C> components);

    /**
     * Add a subcomponent to this component.
     * @param component the subcomponent to add
     * @return a reference to this component to support method chaining
     */
    default <T extends ComponentContainer<C>> T add(C component) {
        setComponentList((ComponentList<C>) getComponentList().add(component));
        return (T) this;
    }

    /**
     * Remove a subcomponent from this component.
     * @param component the subcomponent to remove
     * @return a reference to this component to support method chaining
     */
    default <T extends ComponentContainer<C>> T remove(C component) {
        setComponentList((ComponentList<C>)  getComponentList().remove(component));
        return (T) this;
    }

    /**
     * Add a subcomponent to this component whilst removing all other subcomponents with the same component name.
     * @param component the subcomponent to add
     * @return a reference to the component to support method chaining
     */
    default <T extends ComponentContainer<C>> T replace(C component) {
        setComponentList((ComponentList<C>)  getComponentList().replace(component));
        return (T) this;
    }
}
