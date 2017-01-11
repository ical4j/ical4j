package net.fortuna.ical4j.model.rfc5545;

import net.fortuna.ical4j.model.Rfc5545PropertyRule;
import net.fortuna.ical4j.model.property.DateListProperty;

/**
 * 
 * @author corneliu dobrota
 * @author daniel grigore
 *
 */
public class DateListPropertyRule implements Rfc5545PropertyRule<DateListProperty> {

    @Override
    public void applyTo(DateListProperty element) {
        TzHelper.correctTzParameterFrom(element);
    }

    @Override
    public Class<DateListProperty> getSupportedType() {
        return DateListProperty.class;
    }

}
