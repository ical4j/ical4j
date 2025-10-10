package net.fortuna.ical4j.model;

/**
 * A factory interface for creating instances of {@link Component}.
 * This interface allows for the creation of components with or without properties and sub-components.
 *
 * @param <T> the type of component to create
 */
public interface ComponentFactory<T extends Component> {

    T createComponent();

    T createComponent(PropertyList properties);

    default T createComponent(PropertyList properties, ComponentList<? extends Component> subComponents) {
        // ignore subcomponents by default. override this method for subclasses that support subcomponents..
        return createComponent(properties);
    }

    boolean supports(String name);
}
