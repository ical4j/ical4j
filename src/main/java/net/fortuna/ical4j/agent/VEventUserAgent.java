package net.fortuna.ical4j.agent;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Method;

public class VEventUserAgent extends AbstractUserAgent<VEvent> {

    public VEventUserAgent(Property role) {
        super(role);
    }

    @Override
    public Calendar publish(VEvent... component) {
        return wrap(Method.PUBLISH, component);
    }

    @Override
    public Calendar request(VEvent... component) {
        return wrap(Method.REQUEST, component);
    }

    @Override
    public Calendar reply(VEvent component) {
        return null;
    }

    @Override
    public Calendar add(VEvent component) {
        return null;
    }

    @Override
    public Calendar cancel(VEvent component) {
        return null;
    }

    @Override
    public Calendar refresh(VEvent component) {
        return null;
    }

    @Override
    public Calendar counter(VEvent component) {
        return null;
    }

    @Override
    public Calendar declineCounter(VEvent component) {
        return null;
    }
}
