package net.fortuna.ical4j.model.property;

import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.ValidationResult;

import java.time.temporal.TemporalAmount;

/**
 * Created by fortuna on 11/09/15.
 */
public class RefreshInterval extends Property {

    private static final long serialVersionUID = 1L;

    public static final String PROPERTY_NAME = "REFRESH-INTERVAL";

    private TemporalAmountAdapter duration;

    public RefreshInterval() {
        super(PROPERTY_NAME);
    }

    public RefreshInterval(ParameterList params, String value) {
        super(PROPERTY_NAME, params);
        setValue(value);
    }

    public RefreshInterval(ParameterList params, TemporalAmount duration) {
        super(PROPERTY_NAME, params);
        this.duration = new TemporalAmountAdapter(duration);
    }

    @Override
    public void setValue(String aValue) {
        duration = TemporalAmountAdapter.parse(aValue);
    }

    @Override
    public ValidationResult validate() throws ValidationException {
        return ValidationResult.EMPTY;
    }

    @Override
    public String getValue() {
        return duration.toString();
    }

    @Override
    protected PropertyFactory<RefreshInterval> newFactory() {
        return new Factory();
    }

    public static class Factory extends Content.Factory implements PropertyFactory<RefreshInterval> {
        public Factory() {
            super(PROPERTY_NAME);
        }

        @Override
        public RefreshInterval createProperty() {
            return new RefreshInterval();
        }

        @Override
        public RefreshInterval createProperty(ParameterList parameters, String value) {
            return new RefreshInterval(parameters, value);
        }
    }
}
