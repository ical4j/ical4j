package net.fortuna.ical4j.agent;

import net.fortuna.ical4j.model.Calendar;

public interface CounterAgent {

    /**
     * Apply transformations to the specified calendar.
     * @param object
     * @return the transformed calendar
     */
    Calendar counter(Calendar object) throws Exception;
}
