package net.fortuna.ical4j.model;

import net.fortuna.ical4j.model.component.XComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ComponentBuilder<T extends Component> extends AbstractContentBuilder {

    private final List<ComponentFactory<?>> factories;

    private String name;

    private final List<Property> properties = new ArrayList<>();

    private final List<Component> subComponents = new ArrayList<>();

    public ComponentBuilder() {
        this(Collections.emptyList());
    }

    public ComponentBuilder(List<ComponentFactory<?>> factories) {
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
                    component = factory.createComponent(new PropertyList(properties),
                            new ComponentList<>(subComponents));
                } else {
                    component = factory.createComponent(new PropertyList(properties));
                }
            }
        }

        if (component == null) {
            if (isExperimentalName(name)) {
                component = new XComponent(name, new PropertyList(properties));
            } else if (allowIllegalNames()) {
                component = new XComponent(name, new PropertyList(properties));
            } else {
                throw new IllegalArgumentException("Unsupported component [" + name + "]");
            }
        }
        return (T) component;
    }
}
