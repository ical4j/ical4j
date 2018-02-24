package net.fortuna.ical4j.agent;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VToDo;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.util.UidGenerator;

public class VToDoUserAgent extends AbstractUserAgent<VToDo> {

    public VToDoUserAgent(Organizer organizer, UidGenerator uidGenerator) {
        super(organizer, uidGenerator);
    }

    @Override
    public Calendar publish(VToDo... component) {
        return wrap(Method.PUBLISH, component);
    }

    @Override
    public Calendar request(VToDo... component) {
        return wrap(Method.REQUEST, component);
    }

    @Override
    public Calendar reply(VToDo... component) {
        return wrap(Method.REPLY, component);
    }

    @Override
    public Calendar add(VToDo component) {
        return wrap(Method.ADD, component);
    }

    @Override
    public Calendar cancel(VToDo... component) {
        return wrap(Method.CANCEL, component);
    }

    @Override
    public Calendar refresh(VToDo component) {
        return wrap(Method.REFRESH, component);
    }

    @Override
    public Calendar counter(VToDo component) {
        return wrap(Method.COUNTER, component);
    }

    @Override
    public Calendar declineCounter(VToDo... component) {
        return wrap(Method.DECLINE_COUNTER, component);
    }
}
