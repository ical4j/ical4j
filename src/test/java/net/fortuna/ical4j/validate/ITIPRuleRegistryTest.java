/*
 * Copyright (c) 2024, Ben Fortuna
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

import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VFreeBusy;
import net.fortuna.ical4j.model.component.VJournal;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Uid;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;

import static net.fortuna.ical4j.model.property.immutable.ImmutableMethod.COUNTER;
import static net.fortuna.ical4j.model.property.immutable.ImmutableMethod.PUBLISH;
import static net.fortuna.ical4j.model.property.immutable.ImmutableMethod.REQUEST;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the iTIP rule registry covering the behavioural changes from the
 * rfc-validation-audit OpenSpec change: F1 (PUBLISH/ATTENDEE permitted),
 * F3 (VFREEBUSY/COUNTER rule set), F4 (unsupported method as ValidationResult).
 */
public class ITIPRuleRegistryTest {

    private static VEvent newPublishableVEvent() throws URISyntaxException {
        VEvent event = new VEvent(false);
        event.add(new net.fortuna.ical4j.model.property.DtStamp());
        event.add(new DtStart<>(ZonedDateTime.now()));
        event.add(new Uid("test-uid"));
        event.add(new Organizer(new URI("mailto:organizer@example.com")));
        event.add(new Summary("subject"));
        return event;
    }

    private static boolean anyEntryMentions(ValidationResult result, String token) {
        return result.getEntries().stream()
                .anyMatch(e -> e.getMessage().contains(token));
    }

    // F1: VEVENT/PUBLISH with ATTENDEE validates clean.

    @Test
    public void publishVEventWithSingleAttendeeIsValid() throws URISyntaxException {
        VEvent event = newPublishableVEvent();
        event.add(new Attendee(new URI("mailto:a@example.com")));

        ValidationResult result = ITIPRuleRegistry.validate(event, PUBLISH);

        assertFalse(anyEntryMentions(result, "ATTENDEE"),
                "PUBLISH must not flag ATTENDEE per RFC 5546 §3.2.1.1; got: " + result.getEntries());
    }

    @Test
    public void publishVEventWithMultipleAttendeesIsValid() throws URISyntaxException {
        VEvent event = newPublishableVEvent();
        event.add(new Attendee(new URI("mailto:a@example.com")));
        event.add(new Attendee(new URI("mailto:b@example.com")));

        ValidationResult result = ITIPRuleRegistry.validate(event, PUBLISH);

        assertFalse(anyEntryMentions(result, "ATTENDEE"),
                "PUBLISH must not flag ATTENDEE (multi); got: " + result.getEntries());
    }

    // F3: VFREEBUSY/COUNTER has a defined rule set.

    @Test
    @Disabled("Enabled in Section 6 when VFREEBUSY/COUNTER rule is added (F3)")
    public void counterVFreeBusyWithRequiredPropertiesIsValid() throws URISyntaxException {
        VFreeBusy fb = new VFreeBusy();
        fb.add(new Uid("test-uid"));
        fb.add(new Organizer(new URI("mailto:organizer@example.com")));
        fb.add(new Attendee(new URI("mailto:a@example.com")));
        fb.add(new DtStart<>(ZonedDateTime.now()));
        fb.add(new DtEnd<>(ZonedDateTime.now().plusHours(1)));

        ValidationResult result = ITIPRuleRegistry.validate(fb, COUNTER);

        assertFalse(anyEntryMentions(result, "not applicable"),
                "COUNTER VFREEBUSY must be recognised by the registry; got: " + result.getEntries());
    }

    @Test
    @Disabled("Enabled in Section 6 when VFREEBUSY/COUNTER rule is added (F3)")
    public void counterVFreeBusyMissingUidReportsUid() throws URISyntaxException {
        VFreeBusy fb = new VFreeBusy();
        fb.add(new Organizer(new URI("mailto:organizer@example.com")));
        fb.add(new Attendee(new URI("mailto:a@example.com")));
        fb.add(new DtStart<>(ZonedDateTime.now()));
        fb.add(new DtEnd<>(ZonedDateTime.now().plusHours(1)));
        // UID intentionally missing

        ValidationResult result = ITIPRuleRegistry.validate(fb, COUNTER);

        assertTrue(anyEntryMentions(result, "UID"),
                "Missing UID must surface; got: " + result.getEntries());
    }

    @Test
    public void counterVFreeBusyDoesNotThrow() throws URISyntaxException {
        VFreeBusy fb = new VFreeBusy();
        assertDoesNotThrow(() -> ITIPRuleRegistry.validate(fb, COUNTER));
    }

    // F4: unsupported (component, method) pair returns a ValidationResult, not a throw.

    @Test
    public void vjournalWithRequestReturnsNotApplicableEntry() {
        // RFC 5546 defines VJOURNAL methods only for ADD, CANCEL, PUBLISH.
        VJournal vj = new VJournal(false);

        ValidationResult result = ITIPRuleRegistry.validate(vj, REQUEST);

        assertTrue(anyEntryMentions(result, "not applicable"),
                "VJOURNAL + REQUEST should be flagged not applicable; got: " + result.getEntries());
    }

    @Test
    public void vjournalWithRequestDoesNotThrow() {
        VJournal vj = new VJournal(false);
        assertDoesNotThrow(() -> ITIPRuleRegistry.validate(vj, REQUEST));
    }
}
