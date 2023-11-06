package net.fortuna.ical4j.transform.calendar;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.property.Method;

import java.util.function.UnaryOperator;

/**
 * Created by fortuna on 19/07/2017.
 */
public class MethodUpdate implements UnaryOperator<Calendar> {

    private final Method newMethod;

    public MethodUpdate(Method method) {
        this.newMethod = method;
    }

    @Override
    public Calendar apply(Calendar object) {
        return object.replace(newMethod);
    }
}
