package net.fortuna.ical4j.validate;

import net.fortuna.ical4j.model.Calendar;

import static net.fortuna.ical4j.model.Property.*;
import static net.fortuna.ical4j.validate.ValidationRule.ValidationType.One;
import static net.fortuna.ical4j.validate.ValidationRule.ValidationType.OneOrLess;

/**
 * Factory implementation for creating default calendar validators.
 * <p>
 * This class implements the {@link CalendarValidatorFactory} interface and provides
 * a method to create a new instance of a calendar validator. The validator checks
 * for the presence of required properties such as PRODID and VERSION, and ensures that
 * CALSCALE and METHOD are specified one or fewer times.
 * <p>
 * Usage:
 * <pre>
 * CalendarValidatorFactory factory = new DefaultCalendarValidatorFactory();
 * Validator<Calendar> validator = factory.newInstance();
 * </pre>
 *
 * Created by fortuna on 13/09/15.
 */
public class DefaultCalendarValidatorFactory implements  CalendarValidatorFactory {

    @Override
    public Validator<Calendar> newInstance() {
        return new CalendarValidatorImpl(new ValidationRule<>(One, PRODID, VERSION),
                new ValidationRule<>(OneOrLess, CALSCALE, METHOD));
    }
}
