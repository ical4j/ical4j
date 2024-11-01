package net.fortuna.ical4j.model;

import java.util.function.BiFunction;

public interface ComponentContainer<C extends Component> extends ComponentListAccessor<C> {

    void setComponentList(ComponentList<C> components);

    /**
     * Add a subcomponent to this component.
     * @param component the subcomponent to add
     * @return a reference to this component to support method chaining
     */
    default <T extends ComponentContainer<C>> T add(C component) {
        setComponentList((ComponentList<C>) getComponentList().add(component));
        //noinspection unchecked
        return (T) this;
    }

    /**
     * Remove a subcomponent from this component.
     * @param component the subcomponent to remove
     * @return a reference to this component to support method chaining
     */
    default <T extends ComponentContainer<C>> T remove(C component) {
        setComponentList((ComponentList<C>)  getComponentList().remove(component));
        //noinspection unchecked
        return (T) this;
    }

    /**
     * Add a subcomponent to this component whilst removing all other subcomponents with the same component name.
     * @param component the subcomponent to add
     * @return a reference to the component to support method chaining
     */
    default <T extends ComponentContainer<C>> T replace(C component) {
        setComponentList((ComponentList<C>)  getComponentList().replace(component));
        //noinspection unchecked
        return (T) this;
    }

    /**
     * A functional method used to apply a component to a container in an undefined way.
     *
     * For example, a null check can be introduced as follows:
     *
     *  container.with((container, component) -> if (component != null) container.add(component); return container;)
     * @param f
     * @param c
     * @return
     * @param <T>
     */
    default <T extends ComponentContainer<C>> T with(BiFunction<T, C, T> f, C c) {
        return f.apply((T) this, c);
    }
}
