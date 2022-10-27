package net.fortuna.ical4j.model;

public interface ComponentContainer<T extends Component> extends ComponentListAccessor<T> {

    void setComponentList(ComponentList<T> components);

    /**
     * Add a subcomponent to this component.
     * @param component the subcomponent to add
     * @return a reference to this component to support method chaining
     */
    default ComponentContainer<T> add(T component) {
        setComponentList((ComponentList<T>) getComponentList().add(component));
        return this;
    }

    /**
     * Remove a subcomponent from this component.
     * @param component the subcomponent to remove
     * @return a reference to this component to support method chaining
     */
    default ComponentContainer<T> remove(T component) {
        setComponentList((ComponentList<T>)  getComponentList().remove(component));
        return this;
    }

    /**
     * Add a subcomponent to this component whilst removing all other subcomponents with the same component name.
     * @param component the subcomponent to add
     * @return a reference to the component to support method chaining
     */
    default ComponentContainer<T> replace(T component) {
        setComponentList((ComponentList<T>)  getComponentList().replace(component));
        return this;
    }
}
