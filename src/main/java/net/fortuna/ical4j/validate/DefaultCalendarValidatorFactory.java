package net.fortuna.ical4j.validate;

import net.fortuna.ical4j.model.Calendar;

/**
 * Created by fortuna on 13/09/15.
 */
public class DefaultCalendarValidatorFactory implements  CalendarValidatorFactory {
    @Override
    public Validator<Calendar> newInstance() {
        return new CalendarValidatorImpl();
    }
}
