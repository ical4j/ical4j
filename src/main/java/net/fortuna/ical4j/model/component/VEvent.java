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

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Dur;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.PeriodList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.Validator;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.Clazz;
import net.fortuna.ical4j.model.property.Created;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStamp;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Duration;
import net.fortuna.ical4j.model.property.Geo;
import net.fortuna.ical4j.model.property.LastModified;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.Priority;
import net.fortuna.ical4j.model.property.RecurrenceId;
import net.fortuna.ical4j.model.property.Sequence;
import net.fortuna.ical4j.model.property.Status;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Transp;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Url;
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.util.ComponentValidator;
import net.fortuna.ical4j.util.Dates;
import net.fortuna.ical4j.util.PropertyValidator;
import net.fortuna.ical4j.util.Strings;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * $Id$ [Apr 5, 2004]
 *
 * Defines an iCalendar VEVENT component.
 * 
 * <pre>
 *       4.6.1 Event Component
 *   
 *          Component Name: &quot;VEVENT&quot;
 *   
 *          Purpose: Provide a grouping of component properties that describe an
 *          event.
 *   
 *          Format Definition: A &quot;VEVENT&quot; calendar component is defined by the
 *          following notation:
 *   
 *            eventc     = &quot;BEGIN&quot; &quot;:&quot; &quot;VEVENT&quot; CRLF
 *                         eventprop *alarmc
 *                         &quot;END&quot; &quot;:&quot; &quot;VEVENT&quot; CRLF
 *   
 *            eventprop  = *(
 *   
 *                       ; the following are optional,
 *                       ; but MUST NOT occur more than once
 *   
 *                       class / created / description / dtstart / geo /
 *                       last-mod / location / organizer / priority /
 *                       dtstamp / seq / status / summary / transp /
 *                       uid / url / recurid /
 *   
 *                       ; either 'dtend' or 'duration' may appear in
 *                       ; a 'eventprop', but 'dtend' and 'duration'
 *                       ; MUST NOT occur in the same 'eventprop'
 *   
 *                       dtend / duration /
 *   
 *                       ; the following are optional,
 *                       ; and MAY occur more than once
 *   
 *                       attach / attendee / categories / comment /
 *                       contact / exdate / exrule / rstatus / related /
 *                       resources / rdate / rrule / x-prop
 *   
 *                       )
 * </pre>
 * 
 * Example 1 - Creating a new all-day event:
 * 
 * <pre><code>
 * java.util.Calendar cal = java.util.Calendar.getInstance();
 * cal.set(java.util.Calendar.MONTH, java.util.Calendar.DECEMBER);
 * cal.set(java.util.Calendar.DAY_OF_MONTH, 25);
 * 
 * VEvent christmas = new VEvent(cal.getTime(), &quot;Christmas Day&quot;);
 * 
 * // initialise as an all-day event..
 * christmas.getProperties().getProperty(Property.DTSTART).getParameters().add(
 *         Value.DATE);
 * 
 * // add timezone information..
 * VTimeZone tz = VTimeZone.getDefault();
 * TzId tzParam = new TzId(tz.getProperties().getProperty(Property.TZID)
 *         .getValue());
 * christmas.getProperties().getProperty(Property.DTSTART).getParameters().add(
 *         tzParam);
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
 * VEvent meeting = new VEvent(cal.getTime(), 1000 * 60 * 60, &quot;Progress Meeting&quot;);
 * 
 * // add timezone information..
 * VTimeZone tz = VTimeZone.getDefault();
 * TzId tzParam = new TzId(tz.getProperties().getProperty(Property.TZID)
 *         .getValue());
 * meeting.getProperties().getProperty(Property.DTSTART).getParameters().add(
 *         tzParam);
 * </code></pre>
 * 
 * Example 3 - Retrieve a list of periods representing a recurring event in a specified range:
 * 
 * <pre><code>
 * Calendar weekday9AM = Calendar.getInstance();
 * weekday9AM.set(2005, Calendar.MARCH, 7, 9, 0, 0);
 * weekday9AM.set(Calendar.MILLISECOND, 0);
 * 
 * Calendar weekday5PM = Calendar.getInstance();
 * weekday5PM.set(2005, Calendar.MARCH, 7, 17, 0, 0);
 * weekday5PM.set(Calendar.MILLISECOND, 0);
 * 
 * // Do the recurrence until December 31st.
 * Calendar untilCal = Calendar.getInstance();
 * untilCal.set(2005, Calendar.DECEMBER, 31);
 * untilCal.set(Calendar.MILLISECOND, 0);
 * 
 * // 9:00AM to 5:00PM Rule
 * Recur recur = new Recur(Recur.WEEKLY, untilCal.getTime());
 * recur.getDayList().add(WeekDay.MO);
 * recur.getDayList().add(WeekDay.TU);
 * recur.getDayList().add(WeekDay.WE);
 * recur.getDayList().add(WeekDay.TH);
 * recur.getDayList().add(WeekDay.FR);
 * recur.setInterval(3);
 * recur.setWeekStartDay(WeekDay.MO.getDay());
 * RRule rrule = new RRule(recur);
 * 
 * Summary summary = new Summary(&quot;TEST EVENTS THAT HAPPEN 9-5 MON-FRI&quot;);
 * 
 * weekdayNineToFiveEvents = new VEvent();
 * weekdayNineToFiveEvents.getProperties().add(rrule);
 * weekdayNineToFiveEvents.getProperties().add(summary);
 * weekdayNineToFiveEvents.getProperties().add(new DtStart(weekday9AM.getTime()));
 * weekdayNineToFiveEvents.getProperties().add(new DtEnd(weekday5PM.getTime()));
 * 
 * // Test Start 04/01/2005, End One month later.
 * // Query Calendar Start and End Dates.
 * Calendar queryStartDate = Calendar.getInstance();
 * queryStartDate.set(2005, Calendar.APRIL, 1, 14, 47, 0);
 * queryStartDate.set(Calendar.MILLISECOND, 0);
 * Calendar queryEndDate = Calendar.getInstance();
 * queryEndDate.set(2005, Calendar.MAY, 1, 11, 15, 0);
 * queryEndDate.set(Calendar.MILLISECOND, 0);
 * 
 * // This range is monday to friday every three weeks, starting from
 * // March 7th 2005, which means for our query dates we need
 * // April 18th through to the 22nd.
 * PeriodList periods = weekdayNineToFiveEvents.getPeriods(queryStartDate
 *         .getTime(), queryEndDate.getTime());
 * </code></pre>
 * 
 * @author Ben Fortuna
 */
