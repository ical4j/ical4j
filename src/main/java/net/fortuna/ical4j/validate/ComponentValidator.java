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
package net.fortuna.ical4j.validate;

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.util.CompatibilityHints;

import java.util.List;

import static net.fortuna.ical4j.validate.Validator.assertFalse;

/**
 * @author Ben
 *
 */
public class ComponentValidator<T extends Component> implements Validator<T> {

    private static final String ASSERT_NONE_MESSAGE = "Component [{0}] is not applicable";

    private static final String ASSERT_ONE_OR_LESS_MESSAGE = "Component [{0}] must only be specified once";

    private final List<ValidationRule> rules;

    public ComponentValidator(List<ValidationRule> rules) {
        this.rules = rules;
    }

    @Override
    public void validate(T target) throws ValidationException {
        for (ValidationRule rule : rules) {
            boolean warnOnly = CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION)
                    && rule.isRelaxedModeSupported();

            switch (rule.getType()) {
                case None:
                    rule.getInstances().forEach(s -> assertFalse(input -> input.getProperty(s) != null,
                            PropertyValidator.ASSERT_NONE_MESSAGE, warnOnly, target.getProperties(), s));
                    break;
                case One:
                    rule.getInstances().forEach(s -> assertFalse(input -> input.getProperties(s).size() != 1,
                            PropertyValidator.ASSERT_ONE_MESSAGE, warnOnly, target.getProperties(), s));
                    break;
                case OneOrLess:
                    rule.getInstances().forEach(s -> assertFalse(input -> input.getProperties(s).size() > 1,
                            PropertyValidator.ASSERT_ONE_OR_LESS_MESSAGE, warnOnly, target.getProperties(), s));
                    break;
                case OneOrMore:
                    rule.getInstances().forEach(s -> assertFalse(input -> input.getProperties(s).size() < 1,
                            PropertyValidator.ASSERT_ONE_OR_MORE_MESSAGE, warnOnly, target.getProperties(), s));
                    break;
            }
        }
    }

    /**
     * @param componentName a component name used in the assertion
     * @param components a list of components
     * @throws ValidationException where the assertion fails
     */
    public static void assertNone(String componentName, ComponentList<?> components) throws ValidationException {
        assertFalse(input -> input.getComponent(componentName) != null, ASSERT_NONE_MESSAGE, false,
                components, componentName);
    }
    
    /**
     * @param componentName a component name used in the assertion
     * @param components a list of components
     * @throws ValidationException where the assertion fails
     */
    public static void assertOneOrLess(String componentName, ComponentList<?> components) throws ValidationException {
        assertFalse(input -> input.getComponents(componentName).size() > 1, ASSERT_ONE_OR_LESS_MESSAGE, false,
                components, componentName);
    }
}
