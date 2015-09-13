package net.fortuna.ical4j.validate;

import net.fortuna.ical4j.model.Calendar;

/**
 * Created by fortuna on 13/09/15.
 */
public interface CalendarValidatorFactory {

    /**
     * Provides a validator implementation specific to the factory.
     * @return a new validator instance
     */
    Validator<Calendar> newInstance();
}
