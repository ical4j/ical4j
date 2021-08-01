/*
 * Copyright (c) 2021. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package net.fortuna.ical4j.filter.predicate;

import net.fortuna.ical4j.model.PropertyContainer;
import net.fortuna.ical4j.model.property.Sequence;

import java.util.function.Predicate;

/**
 * Test for a property that is by comparison greater than the provided value.
 *
 * @param <T>
 */
public class PropertyGreaterThanRule<T extends PropertyContainer> implements Predicate<T> {

    private final String propertyName;

    private final Object value;

    private final boolean inclusive;

    public PropertyGreaterThanRule(String propertyName, Object value) {
        this(propertyName, value, false);
    }

    public PropertyGreaterThanRule(String propertyName, Object value, boolean inclusive) {
        this.propertyName = propertyName;
        this.value = value;
        this.inclusive = inclusive;
    }

    @Override
    public boolean test(T t) {
        if ("sequence".equalsIgnoreCase(propertyName)) {
            Sequence sequence = t.getProperty(propertyName);
            if (sequence != null) {
                return inclusive ? sequence.getSequenceNo() >= Integer.parseInt(value.toString())
                        : sequence.getSequenceNo() > Integer.parseInt(value.toString());
            }
        }
        return false;
    }
}
