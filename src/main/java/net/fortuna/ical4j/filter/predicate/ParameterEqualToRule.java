/*
 * Copyright (c) 2012-2021. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */
package net.fortuna.ical4j.filter.predicate;

import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;

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
public class ParameterEqualToRule<T extends Property> implements Predicate<T> {

    private final String parameterName;

    private final Object value;

    public ParameterEqualToRule(String parameterName, Object value) {
        this.parameterName = parameterName;
        this.value = value;
    }

    @Override
    public final boolean test(final Property property) {
        final ParameterList parameters = property.getParameters(parameterName);
        for (final Parameter p : parameters) {
            if (value.equals(p.getValue())) {
                return true;
            }
        }
        return false;
    }
}
