/*
 * Rfc5545RuleManager.java Feb 21, 2014
 * 
 * Copyright (c) 2014 1&1 Internet AG. All rights reserved.
 * 
 * $Id$
 */
package net.fortuna.ical4j.model.rfc5545;

import java.util.LinkedHashSet;
import java.util.Set;

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.Rfc5545ComponentRule;
import net.fortuna.ical4j.model.Rfc5545PropertyRule;
import net.fortuna.ical4j.model.Rfc5545Rule;

/**
 * Manages the rules that can be applied to ICS elements. New rules can be simply added by creating new implementations
 * of {@link Rfc5545PropertyRule} or {@link Rfc5545ComponentRule}.
 * 
 * @author corneliu dobrota
 * @author daniel grigore
 * @see Property
 * @see Component
 *
 */
public class RuleManager {

    private static final Set<Rfc5545PropertyRule<? extends Property>> PROPERTY_RULES = new LinkedHashSet<Rfc5545PropertyRule<? extends Property>>();
    private static final Set<Rfc5545ComponentRule<? extends Component>> COMPONENT_RULES = new LinkedHashSet<Rfc5545ComponentRule<? extends Component>>();

    static {
        register(new VAlarmRule());

        register(new DatePropertyRule());

        register(new DateListPropertyRule());

        register(new VEventRule());

        register(new TzIdRule());

        register(new DTStampRule());
        
        register(new AttendeePropertyRule());
    }

    private static void register(Rfc5545PropertyRule<? extends Property> rule) {
        if (rule.getSupportedType() == null) {
            throw new NullPointerException();
        }
        PROPERTY_RULES.add(rule);
    }

    private static void register(Rfc5545ComponentRule<? extends Component> rule) {
        if (rule.getSupportedType() == null) {
            throw new NullPointerException();
        }
        COMPONENT_RULES.add(rule);
    }

    @SuppressWarnings("unchecked")
    public static Set<Rfc5545PropertyRule<Property>> getSupportedRulesFor(Property element) {
        if (element == null) {
            throw new NullPointerException();
        }
        Set<Rfc5545PropertyRule<Property>> rules = new LinkedHashSet<Rfc5545PropertyRule<Property>>(1);
        for (Rfc5545Rule<? extends Property> rule : PROPERTY_RULES) {
            if (rule.getSupportedType().isInstance(element)) {
                rules.add((Rfc5545PropertyRule<Property>) rule);
            }
        }
        return rules;
    }

    @SuppressWarnings("unchecked")
    public static Set<Rfc5545ComponentRule<Component>> getSupportedRulesFor(Component element) {
        if (element == null) {
            throw new NullPointerException();
        }
        Set<Rfc5545ComponentRule<Component>> rules = new LinkedHashSet<Rfc5545ComponentRule<Component>>(1);
        for (Rfc5545Rule<?> rule : COMPONENT_RULES) {
            if (rule.getSupportedType().isInstance(element)) {
                rules.add((Rfc5545ComponentRule<Component>) rule);
            }
        }
        return rules;
    }
}