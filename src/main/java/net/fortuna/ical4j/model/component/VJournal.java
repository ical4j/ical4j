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
import net.fortuna.ical4j.model.property.*;
import net.fortuna.ical4j.validate.ComponentValidator;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.ValidationRule;
import net.fortuna.ical4j.validate.Validator;

import java.time.temporal.Temporal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static net.fortuna.ical4j.model.Property.*;
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
public class VJournal extends CalendarComponent implements ComponentContainer<Component> {

    private static final long serialVersionUID = -7635140949183238830L;

    private static final Map<Method, Validator<VJournal>> methodValidators = new HashMap<>();
    static {
        methodValidators.put(Method.ADD, new ComponentValidator<VJournal>(new ValidationRule(One, DESCRIPTION, DTSTAMP, DTSTART, ORGANIZER, SEQUENCE, UID),
                new ValidationRule(OneOrLess, CATEGORIES, CLASS, CREATED, LAST_MODIFIED, STATUS, SUMMARY, URL),
                new ValidationRule(None, ATTENDEE, RECURRENCE_ID)));
        methodValidators.put(Method.CANCEL, new ComponentValidator(new ValidationRule(One, DTSTAMP, ORGANIZER, SEQUENCE, UID),
                new ValidationRule(OneOrLess, CATEGORIES, CLASS, CREATED, DESCRIPTION, DTSTART, LAST_MODIFIED,
                        RECURRENCE_ID, STATUS, SUMMARY, URL),
                new ValidationRule(None, REQUEST_STATUS)));
        methodValidators.put(Method.PUBLISH, new ComponentValidator(new ValidationRule(One, DESCRIPTION, DTSTAMP, DTSTART, ORGANIZER, UID),
                new ValidationRule(OneOrLess, CATEGORIES, CLASS, CREATED, LAST_MODIFIED, RECURRENCE_ID, SEQUENCE, STATUS,
                        SUMMARY, URL),
                new ValidationRule(None, ATTENDEE)));
    }

    private final Validator<VJournal> validator = new ComponentValidator<>(
            new ValidationRule<>(One, true, UID, DTSTAMP),
            new ValidationRule<>(OneOrLess, CLASS, CREATED, DESCRIPTION, DTSTART, DTSTAMP, LAST_MODIFIED, ORGANIZER,
                    RECURRENCE_ID, SEQUENCE, STATUS, SUMMARY, UID, URL)
    );

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
    public final void validate(final boolean recurse) throws ValidationException {
        validator.validate(this);

        /*
         * ; the following are optional, ; but MUST NOT occur more than once class / created / description / dtstart /
         * dtstamp / last-mod / organizer / recurid / seq / status / summary / uid / url /
         */

        final Optional<Status> status = getProperties().getFirst(STATUS);
        if (status.isPresent() && !Status.VJOURNAL_DRAFT.getValue().equals(status.get().getValue())
                && !Status.VJOURNAL_FINAL.getValue().equals(status.get().getValue())
                && !Status.VJOURNAL_CANCELLED.getValue().equals(status.get().getValue())) {
            throw new ValidationException("Status property ["
                    + status.toString() + "] may not occur in VJOURNAL");
        }

        /*
         * ; the following are optional, ; and MAY occur more than once attach / attendee / categories / comment /
         * contact / exdate / exrule / related / rdate / rrule / rstatus / x-prop
         */

        if (recurse) {
            validateProperties();
        }
    }

    /**
     * Performs method-specific ITIP validation.
     * @param method the applicable method
     * @throws ValidationException where the component does not comply with RFC2446
     */
    @Override
    public void validate(Method method) throws ValidationException {
        final Validator<VJournal> validator = methodValidators.get(method);
        if (validator != null) {
            validator.validate(this);
        }
        else {
            super.validate(method);
        }
    }

    @Override
    public ComponentList<Component> getComponents() {
        return (ComponentList<Component>) components;
    }

    @Override
    public void setComponents(ComponentList<Component> components) {
        this.components = components;
    }

    /**
     * @return the optional access classification property for a journal entry
     * @deprecated use {@link VJournal#getProperty(String)}
     */
    @Deprecated
    public final Optional<Clazz> getClassification() {
        return getProperty(CLASS);
    }

    /**
     * @return the optional creation-time property for a journal entry
     * @deprecated use {@link VJournal#getProperty(String)}
     */
    @Deprecated
    public final Optional<Created> getCreated() {
        return getProperty(CREATED);
    }

    /**
     * @return the optional description property for a journal entry
     * @deprecated use {@link VJournal#getProperty(String)}
     */
    @Deprecated
    public final Optional<Description> getDescription() {
        return getProperty(DESCRIPTION);
    }

    /**
     * Convenience method to pull the DTSTART out of the property list.
     * @return The DtStart object representation of the start Date
     * @deprecated use {@link VJournal#getProperty(String)}
     */
    @Deprecated
    public final Optional<DtStart<?>> getStartDate() {
        return getProperty(DTSTART);
    }

    /**
     * @return the optional last-modified property for a journal entry
     * @deprecated use {@link VJournal#getProperty(String)}
     */
    @Deprecated
    public final Optional<LastModified> getLastModified() {
        return getProperty(LAST_MODIFIED);
    }

    /**
     * @return the optional organizer property for a journal entry
     * @deprecated use {@link VJournal#getProperty(String)}
     */
    @Deprecated
    public final Optional<Organizer> getOrganizer() {
        return getProperty(ORGANIZER);
    }

    /**
     * @return the optional date-stamp property
     * @deprecated use {@link VJournal#getProperty(String)}
     */
    @Deprecated
    public final Optional<DtStamp> getDateStamp() {
        return getProperty(DTSTAMP);
    }

    /**
     * @return the optional sequence number property for a journal entry
     * @deprecated use {@link VJournal#getProperty(String)}
     */
    @Deprecated
    public final Optional<Sequence> getSequence() {
        return getProperty(SEQUENCE);
    }

    /**
     * @return the optional status property for a journal entry
     * @deprecated use {@link VJournal#getProperty(String)}
     */
    @Deprecated
    public final Optional<Status> getStatus() {
        return getProperty(STATUS);
    }

    /**
     * @return the optional summary property for a journal entry
     * @deprecated use {@link VJournal#getProperty(String)}
     */
    @Deprecated
    public final Optional<Summary> getSummary() {
        return getProperty(SUMMARY);
    }

    /**
     * @return the optional URL property for a journal entry
     * @deprecated use {@link VJournal#getProperty(String)}
     */
    @Deprecated
    public final Optional<Url> getUrl() {
        return getProperty(URL);
    }

    /**
     * @return the optional recurrence identifier property for a journal entry
     * @deprecated use {@link VJournal#getProperty(String)}
     */
    @Deprecated
    public final Optional<RecurrenceId<?>> getRecurrenceId() {
        return getProperty(RECURRENCE_ID);
    }

    /**
     * Returns the UID property of this component if available.
     * @return a Uid instance, or null if no UID property exists
     * @deprecated use {@link VJournal#getProperty(String)}
     */
    @Deprecated
    public final Optional<Uid> getUid() {
        return getProperty(UID);
    }

    @Override
    protected ComponentFactory<VJournal> newFactory() {
        return new Factory();
    }

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