public class VEvent extends CalendarComponent {

    private static final long serialVersionUID = 2547948989200697335L;

    private final Map methodValidators = new HashMap();
    {
        methodValidators.put(Method.ADD, new AddValidator());
        methodValidators.put(Method.CANCEL, new CancelValidator());
        methodValidators.put(Method.COUNTER, new CounterValidator());
        methodValidators.put(Method.DECLINE_COUNTER, new DeclineCounterValidator());
        methodValidators.put(Method.PUBLISH, new PublishValidator());
        methodValidators.put(Method.REFRESH, new RefreshValidator());
        methodValidators.put(Method.REPLY, new ReplyValidator());
        methodValidators.put(Method.REQUEST, new RequestValidator());
    }
    
    private ComponentList alarms;

    /**
     * Default constructor.
     */
    public VEvent() {
        super(VEVENT);
        this.alarms = new ComponentList();
        getProperties().add(new DtStamp());
    }

    /**
     * Constructor.
     * @param properties a list of properties
     */
    public VEvent(final PropertyList properties) {
        super(VEVENT, properties);
        this.alarms = new ComponentList();
    }

    /**
     * Constructor.
     * @param properties a list of properties
     * @param alarms a list of alarms
     */
    public VEvent(final PropertyList properties, final ComponentList alarms) {
        super(VEVENT, properties);
        this.alarms = alarms;
    }

    /**
     * Constructs a new VEVENT instance starting at the specified time with the specified summary.
     * @param start the start date of the new event
     * @param summary the event summary
     */
    public VEvent(final Date start, final String summary) {
        this();
        getProperties().add(new DtStart(start));
        getProperties().add(new Summary(summary));
    }

    /**
     * Constructs a new VEVENT instance starting and ending at the specified times with the specified summary.
     * @param start the start date of the new event
     * @param end the end date of the new event
     * @param summary the event summary
     */
    public VEvent(final Date start, final Date end, final String summary) {
        this();
        getProperties().add(new DtStart(start));
        getProperties().add(new DtEnd(end));
        getProperties().add(new Summary(summary));
    }

