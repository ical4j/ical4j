/*
 *  Copyright (c) 2024, Ben Fortuna
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
package net.fortuna.ical4j.validate;

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.validate.component.VEventValidator;
import net.fortuna.ical4j.validate.component.VTimeZoneValidator;
import net.fortuna.ical4j.validate.component.VToDoValidator;

import java.util.HashMap;
import java.util.Map;

import static net.fortuna.ical4j.model.Property.*;
import static net.fortuna.ical4j.model.property.immutable.ImmutableMethod.*;
import static net.fortuna.ical4j.validate.ValidationRule.ValidationType.*;

/**
 * Central registry of per-(component, method) iTIP validation rule sets, as defined by RFC 5546.
 *
 * <p>Rule sets are keyed by {@code (component-name, Method)}. Each cell maps to a {@link Validator}
 * that, when invoked, evaluates the iTIP-specific constraints for that combination.
 *
 * <p>For components whose iTIP validation is method-agnostic (e.g. {@link VTimeZone}), the registry
 * uses a {@code null}-keyed entry within the per-component map. Lookup falls back to the {@code null}
 * key when no method-specific entry exists.
 *
 * <p>When no rule is registered for a given (component, method) pair, {@link #validate(CalendarComponent, Method)}
 * returns a {@link ValidationResult} containing a single ERROR entry stating the method is not applicable
 * to the component, rather than throwing.
 *
 * <p>This class is intentionally package-internal in spirit (final + private constructor); register or
 * inspect rules via the {@link #validate(CalendarComponent, Method)} entry point.
 */
public final class ITIPRuleRegistry {

    private static final Map<String, Map<Method, Validator<? extends CalendarComponent>>> RULES = new HashMap<>();

