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
package net.fortuna.ical4j.model.component;

import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.property.DtStamp;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.validate.*;

import java.time.temporal.Temporal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static net.fortuna.ical4j.model.Property.*;
import static net.fortuna.ical4j.model.property.immutable.ImmutableMethod.*;
import static net.fortuna.ical4j.validate.ValidationRule.ValidationType.*;

/**
 * $Id$ [Apr 5, 2004]
 *
 * Defines an iCalendar VJOURNAL component.
 *
 * <pre>
 *    4.6.3 Journal Component
 *
 *       Component Name: VJOURNAL
 *
 *       Purpose: Provide a grouping of component properties that describe a
 *       journal entry.
 *
 *       Formal Definition: A &quot;VJOURNAL&quot; calendar component is defined by the
 *       following notation:
 *
 *         journalc   = &quot;BEGIN&quot; &quot;:&quot; &quot;VJOURNAL&quot; CRLF
 *                      jourprop
 *                      &quot;END&quot; &quot;:&quot; &quot;VJOURNAL&quot; CRLF
 *
 *         jourprop   = *(
 *
 *                    ; the following are optional,
 *                    ; but MUST NOT occur more than once
 *
 *                    class / created / description / dtstart / dtstamp /
 *                    last-mod / organizer / recurid / seq / status /
 *                    summary / uid / url /
 *
 *                    ; the following are optional,
 *                    ; and MAY occur more than once
 *
 *                    attach / attendee / categories / comment /
 *                    contact / exdate / exrule / related / rdate /
 *                    rrule / rstatus / x-prop
 *
 *                    )
 * </pre>
 *
 * Example 1 - Creating a journal associated with an event:
 *
 * <pre><code>
 * DtStart meetingDate = (DtStart) meeting.getProperties().getProperty(
 *         Property.DTSTART);
 *
 * VJournal minutes = new VJournal(meetingDate.getTime(),
 *         &quot;Progress Meeting - Minutes&quot;);
 *
 * // add timezone information..
 * TzId tzParam = meetingDate.getParameters().getParmaeter(Parameter.TZID);
 * minutes.getProperties().getProperty(Property.DTSTART).getParameters().add(
 *         tzParam);
 *
 * // add description..
 * minutes.add(new Description(&quot;1. Agenda.., 2. Action Items..&quot;));
 * </code></pre>
 *
 * @author Ben Fortuna
 */
public class VJournal extends CalendarComponent implements ComponentContainer<Component>, RecurrenceSupport<VJournal>,
        DescriptivePropertyAccessor, ChangeManagementPropertyAccessor, DateTimePropertyAccessor,
        RelationshipPropertyAccessor, ParticipantsAccessor, LocationsAccessor, ResourcesAccessor {

    private static final long serialVersionUID = -7635140949183238830L;

    private static final Map<Method, Validator<VJournal>> methodValidators = new HashMap<>();
    static {
        methodValidators.put(ADD, new ComponentValidator<>(VJOURNAL, new ValidationRule<>(One, DESCRIPTION, DTSTAMP, DTSTART, ORGANIZER, SEQUENCE, UID),
                new ValidationRule<>(OneOrLess, CATEGORIES, CLASS, CREATED, LAST_MODIFIED, STATUS, SUMMARY, URL),
                new ValidationRule<>(None, ATTENDEE, RECURRENCE_ID)));
        methodValidators.put(CANCEL, new ComponentValidator<>(VJOURNAL, new ValidationRule<>(One, DTSTAMP, ORGANIZER, SEQUENCE, UID),
                new ValidationRule<>(OneOrLess, CATEGORIES, CLASS, CREATED, DESCRIPTION, DTSTART, LAST_MODIFIED,
                        RECURRENCE_ID, STATUS, SUMMARY, URL),
                new ValidationRule<>(None, REQUEST_STATUS)));
        methodValidators.put(PUBLISH, new ComponentValidator<>(VJOURNAL, new ValidationRule<>(One, DESCRIPTION, DTSTAMP, DTSTART, ORGANIZER, UID),
                new ValidationRule<>(OneOrLess, CATEGORIES, CLASS, CREATED, LAST_MODIFIED, RECURRENCE_ID, SEQUENCE, STATUS,
                        SUMMARY, URL),
                new ValidationRule<>(None, ATTENDEE)));
    }

    /**
     * Default constructor.
     */
    public VJournal() {
        this(true);
    }

    public VJournal(boolean initialise) {
        super(VJOURNAL);
        if (initialise) {
            add(new DtStamp());
        }
    }

    /**
     * Constructor.
     * @param properties a list of properties
     */
    public VJournal(final PropertyList properties) {
        super(VJOURNAL, properties);
    }

    /**
     * Constructs a new VJOURNAL instance associated with the specified time with the specified summary.
     * @param start the date the journal entry is associated with
     * @param summary the journal summary
     */
    public VJournal(final Temporal start, final String summary) {
        this();
        add(new DtStart(start));
        add(new Summary(summary));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValidationResult validate(final boolean recurse) throws ValidationException {
        ValidationResult result = ComponentValidator.VJOURNAL.validate(this);
        if (recurse) {
            result = result.merge(validateProperties());
        }
        return result;
    }

    /**
     * Performs method-specific ITIP validation.
     * @param method the applicable method
     * @throws ValidationException where the component does not comply with RFC2446
     */
    @Override
    public ValidationResult validate(Method method) throws ValidationException {
        final Validator<VJournal> validator = methodValidators.get(method);
        if (validator != null) {
            return validator.validate(this);
        }
        else {
            return super.validate(method);
        }
    }

    /**
     *
     * @return Returns the underlying component list.
     */
    @Override
    public ComponentList<Component> getComponentList() {
        return (ComponentList<Component>) components;
    }

    @Override
    public void setComponentList(ComponentList<Component> components) {
        this.components = components;
    }

    /**
     * Convenience method to pull the DTSTART out of the property list.
     * @return The DtStart object representation of the start Date
     * @deprecated use {@link DateTimePropertyAccessor#getDateTimeStart()}
     */
    @Deprecated
    public final <T extends Temporal> Optional<DtStart<T>> getStartDate() {
        return getDateTimeStart();
    }

    /**
     * @return the optional date-stamp property
     * @deprecated use {@link ChangeManagementPropertyAccessor#getDateTimeStamp()}
     */
    @Deprecated
    public final Optional<DtStamp> getDateStamp() {
        return getDateTimeStamp();
    }

    @Override
    protected ComponentFactory<VJournal> newFactory() {
        return new Factory();
    }

    @Override
    public <T extends Component> T copy() {
        return (T) newFactory().createComponent(new PropertyList(getProperties().parallelStream()
                        .map(Property::copy).collect(Collectors.toList())),
                new ComponentList<>(getComponents().parallelStream()
                        .map(c -> (T) c.copy()).collect(Collectors.toList())));
    }

    /**
     * Default factory.
     */
    public static class Factory extends Content.Factory implements ComponentFactory<VJournal> {

        public Factory() {
            super(VJOURNAL);
        }

        @Override
        public VJournal createComponent() {
            return new VJournal(false);
        }

        @Override
        public VJournal createComponent(PropertyList properties) {
            return new VJournal(properties);
        }
    }
}
