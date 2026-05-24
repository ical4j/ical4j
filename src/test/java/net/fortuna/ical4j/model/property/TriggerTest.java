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
package net.fortuna.ical4j.model.property;

import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.TemporalAdapter;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.ValidationResult;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Created on 7/03/2005
 *
 * $Id$
 *
 * @author Ben Fortuna
 *
 */
public class TriggerTest {

    private static final Logger LOG = LoggerFactory.getLogger(TriggerTest.class);

    @ParameterizedTest(name = "setValue")
    @MethodSource("setValueData")
    public void testSetValue(Trigger trigger) throws ParseException {
        trigger.setValue(TemporalAdapter.from(new DateTime(new Date(0).getTime())).toString(ZoneOffset.UTC));

        LOG.info(TemporalAdapter.from(new DateTime(new Date(0).getTime())).toString());
        LOG.info(trigger.toString());

        trigger.setValue(java.time.Duration.ofSeconds(5).toString());

        LOG.info(java.time.Duration.ofSeconds(5).toString());
        LOG.info(trigger.toString());
    }

    /**
     * Unit test on a duration trigger.
     */
    @ParameterizedTest(name = "triggerDuration")
    @MethodSource("triggerDurationData")
    public void testTriggerDuration(Trigger trigger) {
        assertNotNull(trigger.getDuration());
        assertNull(trigger.getDate());
    }

    /**
     * Unit test on a date-time trigger.
     */
    @ParameterizedTest(name = "triggerDateTime")
    @MethodSource("triggerDateTimeData")
    public void testTriggerDateTime(Trigger trigger) {
        assertNull(trigger.getDuration());
        assertNotNull(trigger.getDate());
        ValidationResult result = trigger.validate();
        assertFalse(result.hasErrors());

        trigger.add(Value.DURATION);
        assertValidationError(trigger);
    }

    private static void assertValidationError(final Property property) {
        try {
            ValidationResult result = property.validate();
            assertTrue(result.hasErrors());
        } catch (ValidationException ve) {
            LOG.debug("Exception caught", ve);
        }
    }

    static Stream<Arguments> setValueData() {
        return Stream.of(
                Arguments.of(new Trigger())
        );
    }

    static Stream<Arguments> triggerDurationData() {
        return Stream.of(
                Arguments.of(new Trigger(java.time.Duration.ofDays(1)))
        );
    }

    static Stream<Arguments> triggerDateTimeData() {
        return Stream.of(
                Arguments.of(new Trigger(Instant.now()))
        );
    }
}
