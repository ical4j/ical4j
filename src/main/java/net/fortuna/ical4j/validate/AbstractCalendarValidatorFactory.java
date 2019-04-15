package net.fortuna.ical4j.validate;

import java.util.ServiceLoader;

/**
 * Created by fortuna on 13/09/15.
 */
public abstract class AbstractCalendarValidatorFactory {

    private static CalendarValidatorFactory instance;
    static {
        instance = ServiceLoader.load(CalendarValidatorFactory.class, DefaultCalendarValidatorFactory.class.getClassLoader()).iterator().next();
    }

    public static CalendarValidatorFactory getInstance() {
        return instance;
    }
}
