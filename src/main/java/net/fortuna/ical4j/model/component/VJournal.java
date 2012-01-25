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

import java.util.HashMap;
import java.util.Map;

import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.Validator;
import net.fortuna.ical4j.model.property.Clazz;
import net.fortuna.ical4j.model.property.Created;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.DtStamp;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.LastModified;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.RecurrenceId;
import net.fortuna.ical4j.model.property.Sequence;
import net.fortuna.ical4j.model.property.Status;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Url;
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.util.PropertyValidator;

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

    private final Map methodValidators = new HashMap();
    {
        methodValidators.put(Method.ADD, new AddValidator());
        methodValidators.put(Method.CANCEL, new CancelValidator());
        methodValidators.put(Method.PUBLISH, new PublishValidator());
    }
    
    /**
     * Default constructor.
     */
    public VJournal() {
        super(VJOURNAL);
        getProperties().add(new DtStamp());
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
        PropertyValidator.getInstance().assertOneOrLess(Property.CLASS,
                getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.CREATED,
                getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.DESCRIPTION,
                getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.DTSTART,
                getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.DTSTAMP,
                getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.LAST_MODIFIED,
                getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.ORGANIZER,
                getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.RECURRENCE_ID,
                getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.SEQUENCE,
                getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.STATUS,
                getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.SUMMARY,
                getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.UID,
                getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.URL,
                getProperties());

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
        return (Validator) methodValidators.get(method);
    }

    /**
     * <pre>
     * Component/Property  Presence
     * ------------------- ----------------------------------------------
     * METHOD               1      MUST be "ADD"
     * VJOURNAL             1
     *     DESCRIPTION      1      Can be null.
     *     DTSTAMP          1
     *     DTSTART          1
     *     ORGANIZER        1
     *     SEQUENCE         1      MUST be greater than 0
     *     UID              1      MUST match that of the original journal
     * 
     *     ATTACH           0+
     *     CATEGORIES       0 or 1 This property MAY contain a list of values
     *     CLASS            0 or 1
     *     COMMENT          0 or 1
     *     CONTACT          0+
     *     CREATED          0 or 1
     *     EXDATE           0+
     *     EXRULE           0+
     *     LAST-MODIFIED    0 or 1
     *     RDATE            0+
     *     RELATED-TO       0+
     *     RRULE            0+
     *     STATUS           0 or 1  MAY be one of DRAFT/FINAL/CANCELLED
     *     SUMMARY          0 or 1  Can be null
     *     URL              0 or 1
     *     X-PROPERTY       0+
     * 
     *     ATTENDEE         0
     *     RECURRENCE-ID    0
     * 
     * VALARM               0+
     * VTIMEZONE            0 or 1 MUST be present if any date/time refers to
     *                             a timezone
     * X-COMPONENT          0+
     * 
     * VEVENT               0
     * VFREEBUSY            0
     * VTODO                0
     * </pre>
     * 
     */
    private class AddValidator implements Validator {
        
		private static final long serialVersionUID = 1L;
        
        public void validate() throws ValidationException {
            PropertyValidator.getInstance().assertOne(Property.DESCRIPTION, getProperties());
            PropertyValidator.getInstance().assertOne(Property.DTSTAMP, getProperties());
            PropertyValidator.getInstance().assertOne(Property.DTSTART, getProperties());
            PropertyValidator.getInstance().assertOne(Property.ORGANIZER, getProperties());
            PropertyValidator.getInstance().assertOne(Property.SEQUENCE, getProperties());
            PropertyValidator.getInstance().assertOne(Property.UID, getProperties());
            
            PropertyValidator.getInstance().assertOneOrLess(Property.CATEGORIES, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.CLASS, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.COMMENT, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.CREATED, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.LAST_MODIFIED, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.STATUS, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.SUMMARY, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.URL, getProperties());
            
            PropertyValidator.getInstance().assertNone(Property.ATTENDEE, getProperties());
            PropertyValidator.getInstance().assertNone(Property.RECURRENCE_ID, getProperties());
        }
    }
    
    /**
     * <pre>
     * Component/Property   Presence
     * -------------------  ---------------------------------------------
     * METHOD               1       MUST be "CANCEL"
     * VJOURNAL             1+      All MUST have the same UID
     *     DTSTAMP          1
     *     ORGANIZER        1
     *     SEQUENCE         1
     *     UID              1       MUST be the UID of the original REQUEST
     * 
     *     ATTACH           0+
     *     ATTENDEE         0+
     *     CATEGORIES       0 or 1  This property MAY contain a list of values
     *     CLASS            0 or 1
     *     COMMENT          0 or 1
     *     CONTACT          0+
     *     CREATED          0 or 1
     *     DESCRIPTION      0 or 1
     *     DTSTART          0 or 1
     *     EXDATE           0+
     *     EXRULE           0+
     *     LAST-MODIFIED    0 or 1
     *     RDATE            0+
     *     RECURRENCE-ID    0 or 1  only if referring to an instance of a
     *                              recurring calendar component.  Otherwise
     *                              it MUST NOT be present.
     *     RELATED-TO       0+
     *     RRULE            0+
     *     STATUS           0 or 1  MAY be present, must be "CANCELLED" if
     *                              present
     *     SUMMARY          0 or 1
     *     URL              0 or 1
     *     X-PROPERTY       0+
     * 
     *     REQUEST-STATUS   0
     * 
     * VTIMEZONE            0+      MUST be present if any date/time refers to
     *                              a timezone
     * X-COMPONENT          0+
     * VALARM               0
     * VEVENT               0
     * VFREEBUSY            0
     * VTODO                0
     * </pre>
     * 
     */
    private class CancelValidator implements Validator {
        
		private static final long serialVersionUID = 1L;

        public void validate() throws ValidationException {
            PropertyValidator.getInstance().assertOne(Property.DTSTAMP, getProperties());
            PropertyValidator.getInstance().assertOne(Property.ORGANIZER, getProperties());
            PropertyValidator.getInstance().assertOne(Property.SEQUENCE, getProperties());
            PropertyValidator.getInstance().assertOne(Property.UID, getProperties());
            
            PropertyValidator.getInstance().assertOneOrLess(Property.CATEGORIES, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.CLASS, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.COMMENT, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.CREATED, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.DESCRIPTION, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.DTSTART, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.LAST_MODIFIED, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.RECURRENCE_ID, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.STATUS, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.SUMMARY, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.URL, getProperties());
            
            PropertyValidator.getInstance().assertNone(Property.REQUEST_STATUS, getProperties());
        }
    }
    
    /**
     * <pre>
     * Component/Property  Presence
     * ------------------- ----------------------------------------------
     * METHOD               1       MUST be "PUBLISH"
     * VJOURNAL             1+
     *     DESCRIPTION      1       Can be null.
     *     DTSTAMP          1
     *     DTSTART          1
     *     ORGANIZER        1
     *     UID              1
     * 
     *     ATTACH           0+
     *     CATEGORIES       0 or 1  This property MAY contain a list of values
     *     CLASS            0 or 1
     *     COMMENT          0 or 1
     *     CONTACT          0+
     *     CREATED          0 or 1
     *     EXDATE           0+
     *     EXRULE           0+
     *     LAST-MODIFIED    0 or 1
     *     RDATE            0+
     *     RECURRENCE-ID    0 or 1  MUST only if referring to an instance of a
     *                              recurring calendar component.  Otherwise
     *                              it MUST NOT be present.
     *     RELATED-TO       0+
     *     RRULE            0+
     *     SEQUENCE         0 or 1  MUST echo the original SEQUENCE number.
     *                              MUST be present if non-zero. MAY be
     *                              present if zero.
     *     STATUS           0 or 1  MAY be one of DRAFT/FINAL/CANCELLED
     *     SUMMARY          0 or 1  Can be null
     *     URL              0 or 1
     *     X-PROPERTY       0+
     * 
     *     ATTENDEE         0
     * 
     * VALARM               0+
     * VTIMEZONE            0+      MUST be present if any date/time refers to
     *                              a timezone
     * X-COMPONENT          0+
     * 
     * VEVENT               0
     * VFREEBUSY            0
     * VTODO                0
     * </pre>
     * 
     */
    private class PublishValidator implements Validator {
        
		private static final long serialVersionUID = 1L;

        public void validate() throws ValidationException {
            PropertyValidator.getInstance().assertOne(Property.DESCRIPTION, getProperties());
            PropertyValidator.getInstance().assertOne(Property.DTSTAMP, getProperties());
            PropertyValidator.getInstance().assertOne(Property.DTSTART, getProperties());
            PropertyValidator.getInstance().assertOne(Property.ORGANIZER, getProperties());
            PropertyValidator.getInstance().assertOne(Property.UID, getProperties());
            
            PropertyValidator.getInstance().assertOneOrLess(Property.CATEGORIES, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.CLASS, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.COMMENT, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.CREATED, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.LAST_MODIFIED, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.RECURRENCE_ID, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.SEQUENCE, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.STATUS, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.SUMMARY, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.URL, getProperties());
            
            PropertyValidator.getInstance().assertNone(Property.ATTENDEE, getProperties());
        }
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
}
