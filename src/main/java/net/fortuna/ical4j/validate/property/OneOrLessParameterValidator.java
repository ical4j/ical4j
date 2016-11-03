package net.fortuna.ical4j.validate.property;

import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.validate.ParameterValidator;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.Validator;
import org.apache.commons.collections4.Closure;
import org.apache.commons.collections4.CollectionUtils;

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
        CollectionUtils.forAllDo(parameters, new Closure<String>() {
            @Override
            public void execute(String input) {
                ParameterValidator.getInstance().assertOneOrLess(input, target.getParameters());
            }
        });
    }
}
