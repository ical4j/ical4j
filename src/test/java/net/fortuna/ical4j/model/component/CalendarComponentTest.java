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
package net.fortuna.ical4j.model.component;

import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.ValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.fortuna.ical4j.model.property.immutable.ImmutableMethod.PUBLISH;
import static net.fortuna.ical4j.model.property.immutable.ImmutableMethod.REPLY;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Helper class providing static assertion methods for {@link CalendarComponent} iTIP method
 * validation tests. After the JUnit 5 migration, calendar component test subclasses no longer
 * extend this class — they delegate to these static helpers instead.
 *
 * @author Ben
 */
public class CalendarComponentTest {

    private static final Logger LOG = LoggerFactory.getLogger(CalendarComponentTest.class);

    private CalendarComponentTest() {
        // helper class — no instances
    }

    public static void assertPublishValidation(CalendarComponent component) throws ValidationException {
        component.validate(PUBLISH);
    }

    public static void assertPublishRelaxedValidation(CalendarComponent component) throws ValidationException {
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION, true);
        try {
            component.validate(PUBLISH);
        } finally {
            CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION);
        }
    }

    public static void assertPublishValidationException(CalendarComponent component) {
        try {
            ValidationResult result = component.validate(PUBLISH);
            assertTrue(result.hasErrors());
        } catch (ValidationException ve) {
            LOG.debug("Exception caught", ve);
        }
    }

    public static void assertRequestValidation(CalendarComponent component) throws ValidationException {
        component.validate(PUBLISH);
    }

    public static void assertRequestRelaxedValidation(CalendarComponent component) throws ValidationException {
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION, true);
        try {
            component.validate(PUBLISH);
        } finally {
            CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION);
        }
    }

    public static void assertRequestValidationException(CalendarComponent component) {
        try {
            component.validate(PUBLISH);
            fail("ValidationException should be thrown!");
        } catch (ValidationException ve) {
            LOG.debug("Exception caught", ve);
        }
    }

    public static void assertReplyValidation(CalendarComponent component) throws ValidationException {
        component.validate(REPLY);
    }

    public static void assertReplyRelaxedValidation(CalendarComponent component) throws ValidationException {
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION, true);
        try {
            component.validate(REPLY);
        } finally {
            CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION);
        }
    }

    public static void assertReplyValidationException(CalendarComponent component) {
        try {
            ValidationResult result = component.validate(REPLY);
            assertTrue(result.hasErrors());
        } catch (ValidationException ve) {
            LOG.debug("Exception caught", ve);
        }
    }
}
