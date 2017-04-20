package net.fortuna.ical4j.transform.rfc5545;

import net.fortuna.ical4j.model.property.TzId;

/**
 * 
 * @author daniel grigore
 * @author corneliu dobrota
 */
public class TzIdRule implements Rfc5545PropertyRule<TzId> {

    @Override
    public void applyTo(TzId element) {
        TzHelper.correctTzValueOf(element);

    }

    @Override
    public Class<TzId> getSupportedType() {
        return TzId.class;
    }
}
