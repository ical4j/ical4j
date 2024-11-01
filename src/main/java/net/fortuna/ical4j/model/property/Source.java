package net.fortuna.ical4j.model.property;

import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactory;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.ValidationResult;

/**
 * Created by fortuna on 11/09/15.
 */
public class Source extends Property {

    private static final long serialVersionUID = 1L;

    public static final String PROPERTY_NAME = "SOURCE";

    private String value;

    public Source() {
        super(PROPERTY_NAME);
    }

    public Source(ParameterList params, String value) {
        super(PROPERTY_NAME, params);
        setValue(value);
    }

    @Override
    public void setValue(String aValue) {
        this.value = aValue;
    }

    @Override
    public ValidationResult validate() throws ValidationException {
        return ValidationResult.EMPTY;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    protected PropertyFactory<Source> newFactory() {
        return new Factory();
    }

    public static class Factory extends Content.Factory implements PropertyFactory<Source> {
        public Factory() {
            super(PROPERTY_NAME);
        }

        @Override
        public Source createProperty() {
            return new Source();
        }

        @Override
        public Source createProperty(ParameterList parameters, String value) {
            return new Source(parameters, value);
        }
    }
}
