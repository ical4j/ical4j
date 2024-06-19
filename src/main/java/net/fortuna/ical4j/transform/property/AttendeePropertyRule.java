package net.fortuna.ical4j.transform.property;

import net.fortuna.ical4j.model.property.Attendee;
import org.apache.commons.lang3.StringUtils;

import java.net.URI;

public class AttendeePropertyRule implements Rfc5545PropertyRule<Attendee> {

    private static final String MAILTO = "mailto";
    private static final String APOSTROPHE = "'";
    private static final int MIN_LENGTH = 3;

    @Override
    public void applyTo(Attendee element) {
        if (element == null) {
            return;
        }
        URI calAddress = element.getCalAddress();
        if (calAddress == null) {
            return;
        }
        String scheme = calAddress.getScheme();
        if (scheme != null && StringUtils.startsWithIgnoreCase(scheme, MAILTO)) {
            String part = calAddress.getSchemeSpecificPart();
            if (part != null && part.length() >= MIN_LENGTH && StringUtils.startsWith(part, APOSTROPHE)
                    && StringUtils.endsWith(part, APOSTROPHE)) {
                String newPart = part.substring(1, part.length() - 1);
                safelySetNewValue(element, newPart);
            }
        }
    }

    private static void safelySetNewValue(Attendee element, String newPart) {
        element.setValue(MAILTO + ":" + newPart);
    }

    @Override
    public Class<Attendee> getSupportedType() {
        return Attendee.class;
    }

}
