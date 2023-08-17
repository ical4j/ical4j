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
import net.fortuna.ical4j.model.ComponentContainer;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.*;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.DateProperty;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static net.fortuna.ical4j.model.Property.*;
import static net.fortuna.ical4j.model.property.immutable.ImmutableStatus.*;
import static net.fortuna.ical4j.validate.ValidationRule.ValidationType.*;

/**
 * @author Ben
 *
 */
public class ComponentValidator<T extends Component> extends AbstractValidator<T> implements ContentValidator<Property> {

    public static final ComponentValidator<Available> AVAILABLE = new ComponentValidator<>(Component.AVAILABLE,
            new ValidationRule<>(One, DTSTART, DTSTAMP, UID),
            new ValidationRule<>(OneOrLess, CREATED, LAST_MODIFIED, RECURRENCE_ID, RRULE, SUMMARY),
            new ValidationRule<>(OneExclusive, DTEND, DURATION),
            new ValidationRule<>(None, (Predicate<Available> & Serializable) a -> a.getProperties(DTSTART, DTEND).stream()
                    .anyMatch(p -> p.getParameters().contains(Value.DATE)),
                    "VAVAILABILITY components and AVAILABLE sub-components MUST be DATE-TIME values",
                    DTSTART, DTEND));

    public static final ComponentValidator<Participant> PARTICIPANT = new ComponentValidator<>(Component.PARTICIPANT,
            new ValidationRule<>(One, PARTICIPANT_TYPE, UID),
            new ValidationRule<>(OneOrLess, CALENDAR_ADDRESS, CREATED, DESCRIPTION,
                    DTSTAMP, GEO, LAST_MODIFIED, PRIORITY, SEQUENCE, STATUS, SUMMARY, URL));

    public static final ComponentValidator<VAvailability> VAVAILABILITY = new ComponentValidator<>(Component.VAVAILABILITY,
            new ValidationRule<>(One, true, UID, DTSTAMP),
            new ValidationRule<>(OneOrLess, CLASS, CREATED, DESCRIPTION, DTSTART,
                    DTSTAMP, LAST_MODIFIED, ORGANIZER, RECURRENCE_ID, SEQUENCE,
                    STATUS, SUMMARY, UID, URL),
            new ValidationRule<>(None, (Predicate<VAvailability> & Serializable) a -> a.getProperties(DTSTART, DTEND).stream()
                    .anyMatch(p -> p.getParameters().contains(Value.DATE)),
                    "VAVAILABILITY components and AVAILABLE sub-components MUST be DATE-TIME values",
                    DTSTART, DTEND));

    public static final ComponentValidator<VEvent> VEVENT = new ComponentValidator<>(Component.VEVENT,
            new ValidationRule<>(One, true, UID, DTSTAMP),
            new ValidationRule<>(OneOrLess, CLASS, CREATED, DESCRIPTION, DTSTART, GEO, LAST_MODIFIED, LOCATION,
                    ORGANIZER, PRIORITY, DTSTAMP, SEQUENCE, STATUS, SUMMARY, TRANSP, UID, URL, RECURRENCE_ID),
            new ValidationRule<>(OneExclusive, DTEND, DURATION));

    public static final ComponentValidator<VFreeBusy> VFREEBUSY = new ComponentValidator<>(Component.VFREEBUSY,
            new ValidationRule<>(One, true, UID, DTSTAMP),
            new ValidationRule<>(OneOrLess, CONTACT, DTSTART, DTEND, DURATION, DTSTAMP, ORGANIZER, UID, URL),
            new ValidationRule<>(None, RRULE, EXRULE, RDATE, EXDATE),
            new ValidationRule<>(None, (Predicate<VFreeBusy> & Serializable) a -> a.getProperties(DTSTART, DTEND).stream()
                    .anyMatch(p -> !((DateProperty<?>) p).isUtc()),
                    "VFREEBUSY date properties MUST be in UTC time",
                    DTSTART, DTEND));

    public static final ComponentValidator<VJournal> VJOURNAL = new ComponentValidator<>(Component.VJOURNAL,
            new ValidationRule<>(One, DTSTART, DTSTAMP, UID),
            new ValidationRule<>(OneOrLess, BUSYTYPE, CREATED, LAST_MODIFIED, ORGANIZER, SEQUENCE, SUMMARY, URL),
            new ValidationRule<>(OneExclusive, DTEND, DURATION),
            new ValidationRule<>(None, (Predicate<VJournal> & Serializable) a -> a.getProperties(STATUS).stream()
                    .anyMatch(p -> !(VJOURNAL_DRAFT.equals(p) || VJOURNAL_FINAL.equals(p)
                            || VJOURNAL_CANCELLED.equals(p))),
                    "STATUS value not applicable for VJOURNAL", STATUS));

    public static final ComponentValidator<VLocation> VLOCATION = new ComponentValidator<>(Component.VLOCATION,
            new ValidationRule<>(One, true, UID),
            new ValidationRule<>(OneOrLess, DESCRIPTION, GEO, LOCATION_TYPE, NAME));

