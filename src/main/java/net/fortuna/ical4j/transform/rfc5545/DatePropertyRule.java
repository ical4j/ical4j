package net.fortuna.ical4j.transform.rfc5545;

import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.property.DateProperty;

/**
 * 
 * @author daniel grigore
 * @author corneliu dobrota
 */
public class DatePropertyRule implements Rfc5545PropertyRule<DateProperty> {

    @Override
    public void applyTo(DateProperty element) {
        TzHelper.correctTzParameterFrom(element);
        if (!element.isUtc() || element.getParameter(Parameter.TZID) == null) {
            return;
        }
        element.getParameters().removeAll(Parameter.TZID);
        element.setUtc(true);
    }

    @Override
    public Class<DateProperty> getSupportedType() {
        return DateProperty.class;
    }

}
