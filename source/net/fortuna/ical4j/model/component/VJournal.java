/*
 * $Id$ [Apr 5, 2004]
 *
 * Copyright (c) 2004, Ben Fortuna
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

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.property.DtStamp;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Status;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.util.PropertyValidator;

/**
 * Defines an iCalendar VJOURNAL component.
 *
 * <pre>
 *   4.6.3 Journal Component
 *   
 *      Component Name: VJOURNAL
 *   
 *      Purpose: Provide a grouping of component properties that describe a
 *      journal entry.
 *   
 *      Formal Definition: A "VJOURNAL" calendar component is defined by the
 *      following notation:
 *   
 *        journalc   = "BEGIN" ":" "VJOURNAL" CRLF
 *                     jourprop
 *                     "END" ":" "VJOURNAL" CRLF
 *   
 *        jourprop   = *(
 *   
 *                   ; the following are optional,
 *                   ; but MUST NOT occur more than once
 *   
 *                   class / created / description / dtstart / dtstamp /
 *                   last-mod / organizer / recurid / seq / status /
 *                   summary / uid / url /
 *   
 *                   ; the following are optional,
 *                   ; and MAY occur more than once
 *   
 *                   attach / attendee / categories / comment /
 *                   contact / exdate / exrule / related / rdate /
 *                   rrule / rstatus / x-prop
 *   
 *                   )
 * </pre>
 * 
 * Example 1 - Creating a journal associated with an event:
 * 
 * <pre><code>
 * DtStart meetingDate = (DtStart) meeting.getProperties().getProperty(Property.DTSTART);
 * 
 * VJournal minutes = new VJournal(meetingDate.getTime(), "Progress Meeting - Minutes");
 * 
 * // add timezone information..
 * TzId tzParam = meetingDate.getParameters().getParmaeter(Parameter.TZID);
 * minutes.getProperties().getProperty(Property.DTSTART).getParameters().add(tzParam);
 * 
 * // add description..
 * minutes.getProperties().add(new Description("1. Agenda.., 2. Action Items.."));
 * </code></pre>
 * 
 * @author Ben Fortuna
 */
public class VJournal extends Component {
    
    private static final long serialVersionUID = -7635140949183238830L;

    /**
     * Default constructor.
     */
    public VJournal() {
        super(VJOURNAL);
    }

    /**
     * Constructor.
     *
     * @param properties
     *            a list of properties
     */
    public VJournal(final PropertyList properties) {
        super(VJOURNAL, properties);
    }
    
    /**
     * Constructs a new VJOURNAL instance associated with the specified
     * time with the specified summary.
     * @param start the date the journal entry is associated with
     * @param summary the journal summary
     */
    public VJournal(final Date start, final String summary) {
        this();
        getProperties().add(new DtStamp(new DateTime()));
        getProperties().add(new DtStart(start));
        getProperties().add(new Summary(summary));
    }

    /* (non-Javadoc)
     * @see net.fortuna.ical4j.model.Component#validate(boolean)
     */
    public final void validate(final boolean recurse) throws ValidationException {
        /*
         *  ; the following are optional, ; but MUST NOT occur more than once
         *
         * class / created / description / dtstart / dtstamp / last-mod /
         * organizer / recurid / seq / status / summary / uid / url /
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

        Status status = (Status) getProperties().getProperty(Property.STATUS);
        if (status != null &&
                !Status.VJOURNAL_DRAFT.equals(status) &&
                !Status.VJOURNAL_FINAL.equals(status) &&
                !Status.VJOURNAL_CANCELLED.equals(status)) {
                throw new ValidationException(
                        "Status property [" + status.toString() + "] may not occur in VJOURNAL");
        }

        /*
         * ; the following are optional, ; and MAY occur more than once
         *
         * attach / attendee / categories / comment / contact / exdate / exrule /
         * related / rdate / rrule / rstatus / x-prop
         */

        if (recurse) {
            validateProperties();
        }
    }
    
    /**
     * Returns the UID property of this component if available.
     * @return a Uid instance, or null if no UID property exists
     */
    public final Uid getUid() {
        return (Uid) getProperties().getProperty(Property.UID);
    }
}
