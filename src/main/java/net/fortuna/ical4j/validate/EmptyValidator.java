package net.fortuna.ical4j.validate;

/**
 * A no-op validator implementation that always returns an empty validation result.
 * <p>
 * This class implements the {@link Validator} interface and provides a simple
 * validation logic that does not perform any checks. It is useful in scenarios where
 * validation is not required or when a placeholder validator is needed.
 * <p>
 * Usage:
 * <pre>
 * Validator<MyType> validator = new EmptyValidator<>();
 * ValidationResult result = validator.validate(myObject);
 * if (result.isValid()) {
 *     // Object is valid
 * } else {
 *     // Handle validation errors
 * }
 * </pre>
 *
 * Created by fortuna on 12/09/15.
 */
public final class EmptyValidator<T> implements Validator<T> {

    private static final long serialVersionUID = 1L;

    @Override
    public ValidationResult validate(T target) throws ValidationException {
        return ValidationResult.EMPTY;
    }
}
