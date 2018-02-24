package net.fortuna.ical4j.agent;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.transform.*;
import net.fortuna.ical4j.util.Calendars;
import net.fortuna.ical4j.util.UidGenerator;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fortuna on 19/07/2017.
 */
public abstract class AbstractUserAgent<T extends CalendarComponent> implements UserAgent<T> {

    private final UidGenerator uidGenerator;

    private final Map<Method, Transformer<Calendar>> transformers;

    public AbstractUserAgent(Organizer organizer, UidGenerator uidGenerator) {
        this.uidGenerator = uidGenerator;
        transformers = new HashMap<>();
        transformers.put(Method.PUBLISH, new PublishTransformer(organizer, uidGenerator,true));
        transformers.put(Method.REQUEST, new RequestTransformer(organizer, uidGenerator,true));
        transformers.put(Method.ADD, new AddTransformer(uidGenerator,false));
        transformers.put(Method.CANCEL, new CancelTransformer(uidGenerator,false));
        transformers.put(Method.REPLY, new ReplyTransformer(uidGenerator,false));
        transformers.put(Method.REFRESH, new RefreshTransformer(uidGenerator,false));
        transformers.put(Method.COUNTER, new CounterTransformer(uidGenerator,false));
        transformers.put(Method.DECLINE_COUNTER, new DeclineCounterTransformer(uidGenerator,false));
    }

    protected Calendar wrap(Method method, T... component) {
        Calendar calendar = Calendars.wrap(component);
        Transformer<Calendar> transformer = transformers.get(method);
        transformer.transform(calendar);
        return calendar;
    }
}
