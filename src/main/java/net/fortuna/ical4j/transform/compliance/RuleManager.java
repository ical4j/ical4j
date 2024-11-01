/*
 *  Copyright (c) 2014-2024, Ben Fortuna
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *   o Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 *
 *   o Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *
 *   o Neither the name of Ben Fortuna nor the names of any other contributors
 *  may be used to endorse or promote products derived from this software
 *  without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 *  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 *  A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *  EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package net.fortuna.ical4j.transform.compliance;

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;

import java.util.LinkedHashSet;
import java.util.ServiceLoader;
import java.util.Set;

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
        for (Rfc5545PropertyRule<?> rule : ServiceLoader.load(Rfc5545PropertyRule.class)) {
            if (rule.getSupportedType() == null) {
                throw new NullPointerException();
            }
            PROPERTY_RULES.add(rule);
        }
        for (Rfc5545ComponentRule<?> rule : ServiceLoader.load(Rfc5545ComponentRule.class)) {
            if (rule.getSupportedType() == null) {
                throw new NullPointerException();
            }
            COMPONENT_RULES.add(rule);
        }
    }

    public static void applyTo(Property element) {
        for (Rfc5545PropertyRule<Property> rule : getSupportedRulesFor(element)) {
            rule.apply(element);
        }
    }

    public static void applyTo(Component element) {
        for (Rfc5545ComponentRule<Component> rule : getSupportedRulesFor(element)) {
            rule.apply(element);
        }
    }

    @SuppressWarnings("unchecked")
    private static Set<Rfc5545PropertyRule<Property>> getSupportedRulesFor(Property element) {
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
    private static Set<Rfc5545ComponentRule<Component>> getSupportedRulesFor(Component element) {
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