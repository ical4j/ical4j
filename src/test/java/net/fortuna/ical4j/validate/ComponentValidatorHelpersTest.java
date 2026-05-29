/*
 * Copyright (c) 2026, Ben Fortuna
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
 */
package net.fortuna.ical4j.validate;

import net.fortuna.ical4j.model.component.Standard;
import net.fortuna.ical4j.model.component.VAlarm;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.component.VToDo;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.TzId;
import net.fortuna.ical4j.model.property.TzOffsetFrom;
import net.fortuna.ical4j.model.property.TzOffsetTo;
import net.fortuna.ical4j.model.property.Trigger;
import net.fortuna.ical4j.model.property.immutable.ImmutableAction;
import net.fortuna.ical4j.model.property.immutable.ImmutableStatus;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the canonical {@link ComponentValidator} helpers introduced by the
 * consolidate-deprecated-validators OpenSpec change. Covers:
 * <ul>
 *   <li>{@link ComponentValidator#validateAlarms(net.fortuna.ical4j.model.ComponentContainer, boolean, ValidationResult)}
 *       — replaces the alarm-validation block previously duplicated in
 *       {@code VEventValidator} / {@code VToDoValidator}.</li>
 *   <li>{@link ComponentValidator#validateObservances(VTimeZone, ValidationResult)}
 *       — replaces the throw-based observance check previously in
 *       {@code VTimeZoneValidator}.</li>
 *   <li>VTODO STATUS allowed-value predicate added to {@link ComponentValidator#VTODO}
 *       mirroring the existing VJOURNAL pattern.</li>
 * </ul>
 */
public class ComponentValidatorHelpersTest {

    private static boolean anyEntryMentions(ValidationResult result, String token) {
        return result.getEntries().stream()
                .anyMatch(e -> e.getMessage().contains(token));
    }

    private static VAlarm validDisplayAlarm() {
        VAlarm alarm = new VAlarm();
        alarm.add(ImmutableAction.DISPLAY);
        alarm.add(new Description("Reminder"));
        alarm.add(new Trigger(Duration.ofMinutes(-15)));
        return alarm;
    }

    private static VAlarm alarmMissingAction() {
        VAlarm alarm = new VAlarm();
        // Intentionally omit ACTION
        alarm.add(new Description("Reminder"));
        alarm.add(new Trigger(Duration.ofMinutes(-15)));
        return alarm;
    }

    // ----- validateAlarms -----

    @Test
    public void alarmsAllowedWithValidAlarmProducesNoEntries() {
        VEvent event = new VEvent(false);
        event.add(validDisplayAlarm());

        ValidationResult result = new ValidationResult();
        ComponentValidator.validateAlarms(event, true, result);

        assertTrue(result.getEntries().isEmpty(),
                "Valid VALARM should produce no entries; got: " + result.getEntries());
    }

    @Test
    public void alarmsAllowedWithAlarmMissingActionReportsAction() {
        VEvent event = new VEvent(false);
        event.add(alarmMissingAction());

        ValidationResult result = new ValidationResult();
        ComponentValidator.validateAlarms(event, true, result);

        assertTrue(anyEntryMentions(result, "ACTION"),
                "VALARM missing ACTION must surface an ACTION entry; got: " + result.getEntries());
    }

    @Test
    public void alarmsDisallowedWithAlarmReportsValarm() {
        VEvent event = new VEvent(false);
        event.add(validDisplayAlarm());

        ValidationResult result = new ValidationResult();
        ComponentValidator.validateAlarms(event, false, result);

        assertTrue(anyEntryMentions(result, "VALARM"),
                "Disallowed VALARM presence must surface a VALARM entry; got: " + result.getEntries());
    }

    // ----- validateObservances -----

    @Test
    public void observancesMissingProducesEntryNotThrow() {
        VTimeZone vtz = new VTimeZone();
        vtz.add(new TzId("Test/Zone"));
        // Intentionally no STANDARD or DAYLIGHT observances

        ValidationResult result = new ValidationResult();
        assertDoesNotThrow(() -> ComponentValidator.validateObservances(vtz, result));

        assertTrue(anyEntryMentions(result, "STANDARD"),
                "Missing observances must produce an entry mentioning STANDARD/DAYLIGHT; got: "
                        + result.getEntries());
    }

    @Test
    public void observancesWithStandardProducesNoPresenceEntry() {
        VTimeZone vtz = new VTimeZone();
        vtz.add(new TzId("Test/Zone"));

        Standard std = new Standard();
        std.add(new net.fortuna.ical4j.model.property.DtStart<>(
                ZonedDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC)));
        std.add(new TzOffsetFrom(ZoneOffset.ofHours(1)));
        std.add(new TzOffsetTo(ZoneOffset.ofHours(2)));
        vtz.add(std);

        ValidationResult result = new ValidationResult();
        ComponentValidator.validateObservances(vtz, result);

        assertFalse(anyEntryMentions(result, "must be specified at least once"),
                "VTIMEZONE with a STANDARD observance must not produce a presence entry; got: "
                        + result.getEntries());
    }

    // ----- VTODO STATUS predicate (Section 4) -----

    @Test
    public void vToDoStatusCompletedProducesNoStatusEntry() {
        VToDo todo = new VToDo(false);
        todo.add(new net.fortuna.ical4j.model.property.DtStamp());
        todo.add(new net.fortuna.ical4j.model.property.Uid("status-completed-uid"));
        todo.add(ImmutableStatus.VTODO_COMPLETED);

        ValidationResult result = ComponentValidator.VTODO.validate(todo);

        assertFalse(anyEntryMentions(result, "STATUS value not applicable for VTODO"),
                "VTODO with STATUS:COMPLETED should not flag STATUS; got: " + result.getEntries());
    }

    @Test
    public void vToDoStatusTentativeProducesStatusEntry() {
        VToDo todo = new VToDo(false);
        todo.add(new net.fortuna.ical4j.model.property.DtStamp());
        todo.add(new net.fortuna.ical4j.model.property.Uid("status-tentative-uid"));
        // TENTATIVE is a VEVENT status, not valid for VTODO.
        todo.add(ImmutableStatus.VEVENT_TENTATIVE);

        ValidationResult result = ComponentValidator.VTODO.validate(todo);

        assertTrue(anyEntryMentions(result, "STATUS value not applicable for VTODO"),
                "VTODO with VEVENT STATUS must flag STATUS; got: " + result.getEntries());
    }
}
