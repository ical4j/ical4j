package net.fortuna.ical4j.agent;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.transform.MethodTransformer;
import net.fortuna.ical4j.transform.Transformer;
import net.fortuna.ical4j.util.Calendars;

/**
 * Created by fortuna on 19/07/2017.
 */
public abstract class AbstractUserAgent<T extends CalendarComponent> implements UserAgent<T> {

    private final Property role;

    private final Transformer<Calendar> publishTransformer;
    private final Transformer<Calendar> requestTransformer;
    private final Transformer<Calendar> addTransformer;
    private final Transformer<Calendar> cancelTransformer;

    public AbstractUserAgent(Property role) {
        this.role = role;

        publishTransformer = new MethodTransformer(Method.PUBLISH, true, false);
        requestTransformer = new MethodTransformer(Method.REQUEST, true, true);
        addTransformer = new MethodTransformer(Method.ADD, true, false);
        cancelTransformer = new MethodTransformer(Method.CANCEL, true, false);
    }

    @Override
    public final Property getRole() {
        return role;
    }

    protected Calendar wrap(Method method, T... component) {
        Calendar calendar = Calendars.wrap(component);
        if (Method.PUBLISH.equals(method)) {
            calendar = publishTransformer.transform(calendar);
        } else if (Method.REQUEST.equals(method)) {
            calendar = requestTransformer.transform(calendar);
        } else if (Method.ADD.equals(method)) {
            calendar = addTransformer.transform(calendar);
        } else if (Method.CANCEL.equals(method)) {
            calendar = cancelTransformer.transform(calendar);
        }
        return calendar;
    }
}
