package net.fortuna.ical4j.transform.rfc5545;

import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.TimeZoneAlias;
import net.fortuna.ical4j.model.TimeZoneRegistryImpl;
import net.fortuna.ical4j.model.parameter.TzId;
import net.fortuna.ical4j.model.property.DateListProperty;
import net.fortuna.ical4j.util.ResourceLoader;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 
 * @author corneliu dobrota
 * @author daniel grigore
 *
 */
public class DateListPropertyRule implements Rfc5545PropertyRule<DateListProperty> {

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
    public void applyTo(DateListProperty element) {
        correctTzParameterFrom(element);
    }

    private void correctTzParameterFrom(Property property) {
        if (property.getParameter(Parameter.TZID) != null) {
            String tzIdValue = property.getParameter(Parameter.TZID).getValue();
            Optional<String> newTimezoneId = TimeZoneAlias.getTimeZoneIdFromAlias(ALIASES, tzIdValue);
            correctTzParameter(property, newTimezoneId);
        }
    }

    private void correctTzParameter(Property property, Optional<String> newTimezoneId) {
        property.getParameters().removeAll(Parameter.TZID);
        if (newTimezoneId.isPresent()) {
            property.getParameters().add(new TzId(newTimezoneId.get()));
        }
    }

    @Override
    public Class<DateListProperty> getSupportedType() {
        return DateListProperty.class;
    }

}
