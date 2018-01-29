package net.fortuna.ical4j.agent;

import net.fortuna.ical4j.model.component.CalendarComponent;

/**
 * Created by fortuna on 19/07/2017.
 */
public interface PublishAgent<T extends CalendarComponent> {

    /**
     * Apply transformations to a copy of the specified calendar object.
     * @param object
     * @return
     */
    T publish(T object) throws Exception;
}
