package net.fortuna.ical4j.model;

import net.fortuna.ical4j.model.property.XProperty;
import net.fortuna.ical4j.util.Strings;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class PropertyBuilder extends AbstractContentBuilder {

    private List<PropertyFactory> factories = new ArrayList<>();

    private String name;

    private String value;

    private ParameterList parameters = new ParameterList();

    public PropertyBuilder factories(List<PropertyFactory> factories) {
        this.factories.addAll(factories);
        return this;
    }

    public PropertyBuilder name(String name) {
        // property names are case-insensitive, but convert to upper case to simplify further processing
        this.name = name.toUpperCase();
        return this;
    }

    public PropertyBuilder value(String value) {
        // remove any trailing whitespace
        this.value = value.trim();
        return this;
    }

    public PropertyBuilder parameter(Parameter parameter) {
        parameters.add(parameter);
        return this;
    }

    public Property build() throws ParseException, IOException, URISyntaxException {
        Property property = null;
        for (PropertyFactory factory : factories) {
            if (factory.supports(name)) {
                property = factory.createProperty(parameters, value);
                if (property instanceof Escapable) {
                    property.setValue(Strings.unescape(value));
                }
            }
        }

        if (property == null) {
            if (isExperimentalName(name)) {
                return new XProperty(name, parameters, value);
            } else if (allowIllegalNames()) {
                return new XProperty(name, parameters, value);
            } else {
                throw new IllegalArgumentException("Illegal property [" + name + "]");
            }
        }

        return property;
    }
}
