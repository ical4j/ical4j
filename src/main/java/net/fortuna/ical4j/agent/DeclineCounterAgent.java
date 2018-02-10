package net.fortuna.ical4j.agent;

import net.fortuna.ical4j.model.Calendar;

public interface DeclineCounterAgent {

    /**
     * Apply transformations to the specified calendar.
     * @param object
     * @return the transformed calendar
     */
    Calendar declineCounter(Calendar object) throws Exception;
}
