package net.fortuna.ical4j.validate;

import net.fortuna.ical4j.model.Calendar;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * Created by fortuna on 7/3/17.
 */
public class CompositeCalendarValidatorFactory implements CalendarValidatorFactory {

    private final transient ServiceLoader<CalendarValidatorFactory> factoryLoader;

    public CompositeCalendarValidatorFactory(ServiceLoader<CalendarValidatorFactory> factoryLoader) {
        this.factoryLoader = factoryLoader;
    }

    @Override
    public Validator<Calendar> newInstance() {
        List<Validator<Calendar>> validators = new ArrayList<>();
        for (CalendarValidatorFactory factory : factoryLoader) {
            validators.add(factory.newInstance());
        }
        return new CompositeValidator<>(validators.toArray(new Validator[validators.size()]));
    }
}
