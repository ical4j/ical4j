package net.fortuna.ical4j.model;

import net.fortuna.ical4j.model.property.DateListProperty;
import net.fortuna.ical4j.model.property.DateProperty;
import net.fortuna.ical4j.model.property.UtcProperty;
import net.fortuna.ical4j.model.property.XProperty;
import org.apache.commons.codec.DecoderException;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides a configurable builder for creating {@link Property} instances from {@link String} values.
 *
 * You can specify an arbitrary list of supported property factories, and a list of property names to ignore.
 */
public class PropertyBuilder extends AbstractContentBuilder {

    private final List<PropertyFactory<?>> factories;

    private String name;

    private String value;

    private final List<Parameter> parameters = new ArrayList<>();

    private TimeZoneRegistry timeZoneRegistry;

    private ZoneId defaultTimeZone;

    public PropertyBuilder() {
        this(new ArrayList<>());
    }

    public PropertyBuilder(List<PropertyFactory<? extends Property>> factories) {
        this.factories = factories;
    }

    /**
     * Set the list of property factories supporting this builder instance.
     * @param factories a list of property factories
     * @return the builder instance
     * @deprecated preference the constructor option for specifying factories
     */
    @Deprecated
    public PropertyBuilder factories(List<PropertyFactory<?>> factories) {
        this.factories.clear();
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

    public PropertyBuilder timeZoneRegistry(TimeZoneRegistry timeZoneRegistry) {
        this.timeZoneRegistry = timeZoneRegistry;
        return this;
    }

    public PropertyBuilder defaultTimeZone(ZoneId defaultTimeZone) {
        this.defaultTimeZone = defaultTimeZone;
        return this;
    }

    public Property build() {
        Property property = null;
        String decodedValue;
        try {
            decodedValue = PropertyCodec.INSTANCE.decode(value);
        } catch (DecoderException e) {
            decodedValue = value;
        }

        for (PropertyFactory<?> factory : factories) {
            if (factory.supports(name)) {
                property = factory.createProperty(new ParameterList(parameters), value);
                if (property instanceof DateProperty) {
                    DateProperty<?> dateProp = (DateProperty<?>) property;
                    // don't set timezone on UTC-formatted properties..
                    if (!(dateProp instanceof UtcProperty)) {
                        dateProp.setTimeZoneRegistry(timeZoneRegistry);
                        dateProp.setDefaultTimeZone(defaultTimeZone);
                        property.setValue(value);
                    }
                } else if (property instanceof DateListProperty) {
                    DateListProperty<?> dateListProperty = (DateListProperty<?>) property;
                    dateListProperty.setTimeZoneRegistry(timeZoneRegistry);
                    dateListProperty.setDefaultTimeZone(defaultTimeZone);
                    property.setValue(value);
                }
            }
        }

        if (property == null) {
            if (isExperimentalName(name)) {
                property = new XProperty(name, new ParameterList(parameters), value);
            } else if (allowIllegalNames()) {
                property = new XProperty(name, new ParameterList(parameters), value);
            } else {
                throw new IllegalArgumentException("Illegal property [" + name + "]");
            }
        }

        if (property instanceof Encodable) {
            property.setValue(decodedValue);
        }

        return property;
    }
}
