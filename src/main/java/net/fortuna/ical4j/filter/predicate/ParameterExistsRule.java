/*
 * Copyright (c) 2021. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package net.fortuna.ical4j.filter.predicate;

import net.fortuna.ical4j.model.Property;

import java.util.function.Predicate;

/**
 * Test for a parameter matching the provided name.
 */
public class ParameterExistsRule implements Predicate<Property> {

    private final String parameterName;

    public ParameterExistsRule(String parameterName) {
        this.parameterName = parameterName;
    }

    @Override
    public boolean test(Property t) {
        String[] param = parameterName.split(":");
        if (param.length > 1) {
            return new ParameterEqualToRule<>(param[0], param[1]).test(t);
        }
        return !t.getParameters(parameterName).isEmpty();
    }
}
