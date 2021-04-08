package net.fortuna.ical4j.model.property;

import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.validate.ValidationException;

import java.time.temporal.TemporalAmount;

/**
 * Created by fortuna on 11/09/15.
 */
public class RefreshInterval extends Property {

    private static final long serialVersionUID = 1L;

    public static final String PROPERTY_NAME = "REFRESH-INTERVAL";

    private TemporalAmountAdapter duration;

    public RefreshInterval() {
        super(PROPERTY_NAME, new Factory());
    }

    public RefreshInterval(ParameterList params, String value) {
        super(PROPERTY_NAME, params, new Factory());
        setValue(value);
    }

    public RefreshInterval(ParameterList params, TemporalAmount duration) {
        super(PROPERTY_NAME, params, new Factory());
        this.duration = new TemporalAmountAdapter(duration);
    }

    @Override
    public void setValue(String aValue) {
        duration = TemporalAmountAdapter.parse(aValue);
    }

    @Override
    public void validate() throws ValidationException {

    }

    @Override
    public String getValue() {
        return duration.toString();
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
            RefreshInterval property = new RefreshInterval(parameters, value);
            return property;
        }
    }
}
