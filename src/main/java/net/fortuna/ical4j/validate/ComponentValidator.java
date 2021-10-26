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
package net.fortuna.ical4j.validate;

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.component.*;
import net.fortuna.ical4j.util.CompatibilityHints;

import java.util.Arrays;
import java.util.List;

import static net.fortuna.ical4j.model.Property.*;
import static net.fortuna.ical4j.validate.ValidationRule.ValidationType.*;
import static net.fortuna.ical4j.validate.Validator.assertFalse;

/**
 * @author Ben
 *
 */
public class ComponentValidator<T extends Component> implements Validator<T> {

    private static final String ASSERT_NONE_MESSAGE = "Component [{0}] is not applicable";

    private static final String ASSERT_ONE_OR_LESS_MESSAGE = "Component [{0}] must only be specified once";

    public static final ComponentValidator<Available> AVAILABLE = new ComponentValidator<>(
            new ValidationRule(One, DTSTART, DTSTAMP, UID),
            new ValidationRule(OneOrLess, CREATED, LAST_MODIFIED, RECURRENCE_ID, RRULE, SUMMARY),
            new ValidationRule(OneExclusive, DTEND, DURATION)
    );

    public static final ComponentValidator<Participant> PARTICIPANT = new ComponentValidator<>(
            new ValidationRule(One, PARTICIPANT_TYPE, UID),
            new ValidationRule(OneOrLess, CALENDAR_ADDRESS, CREATED, DESCRIPTION,
                    DTSTAMP, GEO, LAST_MODIFIED, PRIORITY, SEQUENCE, STATUS, SUMMARY, URL)
    );

    public static final ComponentValidator<VAvailability> VAVAILABILITY = new ComponentValidator<>(
            new ValidationRule(One, DTSTART, DTSTAMP, UID),
            new ValidationRule(OneOrLess, BUSYTYPE, CREATED, LAST_MODIFIED,
                    ORGANIZER, SEQUENCE, SUMMARY, URL),
            new ValidationRule(OneExclusive, DTEND, DURATION));

    public static final ComponentValidator<VEvent> VEVENT = new ComponentValidator<>(
            new ValidationRule(One, true, UID, DTSTAMP),
            new ValidationRule(OneOrLess, CLASS, CREATED, DESCRIPTION, DTSTART, GEO, LAST_MODIFIED, LOCATION,
                    ORGANIZER, PRIORITY, DTSTAMP, SEQUENCE, STATUS, SUMMARY, TRANSP, UID, URL, RECURRENCE_ID),
            new ValidationRule(OneExclusive, DTEND, DURATION));

    public static final ComponentValidator<VFreeBusy> VFREEBUSY = new ComponentValidator<>(
            new ValidationRule(One, true, UID, DTSTAMP),
            new ValidationRule(OneOrLess, CONTACT, DTSTART, DTEND, DURATION, DTSTAMP, ORGANIZER, UID, URL),
            new ValidationRule(None, RRULE, EXRULE, RDATE, EXDATE));

    public static final ComponentValidator<VJournal> VJOURNAL = new ComponentValidator<>(
            new ValidationRule(One, true, UID, DTSTAMP),
            new ValidationRule(OneOrLess, CLASS, CREATED, DESCRIPTION, DTSTART,
                    DTSTAMP, LAST_MODIFIED, ORGANIZER, RECURRENCE_ID, SEQUENCE,
                    STATUS, SUMMARY, UID, URL));

    public static final ComponentValidator<VLocation> VLOCATION = new ComponentValidator<>(
            new ValidationRule(One, true, UID),
            new ValidationRule(OneOrLess, DESCRIPTION, GEO, LOCATION_TYPE, NAME));

    public static final ComponentValidator<VResource> VRESOURCE = new ComponentValidator<>(
            new ValidationRule(One, true, UID),
            new ValidationRule(OneOrLess, DESCRIPTION, GEO, RESOURCE_TYPE, NAME));

