package net.fortuna.ical4j.agent;

import net.fortuna.ical4j.model.Calendar;

public interface RefreshAgent {

    /**
     * Apply transformations to the specified calendar.
     * @param object
     * @return the transformed calendar
     */
    Calendar refresh(Calendar object) throws Exception;
}
