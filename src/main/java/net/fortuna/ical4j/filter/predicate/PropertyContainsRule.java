/*
 * Copyright (c) 2021. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package net.fortuna.ical4j.filter.predicate;

import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyContainer;
import net.fortuna.ical4j.model.PropertyList;

import java.util.function.Predicate;

/**
 * Test for a property that "contains" the provided value.
 *
 * @param <T>
 */
public class PropertyContainsRule<T extends PropertyContainer> implements Predicate<T> {

    private final String propertyName;

    private final Object value;

    public PropertyContainsRule(String propertyName, Object value) {
        this.propertyName = propertyName;
        this.value = value;
    }

    @Override
    public boolean test(T t) {
        final PropertyList<Property> properties = t.getProperties(propertyName);
        for (final Property p : properties) {
            if (p.getValue().contains(value.toString())) {
                return true;
            }
        }
        return false;
    }
}
