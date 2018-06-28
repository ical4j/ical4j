package net.fortuna.ical4j.validate.property;

import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.validate.ParameterValidator;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.Validator;

import java.util.Arrays;
import java.util.List;

/**
 * A validator implementation that ensures each parameter is specified not more than once on a property instance.
 */
public class OneOrLessParameterValidator implements Validator<Property> {

    private final List<String> parameters;

    public OneOrLessParameterValidator(String...parameters) {
        this.parameters = Arrays.asList(parameters);
    }

    @Override
    public void validate(final Property target) throws ValidationException {
        parameters.forEach(parameter -> ParameterValidator.getInstance().assertOneOrLess(parameter, target.getParameters()));
    }
}
