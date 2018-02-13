package net.fortuna.ical4j.agent;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.transform.PublishTransformer;
import net.fortuna.ical4j.transform.RequestTransformer;
import net.fortuna.ical4j.util.Calendars;

/**
 * Created by fortuna on 19/07/2017.
 */
public abstract class AbstractUserAgent<T extends CalendarComponent> implements UserAgent<T> {

    private final Property role;

    private final PublishTransformer publishTransformer;
    private final RequestTransformer requestTransformer;

    public AbstractUserAgent(Property role) {
        this.role = role;

        publishTransformer = new PublishTransformer();
        requestTransformer = new RequestTransformer();
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
        }
        return calendar;
    }
}
