package net.fortuna.ical4j.transform.rfc5545;

import net.fortuna.ical4j.model.property.Created;

import java.text.ParseException;

/**
 * 
 * @author daniel grigore
 *
 */
public class CreatedPropertyRule implements Rfc5545PropertyRule<Created> {

    private static final String UTC_MARKER = "Z";

    @Override
    public void applyTo(Created created) {
        if (created.isUtc() || created.getTimeZone() != null) {
            return;
        }
        try {
            created.setValue(created.getValue() + UTC_MARKER);
        } catch (ParseException e) {
            // Let the value as it is
        }
    }

    @Override
    public Class<Created> getSupportedType() {
        return Created.class;
    }

}
