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

import java.util.Date;

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStamp;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Duration;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.util.PropertyValidator;

/**
 * Defines an iCalendar VEVENT component.
 *
 * <pre>
 *   4.6.1 Event Component
 *   
 *      Component Name: "VEVENT"
 *   
 *      Purpose: Provide a grouping of component properties that describe an
 *      event.
 *   
 *      Format Definition: A "VEVENT" calendar component is defined by the
 *      following notation:
 *   
 *        eventc     = "BEGIN" ":" "VEVENT" CRLF
 *                     eventprop *alarmc
 *                     "END" ":" "VEVENT" CRLF
 *   
 *        eventprop  = *(
 *   
 *                   ; the following are optional,
 *                   ; but MUST NOT occur more than once
 *   
 *                   class / created / description / dtstart / geo /
 *                   last-mod / location / organizer / priority /
 *                   dtstamp / seq / status / summary / transp /
 *                   uid / url / recurid /
 *   
 *                   ; either 'dtend' or 'duration' may appear in
 *                   ; a 'eventprop', but 'dtend' and 'duration'
 *                   ; MUST NOT occur in the same 'eventprop'
 *   
 *                   dtend / duration /
 *   
 *                   ; the following are optional,
 *                   ; and MAY occur more than once
 *   
 *                   attach / attendee / categories / comment /
 *                   contact / exdate / exrule / rstatus / related /
 *                   resources / rdate / rrule / x-prop
 *   
 *                   )
 * </pre>
 * 
 * Example 1 - Creating a new all-day event:
 * 
 * <pre><code>
 * java.util.Calendar cal = java.util.Calendar.getInstance();
 * cal.set(java.util.Calendar.MONTH, java.util.Calendar.DECEMBER);
 * cal.set(java.util.Calendar.DAY_OF_MONTH, 25);
 * 
 * VEvent christmas = new VEvent(cal.getTime(), "Christmas Day");
 * 
 * // initialise as an all-day event..
 * christmas.getProperties().getProperty(Property.DTSTART).getParameters().add(new Value(Value.DATE));
 * 
 * // add timezone information..
 * VTimeZone tz = VTimeZone.getDefault();
 * TzId tzParam = new TzId(tz.getProperties().getProperty(Property.TZID).getValue());
 * christmas.getProperties().getProperty(Property.DTSTART).getParameters().add(tzParam);
 * </code></pre>
 * 
 * Example 2 - Creating an event of one (1) hour duration:
 * 
 * <pre><code>
 * java.util.Calendar cal = java.util.Calendar.getInstance();
 * // tomorrow..
 * cal.add(java.util.Calendar.DAY_OF_MONTH, 1);
 * cal.set(java.util.Calendar.HOUR_OF_DAY, 9);
 * cal.set(java.util.Calendar.MINUTE, 30);
 * 
 * VEvent meeting = new VEvent(cal.getTime(), 1000 * 60 * 60, "Progress Meeting");
 * 
 * // add timezone information..
 * VTimeZone tz = VTimeZone.getDefault();
 * TzId tzParam = new TzId(tz.getProperties().getProperty(Property.TZID).getValue());
 * meeting.getProperties().getProperty(Property.DTSTART).getParameters().add(tzParam);
 * </code></pre>
 * 
 * @author Ben Fortuna
 */
public class VEvent extends Component {

    private ComponentList alarms;

    /**
     * Default constructor.
     */
    public VEvent() {
        super(VEVENT);
        this.alarms = new ComponentList();
    }

    /**
     * Constructor.
     *
     * @param properties
     *            a list of properties
     */
    public VEvent(final PropertyList properties) {
        super(VEVENT, properties);
        this.alarms = new ComponentList();
    }

    /**
     * Constructor.
     *
     * @param properties
     *            a list of properties
     * @param alarms
     *            a list of alarms
     */
    public VEvent(final PropertyList properties, final ComponentList alarms) {
        super(VEVENT, properties);
        this.alarms = alarms;
    }
    
    /**
     * Constructs a new VEVENT instance starting at the specified
     * time with the specified summary.
     * @param start the start date of the new event
     * @param summary the event summary
     */
    public VEvent(final Date start, final String summary) {
        this();
        getProperties().add(new DtStamp(new Date()));
        getProperties().add(new DtStart(start));
        getProperties().add(new Summary(summary));
    }
    
    /**
     * Constructs a new VEVENT instance starting and ending at the specified
     * times with the specified summary.
     * @param start the start date of the new event
     * @param end the end date of the new event
     * @param summary the event summary
     */
    public VEvent(final Date start, final Date end, final String summary) {
        this();
        getProperties().add(new DtStamp(new Date()));
        getProperties().add(new DtStart(start));
        getProperties().add(new DtEnd(end));
        getProperties().add(new Summary(summary));
    }
    
