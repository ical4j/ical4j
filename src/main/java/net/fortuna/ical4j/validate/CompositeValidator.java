package net.fortuna.ical4j.validate;

import java.util.Arrays;
import java.util.List;

/**
 * Created by fortuna on 7/3/17.
 */
public class CompositeValidator<T> implements Validator<T> {

    private final List<Validator<T>> validators;

    public CompositeValidator(Validator<T>... validator) {
        validators = Arrays.asList(validator);
    }

    @Override
    public void validate(T target) throws ValidationException {
        for (Validator<T> validator : validators) {
            validator.validate(target);
        }
    }
}
