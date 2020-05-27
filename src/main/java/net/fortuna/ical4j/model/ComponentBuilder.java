package net.fortuna.ical4j.model;

import net.fortuna.ical4j.model.component.XComponent;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class ComponentBuilder<T extends Component> extends AbstractContentBuilder {

    private final List<ComponentFactory<?>> factories = new ArrayList<>();

    private String name;

    private PropertyList properties = new PropertyList();

    private ComponentList<Component> subComponents = new ComponentList<>();

    public ComponentBuilder<?> factories(List<ComponentFactory<?>> factories) {
        this.factories.addAll(factories);
        return this;
    }

    public ComponentBuilder<?> name(String name) {
        // component names are case-insensitive, but convert to upper case to simplify further processing
        this.name = name.toUpperCase();
        return this;
    }

    public ComponentBuilder<?> property(Property property) {
        properties.add(property);
        return this;
    }

    public ComponentBuilder<?> subComponent(Component subComponent) {
        subComponents.add(subComponent);
        return this;
    }

    @SuppressWarnings("unchecked")
    public T build() {
        Component component = null;
        for (ComponentFactory<?> factory : factories) {
            if (factory.supports(name)) {
                if (!subComponents.isEmpty()) {
                    component = factory.createComponent(properties, subComponents);
                } else {
                    try {
                        component = factory.createComponent(properties);
                    } catch (URISyntaxException e) {
                        throw new IllegalArgumentException("Invalid content", e);
                    }
                }
            }
        }

        if (component == null) {
            if (isExperimentalName(name)) {
                component = new XComponent(name, properties);
            } else if (allowIllegalNames()) {
                component = new XComponent(name, properties);
            } else {
                throw new IllegalArgumentException("Unsupported component [" + name + "]");
            }
        }
        return (T) component;
    }
}
