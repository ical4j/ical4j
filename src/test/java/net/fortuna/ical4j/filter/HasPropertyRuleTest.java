/**
 * Copyright (c) 2012, Ben Fortuna
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  o Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 *  o Neither the name of Ben Fortuna nor the names of any other contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.fortuna.ical4j.filter;

import java.net.URI;

import junit.framework.TestSuite;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.Organizer;

/**
 * $Id$
 *
 * Created on 2/02/2006
 *
 * Unit tests for the has property filter rule.
 * @author Ben Fortuna
 */
public class HasPropertyRuleTest extends ComponentRuleTest {
    
    /**
     * @param testMethod
     * @param rule
     * @param component
     */
    public HasPropertyRuleTest(String testMethod, ComponentRule rule, Component component) {
        super(testMethod, rule, component);
    }
    
    /**
     * @return
     */
    public static TestSuite suite() {
        TestSuite suite = new TestSuite();
        Organizer organiser = new Organizer(URI.create("Mailto:B@example.com"));
        Attendee attendee = new Attendee(URI.create("Mailto:A@example.com"));
        Component component = new Component("test") {
            public void validate(boolean recurse) throws ValidationException {
            }
        };
        component.getProperties().add(organiser);
        component.getProperties().add(attendee);
        HasPropertyRule organiserRule = new HasPropertyRule(organiser);
        suite.addTest(new HasPropertyRuleTest("testMatchComponent", organiserRule, component));
        HasPropertyRule attendeeRule = new HasPropertyRule(attendee);
        suite.addTest(new HasPropertyRuleTest("testMatchComponent", attendeeRule, component));
        return suite;
    }
}
