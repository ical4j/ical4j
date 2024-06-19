package net.fortuna.ical4j.validate;

import net.fortuna.ical4j.model.Calendar;

import static net.fortuna.ical4j.model.Property.*;
import static net.fortuna.ical4j.validate.ValidationRule.ValidationType.One;
import static net.fortuna.ical4j.validate.ValidationRule.ValidationType.OneOrLess;

/**
 * Created by fortuna on 13/09/15.
 */
public class DefaultCalendarValidatorFactory implements  CalendarValidatorFactory {

    @Override
    public Validator<Calendar> newInstance() {
        return new CalendarValidatorImpl(new ValidationRule<>(One, PRODID, VERSION),
                new ValidationRule<>(OneOrLess, CALSCALE, METHOD));
    }
}
