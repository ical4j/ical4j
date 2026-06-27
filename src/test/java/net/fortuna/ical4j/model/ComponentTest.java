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
package net.fortuna.ical4j.model;

import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Due;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.transform.recurrence.Frequency;
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.ValidationResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.Temporal;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * $Id$
 *
 * Created on 12/11/2005
 *
 * Unit tests for <code>Component</code> base class. Also provides static helper
 * assertions used by subclass tests (which no longer extend this class after the
 * JUnit 5 migration).
 * @author Ben Fortuna
 */
public class ComponentTest {

    private static final Logger LOG = LoggerFactory.getLogger(ComponentTest.class);

    @AfterEach
    void tearDown() {
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION);
    }

    /**
     * Asserts the given component is a {@link CalendarComponent}.
     */
    public static void assertIsCalendarComponent(Component component) {
        assertTrue(component instanceof CalendarComponent, "Component is not a calendar component");
    }

    /**
     * Asserts the given component is NOT a {@link CalendarComponent}.
     */
    public static void assertIsNotCalendarComponent(Component component) {
        assertFalse(component instanceof CalendarComponent, "Component is a calendar component");
    }

    /**
     * Asserts the given component validates successfully.
     */
    public static void assertValidation(Component component) throws ValidationException {
        component.validate();
    }

    /**
     * Asserts the given component validates successfully when relaxed validation is enabled.
     */
    public static void assertRelaxedValidation(Component component) throws ValidationException {
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION, true);
        try {
            component.validate();
        } finally {
            CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION);
        }
    }

    /**
     * Asserts that validating the given component either yields a result containing errors,
     * or throws a {@link ValidationException}.
     */
    public static void assertValidationException(Component component) {
        try {
            ValidationResult result = component.validate();
            assertTrue(result.hasErrors());
        } catch (ValidationException ve) {
            LOG.debug("Exception caught", ve);
        }
    }

    /**
     * Asserts that {@link Component#calculateRecurrenceSet(Period)} returns the expected periods.
     */
    public static <T extends Temporal> void assertCalculateRecurrenceSet(
            Component component, Period<T> period, Set<Period<T>> expectedPeriods) {
        Set<Period<T>> periods = component.calculateRecurrenceSet(period);
        assertEquals(expectedPeriods.size(), periods.size(), "Wrong number of periods");
        assertEquals(expectedPeriods, periods);
    }

    @ParameterizedTest(name = "calculateRecurrenceSet")
    @MethodSource("calculateRecurrenceSetData")
    public <T extends Temporal> void testCalculateRecurrenceSet(Component component, Period<T> period,
                                                                Set<Period<T>> expectedPeriods) {
        assertCalculateRecurrenceSet(component, period, expectedPeriods);
    }

    static Stream<Arguments> calculateRecurrenceSetData() {
        Stream.Builder<Arguments> rows = Stream.builder();

        Component component = new Component("test") {
            @Override
            public ValidationResult validate(boolean recurse) throws ValidationException {
                return null;
            }

            @Override
            protected ComponentFactory<?> newFactory() {
                return null;
            }
        };
        rows.add(Arguments.of(component, new Period<>(LocalDate.now(),
                java.time.Period.ofDays(1)), new TreeSet<>()));

        component = new Component("test") {
            @Override
            public ValidationResult validate(boolean recurse) throws ValidationException {
                return null;
            }

            @Override
            protected ComponentFactory<?> newFactory() {
                return null;
            }
        };
        // 10am-12pm for 7 days..
        component.add(new DtStart("20080601T100000Z"));
        component.add(new DtEnd("20080601T120000Z"));
        Recur recur = new Recur.Builder().frequency(Frequency.DAILY).count(7).build();
        component.add(new RRule(recur));
        Set<Period<Instant>> expectedPeriods = new TreeSet<>();
        expectedPeriods.add(Period.parse("20080601T100000Z/PT2H"));
        expectedPeriods.add(Period.parse("20080602T100000Z/PT2H"));
        expectedPeriods.add(Period.parse("20080603T100000Z/PT2H"));
        expectedPeriods.add(Period.parse("20080604T100000Z/PT2H"));
        expectedPeriods.add(Period.parse("20080605T100000Z/PT2H"));
        expectedPeriods.add(Period.parse("20080606T100000Z/PT2H"));
        expectedPeriods.add(Period.parse("20080607T100000Z/PT2H"));
        rows.add(Arguments.of(component, new Period(TemporalAdapter.parse("20080601T000000Z").getTemporal(),
                java.time.Period.ofDays(7)), expectedPeriods));

        component = new Component("test") {
            @Override
            public ValidationResult validate(boolean recurse) throws ValidationException {
                return null;
            }

            @Override
            protected ComponentFactory<?> newFactory() {
                return null;
            }
        };
        // weekly for 5 instances using DATE format and due date.
        component.add(new DtStart<>((LocalDate) TemporalAdapter.parse("20080601").getTemporal()));
        component.add(new Due<>((LocalDate) TemporalAdapter.parse("20080602").getTemporal()));
        recur = new Recur.Builder().frequency(Frequency.WEEKLY).count(5).build();
        component.add(new RRule(recur));
        Set<Period<LocalDate>> expectedPeriods2 = new TreeSet<>();
        expectedPeriods2.add(Period.parse("20080601/P1D"));
        expectedPeriods2.add(Period.parse("20080608/P1D"));
        expectedPeriods2.add(Period.parse("20080615/P1D"));
        expectedPeriods2.add(Period.parse("20080622/P1D"));
        expectedPeriods2.add(Period.parse("20080629/P1D"));
        rows.add(Arguments.of(component, new Period<>((LocalDate) TemporalAdapter.parse("20080601").getTemporal(),
                java.time.Period.ofWeeks(6)), expectedPeriods2));

        return rows.build();
    }
}
