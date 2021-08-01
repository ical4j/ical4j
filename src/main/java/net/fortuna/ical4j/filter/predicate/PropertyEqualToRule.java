/*
 * Copyright (c) 2012-2021. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
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
 * $Id$
 *
 * Created on 5/02/2006
 *
 * A rule that matches any component containing the specified property. Note that this rule ignores any parameters
 * matching only on the value of the property.
 * @author Ben Fortuna
 */
public class PropertyEqualToRule<T extends PropertyContainer, V> implements Predicate<T> {

    private final String propertyName;

    private final V value;

    /**
     * Constructs a new instance with the specified property. Ignores any parameters matching only on the value of the
     * property.
     * @param property a property instance to check for
     */
    public PropertyEqualToRule(final Property property) {
        this(property.getName(), (V) property.getValue());
    }

    public PropertyEqualToRule(String propertyName, V value) {
        this.propertyName = propertyName;
        this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean test(final T component) {
        final PropertyList<Property> properties = component.getProperties(propertyName);
        for (final Property p : properties) {
            if (value.equals(p.getValue())) {
                return true;
            }
        }
        return false;
    }
}
