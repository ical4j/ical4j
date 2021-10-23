package net.fortuna.ical4j.model;

import net.fortuna.ical4j.model.component.XComponent;

import java.util.Collections;
import java.util.List;

public class ComponentBuilder<T extends Component> extends AbstractContentBuilder {

    private final List<ComponentFactory<?>> factories;

    private String name;

    private PropertyList<Property> properties = new PropertyList<>();

    private ComponentList<Component> subComponents = new ComponentList<>();

    public ComponentBuilder() {
        this(true);
    }

    public ComponentBuilder(boolean allowIllegalNames) {
        this(Collections.emptyList(), allowIllegalNames);
    }

    public ComponentBuilder(List<ComponentFactory<?>> factories) {
        this(factories, true);
    }

    public ComponentBuilder(List<ComponentFactory<?>> factories, boolean allowIllegalNames) {
        super(allowIllegalNames);
        this.factories = factories;
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
                    component = factory.createComponent(properties);
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
