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

    /**
     * <pre></pre>
     *
     * @param component one or more component objects
     * @return
     */
    @Override
    public Calendar publish(VToDo... component) {
        Calendar published = wrap(Method.PUBLISH, component);
        published.validate();
        return published;
    }

    /**
     * <pre></pre>
     *
     * @param component
     * @return
     */
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

    /**
     * <pre></pre>
     *
     * @param request
     * @return
     */
    @Override
    public Calendar reply(Calendar request) {
        Calendar reply = transform(Method.REPLY, request);
        reply.validate();
        return reply;
    }

    /**
     * <pre></pre>
     *
     * @param component a calendar component to add
     * @return
     */
    @Override
    public Calendar add(VToDo component) {
        Calendar add = wrap(Method.ADD, component);
        add.validate();
        return add;
    }

    /**
     * <pre></pre>
     *
     * @param component one or more component objects
     * @return
     */
    @Override
    public Calendar cancel(VToDo... component) {
        Calendar cancel = wrap(Method.CANCEL, component);
        cancel.validate();
        return cancel;
    }

    /**
     * <pre></pre>
     *
     * @param component a calendar component to refresh
     * @return
     */
    @Override
    public Calendar refresh(VToDo component) {
        Calendar refresh = wrap(Method.REFRESH, component);
        refresh.validate();
        return refresh;
    }

    /**
     * <pre></pre>
     *
     * @param request a calendar request to counter
     * @return
     */
    @Override
    public Calendar counter(Calendar request) {
        Calendar counter = transform(Method.COUNTER, request);
        counter.validate();
        return counter;
    }

    /**
     * <pre></pre>
     *
     * @param counter a counter to a request
     * @return
     */
    @Override
    public Calendar declineCounter(Calendar counter) {
        Calendar declineCounter = transform(Method.DECLINE_COUNTER, counter);
        declineCounter.validate();
        return declineCounter;
    }
}
