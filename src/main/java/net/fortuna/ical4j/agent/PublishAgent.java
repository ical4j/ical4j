package net.fortuna.ical4j.agent;

/**
 * Created by fortuna on 19/07/2017.
 */
public interface PublishAgent<T> {

    /**
     * Apply transformations to a copy of the specified calendar object.
     * @param object
     * @return
     */
    T publish(T object) throws Exception;
}
