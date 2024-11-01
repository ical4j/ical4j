package net.fortuna.ical4j.transform.itip;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.transform.Transformer;

import java.util.function.BiFunction;

/**
 * Created by fortuna on 19/07/2017.
 * @deprecated use {@link net.fortuna.ical4j.model.PropertyContainer#with(BiFunction, Object)} and
 * {@link net.fortuna.ical4j.model.CalendarPropertyModifiers#METHOD} instead.
 */
@Deprecated
public class MethodUpdate implements Transformer<Calendar> {

    private final Method newMethod;

    public MethodUpdate(Method method) {
        this.newMethod = method;
    }

    @Override
    public Calendar apply(Calendar object) {
        return object.replace(newMethod);
    }
}
