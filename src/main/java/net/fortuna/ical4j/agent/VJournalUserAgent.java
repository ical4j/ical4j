package net.fortuna.ical4j.agent;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VJournal;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.util.UidGenerator;

public class VJournalUserAgent extends AbstractUserAgent<VJournal> {

    public VJournalUserAgent(Organizer organizer, UidGenerator uidGenerator) {
        super(organizer, uidGenerator);
    }

    @Override
    public Calendar publish(VJournal... component) {
        return wrap(Method.PUBLISH, component);
    }

    @Override
    public Calendar request(VJournal... component) {
        throw new UnsupportedOperationException("Method [REQUEST] not supported by VJOURNAL");
    }

    @Override
    public Calendar reply(Calendar request) {
        throw new UnsupportedOperationException("Method [REPLY] not supported by VJOURNAL");
    }

    @Override
    public Calendar add(VJournal component) {
        return wrap(Method.ADD, component);
    }

    @Override
    public Calendar cancel(VJournal... component) {
        return wrap(Method.CANCEL, component);
    }

    @Override
    public Calendar refresh(VJournal component) {
        throw new UnsupportedOperationException("Method [REFRESH] not supported by VJOURNAL");
    }

    @Override
    public Calendar counter(Calendar request) {
        throw new UnsupportedOperationException("Method [COUNTER] not supported by VJOURNAL");
    }

    @Override
    public Calendar declineCounter(Calendar counter) {
        throw new UnsupportedOperationException("Method [DECLINECOUNTER] not supported by VJOURNAL");
    }
}
