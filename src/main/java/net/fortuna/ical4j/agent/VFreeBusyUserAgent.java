package net.fortuna.ical4j.agent;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VFreeBusy;
import net.fortuna.ical4j.model.property.Method;

public class VFreeBusyUserAgent extends AbstractUserAgent<VFreeBusy> {

    public VFreeBusyUserAgent(Property role) {
        super(role);
    }

    @Override
    public Calendar publish(VFreeBusy... component) {
        return wrap(Method.PUBLISH, component);
    }

    @Override
    public Calendar request(VFreeBusy... component) {
        return null;
    }

    @Override
    public Calendar reply(VFreeBusy component) {
        return null;
    }

    @Override
    public Calendar add(VFreeBusy component) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Calendar cancel(VFreeBusy component) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Calendar refresh(VFreeBusy component) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Calendar counter(VFreeBusy component) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Calendar declineCounter(VFreeBusy component) {
        throw new UnsupportedOperationException();
    }
}