    /**
     * Constructs a new VEVENT instance starting at the specified
     * times, for the specified duration, with the specified summary.
     * @param start the start date of the new event
     * @param duration the duration of the new event
     * @param summary the event summary
     */
    public VEvent(final Date start, final long duration, final String summary) {
        this();
        getProperties().add(new DtStamp(new Date()));
        getProperties().add(new DtStart(start));
        getProperties().add(new Duration(duration));
        getProperties().add(new Summary(summary));
    }

    /**
     * Returns the list of alarms for this event.
     * @return a component list
     */
    public final ComponentList getAlarms() {
		return alarms;
	}

    /**
     * @see java.lang.Object#toString()
     */
    public final String toString() {

        return BEGIN + ":" + getName() + "\r\n" + getProperties() + alarms
                + END + ":" + getName() + "\r\n";
    }

    /**
     * @see net.fortuna.ical4j.model.Component#validate(boolean)
     */
    public final void validate(boolean recurse) throws ValidationException {

        /*
         * ; the following are optional, ; but MUST NOT occur more than once
         *
         * class / created / description / dtstart / geo / last-mod / location /
         * organizer / priority / dtstamp / seq / status / summary / transp /
         * uid / url / recurid /
         */
        PropertyValidator.getInstance().validateOneOrLess(Property.CLASS,
                getProperties());
        PropertyValidator.getInstance().validateOneOrLess(Property.CREATED,
                getProperties());
        PropertyValidator.getInstance().validateOneOrLess(Property.DESCRIPTION,
                getProperties());
        PropertyValidator.getInstance().validateOneOrLess(Property.DTSTART,
                getProperties());
        PropertyValidator.getInstance().validateOneOrLess(Property.GEO,
                getProperties());
        PropertyValidator.getInstance().validateOneOrLess(
                Property.LAST_MODIFIED, getProperties());
        PropertyValidator.getInstance().validateOneOrLess(Property.LOCATION,
                getProperties());
        PropertyValidator.getInstance().validateOneOrLess(Property.ORGANIZER,
                getProperties());
        PropertyValidator.getInstance().validateOneOrLess(Property.PRIORITY,
                getProperties());
        PropertyValidator.getInstance().validateOneOrLess(Property.DTSTAMP,
                getProperties());
        PropertyValidator.getInstance().validateOneOrLess(Property.SEQUENCE,
                getProperties());
        PropertyValidator.getInstance().validateOneOrLess(Property.STATUS,
                getProperties());
        PropertyValidator.getInstance().validateOneOrLess(Property.SUMMARY,
                getProperties());
        PropertyValidator.getInstance().validateOneOrLess(Property.TRANSP,
                getProperties());
        PropertyValidator.getInstance().validateOneOrLess(Property.UID,
                getProperties());
        PropertyValidator.getInstance().validateOneOrLess(Property.URL,
                getProperties());
        PropertyValidator.getInstance().validateOneOrLess(
                Property.RECURRENCE_ID, getProperties());

        /*
         * ; either 'dtend' or 'duration' may appear in ; a 'eventprop', but
         * 'dtend' and 'duration' ; MUST NOT occur in the same 'eventprop'
         *
         * dtend / duration /
         */
        if (getProperties().getProperty(Property.DTEND) != null) {
            if (getProperties().getProperty(Property.DURATION) != null) {
                throw new ValidationException(
                    "Properties [" + Property.DTEND + "," + Property.DURATION
                            + "] may not occur in the same VEVENT");
                }
            
            /*
             *  The "VEVENT" is also the calendar component used to specify an
             *  anniversary or daily reminder within a calendar. These events have a
             *  DATE value type for the "DTSTART" property instead of the default
             *  data type of DATE-TIME. If such a "VEVENT" has a "DTEND" property, it
             *  MUST be specified as a DATE value also. The anniversary type of
             *  "VEVENT" can span more than one date (i.e, "DTEND" property value is
             *  set to a calendar date after the "DTSTART" property value).
             */
            DtStart start = (DtStart) getProperties().getProperty(Property.DTSTART);
            DtEnd end = (DtEnd) getProperties().getProperty(Property.DTEND);
            if (start != null) {
                Parameter value = start.getParameters().getParameter(Parameter.VALUE);
                if (value != null && !value.equals(end.getParameters().getParameter(Parameter.VALUE))) {
                    throw new ValidationException("Property ["
                                + Property.DTEND + "] must have the same ["
                                + Parameter.VALUE + "] as [" + Property.DTSTART + "]");
                }
            }
        }

        /*
         * ; the following are optional, ; and MAY occur more than once
         *
         * attach / attendee / categories / comment / contact / exdate / exrule /
         * rstatus / related / resources / rdate / rrule / x-prop
         */

        if (recurse) {
            validateProperties();
        }
    }
}