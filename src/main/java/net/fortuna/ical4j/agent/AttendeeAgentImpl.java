package net.fortuna.ical4j.agent;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Property;

/**
 * Created by fortuna on 19/07/2017.
 */
public class AttendeeAgentImpl extends AbstractUserAgent implements ReplyAgent, RefreshAgent, CounterAgent {

    public AttendeeAgentImpl(Property role) {
        super(role);
    }

    @Override
    public Calendar counter(Calendar object) throws Exception {
        return null;
    }

    @Override
    public Calendar refresh(Calendar object) throws Exception {
        return null;
    }

    @Override
    public Calendar reply(Calendar object) throws Exception {
        return null;
    }
}
