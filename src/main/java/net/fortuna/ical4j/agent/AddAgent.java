package net.fortuna.ical4j.agent;

import net.fortuna.ical4j.model.Calendar;

public interface AddAgent {

    /**
     * Apply transformations to the specified calendar.
     * @param object
     * @return the transformed calendar
     */
    Calendar add(Calendar object) throws Exception;
}
