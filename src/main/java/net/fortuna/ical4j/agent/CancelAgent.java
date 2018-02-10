package net.fortuna.ical4j.agent;

import net.fortuna.ical4j.model.Calendar;

public interface CancelAgent {

    /**
     * Apply transformations to the specified calendar.
     * @param object
     * @return the transformed calendar
     */
    Calendar cancel(Calendar object) throws Exception;
}
