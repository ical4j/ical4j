package net.fortuna.ical4j.model;

import java.net.URISyntaxException;

/**
 * Created by fortuna on 12/09/14.
 */
public interface ComponentFactory<T extends Component> {

    T createComponent();

    T createComponent(PropertyList properties) throws URISyntaxException;

    T createComponent(PropertyList properties, ComponentList subComponents);

    boolean supports(String name);
}
