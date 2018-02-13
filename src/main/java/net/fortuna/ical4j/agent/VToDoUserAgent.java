package net.fortuna.ical4j.agent;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VToDo;
import net.fortuna.ical4j.model.property.Method;

public class VToDoUserAgent extends AbstractUserAgent<VToDo> {

    public VToDoUserAgent(Property role) {
        super(role);
    }

    @Override
    public Calendar publish(VToDo... component) {
        return wrap(Method.PUBLISH, component);
    }

    @Override
    public Calendar request(VToDo... component) {
        return null;
    }

    @Override
    public Calendar reply(VToDo... component) {
        return null;
    }

    @Override
    public Calendar add(VToDo component) {
        return null;
    }

    @Override
    public Calendar cancel(VToDo... component) {
        return null;
    }

    @Override
    public Calendar refresh(VToDo component) {
        return null;
    }

    @Override
    public Calendar counter(VToDo component) {
        return null;
    }

    @Override
    public Calendar declineCounter(VToDo... component) {
        return null;
    }
}
