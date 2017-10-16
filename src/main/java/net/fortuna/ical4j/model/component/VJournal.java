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
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.validate.PropertyValidator;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.Validator;
import net.fortuna.ical4j.validate.component.VJournalAddValidator;
import net.fortuna.ical4j.validate.component.VJournalCancelValidator;
import net.fortuna.ical4j.validate.component.VJournalPublishValidator;
import org.apache.commons.collections4.Closure;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
 * minutes.getProperties().add(new Description(&quot;1. Agenda.., 2. Action Items..&quot;));
 * </code></pre>
 *
 * @author Ben Fortuna
 */
public class VJournal extends CalendarComponent {

    private static final long serialVersionUID = -7635140949183238830L;

    private final Map<Method, Validator> methodValidators = new HashMap<Method, Validator>();
    {
        methodValidators.put(Method.ADD, new VJournalAddValidator());
        methodValidators.put(Method.CANCEL, new VJournalCancelValidator());
        methodValidators.put(Method.PUBLISH, new VJournalPublishValidator());
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
            getProperties().add(new DtStamp());
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
    public VJournal(final Date start, final String summary) {
        this();
        getProperties().add(new DtStart(start));
        getProperties().add(new Summary(summary));
    }

    /**
     * {@inheritDoc}
     */
    public final void validate(final boolean recurse)
            throws ValidationException {

        if (!CompatibilityHints
                .isHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION)) {

            // From "4.8.4.7 Unique Identifier":
            // Conformance: The property MUST be specified in the "VEVENT", "VTODO",
            // "VJOURNAL" or "VFREEBUSY" calendar components.
            PropertyValidator.getInstance().assertOne(Property.UID,
                    getProperties());

            // From "4.8.7.2 Date/Time Stamp":
            // Conformance: This property MUST be included in the "VEVENT", "VTODO",
            // "VJOURNAL" or "VFREEBUSY" calendar components.
            PropertyValidator.getInstance().assertOne(Property.DTSTAMP,
                    getProperties());
        }

        /*
         * ; the following are optional, ; but MUST NOT occur more than once class / created / description / dtstart /
         * dtstamp / last-mod / organizer / recurid / seq / status / summary / uid / url /
         */
        CollectionUtils.forAllDo(Arrays.asList(Property.CLASS, Property.CREATED, Property.DESCRIPTION, Property.DTSTART,
                Property.DTSTAMP, Property.LAST_MODIFIED, Property.ORGANIZER, Property.RECURRENCE_ID, Property.SEQUENCE,
                Property.STATUS, Property.SUMMARY, Property.UID, Property.URL), new Closure<String>() {
            @Override
            public void execute(String input) {
                PropertyValidator.getInstance().assertOneOrLess(input, getProperties());
            }
        });

        final Status status = (Status) getProperty(Property.STATUS);
        if (status != null && !Status.VJOURNAL_DRAFT.getValue().equals(status.getValue())
                && !Status.VJOURNAL_FINAL.getValue().equals(status.getValue())
                && !Status.VJOURNAL_CANCELLED.getValue().equals(status.getValue())) {
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
     * {@inheritDoc}
     */
    protected Validator getValidator(Method method) {
        return methodValidators.get(method);
    }

    /**
     * @return the optional access classification property for a journal entry
     */
    public final Clazz getClassification() {
        return (Clazz) getProperty(Property.CLASS);
    }

    /**
     * @return the optional creation-time property for a journal entry
     */
    public final Created getCreated() {
        return (Created) getProperty(Property.CREATED);
    }

    /**
     * @return the optional description property for a journal entry
     */
    public final Description getDescription() {
        return (Description) getProperty(Property.DESCRIPTION);
    }

    /**
     * Convenience method to pull the DTSTART out of the property list.
     * @return The DtStart object representation of the start Date
     */
    public final DtStart getStartDate() {
        return (DtStart) getProperty(Property.DTSTART);
    }

    /**
     * @return the optional last-modified property for a journal entry
     */
    public final LastModified getLastModified() {
        return (LastModified) getProperty(Property.LAST_MODIFIED);
    }

    /**
     * @return the optional organizer property for a journal entry
     */
    public final Organizer getOrganizer() {
        return (Organizer) getProperty(Property.ORGANIZER);
    }

    /**
     * @return the optional date-stamp property
     */
    public final DtStamp getDateStamp() {
        return (DtStamp) getProperty(Property.DTSTAMP);
    }

    /**
     * @return the optional sequence number property for a journal entry
     */
    public final Sequence getSequence() {
        return (Sequence) getProperty(Property.SEQUENCE);
    }

    /**
     * @return the optional status property for a journal entry
     */
    public final Status getStatus() {
        return (Status) getProperty(Property.STATUS);
    }

    /**
     * @return the optional summary property for a journal entry
     */
    public final Summary getSummary() {
        return (Summary) getProperty(Property.SUMMARY);
    }

    /**
     * @return the optional URL property for a journal entry
     */
    public final Url getUrl() {
        return (Url) getProperty(Property.URL);
    }

    /**
     * @return the optional recurrence identifier property for a journal entry
     */
    public final RecurrenceId getRecurrenceId() {
        return (RecurrenceId) getProperty(Property.RECURRENCE_ID);
    }

    /**
     * Returns the UID property of this component if available.
     * @return a Uid instance, or null if no UID property exists
     */
    public final Uid getUid() {
        return (Uid) getProperty(Property.UID);
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

        @Override
        public VJournal createComponent(PropertyList properties, ComponentList subComponents) {
            throw new UnsupportedOperationException(String.format("%s does not support sub-components", VJOURNAL));
        }
    }
}
