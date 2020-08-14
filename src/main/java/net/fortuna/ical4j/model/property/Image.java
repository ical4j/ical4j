package net.fortuna.ical4j.model.property;

import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactory;
import net.fortuna.ical4j.validate.ValidationException;

/**
 * Created by fortuna on 11/09/15.
 */
public class Image extends Property {

    private static final long serialVersionUID = 1L;

    public static final String PROPERTY_NAME = "IMAGE";

    private String value;

    public Image() {
        super(PROPERTY_NAME, new Factory());
    }

    public Image(ParameterList params, String value) {
        super(PROPERTY_NAME, params, new Factory());
        setValue(value);
    }

    @Override
    public void setValue(String aValue) {
        this.value = aValue;
    }

    @Override
    public void validate() throws ValidationException {

    }

    @Override
    public String getValue() {
        return value;
    }

    public static class Factory extends Content.Factory implements PropertyFactory<Image> {
        public Factory() {
            super(PROPERTY_NAME);
        }

        @Override
        public Image createProperty() {
            return new Image();
        }

        @Override
        public Image createProperty(ParameterList parameters, String value) {
            Image property = new Image(parameters, value);
            return property;
        }
    }
}