    public static final ComponentValidator<VTimeZone> VTIMEZONE = new ComponentValidator<>(
            new ValidationRule(One, TZID),
            new ValidationRule(OneOrLess, LAST_MODIFIED, TZURL));

    public static final ComponentValidator<VToDo> VTODO = new ComponentValidator<>(
            new ValidationRule(One, true, UID),
            new ValidationRule(OneOrLess, CLASS, COMPLETED, CREATED, DESCRIPTION,
                    DTSTAMP, DTSTART, GEO, LAST_MODIFIED, LOCATION, ORGANIZER,
                    PERCENT_COMPLETE, PRIORITY, RECURRENCE_ID, SEQUENCE, STATUS,
                    SUMMARY, UID, URL),
            new ValidationRule(OneExclusive, DUE, DURATION));

    public static final ComponentValidator<VVenue> VVENUE = new ComponentValidator<>(
            new ValidationRule(One, UID),
            new ValidationRule(OneOrLess, NAME, DESCRIPTION, STREET_ADDRESS, EXTENDED_ADDRESS,
                    LOCALITY, REGION, COUNTRY, POSTALCODE, TZID, GEO,
                    LOCATION_TYPE, CATEGORIES, DTSTAMP, CREATED, LAST_MODIFIED));

    public static final ComponentValidator<Observance> OBSERVANCE_ITIP = new ComponentValidator<>(
            new ValidationRule(One, DTSTART, TZOFFSETFROM, TZOFFSETTO),
            new ValidationRule(OneOrLess, TZNAME));

    public static final ComponentValidator<VAlarm> VALARM_AUDIO = new ComponentValidator<>(
            new ValidationRule(One, ACTION, TRIGGER),
            new ValidationRule(AllOrNone, DURATION, REPEAT),
            new ValidationRule(OneOrLess, ATTACH));

    public static final ComponentValidator<VAlarm> VALARM_DISPLAY = new ComponentValidator<>(
            new ValidationRule(One, ACTION, DESCRIPTION, TRIGGER),
            new ValidationRule(AllOrNone, DURATION, REPEAT));

    public static final ComponentValidator<VAlarm> VALARM_EMAIL = new ComponentValidator<>(
            new ValidationRule(One, ACTION, DESCRIPTION, TRIGGER, SUMMARY),
            new ValidationRule(OneOrMore, ATTENDEE),
            new ValidationRule(AllOrNone, DURATION, REPEAT));

    public static final ComponentValidator<VAlarm> VALARM_ITIP = new ComponentValidator<>(
            new ValidationRule(One, ACTION, TRIGGER),
            new ValidationRule(OneOrLess, DESCRIPTION, DURATION, REPEAT, SUMMARY));

    private final List<ValidationRule> rules;

    public ComponentValidator(ValidationRule... rules) {
        this.rules = Arrays.asList(rules);
    }

    @Override
    public void validate(T target) throws ValidationException {
        ValidationResult result = new ValidationResult();

        for (ValidationRule rule : rules) {
            boolean warnOnly = CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION)
                    && rule.isRelaxedModeSupported();

            if (warnOnly) {
                result.getWarnings().addAll(apply(rule, target));
            } else {
                result.getErrors().addAll(apply(rule, target));
            }
        }
        if (result.hasErrors()) {
            throw new ValidationException(result);
        }
    }

    /**
     * @param componentName a component name used in the assertion
     * @param components a list of components
     * @throws ValidationException where the assertion fails
     */
    public static void assertNone(String componentName, ComponentList<?> components) throws ValidationException {
        assertFalse(input -> input.getComponent(componentName) != null, ASSERT_NONE_MESSAGE, false,
                components, componentName);
    }
    
    /**
     * @param componentName a component name used in the assertion
     * @param components a list of components
     * @throws ValidationException where the assertion fails
     */
    public static void assertOneOrLess(String componentName, ComponentList<?> components) throws ValidationException {
        assertFalse(input -> input.getComponents(componentName).size() > 1, ASSERT_ONE_OR_LESS_MESSAGE, false,
                components, componentName);
    }
}