    static {
        // ----- VEVENT -----
        Map<Method, Validator<? extends CalendarComponent>> vevent = new HashMap<>();
        vevent.put(ADD, new VEventValidator(
                new ValidationRule<>(One, DTSTAMP, DTSTART, ORGANIZER, SEQUENCE, SUMMARY, UID),
                new ValidationRule<>(OneOrLess, CATEGORIES, CLASS, CREATED, DESCRIPTION, DTEND, DURATION, GEO,
                        LAST_MODIFIED, LOCATION, PRIORITY, RESOURCES, STATUS, TRANSP, URL),
                new ValidationRule<>(None, RECURRENCE_ID, REQUEST_STATUS)));
        vevent.put(CANCEL, new VEventValidator(false,
                new ValidationRule<>(One, DTSTAMP, DTSTART, ORGANIZER, SEQUENCE, UID),
                new ValidationRule<>(OneOrLess, CATEGORIES, CLASS, CREATED, DESCRIPTION, DTEND, DTSTART, DURATION, GEO,
                        LAST_MODIFIED, LOCATION, PRIORITY, RECURRENCE_ID, RESOURCES, STATUS, SUMMARY, TRANSP, URL),
                new ValidationRule<>(None, REQUEST_STATUS)));
        vevent.put(COUNTER, new VEventValidator(
                new ValidationRule<>(One, DTSTAMP, DTSTART, SEQUENCE, SUMMARY, UID),
                new ValidationRule<>(One, true, ORGANIZER),
                new ValidationRule<>(OneOrLess, CATEGORIES, CLASS, CREATED, DESCRIPTION, DTEND, DURATION, GEO,
                        LAST_MODIFIED, LOCATION, PRIORITY, RECURRENCE_ID, RESOURCES, STATUS, TRANSP, URL)));
        vevent.put(DECLINE_COUNTER, new VEventValidator(false,
                new ValidationRule<>(One, DTSTAMP, ORGANIZER, UID),
                new ValidationRule<>(OneOrLess, RECURRENCE_ID, SEQUENCE),
                new ValidationRule<>(None, ATTACH, ATTENDEE, CATEGORIES, CLASS, CONTACT, CREATED, DESCRIPTION, DTEND,
                        DTSTART, DURATION, EXDATE, EXRULE, GEO, LAST_MODIFIED, LOCATION, PRIORITY, RDATE, RELATED_TO,
                        RESOURCES, RRULE, STATUS, SUMMARY, TRANSP, URL)));
        // RFC 5546 §3.2.1.1: VEVENT/PUBLISH allows ATTENDEE 0+. The previously-present
        // `new ValidationRule<>(None, true, ATTENDEE)` rule contradicted the RFC and
        // rejected real-world Outlook/Google PUBLISH exports that legitimately list
        // attendees. Removed in this change (F1 of rfc-validation-audit).
        vevent.put(PUBLISH, new VEventValidator(
                new ValidationRule<>(One, DTSTART, UID),
                new ValidationRule<>(One, true, DTSTAMP, ORGANIZER, SUMMARY),
                new ValidationRule<>(OneOrLess, RECURRENCE_ID, SEQUENCE, CATEGORIES, CLASS, CREATED, DESCRIPTION, DTEND,
                        DURATION, GEO, LAST_MODIFIED, LOCATION, PRIORITY, RESOURCES, STATUS, TRANSP, URL),
                new ValidationRule<>(None, REQUEST_STATUS)));
        vevent.put(REFRESH, new VEventValidator(false,
                new ValidationRule<>(One, ATTENDEE, DTSTAMP, ORGANIZER, UID),
                new ValidationRule<>(OneOrLess, RECURRENCE_ID),
                new ValidationRule<>(None, ATTACH, CATEGORIES, CLASS, CONTACT, CREATED, DESCRIPTION, DTEND, DTSTART,
                        DURATION, EXDATE, EXRULE, GEO, LAST_MODIFIED, LOCATION, PRIORITY, RDATE, RELATED_TO,
                        REQUEST_STATUS, RESOURCES, RRULE, SEQUENCE, STATUS, SUMMARY, TRANSP, URL)));
        // NOTE: VEVENT/REPLY's alarms-allowed flag is determined by the KEY_RELAXED_VALIDATION
        // compatibility hint at registry-class load time. This mirrors the legacy behaviour from
        // VEvent.java where the static initialiser captured the hint's value once at class load.
        // (Behaviour preserved verbatim by this refactor; revisiting this is out of scope here.)
        vevent.put(REPLY, new VEventValidator(CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION),
                new ValidationRule<>(One, ATTENDEE, DTSTAMP, ORGANIZER, UID),
                new ValidationRule<>(OneOrLess, RECURRENCE_ID, SEQUENCE, CATEGORIES, CLASS, CREATED, DESCRIPTION, DTEND,
                        DTSTART, DURATION, GEO, LAST_MODIFIED, LOCATION, PRIORITY, RESOURCES, STATUS, SUMMARY, TRANSP,
                        URL)));
        vevent.put(REQUEST, new VEventValidator(
                new ValidationRule<>(OneOrMore, true, ATTENDEE),
                new ValidationRule<>(One, DTSTAMP, DTSTART, ORGANIZER, SUMMARY, UID),
                new ValidationRule<>(OneOrLess, SEQUENCE, CATEGORIES, CLASS, CREATED, DESCRIPTION, DTEND, DURATION, GEO,
                        LAST_MODIFIED, LOCATION, PRIORITY, RECURRENCE_ID, RESOURCES, STATUS, TRANSP, URL)));
        RULES.put(Component.VEVENT, vevent);

