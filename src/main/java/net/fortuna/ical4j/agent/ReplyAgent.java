package net.fortuna.ical4j.agent;

import net.fortuna.ical4j.model.Calendar;

public interface ReplyAgent {

    /**
     * Apply transformations to the specified calendar.
     * @param object
     * @return the transformed calendar
     */
    Calendar reply(Calendar object) throws Exception;
}
