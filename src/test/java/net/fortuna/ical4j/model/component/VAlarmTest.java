/*
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

import net.fortuna.ical4j.model.property.*;
import net.fortuna.ical4j.model.property.immutable.ImmutableAction;
import net.fortuna.ical4j.validate.ValidationEntries;
import net.fortuna.ical4j.validate.ValidationEntry.Severity;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.Instant;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Unit tests for VAlarm component.
 */
class VAlarmTest {

    @Test
    void validate_AUDIO_minimal_properties() {
        VAlarm alarm = new VAlarm()
                .add(ImmutableAction.AUDIO)
                .add(new Trigger(Instant.now()));

        var result = alarm.validate();

        assertFalse(result.hasErrors());
    }

    @Test
    void validate_AUDIO_all_properties() {
        VAlarm alarm = new VAlarm()
                .add(ImmutableAction.AUDIO)
                .add(new Trigger(Instant.now()))
                .add(new Duration(java.time.Duration.ofMinutes(5)))
                .add(new Repeat(3))
                .add(new Attach(URI.create("https://domain.example/alarm.mp3")));

        var result = alarm.validate();

        assertFalse(result.hasErrors());
    }

    @Test
    void validate_AUDIO_with_REPEAT_but_no_DURATION() {
        VAlarm alarm = new VAlarm()
                .add(ImmutableAction.AUDIO)
                .add(new Trigger(Instant.now()))
                .add(new Repeat(3));

        var result = alarm.validate();

        assertEquals(
                Set.of(ValidationEntries.allOrNone("DURATION,REPEAT", Severity.ERROR, "VALARM")),
                result.getEntries()
        );
    }

    @Test
    void validate_DISPLAY_minimal_properties() {
        VAlarm alarm = new VAlarm()
                .add(ImmutableAction.DISPLAY)
                .add(new Trigger(Instant.now()))
                .add(new Description("irrelevant"));

        var result = alarm.validate();

        assertFalse(result.hasErrors());
    }

    @Test
    void validate_DISPLAY_all_properties() {
        VAlarm alarm = new VAlarm()
                .add(ImmutableAction.DISPLAY)
                .add(new Trigger(Instant.now()))
                .add(new Description("irrelevant"))
                .add(new Duration(java.time.Duration.ofMinutes(5)))
                .add(new Repeat(3));

        var result = alarm.validate();

        assertFalse(result.hasErrors());
    }

    @Test
    void validate_DISPLAY_without_DESCRIPTION() {
        VAlarm alarm = new VAlarm()
                .add(ImmutableAction.DISPLAY)
                .add(new Trigger(Instant.now()))
                .add(new Duration(java.time.Duration.ofMinutes(5)))
                .add(new Repeat(3));

        var result = alarm.validate();

        assertEquals(Set.of(ValidationEntries.one("DESCRIPTION", Severity.ERROR, "VALARM")), result.getEntries());
    }

    @Test
    void validate_EMAIL_minimal_properties() {
        VAlarm alarm = new VAlarm()
                .add(ImmutableAction.EMAIL)
                .add(new Trigger(Instant.now()))
                .add(new Description("irrelevant"))
                .add(new Summary("irrelevant"))
                .add(new Attendee("mailto:participant@domain.example"));

        var result = alarm.validate();

        assertFalse(result.hasErrors());
    }

    @Test
    void validate_EMAIL_all_properties() {
        VAlarm alarm = new VAlarm()
                .add(ImmutableAction.EMAIL)
                .add(new Trigger(Instant.now()))
                .add(new Description("irrelevant"))
                .add(new Summary("irrelevant"))
                .add(new Attendee("mailto:participant@domain.example"))
                .add(new Duration(java.time.Duration.ofMinutes(5)))
                .add(new Repeat(3));

        var result = alarm.validate();

        assertFalse(result.hasErrors());
    }

    @Test
    void validate_EMAIL_without_SUMMARY() {
        VAlarm alarm = new VAlarm()
                .add(ImmutableAction.EMAIL)
                .add(new Trigger(Instant.now()))
                .add(new Description("irrelevant"))
                .add(new Attendee("mailto:participant@domain.example"));

        var result = alarm.validate();

        assertEquals(Set.of(ValidationEntries.one("SUMMARY", Severity.ERROR, "VALARM")), result.getEntries());
    }

    @Test
    void validate_without_ACTION() {
        VAlarm alarm = new VAlarm()
                .add(new Trigger(Instant.now()));

        var result = alarm.validate();

        assertEquals(Set.of(ValidationEntries.one("ACTION", Severity.ERROR, "VALARM")), result.getEntries());
    }
}
