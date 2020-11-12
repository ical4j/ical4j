package net.fortuna.ical4j.agent;

import io.opentracing.Tracer;
import io.opentracing.noop.NoopTracerFactory;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.transform.Transformer;
import net.fortuna.ical4j.transform.calendar.*;
import net.fortuna.ical4j.util.Calendars;
import net.fortuna.ical4j.util.UidGenerator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by fortuna on 19/07/2017.
 */
public abstract class AbstractUserAgent<T extends CalendarComponent> implements UserAgent<T> {

    protected final Tracer tracer;

    private final ProdId prodId;

    private final Map<Method, Transformer<Calendar>> methodTransformers;

    public AbstractUserAgent(ProdId prodId, Organizer organizer, UidGenerator uidGenerator) {
        this(prodId, organizer, uidGenerator, null);
    }

    public AbstractUserAgent(ProdId prodId, Organizer organizer, UidGenerator uidGenerator, Tracer tracer) {
        this.prodId = prodId;
        this.tracer = Optional.ofNullable(tracer).orElse(NoopTracerFactory.create());

        methodTransformers = new HashMap<>();
        methodTransformers.put(Method.PUBLISH, new PublishTransformer(organizer, uidGenerator,true));
        methodTransformers.put(Method.REQUEST, new RequestTransformer(organizer, uidGenerator));
        methodTransformers.put(Method.ADD, new AddTransformer(organizer, uidGenerator));
        methodTransformers.put(Method.CANCEL, new CancelTransformer(organizer, uidGenerator));
        methodTransformers.put(Method.REPLY, new ReplyTransformer(uidGenerator));
        methodTransformers.put(Method.REFRESH, new RefreshTransformer(uidGenerator));
        methodTransformers.put(Method.COUNTER, new CounterTransformer(uidGenerator));
        methodTransformers.put(Method.DECLINE_COUNTER, new DeclineCounterTransformer(organizer, uidGenerator));
    }

    @SafeVarargs
    protected final Calendar wrap(Method method, T... component) {
        PropertyList props = new PropertyList(Arrays.asList(prodId, Version.VERSION_2_0));
        Calendar calendar = Calendars.wrap(props, component);
        return transform(method, calendar);
    }

    protected Calendar transform(Method method, Calendar calendar) {
        Transformer<Calendar> transformer = methodTransformers.get(method);
        transformer.transform(calendar);
        return calendar;
    }

    public ProdId getProdId() {
        return prodId;
    }
}
