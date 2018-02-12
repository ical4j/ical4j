package net.fortuna.ical4j.agent;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VJournal;
import net.fortuna.ical4j.model.property.Method;

public class VJournalUserAgent extends AbstractUserAgent<VJournal> {

    public VJournalUserAgent(Property role) {
        super(role);
    }

    @Override
    public Calendar publish(VJournal... component) {
        return wrap(Method.PUBLISH, component);
    }

    @Override
    public Calendar request(VJournal component) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Calendar reply(VJournal component) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Calendar add(VJournal component) {
        return null;
    }

    @Override
    public Calendar cancel(VJournal component) {
        return null;
    }

    @Override
    public Calendar refresh(VJournal component) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Calendar counter(VJournal component) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Calendar declineCounter(VJournal component) {
        throw new UnsupportedOperationException();
    }
}
