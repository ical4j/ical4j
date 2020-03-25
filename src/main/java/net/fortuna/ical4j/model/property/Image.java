package net.fortuna.ical4j.model.property;

import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactory;
import net.fortuna.ical4j.validate.ValidationException;

import java.util.List;

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

    public Image(List<Parameter> params, String value) {
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

    @Override
    public Property copy() {
        return new Factory().createProperty(getParameters(), getValue());
    }

    public static class Factory extends Content.Factory implements PropertyFactory<Image> {
        public Factory() {
            super(PROPERTY_NAME);
        }

        public Image createProperty() {
            return new Image();
        }

        public Image createProperty(List<Parameter> parameters, String value) {
            Image property = new Image(parameters, value);
            return property;
        }
    }
}
