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

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ValidationException;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * $Id$
 *
 * Created on 30/11/2008
 *
 * @author Ben
 *
 */
public class ComponentRuleTest extends TestCase {

    private ComponentRule rule;
    
    private Component component;
    
    /**
     * @param testMethod
     * @param rule
     * @param component
     */
    public ComponentRuleTest(String testMethod, ComponentRule rule, Component component) {
        super(testMethod);
        this.rule = rule;
        this.component = component;
    }
    
    /**
     * Test method for {@link net.fortuna.ical4j.filter.ComponentRule#match(net.fortuna.ical4j.model.Component)}.
     */
    public void testMatchComponent() {
        assertTrue(rule.match(component));
    }
    
    /**
     * Test method for {@link net.fortuna.ical4j.filter.ComponentRule#match(net.fortuna.ical4j.model.Component)}.
     */
    public void testNotMatchComponent() {
        assertFalse(rule.match(component));
    }

    /**
     * @return
     */
    public static TestSuite suite() {
        TestSuite suite = new TestSuite();
        Component component = new Component("test") {
            public void validate(boolean recurse) throws ValidationException {
            }
        };
        ComponentRule matchRule = new ComponentRule() {
            public boolean match(Component component) {
                return true;
            }
        };
        suite.addTest(new ComponentRuleTest("testMatchComponent", matchRule, component));
        ComponentRule notMatchRule = new ComponentRule() {
            public boolean match(Component component) {
                return false;
            }
        };
        suite.addTest(new ComponentRuleTest("testNotMatchComponent", notMatchRule, component));
        
        return suite;
    }
}