        // ----- VTODO -----
        Map<Method, Validator<? extends CalendarComponent>> vtodo = new HashMap<>();
        vtodo.put(ADD, new VToDoValidator(new ValidationRule<>(One, DTSTAMP, ORGANIZER, PRIORITY, SEQUENCE, SUMMARY, UID),
                new ValidationRule<>(OneOrLess, CATEGORIES, CLASS, CREATED, DESCRIPTION, DTSTART, DUE, DURATION, GEO,
                        LAST_MODIFIED, LOCATION, PERCENT_COMPLETE, RESOURCES, STATUS, URL),
                new ValidationRule<>(None, RECURRENCE_ID, REQUEST_STATUS)));
        vtodo.put(CANCEL, new VToDoValidator(false, new ValidationRule<>(One, UID, DTSTAMP, ORGANIZER, SEQUENCE),
                new ValidationRule<>(OneOrLess, CATEGORIES, CLASS, CREATED, DESCRIPTION, DTSTART, DUE, DURATION, GEO,
                        LAST_MODIFIED, LOCATION, PERCENT_COMPLETE, RECURRENCE_ID, RESOURCES, PRIORITY, STATUS, URL),
                new ValidationRule<>(None, REQUEST_STATUS)));
        vtodo.put(COUNTER, new VToDoValidator(new ValidationRule<>(OneOrMore, ATTENDEE),
                new ValidationRule<>(One, DTSTAMP, ORGANIZER, PRIORITY, SUMMARY, UID),
                new ValidationRule<>(OneOrLess, CATEGORIES, CLASS, CREATED, DESCRIPTION, DTSTART, DUE, DURATION, GEO,
                        LAST_MODIFIED, LOCATION, PERCENT_COMPLETE, RECURRENCE_ID, RESOURCES, RRULE, SEQUENCE, STATUS,
                        URL)));
        vtodo.put(DECLINE_COUNTER, new VToDoValidator(false, new ValidationRule<>(OneOrMore, ATTENDEE),
                new ValidationRule<>(One, DTSTAMP, ORGANIZER, SEQUENCE, UID),
                new ValidationRule<>(OneOrLess, CATEGORIES, CLASS, CREATED, DESCRIPTION, DTSTART, DUE, DURATION, GEO,
                        LAST_MODIFIED, LOCATION, LOCATION, PERCENT_COMPLETE, PRIORITY, RECURRENCE_ID, RESOURCES, STATUS,
                        URL)));
        vtodo.put(PUBLISH, new VToDoValidator(new ValidationRule<>(One, DTSTAMP, SUMMARY, UID),
                new ValidationRule<>(One, true, ORGANIZER, PRIORITY),
                new ValidationRule<>(OneOrLess, DTSTART, SEQUENCE, CATEGORIES, CLASS, CREATED, DESCRIPTION, DUE, DURATION,
                        GEO, LAST_MODIFIED, LOCATION, PERCENT_COMPLETE, RECURRENCE_ID, RESOURCES, STATUS, URL),
                new ValidationRule<>(None, ATTENDEE, REQUEST_STATUS)));
        vtodo.put(REFRESH, new VToDoValidator(false, new ValidationRule<>(One, ATTENDEE, DTSTAMP, UID),
                new ValidationRule<>(OneOrLess, RECURRENCE_ID),
                new ValidationRule<>(None, ATTACH, CATEGORIES, CLASS, CONTACT, CREATED, DESCRIPTION, DTSTART, DUE,
                        DURATION, EXDATE, EXRULE, GEO, LAST_MODIFIED, LOCATION, ORGANIZER, PERCENT_COMPLETE, PRIORITY,
                        RDATE, RELATED_TO, REQUEST_STATUS, RESOURCES, RRULE, SEQUENCE, STATUS, URL)));
        vtodo.put(REPLY, new VToDoValidator(false, new ValidationRule<>(OneOrMore, ATTENDEE),
                new ValidationRule<>(One, DTSTAMP, ORGANIZER, UID),
                new ValidationRule<>(OneOrLess, CATEGORIES, CLASS, CREATED, DESCRIPTION, DTSTART, DUE, DURATION, GEO,
                        LAST_MODIFIED, LOCATION, PERCENT_COMPLETE, PRIORITY, RESOURCES, RECURRENCE_ID, SEQUENCE, STATUS,
                        SUMMARY, URL)));
        vtodo.put(REQUEST, new VToDoValidator(new ValidationRule<>(OneOrMore, ATTENDEE),
                new ValidationRule<>(One, DTSTAMP, DTSTART, ORGANIZER, PRIORITY, SUMMARY, UID),
                new ValidationRule<>(OneOrLess, SEQUENCE, CATEGORIES, CLASS, CREATED, DESCRIPTION, DUE, DURATION, GEO,
                        LAST_MODIFIED, LOCATION, PERCENT_COMPLETE, RECURRENCE_ID, RESOURCES, STATUS, URL),
                new ValidationRule<>(None, REQUEST_STATUS)));
        RULES.put(Component.VTODO, vtodo);

        // ----- VJOURNAL -----
        Map<Method, Validator<? extends CalendarComponent>> vjournal = new HashMap<>();
        vjournal.put(ADD, new ComponentValidator<>(Component.VJOURNAL,
                new ValidationRule<>(One, DESCRIPTION, DTSTAMP, DTSTART, ORGANIZER, SEQUENCE, UID),
                new ValidationRule<>(OneOrLess, CATEGORIES, CLASS, CREATED, LAST_MODIFIED, STATUS, SUMMARY, URL),
                new ValidationRule<>(None, ATTENDEE, RECURRENCE_ID)));
        vjournal.put(CANCEL, new ComponentValidator<>(Component.VJOURNAL,
                new ValidationRule<>(One, DTSTAMP, ORGANIZER, SEQUENCE, UID),
                new ValidationRule<>(OneOrLess, CATEGORIES, CLASS, CREATED, DESCRIPTION, DTSTART, LAST_MODIFIED,
                        RECURRENCE_ID, STATUS, SUMMARY, URL),
                new ValidationRule<>(None, REQUEST_STATUS)));
        vjournal.put(PUBLISH, new ComponentValidator<>(Component.VJOURNAL,
                new ValidationRule<>(One, DESCRIPTION, DTSTAMP, DTSTART, ORGANIZER, UID),
                new ValidationRule<>(OneOrLess, CATEGORIES, CLASS, CREATED, LAST_MODIFIED, RECURRENCE_ID, SEQUENCE, STATUS,
                        SUMMARY, URL),
                new ValidationRule<>(None, ATTENDEE)));
        RULES.put(Component.VJOURNAL, vjournal);

