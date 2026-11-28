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
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.validate.ValidationEntry;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.ValidationResult;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.DateTimeException;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * 12 — Calendar#validate (CalendarValidatorImpl, the component/property/parameter validators, ITIPValidator when
 * METHOD is set) on a parsed calendar (byte 0 as in 01). validate() returns a ValidationResult or throws
 * ValidationException, nothing else; twice gives the same result; it never changes the calendar's text; relaxed
 * validation reports no ERROR entry that strict validation does not.
 */
public class CalendarValidationFuzzer {

    public static void fuzzerInitialize() {
        Ical.hints(false);
    }

    /** null when validate() throws ValidationException. */
    private static ValidationResult validate(Calendar calendar) {
        try {
            return calendar.validate();
        } catch (ValidationException e) {
            return null;
        } catch (DateTimeException e) {
            // the defect of tmail ISSUE-2806: a rule reads a lazily parsed value; one signature per ical4j path
            throw Ical.withoutJdkFrames("validate() lets a DateTimeException escape", e);
        }
    }

    private static Set<String> errors(ValidationResult r) {
        return r == null ? Set.of() : r.getEntries().stream()
                .filter(e -> e.getSeverity() == ValidationEntry.Severity.ERROR)
                .map(ValidationEntry::toString).collect(Collectors.toCollection(TreeSet::new));
    }

    public static void fuzzerTestOneInput(byte[] input) {
        if (input.length < 1) {
            return;
        }
        int selector = input[0] & 0xff;
        boolean relaxed = (selector & 1) != 0;
        Ical.hints(relaxed);
        Calendar calendar;
        try {
            calendar = Ical.builder((selector & 2) != 0).build(new ByteArrayInputStream(input, 1, input.length - 1));
        } catch (ParserException | IOException e) {
            return;
        }
        if (calendar == null) {
            return;
        }
        String before;
        try {
            before = calendar.toString();
        } catch (DateTimeException e) {
            return; // scenario 01's finding: a lazily parsed date that cannot be written
        }

        ValidationResult first = validate(calendar);
        ValidationResult second = validate(calendar);
        if ((first == null) != (second == null) || (first != null && !first.equals(second))) {
            throw new AssertionError("validating twice differs:\n" + first + "\n--- vs ---\n" + second);
        }
        String after = calendar.toString();
        if (!after.equals(before)) {
            throw new AssertionError("validate() changed the calendar:\n" + Ical.abbreviate(before, 1500)
                    + "\n--- became ---\n" + Ical.abbreviate(after, 1500));
        }

        // strict vs relaxed validation of the same model (the other hints unchanged)
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION, false);
        ValidationResult strict = validate(calendar);
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION, true);
        ValidationResult lax = validate(calendar);
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION, relaxed);
        Set<String> extra = new TreeSet<>(errors(lax));
        extra.removeAll(errors(strict));
        if (!extra.isEmpty()) {
            throw new AssertionError("relaxed validation reports errors strict validation does not: " + extra);
        }
    }
}
