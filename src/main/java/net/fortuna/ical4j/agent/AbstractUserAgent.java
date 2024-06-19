package net.fortuna.ical4j.agent;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.transform.calendar.*;
import net.fortuna.ical4j.util.Calendars;
import net.fortuna.ical4j.util.UidGenerator;
import net.fortuna.ical4j.validate.ValidationResult;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.UnaryOperator;

import static net.fortuna.ical4j.model.property.immutable.ImmutableMethod.*;
import static net.fortuna.ical4j.model.property.immutable.ImmutableVersion.VERSION_2_0;

/**
 * Created by fortuna on 19/07/2017.
 */
public abstract class AbstractUserAgent<T extends CalendarComponent> implements UserAgent<T> {

    private final ProdId prodId;

    private final Map<Method, UnaryOperator<Calendar>> methodTransformers;

    public AbstractUserAgent(ProdId prodId, Organizer organizer, UidGenerator uidGenerator) {
        this.prodId = prodId;

        methodTransformers = new HashMap<>();
        methodTransformers.put(PUBLISH, new PublishTransformer(organizer, uidGenerator,true));
        methodTransformers.put(REQUEST, new RequestTransformer(organizer, uidGenerator));
        methodTransformers.put(ADD, new AddTransformer(organizer, uidGenerator));
        methodTransformers.put(CANCEL, new CancelTransformer(organizer, uidGenerator));
        methodTransformers.put(REPLY, new ReplyTransformer(uidGenerator));
        methodTransformers.put(REFRESH, new RefreshTransformer(uidGenerator));
        methodTransformers.put(COUNTER, new CounterTransformer(uidGenerator));
        methodTransformers.put(DECLINE_COUNTER, new DeclineCounterTransformer(organizer, uidGenerator));
    }

    @SafeVarargs
    protected final Calendar wrap(Method method, T... component) {
        PropertyList props = new PropertyList(Arrays.asList(prodId, VERSION_2_0));
        Calendar calendar = Calendars.wrap(props, component);
        return transform(method, calendar);
    }

    protected Calendar transform(Method method, Calendar calendar) {
        UnaryOperator<Calendar> transformer = methodTransformers.get(method);
        return transformer.apply(calendar);
    }

    protected Calendar validate(Calendar calendar) {
        ValidationResult result = calendar.validate();
        if (result.hasErrors()) {
            throw new RuntimeException(String.format("One or more components has errors: %s", result));
        }
        return calendar;
    }

    public ProdId getProdId() {
        return prodId;
    }
}
