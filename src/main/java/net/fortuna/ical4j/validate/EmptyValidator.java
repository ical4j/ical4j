package net.fortuna.ical4j.validate;

/**
 * Created by fortuna on 12/09/15.
 */
public final class EmptyValidator<T> implements Validator<T> {

    private static final long serialVersionUID = 1L;

    @Override
    public ValidationResult validate(T target) throws ValidationException {
        return ValidationResult.EMPTY;
    }
}
