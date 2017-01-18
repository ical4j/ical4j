package net.fortuna.ical4j.model.rfc5545;

import net.fortuna.ical4j.model.Rfc5545PropertyRule;
import net.fortuna.ical4j.model.property.DtStamp;

/**
 * 
 * @author daniel grigore
 *
 */
public class DTStampRule implements Rfc5545PropertyRule<DtStamp> {

    @Override
    public void applyTo(DtStamp element) {
        if (element.getValue() != null && !element.isUtc()) {
            element.setUtc(true);
        }
    }

    @Override
    public Class<DtStamp> getSupportedType() {
        return DtStamp.class;
    }
}
