package net.fortuna.ical4j.agent;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.transform.MethodTransformer;
import net.fortuna.ical4j.transform.Transformer;
import net.fortuna.ical4j.util.Calendars;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fortuna on 19/07/2017.
 */
public abstract class AbstractUserAgent<T extends CalendarComponent> implements UserAgent<T> {

    private final Property role;

    private final Map<Method, Transformer<Calendar>> transformers;

    public AbstractUserAgent(Property role) {
        this.role = role;
        transformers = new HashMap<>();
        transformers.put(Method.PUBLISH, new MethodTransformer(Method.PUBLISH, true, false));
        transformers.put(Method.REQUEST, new MethodTransformer(Method.REQUEST, true, true));
        transformers.put(Method.ADD, new MethodTransformer(Method.ADD, true, false));
        transformers.put(Method.CANCEL, new MethodTransformer(Method.CANCEL, true, false));
        transformers.put(Method.REPLY, new MethodTransformer(Method.REPLY, false, false));
        transformers.put(Method.REFRESH, new MethodTransformer(Method.REFRESH, false, false));
        transformers.put(Method.COUNTER, new MethodTransformer(Method.COUNTER, false, false));
        transformers.put(Method.DECLINE_COUNTER, new MethodTransformer(Method.DECLINE_COUNTER, false, false));
    }

    @Override
    public final Property getRole() {
        return role;
    }

    protected Calendar wrap(Method method, T... component) {
        Calendar calendar = Calendars.wrap(component);
        Transformer<Calendar> transformer = transformers.get(method);
        transformer.transform(calendar);
        return calendar;
    }
}
