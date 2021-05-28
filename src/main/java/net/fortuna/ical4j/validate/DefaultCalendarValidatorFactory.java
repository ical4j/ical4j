package net.fortuna.ical4j.validate;

import net.fortuna.ical4j.model.Calendar;

import static net.fortuna.ical4j.model.Property.*;
import static net.fortuna.ical4j.validate.ValidationRule.ValidationType.One;
import static net.fortuna.ical4j.validate.ValidationRule.ValidationType.OneOrLess;

/**
 * Created by fortuna on 13/09/15.
 */
public class DefaultCalendarValidatorFactory implements  CalendarValidatorFactory {

    private static final ValidationRule REQUIRED_PROPERTIES_RULE = new ValidationRule(One, PRODID, VERSION);
    private static final ValidationRule OPTIONAL_PROPERTIES_RULE = new ValidationRule(OneOrLess, CALSCALE, METHOD);

    @Override
    public Validator<Calendar> newInstance() {
        return new CalendarValidatorImpl(REQUIRED_PROPERTIES_RULE,
                OPTIONAL_PROPERTIES_RULE);
    }
}
