package net.fortuna.ical4j.model;

/**
 * Created by fortuna on 8/09/14.
 */
public interface ParameterBuilder<T extends Parameter> {

    /**
     * @return a new parameter instance
     */
    T build();
}
