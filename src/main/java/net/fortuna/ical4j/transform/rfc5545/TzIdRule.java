package net.fortuna.ical4j.transform.rfc5545;

import net.fortuna.ical4j.model.TimeZoneAlias;
import net.fortuna.ical4j.model.TimeZoneRegistryImpl;
import net.fortuna.ical4j.model.property.TzId;
import net.fortuna.ical4j.util.ResourceLoader;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 
 * @author daniel grigore
 * @author corneliu dobrota
 */
public class TzIdRule implements Rfc5545PropertyRule<TzId> {

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
    public void applyTo(TzId element) {
        correctTzValueOf(element);

    }

    private void correctTzValueOf(TzId tzProperty) {
        Optional<String> validTimezone = TimeZoneAlias.getTimeZoneIdFromAlias(ALIASES, tzProperty.getValue());
        if (validTimezone.isPresent()) {
            tzProperty.setValue(validTimezone.get());
        }
    }

    @Override
    public Class<TzId> getSupportedType() {
        return TzId.class;
    }
}