    public static final ComponentValidator<VResource> VRESOURCE = new ComponentValidator<>(Component.VRESOURCE,
            new ValidationRule<>(One, true, UID),
            new ValidationRule<>(OneOrLess, DESCRIPTION, GEO, RESOURCE_TYPE, NAME));

    public static final ComponentValidator<VTimeZone> VTIMEZONE = new ComponentValidator<>(Component.VTIMEZONE,
            new ValidationRule<>(One, TZID),
            new ValidationRule<>(OneOrLess, LAST_MODIFIED, TZURL));

    public static final ComponentValidator<VToDo> VTODO = new ComponentValidator<>(Component.VTODO,
            new ValidationRule<>(One, true, UID),
            new ValidationRule<>(OneOrLess, CLASS, COMPLETED, CREATED, DESCRIPTION,
                    DTSTAMP, DTSTART, GEO, LAST_MODIFIED, LOCATION, ORGANIZER,
                    PERCENT_COMPLETE, PRIORITY, RECURRENCE_ID, SEQUENCE, STATUS,
                    SUMMARY, UID, URL),
            new ValidationRule<>(OneExclusive, DUE, DURATION));

    public static final ComponentValidator<VVenue> VVENUE = new ComponentValidator<>(Component.VVENUE,
            new ValidationRule<>(One, UID),
            new ValidationRule<>(OneOrLess, NAME, DESCRIPTION, STREET_ADDRESS, EXTENDED_ADDRESS,
                    LOCALITY, REGION, COUNTRY, POSTALCODE, TZID, GEO,
                    LOCATION_TYPE, CATEGORIES, DTSTAMP, CREATED, LAST_MODIFIED));

    public static final ComponentValidator<Observance> OBSERVANCE_ITIP = new ComponentValidator<>(Component.VTIMEZONE,
            new ValidationRule<>(One, DTSTART, TZOFFSETFROM, TZOFFSETTO),
            new ValidationRule<>(OneOrLess, TZNAME));

    public static final ComponentValidator<VAlarm> VALARM_AUDIO = new ComponentValidator<>(Component.VALARM,
            new ValidationRule<>(One, ACTION, TRIGGER),
            new ValidationRule<>(AllOrNone, DURATION, REPEAT),
            new ValidationRule<>(OneOrLess, ATTACH));

    public static final ComponentValidator<VAlarm> VALARM_DISPLAY = new ComponentValidator<>(Component.VALARM,
            new ValidationRule<>(One, ACTION, DESCRIPTION, TRIGGER),
            new ValidationRule<>(AllOrNone, DURATION, REPEAT));

    public static final ComponentValidator<VAlarm> VALARM_EMAIL = new ComponentValidator<>(Component.VALARM,
            new ValidationRule<>(One, ACTION, DESCRIPTION, TRIGGER, SUMMARY),
            new ValidationRule<>(OneOrMore, ATTENDEE),
            new ValidationRule<>(AllOrNone, DURATION, REPEAT));

    public static final ComponentValidator<VAlarm> VALARM_ITIP = new ComponentValidator<>(Component.VALARM,
            new ValidationRule<>(One, ACTION, TRIGGER),
            new ValidationRule<>(OneOrLess, DESCRIPTION, DURATION, REPEAT, SUMMARY));

    public static final ValidationRule<ComponentContainer<?>> NO_ALARMS = new ValidationRule<>(None, Component.VALARM);

    @SafeVarargs
    public ComponentValidator(String context, ValidationRule<T>... rules) {
        super(context, new PropertyContainerRuleSet<>(rules));
    }

    private ComponentValidator(String context, Set<ValidationRule<T>> rules) {
        super(context, new PropertyContainerRuleSet<>(rules.toArray(new ValidationRule[0])));
    }

    @Override
    public ValidationResult validate(T target) throws ValidationException {
        ValidationResult result = super.validate(target);
        result.getEntries().addAll(target.getProperties().stream().map(p -> p.validate().getEntries())
                .flatMap(Collection::stream).collect(Collectors.toSet()));
        return result;
    }

    /**
     * @param componentName a component name used in the assertion
     * @param components a list of components
     * @throws ValidationException where the assertion fails
     * @deprecated see {@link ContentValidator#assertNone(String, List, boolean)}
     */
    @Deprecated
    public static void assertNone(String componentName, ComponentList<?> components) throws ValidationException {
        Validator.assertFalse(input -> input.getComponent(componentName) != null, ASSERT_NONE_MESSAGE, false,
                components, componentName);
    }

    /**
     * @param componentName a component name used in the assertion
     * @param components a list of components
     * @throws ValidationException where the assertion fails
     * @deprecated see {@link ContentValidator#assertOneOrLess(String, List, boolean)}
     */
    @Deprecated
    public static void assertOneOrLess(String componentName, ComponentList<?> components) throws ValidationException {
        Validator.assertFalse(input -> input.getComponents(componentName).size() > 1, ASSERT_ONE_OR_LESS_MESSAGE, false,
                components, componentName);
    }
}
