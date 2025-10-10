package net.fortuna.ical4j.validate;

import java.util.ServiceLoader;

/**
 * Factory for creating calendar validators.
 * <p>
 * This class provides a static method to retrieve an instance of
 * {@link CalendarValidatorFactory}. The factory instance is loaded using
 * the Java ServiceLoader mechanism, allowing for flexible and extensible
 * implementations.
 * <p>
 * The default implementation is provided by {@link DefaultCalendarValidatorFactory}.
 * This allows users to easily switch to a custom implementation by providing
 * their own factory class in the classpath.
 * <p>
 * Usage:
 * <pre>
 * CalendarValidatorFactory factory = AbstractCalendarValidatorFactory.getInstance();
 * CalendarValidator validator = factory.createValidator();
 * </pre>
 *
 * Created by fortuna on 13/09/15.
 */
public abstract class AbstractCalendarValidatorFactory {

    private static final CalendarValidatorFactory instance;
    static {
        instance = ServiceLoader.load(CalendarValidatorFactory.class, DefaultCalendarValidatorFactory.class.getClassLoader()).iterator().next();
    }

    public static CalendarValidatorFactory getInstance() {
        return instance;
    }
}
