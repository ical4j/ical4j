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

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.immutable.ImmutableVersion;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link CalendarValidatorImpl} covering the F2 fix from the
 * rfc-validation-audit OpenSpec change: PRODID and VERSION presence are
 * required, enforced unconditionally and independent of constructor-passed rules.
 */
public class CalendarValidatorImplPropPresenceTest {

    private static boolean anyEntryMentions(ValidationResult result, String token) {
        return result.getEntries().stream()
                .anyMatch(e -> e.getMessage().contains(token));
    }

    @Test
    public void calendarMissingProdIdReportsError() {
        Calendar cal = new Calendar();
        // Add VERSION so we isolate the PRODID check
        cal.add(ImmutableVersion.VERSION_2_0);
        cal.add(new VEvent());

        ValidationResult result = new CalendarValidatorImpl().validate(cal);

        assertTrue(anyEntryMentions(result, "PRODID"),
                "Missing PRODID must produce a ValidationEntry; got: " + result.getEntries());
    }

    @Test
    public void calendarMissingVersionReportsError() {
        Calendar cal = new Calendar();
        cal.add(new ProdId("-//test//ical4j//EN"));
        cal.add(new VEvent());

        ValidationResult result = new CalendarValidatorImpl().validate(cal);

        assertTrue(anyEntryMentions(result, "VERSION"),
                "Missing VERSION must produce a ValidationEntry; got: " + result.getEntries());
    }

    @Test
    public void calendarMissingBothReportsBoth() {
        Calendar cal = new Calendar();
        cal.add(new VEvent());

        ValidationResult result = new CalendarValidatorImpl().validate(cal);

        assertTrue(anyEntryMentions(result, "PRODID"),
                "Missing PRODID must produce a ValidationEntry");
        assertTrue(anyEntryMentions(result, "VERSION"),
                "Missing VERSION must produce a ValidationEntry");
    }

    @Test
    public void calendarWithProdIdAndVersionDoesNotReportEither() {
        Calendar cal = new Calendar();
        cal.add(new ProdId("-//test//ical4j//EN"));
        cal.add(ImmutableVersion.VERSION_2_0);
        cal.add(new VEvent());

        ValidationResult result = new CalendarValidatorImpl().validate(cal);

        assertFalse(anyEntryMentions(result, "PRODID is required"),
                "PRODID present should not produce 'PRODID is required'; got: " + result.getEntries());
        assertFalse(anyEntryMentions(result, "VERSION is required"),
                "VERSION present should not produce 'VERSION is required'; got: " + result.getEntries());
    }
}
