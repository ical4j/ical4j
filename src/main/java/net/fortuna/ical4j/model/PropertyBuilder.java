package net.fortuna.ical4j.model;

import net.fortuna.ical4j.model.property.DateListProperty;
import net.fortuna.ical4j.model.property.DateProperty;
import net.fortuna.ical4j.model.property.XProperty;
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private TimeZone timeZone;

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

    public PropertyBuilder timeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
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

        if (property != null && timeZone != null) {
            updateTimeZone(property, timeZone);
        }

        return property;
    }

    private void updateTimeZone(Property property, TimeZone timezone) throws IOException {
        // Get the String representation of date(s) as
        // we will need this after changing the timezone
        final String strDate = property.getValue();

        try {
            ((DateProperty) property).setTimeZone(timezone);
        } catch (ClassCastException e) {
            try {
                ((DateListProperty) property).setTimeZone(timezone);
            } catch (ClassCastException e2) {
                if (CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING)) {
                    Logger log = LoggerFactory.getLogger(PropertyBuilder.class);
                    log.warn("Error setting timezone [" + timezone.getID()
                            + "] on property [" + property.getName()
                            + "]", e);
                } else {
                    throw e2;
                }
            }
        }

        // Reset value
        try {
            property.setValue(strDate);
        } catch (ParseException | URISyntaxException e) {
            // shouldn't happen as its already been parsed
            throw new CalendarException(e);
        }
    }
}
