/**
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

package net.fortuna.ical4j.fuzz;

import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.transform.compliance.Rfc5545Transformer;
import net.fortuna.ical4j.validate.ValidationEntry;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.ValidationResult;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 18 — Rfc5545Transformer#apply on calendars parsed under the relaxed hints (byte 0: bit 1 the suppress-invalid
 * builder). apply returns, or throws IllegalArgumentException from a rule; applying it twice gives the same text as
 * once; the result re-parses and validates with no more errors than the input; no component, UID, DTSTART or
 * ATTENDEE is removed, and a DTSTART whose TZID is only renamed keeps its instant.
 */
public class ComplianceTransformerFuzzer {

    private static final Rfc5545Transformer TRANSFORMER = new Rfc5545Transformer();

    public static void fuzzerInitialize() {
        Ical.hints(true);
    }

    /** One record per component (sub-components included, depth first). */
    private static final class Snapshot {
        final String name;
        final String uid;
        final int dtstarts;
        final int attendees;
        final String dtstartTzid;
        final Instant dtstartInstant;

        Snapshot(Component c) {
            name = c.getName();
            uid = c.getProperty(Property.UID).map(p -> ((Property) p).getValue()).orElse(null);
            dtstarts = c.getProperties(Property.DTSTART).size();
            attendees = c.getProperties(Property.ATTENDEE).size();
            Optional<Property> start = c.getProperty(Property.DTSTART);
            dtstartTzid = start.flatMap(p -> p.getParameter(Parameter.TZID)).map(t -> ((Parameter) t).getValue())
                    .orElse(null);
            Instant instant = null;
            if (start.isPresent() && start.get() instanceof DtStart) {
                try {
                    Temporal t = ((DtStart<?>) start.get()).getDate();
                    if (t instanceof ZonedDateTime) {
                        instant = ((ZonedDateTime) t).toInstant();
                    } else if (t instanceof OffsetDateTime) {
                        instant = ((OffsetDateTime) t).toInstant();
                    } else if (t instanceof Instant) {
                        instant = (Instant) t;
                    }
                } catch (DateTimeException | IllegalArgumentException e) {
                    // an unparsable value: no instant to compare
                }
            }
            dtstartInstant = instant;
        }
    }

    private static void snapshot(Component c, List<Snapshot> out, int depth) {
        out.add(new Snapshot(c));
        if (depth < 12) {
            for (Component sub : Diff.subComponents(c)) {
                snapshot(sub, out, depth + 1);
            }
        }
    }

    private static List<Snapshot> snapshot(Calendar calendar) {
        List<Snapshot> out = new ArrayList<>();
        for (Component c : calendar.getComponents()) {
            snapshot(c, out, 0);
        }
        return out;
    }

    /** -1 when validation throws. */
    private static long errors(Calendar calendar) {
        try {
            ValidationResult r = calendar.validate();
            return r.getEntries().stream().filter(e -> e.getSeverity() == ValidationEntry.Severity.ERROR).count();
        } catch (ValidationException | DateTimeException e) {
            return -1; // validate()'s own defects are scenario 12's
        }
    }

    public static void fuzzerTestOneInput(byte[] input) {
        if (input.length < 1) {
            return;
        }
        Ical.hints(true);
        boolean suppress = (input[0] & 2) != 0;
        Calendar calendar;
        try {
            calendar = Ical.builder(suppress).build(new ByteArrayInputStream(input, 1, input.length - 1));
        } catch (ParserException | IOException e) {
            return;
        }
        if (calendar == null) {
            return;
        }
        String original;
        try {
            original = calendar.toString();
        } catch (DateTimeException e) {
            return; // scenario 01's finding
        }
        List<Snapshot> before = snapshot(calendar);
        long errorsBefore = errors(calendar);

        try {
            TRANSFORMER.apply(calendar);
        } catch (IllegalArgumentException e) {
            return;
        } catch (DateTimeException e) {
            throw Ical.withoutJdkFrames("apply() lets a DateTimeException escape", e);
        }
        String once;
        try {
            once = calendar.toString();
        } catch (DateTimeException e) {
            throw Ical.withoutJdkFrames("apply() made a writable calendar unwritable (toString() throws)", e);
        }
        List<Snapshot> after = snapshot(calendar);

        if (after.size() != before.size()) {
            throw new AssertionError("apply() changed the number of components from " + before.size() + " to "
                    + after.size());
        }
        for (int i = 0; i < before.size(); i++) {
            Snapshot b = before.get(i);
            Snapshot a = after.get(i);
            if (!b.name.equals(a.name)) {
                throw new AssertionError("apply() replaced a " + b.name + " by a " + a.name);
            }
            if (b.uid != null && !b.uid.equals(a.uid)) {
                throw new AssertionError("apply() removed or changed the UID [" + b.uid + "] of a " + b.name);
            }
            if (a.dtstarts < b.dtstarts) {
                throw new AssertionError("apply() removed a DTSTART of a " + b.name);
            }
            if (a.attendees < b.attendees) {
                throw new AssertionError("apply() removed " + (b.attendees - a.attendees) + " ATTENDEE of a " + b.name);
            }
            if (b.dtstartInstant != null && a.dtstartInstant != null && !Objects.equals(b.dtstartTzid, a.dtstartTzid)
                    && !b.dtstartInstant.equals(a.dtstartInstant)) {
                throw new AssertionError("apply() renamed TZID " + b.dtstartTzid + " to " + a.dtstartTzid + " and moved "
                        + b.name + " DTSTART from " + b.dtstartInstant + " to " + a.dtstartInstant);
            }
        }

        TRANSFORMER.apply(calendar);
        String twice = calendar.toString();
        if (!twice.equals(once)) {
            throw new AssertionError("apply() is not idempotent:\n" + Ical.abbreviate(once, 1500) + "\n--- then ---\n"
                    + Ical.abbreviate(twice, 1500));
        }

        // a calendar whose output does not re-parse before apply() is scenario 05's finding (the parser keeping a
        // line it cannot write back), not a defect of the transformer
        try {
            Ical.parse(original, suppress);
        } catch (ParserException | IOException e) {
            return;
        }
        Calendar reparsed;
        try {
            reparsed = Ical.parse(once, suppress);
        } catch (ParserException | IOException e) {
            throw new AssertionError("the transformed calendar does not re-parse: " + e.getMessage() + "\n"
                    + Ical.abbreviate(once, 1500), e);
        }
        if (reparsed == null) {
            throw new AssertionError("the transformed calendar re-parses to no calendar");
        }
        long errorsAfter = errors(reparsed);
        if (errorsBefore >= 0 && errorsAfter > errorsBefore) {
            throw new AssertionError("the transformed calendar has " + errorsAfter + " validation errors, the input "
                    + errorsBefore + ":\n" + reparsed.validate());
        }
    }
}