        // ----- VFREEBUSY -----
        Map<Method, Validator<? extends CalendarComponent>> vfreebusy = new HashMap<>();
        vfreebusy.put(PUBLISH, new ComponentValidator<>(Component.VFREEBUSY,
                new ValidationRule<>(OneOrMore, FREEBUSY),
                new ValidationRule<>(One, DTSTAMP, DTSTART, DTEND, ORGANIZER, UID),
                new ValidationRule<>(OneOrLess, URL),
                new ValidationRule<>(None, ATTENDEE, DURATION, REQUEST_STATUS)));
        vfreebusy.put(REPLY, new ComponentValidator<>(Component.VFREEBUSY,
                new ValidationRule<>(One, ATTENDEE, DTSTAMP, DTEND, DTSTART, ORGANIZER, UID),
                new ValidationRule<>(OneOrLess, URL),
                new ValidationRule<>(None, DURATION, SEQUENCE)));
        vfreebusy.put(REQUEST, new ComponentValidator<>(Component.VFREEBUSY,
                new ValidationRule<>(OneOrMore, ATTENDEE),
                new ValidationRule<>(One, DTEND, DTSTAMP, DTSTART, ORGANIZER, UID),
                new ValidationRule<>(None, FREEBUSY, DURATION, REQUEST_STATUS, URL)));
        RULES.put(Component.VFREEBUSY, vfreebusy);

        // ----- VTIMEZONE -----
        // VTIMEZONE's iTIP validation is method-agnostic: the same validator runs for any METHOD.
        // The convention: a null-keyed entry in the per-component map serves as the
        // "any method" fallback, consulted when no method-specific entry exists.
        Map<Method, Validator<? extends CalendarComponent>> vtimezone = new HashMap<>();
        vtimezone.put(null, new VTimeZoneValidator());
        RULES.put(Component.VTIMEZONE, vtimezone);
    }

    private ITIPRuleRegistry() {
        // utility class - no instances
    }

    /**
     * Apply the registered iTIP validator (if any) for the given component and method.
     *
     * @param component the target component
     * @param method the iTIP method to validate against
     * @return a {@link ValidationResult} produced by the registered validator, or a result containing
     * a single ERROR entry indicating that the method is not applicable to the component
     */
    public static ValidationResult validate(CalendarComponent component, Method method) {
        Map<Method, Validator<? extends CalendarComponent>> byMethod = RULES.get(component.getName());
        if (byMethod == null) {
            return notApplicable(component, method);
        }
        @SuppressWarnings("unchecked")
        Validator<CalendarComponent> validator = (Validator<CalendarComponent>) byMethod.get(method);
        if (validator == null) {
            // Fall back to the method-agnostic entry (used by VTIMEZONE).
            @SuppressWarnings("unchecked")
            Validator<CalendarComponent> fallback = (Validator<CalendarComponent>) byMethod.get(null);
            if (fallback == null) {
                return notApplicable(component, method);
            }
            validator = fallback;
        }
        try {
            return validator.validate(component);
        } catch (ValidationException ve) {
            // Defensive: cardinality-based validators should not throw, but if a legacy
            // validator does we surface its message as an ERROR entry rather than propagating.
            ValidationResult result = new ValidationResult();
            result.getEntries().add(new ValidationEntry(ve.getMessage(),
                    ValidationEntry.Severity.ERROR, component.getName()));
            return result;
        }
    }

    private static ValidationResult notApplicable(CalendarComponent component, Method method) {
        ValidationResult result = new ValidationResult();
        String methodValue = method != null ? method.getValue() : "<null>";
        result.getEntries().add(new ValidationEntry(
                String.format("Method %s not applicable to component %s", methodValue, component.getName()),
                ValidationEntry.Severity.ERROR,
                component.getName()));
        return result;
    }
}