    /**
     * Constructs a new VEVENT instance starting at the specified times, for the specified duration, with the specified
     * summary.
     * @param start the start date of the new event
     * @param duration the duration of the new event
     * @param summary the event summary
     */
    public VEvent(final Date start, final Dur duration, final String summary) {
        this();
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
     * {@inheritDoc}
     */
    public final String toString() {
        final StringBuffer b = new StringBuffer();
        b.append(BEGIN);
        b.append(':');
        b.append(getName());
        b.append(Strings.LINE_SEPARATOR);
        b.append(getProperties());
        b.append(getAlarms());
        b.append(END);
        b.append(':');
        b.append(getName());
        b.append(Strings.LINE_SEPARATOR);
        return b.toString();
    }

    /**
     * {@inheritDoc}
     */
    public final void validate(final boolean recurse) throws ValidationException {

        // validate that getAlarms() only contains VAlarm components
        final Iterator iterator = getAlarms().iterator();
        while (iterator.hasNext()) {
            final Component component = (Component) iterator.next();

            if (!(component instanceof VAlarm)) {
                throw new ValidationException("Component ["
                        + component.getName() + "] may not occur in VEVENT");
            }
            
            ((VAlarm) component).validate(recurse);
        }

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
         * geo / last-mod / location / organizer / priority / dtstamp / seq / status / summary / transp / uid / url /
         * recurid /
         */
        PropertyValidator.getInstance().assertOneOrLess(Property.CLASS,
                getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.CREATED,
                getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.DESCRIPTION,
                getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.DTSTART,
                getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.GEO,
                getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.LAST_MODIFIED,
                getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.LOCATION,
                getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.ORGANIZER,
                getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.PRIORITY,
                getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.DTSTAMP,
                getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.SEQUENCE,
                getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.STATUS,
                getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.SUMMARY,
                getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.TRANSP,
                getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.UID,
                getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.URL,
                getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.RECURRENCE_ID,
                getProperties());

        final Status status = (Status) getProperty(Property.STATUS);
        if (status != null && !Status.VEVENT_TENTATIVE.getValue().equals(status.getValue())
                && !Status.VEVENT_CONFIRMED.getValue().equals(status.getValue())
                && !Status.VEVENT_CANCELLED.getValue().equals(status.getValue())) {
            throw new ValidationException("Status property ["
                    + status.toString() + "] is not applicable for VEVENT");
        }

        /*
         * ; either 'dtend' or 'duration' may appear in ; a 'eventprop', but 'dtend' and 'duration' ; MUST NOT occur in
         * the same 'eventprop' dtend / duration /
         */
        try {
            PropertyValidator.getInstance().assertNone(Property.DTEND,
                    getProperties());
        }
        catch (ValidationException ve) {
            PropertyValidator.getInstance().assertNone(Property.DURATION,
                    getProperties());
        }

        if (getProperty(Property.DTEND) != null) {

            /*
             * The "VEVENT" is also the calendar component used to specify an anniversary or daily reminder within a
             * calendar. These events have a DATE value type for the "DTSTART" property instead of the default data type
             * of DATE-TIME. If such a "VEVENT" has a "DTEND" property, it MUST be specified as a DATE value also. The
             * anniversary type of "VEVENT" can span more than one date (i.e, "DTEND" property value is set to a
             * calendar date after the "DTSTART" property value).
             */
            final DtStart start = (DtStart) getProperty(Property.DTSTART);
            final DtEnd end = (DtEnd) getProperty(Property.DTEND);

            if (start != null) {
                final Parameter startValue = start.getParameter(Parameter.VALUE);
                final Parameter endValue = end.getParameter(Parameter.VALUE);
                
                boolean startEndValueMismatch = false;
                if (endValue != null) {
                    if (startValue != null && !endValue.equals(startValue)) {
                        // invalid..
                        startEndValueMismatch = true;
                    }
                    else if (startValue == null && !Value.DATE_TIME.equals(endValue)) {
                        // invalid..
                        startEndValueMismatch = true;
                    }
                }
                else if (startValue != null && !Value.DATE_TIME.equals(startValue)) {
                    //invalid..
                    startEndValueMismatch = true;
                }
                if (startEndValueMismatch) {
                    throw new ValidationException("Property [" + Property.DTEND
                            + "] must have the same [" + Parameter.VALUE
                            + "] as [" + Property.DTSTART + "]");
                }
            }
        }

        /*
         * ; the following are optional, ; and MAY occur more than once attach / attendee / categories / comment /
         * contact / exdate / exrule / rstatus / related / resources / rdate / rrule / x-prop
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
     * METHOD:ADD Validator.
     * 
     * <pre>
     * Component/Property  Presence
     * ------------------- ----------------------------------------------
     * METHOD              1      MUST be "ADD"
     * VEVENT              1
     *     DTSTAMP         1
     *     DTSTART         1
     *     ORGANIZER       1
     *     SEQUENCE        1      MUST be greater than 0
     *     SUMMARY         1      Can be null
     *     UID             1      MUST match that of the original event
     * 
     *     ATTACH          0+
     *     ATTENDEE        0+
     *     CATEGORIES      0 or 1 This property MAY contain a list of values
     *     CLASS           0 or 1
     *     COMMENT         0 or 1
     *     CONTACT         0+
     *     CREATED         0 or 1
     *     DESCRIPTION     0 or 1  Can be null
     *     DTEND           0 or 1  if present DURATION MUST NOT be present
     *     DURATION        0 or 1  if present DTEND MUST NOT be present
     *     EXDATE          0+
     *     EXRULE          0+
     *     GEO             0 or 1
     *     LAST-MODIFIED   0 or 1
     *     LOCATION        0 or 1
     *     PRIORITY        0 or 1
     *     RDATE           0+
     *     RELATED-TO      0+
     *     RESOURCES       0 or 1  This property MAY contain a list of values
     *     RRULE           0+
     *     STATUS          0 or 1  MAY be one of TENTATIVE/CONFIRMED
     *     TRANSP          0 or 1
     *     URL             0 or 1
     *     X-PROPERTY      0+
     * 
     *     RECURRENCE-ID   0
     *     REQUEST-STATUS  0
     * 
     * VALARM              0+
     * VTIMEZONE           0+     MUST be present if any date/time refers to
     *                            a timezone
     * X-COMPONENT         0+
     * 
     * VFREEBUSY           0
     * VTODO               0
     * VJOURNAL            0
     * </pre>
     * 
     */
    private class AddValidator implements Validator {
        
		private static final long serialVersionUID = 1L;

		public void validate() throws ValidationException {
            PropertyValidator.getInstance().assertOne(Property.DTSTAMP, getProperties());
            PropertyValidator.getInstance().assertOne(Property.DTSTART, getProperties());
            PropertyValidator.getInstance().assertOne(Property.ORGANIZER, getProperties());
            PropertyValidator.getInstance().assertOne(Property.SEQUENCE, getProperties());
            PropertyValidator.getInstance().assertOne(Property.SUMMARY, getProperties());
            PropertyValidator.getInstance().assertOne(Property.UID, getProperties());
            
            PropertyValidator.getInstance().assertOneOrLess(Property.CATEGORIES, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.CLASS, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.COMMENT, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.CREATED, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.DESCRIPTION, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.DTEND, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.DURATION, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.GEO, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.LAST_MODIFIED, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.LOCATION, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.PRIORITY, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.RESOURCES, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.STATUS, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.TRANSP, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.URL, getProperties());
            
            PropertyValidator.getInstance().assertNone(Property.RECURRENCE_ID, getProperties());
            PropertyValidator.getInstance().assertNone(Property.REQUEST_STATUS, getProperties());
            
            for (final Iterator i = getAlarms().iterator(); i.hasNext();) {
                final VAlarm alarm = (VAlarm) i.next();
                alarm.validate(Method.ADD);
            }
        }
    }
    
    /**
     * METHOD:CANCEL Validator.
     * 
     * <pre>
     * Component/Property  Presence
     * ------------------- ----------------------------------------------
     * METHOD              1      MUST be "CANCEL"
     * 
     * VEVENT              1+     All must have the same UID
     *     ATTENDEE        0+     MUST include all "Attendees" being removed
     *                            the event. MUST include all "Attendees" if
     *                            the entire event is cancelled.
     *     DTSTAMP         1
     *     ORGANIZER       1
     *     SEQUENCE        1
     *     UID             1       MUST be the UID of the original REQUEST
     * 
     *     COMMENT         0 or 1
     *     ATTACH          0+
     *     CATEGORIES      0 or 1  This property may contain a list of values
     *     CLASS           0 or 1
     *     CONTACT         0+
     *     CREATED         0 or 1
     *     DESCRIPTION     0 or 1
     *     DTEND           0 or 1 if present DURATION MUST NOT be present
     *     DTSTART         0 or 1
     *     DURATION        0 or 1 if present DTEND MUST NOT be present
     *     EXDATE          0+
     *     EXRULE          0+
     *     GEO             0 or 1
     *     LAST-MODIFIED   0 or 1
     *     LOCATION        0 or 1
     *     PRIORITY        0 or 1
     *     RDATE           0+
     *     RECURRENCE-ID   0 or 1  MUST be present if referring to one or
     *                             more or more recurring instances.
     *                             Otherwise it MUST NOT be present
     *     RELATED-TO      0+
     *     RESOURCES       0 or 1
     *     RRULE           0+
     *     STATUS          0 or 1  MUST be set to CANCELLED. If uninviting
     *                             specific "Attendees" then MUST NOT be
     *                             included.
     *     SUMMARY         0 or 1
     *     TRANSP          0 or 1
     *     URL             0 or 1
     *     X-PROPERTY      0+
     *     REQUEST-STATUS  0
     * 
     * VTIMEZONE           0+     MUST be present if any date/time refers to
     *                            a timezone
     * X-COMPONENT         0+
     * 
     * VTODO               0
     * VJOURNAL            0
     * VFREEBUSY           0
     * VALARM              0
     * </pre>
     * 
     */
    private class CancelValidator implements Validator {
        
		private static final long serialVersionUID = 1L;

        public final void validate() throws ValidationException {
            PropertyValidator.getInstance().assertOne(Property.DTSTAMP, getProperties());
            PropertyValidator.getInstance().assertOne(Property.DTSTART, getProperties());
            PropertyValidator.getInstance().assertOne(Property.ORGANIZER, getProperties());
            PropertyValidator.getInstance().assertOne(Property.SEQUENCE, getProperties());
            PropertyValidator.getInstance().assertOne(Property.UID, getProperties());
            
            PropertyValidator.getInstance().assertOneOrLess(Property.COMMENT, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.CATEGORIES, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.CLASS, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.CREATED, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.DESCRIPTION, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.DTEND, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.DTSTART, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.DURATION, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.GEO, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.LAST_MODIFIED, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.LOCATION, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.PRIORITY, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.RECURRENCE_ID, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.RESOURCES, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.STATUS, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.SUMMARY, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.TRANSP, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.URL, getProperties());
            
            PropertyValidator.getInstance().assertNone(Property.REQUEST_STATUS, getProperties());
            
            ComponentValidator.assertNone(Component.VALARM, getAlarms());
        }
    }
    
    /**
     * METHOD:COUNTER Validator.
     * 
     * <pre>
     * Component/Property  Presence
     * ------------------- ----------------------------------------------
     * METHOD              1      MUST be "COUNTER"
     * 
     * VEVENT              1
     *     DTSTAMP         1
     *     DTSTART         1
     *     ORGANIZER       1       MUST be the "Organizer" of the original
     *                             event
     *     SEQUENCE        1       MUST be present if value is greater than 0,
     *                             MAY be present if 0
     *     SUMMARY         1       Can be null
     *     UID             1       MUST be the UID associated with the REQUEST
     *                             being countered
     * 
     *     ATTACH          0+
     *     ATTENDEE        0+      Can also  be used to propose other
     *                             "Attendees"
     *     CATEGORIES      0 or 1  This property may contain a list of values
     *     CLASS           0 or 1
     *     COMMENT         0 or 1
     *     CONTACT         0+
     *     CREATED         0 or 1
     *     DESCRIPTION     0 or 1
     *     DTEND           0 or 1  if present DURATION MUST NOT be present
     *     DURATION        0 or 1  if present DTEND MUST NOT be present
     *     EXDATE          0+
     *     EXRULE          0+
     *     GEO             0 or 1
     *     LAST-MODIFIED   0 or 1
     *     LOCATION        0 or 1
     *     PRIORITY        0 or 1
     *     RDATE           0+
     *     RECURRENCE-ID   0 or 1  MUST only if referring to an instance of a
     *                             recurring calendar component.  Otherwise it
     *                             MUST NOT be present.
     *     RELATED-TO      0+
     *     REQUEST-STATUS  0+
     *     RESOURCES       0 or 1  This property may contain a list of values
     *     RRULE           0+
     *     STATUS          0 or 1  Value must be one of CONFIRMED/TENATIVE/
     *                             CANCELLED
     *     TRANSP          0 or 1
     *     URL             0 or 1
     *     X-PROPERTY      0+
     * 
     * VALARM              0+
     * VTIMEZONE           0+      MUST be present if any date/time refers to
     *                             a timezone
     * X-COMPONENT         0+
     * 
     * VTODO               0
     * VJOURNAL            0
     * VFREEBUSY           0
     * </pre>
     * 
     */
    private class CounterValidator implements Validator {
        
		private static final long serialVersionUID = 1L;

        public void validate() throws ValidationException {
            PropertyValidator.getInstance().assertOne(Property.DTSTAMP, getProperties());
            PropertyValidator.getInstance().assertOne(Property.DTSTART, getProperties());
            
            if (!CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION)) {
                PropertyValidator.getInstance().assertOne(Property.ORGANIZER, getProperties());
            }
            
            PropertyValidator.getInstance().assertOne(Property.SEQUENCE, getProperties());
            PropertyValidator.getInstance().assertOne(Property.SUMMARY, getProperties());
            PropertyValidator.getInstance().assertOne(Property.UID, getProperties());
            
            PropertyValidator.getInstance().assertOneOrLess(Property.CATEGORIES, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.CLASS, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.COMMENT, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.CREATED, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.DESCRIPTION, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.DTEND, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.DURATION, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.GEO, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.LAST_MODIFIED, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.LOCATION, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.PRIORITY, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.RECURRENCE_ID, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.RESOURCES, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.STATUS, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.TRANSP, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.URL, getProperties());
            
            for (final Iterator i = getAlarms().iterator(); i.hasNext();) {
                final VAlarm alarm = (VAlarm) i.next();
                alarm.validate(Method.COUNTER);
            }
        }
    }
    
    /**
     * METHOD:DECLINECOUNTER Validator.
     * 
     * <pre>
     * Component/Property  Presence
     * ------------------- ----------------------------------------------
     * METHOD              1      MUST be "DECLINECOUNTER"
     * 
     * VEVENT              1
     *     DTSTAMP         1
     *     ORGANIZER       1
     *     UID             1       MUST, same UID specified in original
     *                             REQUEST and subsequent COUNTER
     *     COMMENT         0 or 1
     *     RECURRENCE-ID   0 or 1  MUST only if referring to an instance of a
     *                             recurring calendar component.  Otherwise it
     *                             MUST NOT be present.
     *     REQUEST-STATUS  0+
     *     SEQUENCE        0 OR 1  MUST be present if value is greater than 0,
     *                             MAY be present if 0
     *     X-PROPERTY      0+
     *     ATTACH          0
     *     ATTENDEE        0
     *     CATEGORIES      0
     *     CLASS           0
     *     CONTACT         0
     *     CREATED         0
     *     DESCRIPTION     0
     *     DTEND           0
     *     DTSTART         0
     *     DURATION        0
     *     EXDATE          0
     *     EXRULE          0
     *     GEO             0
     *     LAST-MODIFIED   0
     *     LOCATION        0
     *     PRIORITY        0
     *     RDATE           0
     *     RELATED-TO      0
     *     RESOURCES       0
     *     RRULE           0
     *     STATUS          0
     *     SUMMARY         0
     *     TRANSP          0
     *     URL             0
     * 
     * X-COMPONENT         0+
     * VTODO               0
     * VJOURNAL            0
     * VFREEBUSY           0
     * VTIMEZONE           0
     * VALARM              0
     * </pre>
     * 
     */
    private class DeclineCounterValidator implements Validator {
        
		private static final long serialVersionUID = 1L;

        public void validate() throws ValidationException {
            PropertyValidator.getInstance().assertOne(Property.DTSTAMP, getProperties());
            PropertyValidator.getInstance().assertOne(Property.ORGANIZER, getProperties());
            PropertyValidator.getInstance().assertOne(Property.UID, getProperties());
            
            PropertyValidator.getInstance().assertOneOrLess(Property.COMMENT, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.RECURRENCE_ID, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.SEQUENCE, getProperties());
            
            PropertyValidator.getInstance().assertNone(Property.ATTACH, getProperties());
            PropertyValidator.getInstance().assertNone(Property.ATTENDEE, getProperties());
            PropertyValidator.getInstance().assertNone(Property.CATEGORIES, getProperties());
            PropertyValidator.getInstance().assertNone(Property.CLASS, getProperties());
            PropertyValidator.getInstance().assertNone(Property.CONTACT, getProperties());
            PropertyValidator.getInstance().assertNone(Property.CREATED, getProperties());
            PropertyValidator.getInstance().assertNone(Property.DESCRIPTION, getProperties());
            PropertyValidator.getInstance().assertNone(Property.DTEND, getProperties());
            PropertyValidator.getInstance().assertNone(Property.DTSTART, getProperties());
            PropertyValidator.getInstance().assertNone(Property.DURATION, getProperties());
            PropertyValidator.getInstance().assertNone(Property.EXDATE, getProperties());
            PropertyValidator.getInstance().assertNone(Property.EXRULE, getProperties());
            PropertyValidator.getInstance().assertNone(Property.GEO, getProperties());
            PropertyValidator.getInstance().assertNone(Property.LAST_MODIFIED, getProperties());
            PropertyValidator.getInstance().assertNone(Property.LOCATION, getProperties());
            PropertyValidator.getInstance().assertNone(Property.PRIORITY, getProperties());
            PropertyValidator.getInstance().assertNone(Property.RDATE, getProperties());
            PropertyValidator.getInstance().assertNone(Property.RELATED_TO, getProperties());
            PropertyValidator.getInstance().assertNone(Property.RESOURCES, getProperties());
            PropertyValidator.getInstance().assertNone(Property.RRULE, getProperties());
            PropertyValidator.getInstance().assertNone(Property.STATUS, getProperties());
            PropertyValidator.getInstance().assertNone(Property.SUMMARY, getProperties());
            PropertyValidator.getInstance().assertNone(Property.TRANSP, getProperties());
            PropertyValidator.getInstance().assertNone(Property.URL, getProperties());
            
            ComponentValidator.assertNone(Component.VALARM, getAlarms());
        }
    }
    
    /**
     * METHOD:PUBLISH Validator.
     * 
     * <pre>
     * Component/Property  Presence
     * ------------------- ----------------------------------------------
     * METHOD              1       MUST equal "PUBLISH"
     * VEVENT              1+
     *      DTSTAMP        1
     *      DTSTART        1
     *      ORGANIZER      1
     *      SUMMARY        1       Can be null.
     *      UID            1
     *      RECURRENCE-ID  0 or 1  only if referring to an instance of a
     *                             recurring calendar component.  Otherwise
     *                             it MUST NOT be present.
     *      SEQUENCE       0 or 1  MUST be present if value is greater than
     *                             0, MAY be present if 0
     *      ATTACH         0+
     *      CATEGORIES     0 or 1  This property may contain a list of
     *                             values
     *      CLASS          0 or 1
     *      COMMENT        0 or 1
     *      CONTACT        0+
     *      CREATED        0 or 1
     *      DESCRIPTION    0 or 1  Can be null
     *      DTEND          0 or 1  if present DURATION MUST NOT be present
     *      DURATION       0 or 1  if present DTEND MUST NOT be present
     *      EXDATE         0+
     *      EXRULE         0+
     *      GEO            0 or 1
     *      LAST-MODIFIED  0 or 1
     *      LOCATION       0 or 1
     *      PRIORITY       0 or 1
     *      RDATE          0+
     *      RELATED-TO     0+
     *      RESOURCES      0 or 1 This property MAY contain a list of values
     *      RRULE          0+
     *      STATUS         0 or 1 MAY be one of TENTATIVE/CONFIRMED/CANCELLED
     *      TRANSP         0 or 1
     *      URL            0 or 1
     *      X-PROPERTY     0+
     * 
     *      ATTENDEE       0
     *      REQUEST-STATUS 0
     * 
     * VALARM              0+
     * VFREEBUSY           0
     * VJOURNAL            0
     * VTODO               0
     * VTIMEZONE           0+    MUST be present if any date/time refers to
     *                           a timezone
     * X-COMPONENT         0+
     * </pre>
     * 
     */
    private class PublishValidator implements Validator {
        
		private static final long serialVersionUID = 1L;

        public void validate() throws ValidationException {
            PropertyValidator.getInstance().assertOne(Property.DTSTAMP, getProperties());
            PropertyValidator.getInstance().assertOne(Property.DTSTART, getProperties());
            
            if (!CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION)) {
                PropertyValidator.getInstance().assertOne(Property.ORGANIZER, getProperties());
                PropertyValidator.getInstance().assertOne(Property.SUMMARY, getProperties());
            }
            
            PropertyValidator.getInstance().assertOne(Property.UID, getProperties());
            
            PropertyValidator.getInstance().assertOneOrLess(Property.RECURRENCE_ID, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.SEQUENCE, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.CATEGORIES, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.CLASS, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.COMMENT, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.CREATED, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.DESCRIPTION, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.DTEND, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.DURATION, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.GEO, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.LAST_MODIFIED, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.LOCATION, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.PRIORITY, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.RESOURCES, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.STATUS, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.TRANSP, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.URL, getProperties());
            
            if (!CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION)) {
                PropertyValidator.getInstance().assertNone(Property.ATTENDEE, getProperties());
            }
            
            PropertyValidator.getInstance().assertNone(Property.REQUEST_STATUS, getProperties());
            
            for (final Iterator i = getAlarms().iterator(); i.hasNext();) {
                final VAlarm alarm = (VAlarm) i.next();
                alarm.validate(Method.PUBLISH);
            }
        }
    }
    
    /**
     * METHOD:REFRESH Validator.
     * 
     * <pre>
     * Component/Property  Presence
     * ------------------- ----------------------------------------------
     * METHOD              1      MUST be "REFRESH"
     * 
     * VEVENT              1
     *     ATTENDEE        1      MUST be the address of requestor
     *     DTSTAMP         1
     *     ORGANIZER       1
     *     UID             1      MUST be the UID associated with original
     *                            REQUEST
     *     COMMENT         0 or 1
     *     RECURRENCE-ID   0 or 1 MUST only if referring to an instance of a
     *                            recurring calendar component.  Otherwise
     *                            it must NOT be present.
     *     X-PROPERTY      0+
     * 
     *     ATTACH          0
     *     CATEGORIES      0
     *     CLASS           0
     *     CONTACT         0
     *     CREATED         0
     *     DESCRIPTION     0
     *     DTEND           0
     *     DTSTART         0
     *     DURATION        0
     *     EXDATE          0
     *     EXRULE          0
     *     GEO             0
     *     LAST-MODIFIED   0
     *     LOCATION        0
     *     PRIORITY        0
     *     RDATE           0
     *     RELATED-TO      0
     *     REQUEST-STATUS  0
     *     RESOURCES       0
     *     RRULE           0
     *     SEQUENCE        0
     *     STATUS          0
     *     SUMMARY         0
     *     TRANSP          0
     *     URL             0
     * 
     * X-COMPONENT         0+
     * 
     * VTODO               0
     * VJOURNAL            0
     * VFREEBUSY           0
     * VTIMEZONE           0
     * VALARM              0
     * </pre>
     * 
     */
    private class RefreshValidator implements Validator {
        
		private static final long serialVersionUID = 1L;

        public void validate() throws ValidationException {
            PropertyValidator.getInstance().assertOne(Property.ATTENDEE, getProperties());
            PropertyValidator.getInstance().assertOne(Property.DTSTAMP, getProperties());
            PropertyValidator.getInstance().assertOne(Property.ORGANIZER, getProperties());
            PropertyValidator.getInstance().assertOne(Property.UID, getProperties());
            
            PropertyValidator.getInstance().assertOneOrLess(Property.COMMENT, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.RECURRENCE_ID, getProperties());
            
            PropertyValidator.getInstance().assertNone(Property.ATTACH, getProperties());
            PropertyValidator.getInstance().assertNone(Property.CATEGORIES, getProperties());
            PropertyValidator.getInstance().assertNone(Property.CLASS, getProperties());
            PropertyValidator.getInstance().assertNone(Property.CONTACT, getProperties());
            PropertyValidator.getInstance().assertNone(Property.CREATED, getProperties());
            PropertyValidator.getInstance().assertNone(Property.DESCRIPTION, getProperties());
            PropertyValidator.getInstance().assertNone(Property.DTEND, getProperties());
            PropertyValidator.getInstance().assertNone(Property.DTSTART, getProperties());
            PropertyValidator.getInstance().assertNone(Property.DURATION, getProperties());
            PropertyValidator.getInstance().assertNone(Property.EXDATE, getProperties());
            PropertyValidator.getInstance().assertNone(Property.EXRULE, getProperties());
            PropertyValidator.getInstance().assertNone(Property.GEO, getProperties());
            PropertyValidator.getInstance().assertNone(Property.LAST_MODIFIED, getProperties());
            PropertyValidator.getInstance().assertNone(Property.LOCATION, getProperties());
            PropertyValidator.getInstance().assertNone(Property.PRIORITY, getProperties());
            PropertyValidator.getInstance().assertNone(Property.RDATE, getProperties());
            PropertyValidator.getInstance().assertNone(Property.RELATED_TO, getProperties());
            PropertyValidator.getInstance().assertNone(Property.REQUEST_STATUS, getProperties());
            PropertyValidator.getInstance().assertNone(Property.RESOURCES, getProperties());
            PropertyValidator.getInstance().assertNone(Property.RRULE, getProperties());
            PropertyValidator.getInstance().assertNone(Property.SEQUENCE, getProperties());
            PropertyValidator.getInstance().assertNone(Property.STATUS, getProperties());
            PropertyValidator.getInstance().assertNone(Property.SUMMARY, getProperties());
            PropertyValidator.getInstance().assertNone(Property.TRANSP, getProperties());
            PropertyValidator.getInstance().assertNone(Property.URL, getProperties());
            
            ComponentValidator.assertNone(Component.VALARM, getAlarms());
        }
    }
    
    /**
     * METHOD:REPLY Validator.
     * 
     * <pre>
     * Component/Property  Presence
     * ------------------- ----------------------------------------------
     * METHOD              1       MUST be "REPLY"
     * VEVENT              1+      All components MUST have the same UID
     *     ATTENDEE        1       MUST be the address of the Attendee
     *                             replying.
     *     DTSTAMP         1
     *     ORGANIZER       1
     *     RECURRENCE-ID   0 or 1  only if referring to an instance of a
     *                             recurring calendar component.  Otherwise
     *                             it must NOT be present.
     *     UID             1       MUST be the UID of the original REQUEST
     *     
     *     SEQUENCE        0 or 1  MUST if non-zero, MUST be the sequence
     *                             number of the original REQUEST. MAY be
     *                             present if 0.
     *     
     *     ATTACH          0+
     *     CATEGORIES      0 or 1  This property may contain a list of values
     *     CLASS           0 or 1
     *     COMMENT         0 or 1
     *     CONTACT         0+
     *     CREATED         0 or 1
     *     DESCRIPTION     0 or 1
     *     DTEND           0 or 1  if present DURATION MUST NOT be present
     *     DTSTART         0 or 1
     *     DURATION        0 or 1  if present DTEND MUST NOT be present
     *     EXDATE          0+
     *     EXRULE          0+
     *     GEO             0 or 1
     *     LAST-MODIFIED   0 or 1
     *     LOCATION        0 or 1
     *     PRIORITY        0 or 1
     *     RDATE           0+
     *     RELATED-TO      0+
     *     RESOURCES       0 or 1  This property MAY contain a list of values
     *     REQUEST-STATUS  0+
     *     RRULE           0+
     *     STATUS          0 or 1
     *     SUMMARY         0 or 1
     *     TRANSP          0 or 1
     *     URL             0 or 1
     *     X-PROPERTY      0+
     *     
     * VTIMEZONE           0 or 1 MUST be present if any date/time refers
     *                            to a timezone
     * X-COMPONENT         0+
     * 
     * VALARM              0
     * VFREEBUSY           0
     * VJOURNAL            0
     * VTODO               0
     * </pre>
     * 
     */
    private class ReplyValidator implements Validator {
        
		private static final long serialVersionUID = 1L;

        public void validate() throws ValidationException {
            PropertyValidator.getInstance().assertOne(Property.ATTENDEE, getProperties());
            PropertyValidator.getInstance().assertOne(Property.DTSTAMP, getProperties());
            PropertyValidator.getInstance().assertOne(Property.ORGANIZER, getProperties());
            PropertyValidator.getInstance().assertOne(Property.UID, getProperties());
            
            PropertyValidator.getInstance().assertOneOrLess(Property.RECURRENCE_ID, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.SEQUENCE, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.CATEGORIES, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.CLASS, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.COMMENT, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.CREATED, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.DESCRIPTION, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.DTEND, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.DTSTART, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.DURATION, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.GEO, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.LAST_MODIFIED, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.LOCATION, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.PRIORITY, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.RESOURCES, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.STATUS, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.SUMMARY, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.TRANSP, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.URL, getProperties());
            
            ComponentValidator.assertNone(Component.VALARM, getAlarms());
        }
    }
    
    /**
     * METHOD:REQUEST Validator.
     * 
     * <pre>
     * Component/Property  Presence
     * -----------------------------------------------------------------
     * METHOD              1       MUST be "REQUEST"
     * VEVENT              1+      All components MUST have the same UID
     *     ATTENDEE        1+
     *     DTSTAMP         1
     *     DTSTART         1
     *     ORGANIZER       1
     *     SEQUENCE        0 or 1  MUST be present if value is greater than 0,
     *                             MAY be present if 0
     *     SUMMARY         1       Can be null
     *     UID             1
     *     
     *     ATTACH          0+
     *     CATEGORIES      0 or 1  This property may contain a list of values
     *     CLASS           0 or 1
     *     COMMENT         0 or 1
     *     CONTACT         0+
     *     CREATED         0 or 1
     *     DESCRIPTION     0 or 1  Can be null
     *     DTEND           0 or 1  if present DURATION MUST NOT be present
     *     DURATION        0 or 1  if present DTEND MUST NOT be present
     *     EXDATE          0+
     *     EXRULE          0+
     *     GEO             0 or 1
     *     LAST-MODIFIED   0 or 1
     *     LOCATION        0 or 1
     *     PRIORITY        0 or 1
     *     RDATE           0+
     *     RECURRENCE-ID   0 or 1  only if referring to an instance of a
     *                             recurring calendar component.  Otherwise it
     *                             MUST NOT be present.
     *     RELATED-TO      0+
     *     REQUEST-STATUS  0+
     *     RESOURCES       0 or 1  This property MAY contain a list of values
     *     RRULE           0+
     *     STATUS          0 or 1  MAY be one of TENTATIVE/CONFIRMED
     *     TRANSP          0 or 1
     *     URL             0 or 1
     *     X-PROPERTY      0+
     *     
     * VALARM              0+
     * VTIMEZONE           0+      MUST be present if any date/time refers to
     *                             a timezone
     * X-COMPONENT         0+
     * VFREEBUSY           0
     * VJOURNAL            0
     * VTODO               0
     * </pre>
     * 
     */
    private class RequestValidator implements Validator {
        
		private static final long serialVersionUID = 1L;

        public void validate() throws ValidationException {
            if (!CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION)) {
                PropertyValidator.getInstance().assertOneOrMore(Property.ATTENDEE, getProperties());
            }
            
            PropertyValidator.getInstance().assertOne(Property.DTSTAMP, getProperties());
            PropertyValidator.getInstance().assertOne(Property.DTSTART, getProperties());
            PropertyValidator.getInstance().assertOne(Property.ORGANIZER, getProperties());
            PropertyValidator.getInstance().assertOne(Property.SUMMARY, getProperties());
            PropertyValidator.getInstance().assertOne(Property.UID, getProperties());
            
            PropertyValidator.getInstance().assertOneOrLess(Property.SEQUENCE, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.CATEGORIES, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.CLASS, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.COMMENT, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.CREATED, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.DESCRIPTION, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.DTEND, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.DURATION, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.GEO, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.LAST_MODIFIED, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.LOCATION, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.PRIORITY, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.RECURRENCE_ID, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.RESOURCES, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.STATUS, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.TRANSP, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.URL, getProperties());
            
            for (final Iterator i = getAlarms().iterator(); i.hasNext();) {
                final VAlarm alarm = (VAlarm) i.next();
                alarm.validate(Method.REQUEST);
            }
        }
    }
    /**
     * Returns a normalised list of periods representing the consumed time for this event.
     * @param rangeStart the start of a range
     * @param rangeEnd the end of a range
     * @return a normalised list of periods representing consumed time for this event
     * @see VEvent#getConsumedTime(Date, Date, boolean)
     */
    public final PeriodList getConsumedTime(final Date rangeStart,
            final Date rangeEnd) {
        return getConsumedTime(rangeStart, rangeEnd, true);
    }

    /**
     * Returns a list of periods representing the consumed time for this event in the specified range. Note that the
     * returned list may contain a single period for non-recurring components or multiple periods for recurring
     * components. If no time is consumed by this event an empty list is returned.
     * @param rangeStart the start of the range to check for consumed time
     * @param rangeEnd the end of the range to check for consumed time
     * @param normalise indicate whether the returned list of periods should be normalised
     * @return a list of periods representing consumed time for this event
     */
    public final PeriodList getConsumedTime(final Date rangeStart,
            final Date rangeEnd, final boolean normalise) {
        PeriodList periods = new PeriodList();
        // if component is transparent return empty list..
        if (!Transp.TRANSPARENT.equals(getProperty(Property.TRANSP))) {

//          try {
          periods = calculateRecurrenceSet(new Period(new DateTime(rangeStart),
                  new DateTime(rangeEnd)));
//          }
//          catch (ValidationException ve) {
//              log.error("Invalid event data", ve);
//              return periods;
//          }

          // if periods already specified through recurrence, return..
          // ..also normalise before returning.
          if (!periods.isEmpty() && normalise) {
              periods = periods.normalise();
          }
        }

        return periods;
    }

    /**
     * Returns a single occurrence of a recurring event.
     * @param date a date on which the occurence should occur
     * @return a single non-recurring event instance for the specified date, or null if the event doesn't
     * occur on the specified date
     * @throws IOException where an error occurs reading data
     * @throws URISyntaxException where an invalid URI is encountered
     * @throws ParseException where an error occurs parsing data
     */
    public final VEvent getOccurrence(final Date date) throws IOException,
        URISyntaxException, ParseException {
        
        final PeriodList consumedTime = getConsumedTime(date, date);
        for (final Iterator i = consumedTime.iterator(); i.hasNext();) {
            final Period p = (Period) i.next();
            if (p.getStart().equals(date)) {
                final VEvent occurrence = (VEvent) this.copy();
                occurrence.getProperties().add(new RecurrenceId(date));
                return occurrence;
            }
        }
        return null;
    }
    
    /**
     * @return the optional access classification property for an event
     */
    public final Clazz getClassification() {
        return (Clazz) getProperty(Property.CLASS);
    }

    /**
     * @return the optional creation-time property for an event
     */
    public final Created getCreated() {
        return (Created) getProperty(Property.CREATED);
    }

    /**
     * @return the optional description property for an event
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
     * @return the optional geographic position property for an event
     */
    public final Geo getGeographicPos() {
        return (Geo) getProperty(Property.GEO);
    }

    /**
     * @return the optional last-modified property for an event
     */
    public final LastModified getLastModified() {
        return (LastModified) getProperty(Property.LAST_MODIFIED);
    }

    /**
     * @return the optional location property for an event
     */
    public final Location getLocation() {
        return (Location) getProperty(Property.LOCATION);
    }

    /**
     * @return the optional organizer property for an event
     */
    public final Organizer getOrganizer() {
        return (Organizer) getProperty(Property.ORGANIZER);
    }

    /**
     * @return the optional priority property for an event
     */
    public final Priority getPriority() {
        return (Priority) getProperty(Property.PRIORITY);
    }

    /**
     * @return the optional date-stamp property
     */
    public final DtStamp getDateStamp() {
        return (DtStamp) getProperty(Property.DTSTAMP);
    }

    /**
     * @return the optional sequence number property for an event
     */
    public final Sequence getSequence() {
        return (Sequence) getProperty(Property.SEQUENCE);
    }

    /**
     * @return the optional status property for an event
     */
    public final Status getStatus() {
        return (Status) getProperty(Property.STATUS);
    }

    /**
     * @return the optional summary property for an event
     */
    public final Summary getSummary() {
        return (Summary) getProperty(Property.SUMMARY);
    }

    /**
     * @return the optional time transparency property for an event
     */
    public final Transp getTransparency() {
        return (Transp) getProperty(Property.TRANSP);
    }

    /**
     * @return the optional URL property for an event
     */
    public final Url getUrl() {
        return (Url) getProperty(Property.URL);
    }

    /**
     * @return the optional recurrence identifier property for an event
     */
    public final RecurrenceId getRecurrenceId() {
        return (RecurrenceId) getProperty(Property.RECURRENCE_ID);
    }

    /**
     * Returns the end date of this event. Where an end date is not available it will be derived from the event
     * duration.
     * @return a DtEnd instance, or null if one cannot be derived
     */
    public final DtEnd getEndDate() {
        return getEndDate(true);
    }

    /**
     * Convenience method to pull the DTEND out of the property list. If DTEND was not specified, use the DTSTART +
     * DURATION to calculate it.
     * @param deriveFromDuration specifies whether to derive an end date from the event duration where an end date is
     * not found
     * @return The end for this VEVENT.
     */
    public final DtEnd getEndDate(final boolean deriveFromDuration) {
        DtEnd dtEnd = (DtEnd) getProperty(Property.DTEND);
        // No DTEND? No problem, we'll use the DURATION.
        if (dtEnd == null && deriveFromDuration) {
            final DtStart dtStart = getStartDate();
            final Duration vEventDuration;
            if (getDuration() != null) {
                vEventDuration = getDuration();
            } else if (dtStart.getDate() instanceof DateTime) {
                // If "DTSTART" is a DATE-TIME, then the event's duration is zero (see: RFC 5545, 3.6.1 Event Component)
                vEventDuration = new Duration(new Dur(0, 0, 0, 0));
            } else {
                // If "DTSTART" is a DATE, then the event's duration is one day (see: RFC 5545, 3.6.1 Event Component)
                vEventDuration = new Duration(new Dur(1, 0, 0, 0));
            }

            dtEnd = new DtEnd(Dates.getInstance(vEventDuration.getDuration()
                    .getTime(dtStart.getDate()), (Value) dtStart
                    .getParameter(Parameter.VALUE)));
            if (dtStart.isUtc()) {
                dtEnd.setUtc(true);
            }
        }
        return dtEnd;
    }

    /**
     * @return the optional Duration property
     */
    public final Duration getDuration() {
        return (Duration) getProperty(Property.DURATION);
    }

    /**
     * Returns the UID property of this component if available.
     * @return a Uid instance, or null if no UID property exists
     */
    public final Uid getUid() {
        return (Uid) getProperty(Property.UID);
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(final Object arg0) {
        if (arg0 instanceof VEvent) {
            return super.equals(arg0)
                    && ObjectUtils.equals(alarms, ((VEvent) arg0).getAlarms());
        }
        return super.equals(arg0);
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return new HashCodeBuilder().append(getName()).append(getProperties())
                .append(getAlarms()).toHashCode();
    }

    /**
     * Overrides default copy method to add support for copying alarm sub-components.
     * @return a copy of the instance
     * @throws ParseException where values in the instance cannot be parsed
     * @throws IOException where values in the instance cannot be read
     * @throws URISyntaxException where an invalid URI value is encountered in the instance
     * @see net.fortuna.ical4j.model.Component#copy()
     */
    public Component copy() throws ParseException, IOException,
            URISyntaxException {
        final VEvent copy = (VEvent) super.copy();
        copy.alarms = new ComponentList(alarms);
        return copy;
    }
}
