package net.fortuna.ical4j.validate;

import net.fortuna.ical4j.model.Calendar;

/**
 * Factory interface for creating calendar validators.
 * <p>
 * This interface defines a method to create a new instance of a validator
 * that can validate iCalendar {@link Calendar} objects. Implementations of this
 * interface should provide specific validation logic as required by the
 * iCalendar specification or application-specific rules.
 * <p>
 * The default implementation is provided by {@link DefaultCalendarValidatorFactory}.
 * This allows users to easily switch to a custom implementation by providing
 * their own factory class in the classpath.
 * <p>
 * Usage:
 * <pre>
 * CalendarValidatorFactory factory = AbstractCalendarValidatorFactory.getInstance();
 * Validator<Calendar> validator = factory.newInstance();
 * </pre>
 *
 * Created by fortuna on 13/09/15.
 */
public interface CalendarValidatorFactory {

    /**
     * Provides a validator implementation specific to the factory.
     * @return a new validator instance
     */
    Validator<Calendar> newInstance();
}
