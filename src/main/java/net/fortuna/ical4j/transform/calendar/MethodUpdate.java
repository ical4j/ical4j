package net.fortuna.ical4j.transform.calendar;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.transform.Transformer;

/**
 * Created by fortuna on 19/07/2017.
 */
public class MethodUpdate implements Transformer<Calendar> {

    private final Method newMethod;

    public MethodUpdate(Method method) {
        this.newMethod = method;
    }

    @Override
    public Calendar transform(Calendar object) {
        return object.replace(newMethod);
    }
}
