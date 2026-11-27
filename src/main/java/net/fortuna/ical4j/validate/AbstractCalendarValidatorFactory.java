package net.fortuna.ical4j.validate;

import java.util.Iterator;
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
        // fall back to the default implementation when no service registration is visible, e.g. when
        // META-INF/services has been stripped by ProGuard/R8 or a packaging tool (#125)..
        instance = select(ServiceLoader.load(CalendarValidatorFactory.class,
                DefaultCalendarValidatorFactory.class.getClassLoader()).iterator());
    }

    /**
     * Selects the factory to use from those registered. Any factory other than
     * {@link DefaultCalendarValidatorFactory} is preferred over it, so the result does not depend on
     * classpath order. The default is used only when nothing else is registered.
     */
    static CalendarValidatorFactory select(Iterator<? extends CalendarValidatorFactory> factories) {
        CalendarValidatorFactory fallback = null;
        while (factories.hasNext()) {
            CalendarValidatorFactory factory = factories.next();
            if (!(factory instanceof DefaultCalendarValidatorFactory)) {
                return factory;
            }
            if (fallback == null) {
                fallback = factory;
            }
        }
        return fallback != null ? fallback : new DefaultCalendarValidatorFactory();
    }

    public static CalendarValidatorFactory getInstance() {
        return instance;
    }
}
