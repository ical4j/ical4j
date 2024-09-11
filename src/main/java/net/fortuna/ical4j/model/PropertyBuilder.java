package net.fortuna.ical4j.model;

import net.fortuna.ical4j.model.property.DateListProperty;
import net.fortuna.ical4j.model.property.DateProperty;
import net.fortuna.ical4j.model.property.UtcProperty;
import net.fortuna.ical4j.model.property.XProperty;
import net.fortuna.ical4j.util.CompatibilityHints;
import org.apache.commons.codec.DecoderException;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Provides a configurable builder for creating {@link Property} instances from {@link String} values.
 *
 * You can specify an arbitrary list of supported property factories, and a list of property names to ignore.
 */
public class PropertyBuilder extends AbstractContentBuilder {

    private final List<PropertyFactory<?>> factories;

    private String name;

    private String prefix;

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
        var nameParts = name.split("\\.");
        if (nameParts.length > 1) {
            this.prefix = String.join(".", Arrays.copyOfRange(nameParts, 0, nameParts.length - 1));
            this.name = nameParts[nameParts.length-1];
        } else {
            this.name = name;
        }
        return this;
    }

    public boolean hasName(String name) {
        return name.equalsIgnoreCase(this.name);
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

        // remove TZID parameters for FORM #2 dates..
        if (CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING)) {
            parameters.removeIf(p -> "UTC".equals(p.getValue()));
        }

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

        if (prefix != null) {
            property.setPrefix(prefix);
        }

        return property;
    }
}
