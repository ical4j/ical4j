package net.fortuna.ical4j.agent;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VToDo;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.transform.RequestTransformer;
import net.fortuna.ical4j.util.UidGenerator;

public class VToDoUserAgent extends AbstractUserAgent<VToDo> {

    private final RequestTransformer delegateTransformer;

    public VToDoUserAgent(ProdId prodId, Organizer organizer, UidGenerator uidGenerator) {
        super(prodId, organizer, uidGenerator);
        delegateTransformer = new RequestTransformer(uidGenerator);
    }

    @Override
    public Calendar publish(VToDo... component) {
        Calendar published = wrap(Method.PUBLISH, component);
        published.validate();
        return published;
    }

    @Override
    public Calendar request(VToDo... component) {
        Calendar request = wrap(Method.REQUEST, component);
        request.validate();
        return request;
    }

    @Override
    public Calendar delegate(Calendar request) {
        Calendar delegated = delegateTransformer.transform(request);
        delegated.validate();
        return delegated;
    }

    @Override
    public Calendar reply(Calendar request) {
        Calendar reply = transform(Method.REPLY, request);
        reply.validate();
        return reply;
    }

    @Override
    public Calendar add(VToDo component) {
        Calendar add = wrap(Method.ADD, component);
        add.validate();
        return add;
    }

    @Override
    public Calendar cancel(VToDo... component) {
        Calendar cancel = wrap(Method.CANCEL, component);
        cancel.validate();
        return cancel;
    }

    @Override
    public Calendar refresh(VToDo component) {
        Calendar refresh = wrap(Method.REFRESH, component);
        refresh.validate();
        return refresh;
    }

    @Override
    public Calendar counter(Calendar request) {
        Calendar counter = transform(Method.COUNTER, request);
        counter.validate();
        return counter;
    }

    @Override
    public Calendar declineCounter(Calendar counter) {
        Calendar declineCounter = transform(Method.DECLINE_COUNTER, counter);
        declineCounter.validate();
        return declineCounter;
    }
}
