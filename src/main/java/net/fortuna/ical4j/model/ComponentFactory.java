package net.fortuna.ical4j.model;

/**
 * Created by fortuna on 12/09/14.
 */
public interface ComponentFactory<T extends Component> {

    T createComponent();

    T createComponent(PropertyList properties);

    T createComponent(PropertyList properties, ComponentList subComponents);

    boolean supports(String name);
}
