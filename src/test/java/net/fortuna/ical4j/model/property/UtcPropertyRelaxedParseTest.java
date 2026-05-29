/*
 *  Copyright (c) 2026, Ben Fortuna
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

package net.fortuna.ical4j.model.property;

import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.util.CompatibilityHints;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UtcPropertyRelaxedParseTest {

    @BeforeEach
    void enableRelaxedParsing() {
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING, true);
    }

    @AfterEach
    void clearRelaxedParsing() {
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING);
    }

    @Test
    void dtStamp_dateOnly_coercesToMidnightUtc() {
        DtStamp dtStamp = new DtStamp("20240601");
        assertEquals("20240601T000000Z", dtStamp.getValue());
        assertInstanceOf(Instant.class, dtStamp.getDate());
    }

    @Test
    void dtStamp_floatingDateTime_coercesToUtcWallClock() {
        DtStamp dtStamp = new DtStamp("20240601T120000");
        assertEquals("20240601T120000Z", dtStamp.getValue());
        assertInstanceOf(Instant.class, dtStamp.getDate());
    }

    @Test
    void dtStamp_offsetDateTime_coercesToEquivalentUtcInstant() {
        DtStamp dtStamp = new DtStamp("20240601T120000+0500");
        assertEquals("20240601T070000Z", dtStamp.getValue());
        assertInstanceOf(Instant.class, dtStamp.getDate());
    }

    @Test
    void dtStamp_alreadyUtc_preserved() {
        DtStamp dtStamp = new DtStamp("20240601T120000Z");
        assertEquals("20240601T120000Z", dtStamp.getValue());
        assertInstanceOf(Instant.class, dtStamp.getDate());
    }

    @Test
    void created_dateOnly_coercesToMidnightUtc() {
        Created created = new Created("20240601");
        assertEquals("20240601T000000Z", created.getValue());
        assertInstanceOf(Instant.class, created.getDate());
    }

    @Test
    void created_floatingDateTime_coercesToUtcWallClock() {
        Created created = new Created("20240601T120000");
        assertEquals("20240601T120000Z", created.getValue());
    }

    @Test
    void created_offsetDateTime_coercesToEquivalentUtcInstant() {
        Created created = new Created("20240601T120000+0500");
        assertEquals("20240601T070000Z", created.getValue());
    }

    @ParameterizedTest
    @MethodSource("utcPropertyConstructors")
    void everyUtcProperty_dateOnly_coercesToMidnightUtc(String label, BiFunction<ParameterList, String, ? extends DateProperty<?>> ctor) {
        DateProperty<?> property = ctor.apply(new ParameterList(), "20240601");
        assertTrue(property.getValue().endsWith("T000000Z"),
                label + " getValue() should end with T000000Z, was: " + property.getValue());
        assertInstanceOf(Instant.class, property.getDate(),
                label + " should hold an Instant, was: " + property.getDate().getClass().getSimpleName());
    }

    static Stream<Arguments> utcPropertyConstructors() {
        return Stream.of(
                Arguments.of("DtStamp",       (BiFunction<ParameterList, String, DateProperty<?>>) DtStamp::new),
                Arguments.of("Created",       (BiFunction<ParameterList, String, DateProperty<?>>) Created::new),
                Arguments.of("LastModified",  (BiFunction<ParameterList, String, DateProperty<?>>) LastModified::new),
                Arguments.of("Completed",     (BiFunction<ParameterList, String, DateProperty<?>>) Completed::new),
                Arguments.of("Acknowledged",  (BiFunction<ParameterList, String, DateProperty<?>>) Acknowledged::new),
                Arguments.of("TzUntil",       (BiFunction<ParameterList, String, DateProperty<?>>) TzUntil::new),
                Arguments.of("Trigger",       (BiFunction<ParameterList, String, DateProperty<?>>) Trigger::new)
        );
    }

    @Test
    void strictMode_dateOnly_throws() {
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING);
        assertThrows(DateTimeParseException.class, () -> new DtStamp("20240601"));
    }

    @Test
    void strictMode_floatingDateTime_throws() {
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING);
        assertThrows(DateTimeParseException.class, () -> new DtStamp("20240601T120000"));
    }

    @Test
    void strictMode_offsetDateTime_throws() {
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING);
        assertThrows(DateTimeParseException.class, () -> new DtStamp("20240601T120000+0500"));
    }
}
