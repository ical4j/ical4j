package net.fortuna.ical4j.transform.rfc5545;

import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.parameter.TzId;
import net.fortuna.ical4j.model.property.DateProperty;
import net.fortuna.ical4j.util.ResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 
 * @author daniel grigore
 * @author corneliu dobrota
 */
public class DatePropertyRule implements Rfc5545PropertyRule<DateProperty> {

    private static final Logger LOG = LoggerFactory.getLogger(DatePropertyRule.class);
    private static final TimeZoneRegistry TIMEZONE_REGISTRY = DefaultTimeZoneRegistryFactory.getInstance()
            .createRegistry();

    private static final List<TimeZoneAlias> ALIASES = new ArrayList<>();
    static {
        try (InputStream aliasInputStream = ResourceLoader.getResourceAsStream("net/fortuna/ical4j/transform/rfc5545/msTimezones")) {
            ALIASES.addAll(TimeZoneAlias.loadAliases(aliasInputStream));
        } catch (IOException ioe) {
            LoggerFactory.getLogger(TimeZoneRegistryImpl.class).warn(
                    "Error loading timezone aliases: " + ioe.getMessage());
        }
    }

    @Override
    public void applyTo(DateProperty element) {
        correctTzParameterFrom(element);
        if (!element.isUtc() || element.getParameter(Parameter.TZID) == null) {
            return;
        }
        element.getParameters().removeAll(Parameter.TZID);
        element.setUtc(true);
    }

    private void correctTzParameterFrom(DateProperty property) {
        if (property.getValue() != null && property.getValue().endsWith("Z")) {
            property.getParameters().removeAll(Parameter.TZID);
            return;
        }
        if (property.getParameter(Parameter.TZID) != null) {
            String tzIdValue = property.getParameter(Parameter.TZID).getValue();
            Optional<String> newTimezoneId = TimeZoneAlias.getTimeZoneIdFromAlias(ALIASES, tzIdValue);
            String value = property.getValue();
            correctTzParameter(property, newTimezoneId);
            if (newTimezoneId.isPresent()) {
                property.setTimeZone(TIMEZONE_REGISTRY.getTimeZone(newTimezoneId.get()));
                try {
                    property.setValue(value);
                } catch (ParseException e) {
                    LOG.warn("Failed to reset property value", e);
                }
            } else {
                property.setUtc(true);
            }
        }
    }

    private void correctTzParameter(Property property, Optional<String> newTimezoneId) {
        property.getParameters().removeAll(Parameter.TZID);
        if (newTimezoneId.isPresent()) {
            property.getParameters().add(new TzId(newTimezoneId.get()));
        }
    }

    @Override
    public Class<DateProperty> getSupportedType() {
        return DateProperty.class;
    }

}
