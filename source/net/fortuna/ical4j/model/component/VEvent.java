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

import java.util.Iterator;

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateList;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Dur;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.PeriodList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStamp;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Duration;
import net.fortuna.ical4j.model.property.ExDate;
import net.fortuna.ical4j.model.property.ExRule;
import net.fortuna.ical4j.model.property.RDate;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.model.property.Status;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Transp;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.util.Dates;
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
 * christmas.getProperties().getProperty(Property.DTSTART).getParameters().add(Value.DATE);
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
 * Example 3 - Retrieve a list of periods representing a recurring event in a
 * specified range:
 * 
 * <pre><code>
 *       Calendar weekday9AM = Calendar.getInstance();
 *       weekday9AM.set(2005, Calendar.MARCH, 7, 9, 0, 0);
 *       weekday9AM.set(Calendar.MILLISECOND, 0);
 *
 *       Calendar weekday5PM = Calendar.getInstance();
 *       weekday5PM.set(2005, Calendar.MARCH, 7, 17, 0, 0);
 *       weekday5PM.set(Calendar.MILLISECOND, 0);
 *
 *       // Do the recurrence until December 31st.
 *       Calendar untilCal = Calendar.getInstance();
 *       untilCal.set(2005, Calendar.DECEMBER, 31);
 *       untilCal.set(Calendar.MILLISECOND, 0);
 *
 *       // 9:00AM to 5:00PM Rule
 *       Recur recur = new Recur(Recur.WEEKLY, untilCal.getTime());
 *       recur.getDayList().add(WeekDay.MO);
 *       recur.getDayList().add(WeekDay.TU);
 *       recur.getDayList().add(WeekDay.WE);
 *       recur.getDayList().add(WeekDay.TH);
 *       recur.getDayList().add(WeekDay.FR);
 *       recur.setInterval(3);
 *       recur.setWeekStartDay(WeekDay.MO.getDay());
 *       RRule rrule = new RRule(recur);
 *
 *       Summary summary = new Summary("TEST EVENTS THAT HAPPEN 9-5 MON-FRI");
 *
 *       weekdayNineToFiveEvents = new VEvent();
 *       weekdayNineToFiveEvents.getProperties().add(rrule);
 *       weekdayNineToFiveEvents.getProperties().add(summary);
 *       weekdayNineToFiveEvents.getProperties().add(
 *                                       new DtStart(weekday9AM.getTime()));
 *       weekdayNineToFiveEvents.getProperties().add(
 *                                       new DtEnd(weekday5PM.getTime()));
 *
 *       // Test Start 04/01/2005, End One month later.
 *       // Query Calendar Start and End Dates.
 *       Calendar queryStartDate = Calendar.getInstance();
 *       queryStartDate.set(2005, Calendar.APRIL, 1, 14, 47, 0);
 *       queryStartDate.set(Calendar.MILLISECOND, 0);
 *       Calendar queryEndDate = Calendar.getInstance();
 *       queryEndDate.set(2005, Calendar.MAY, 1, 11, 15, 0);
 *       queryEndDate.set(Calendar.MILLISECOND, 0);
 *
 *       // This range is monday to friday every three weeks, starting from
 *       // March 7th 2005, which means for our query dates we need
 *       // April 18th through to the 22nd.
 *       PeriodList periods =
 *               weekdayNineToFiveEvents.getPeriods(queryStartDate.getTime(),
 *                                                     queryEndDate.getTime());
 * </code></pre>
 * 
 * @author Ben Fortuna
 */
public class VEvent extends Component {
    
    private static final long serialVersionUID = 2547948989200697335L;

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
        getProperties().add(new DtStamp(new DateTime()));
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
        getProperties().add(new DtStamp(new DateTime()));
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
    public VEvent(final Date start, final Dur duration, final String summary) {
        this();
        getProperties().add(new DtStamp(new DateTime()));
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

        return BEGIN + ":" + getName() + "\r\n" + getProperties() + getAlarms()
                + END + ":" + getName() + "\r\n";
    }

    /**
     * @see net.fortuna.ical4j.model.Component#validate(boolean)
     */
    public final void validate(final boolean recurse) throws ValidationException {

        // validate that getAlarms() only contains VAlarm components
        Iterator iterator = getAlarms().iterator();
        while (iterator.hasNext()) {
            Component component = (Component) iterator.next();

            if (!(component instanceof VAlarm)) {
                throw new ValidationException(
                    "Component [" + component.getName() + "] may not occur in VEVENT");
            }
        }

        /*
         * ; the following are optional, ; but MUST NOT occur more than once
         *
         * class / created / description / dtstart / geo / last-mod / location /
         * organizer / priority / dtstamp / seq / status / summary / transp /
         * uid / url / recurid /
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

        Status status = (Status) getProperty(Property.STATUS);
        if (status != null
                && !Status.VEVENT_TENTATIVE.equals(status)
                && !Status.VEVENT_CONFIRMED.equals(status)
                && !Status.VEVENT_CANCELLED.equals(status)) {
                throw new ValidationException(
                        "Status property [" + status.toString() + "] is not applicable for VEVENT");
        }

        /*
         * ; either 'dtend' or 'duration' may appear in ; a 'eventprop', but
         * 'dtend' and 'duration' ; MUST NOT occur in the same 'eventprop'
         *
         * dtend / duration /
         */
        try {
            PropertyValidator.getInstance().assertNone(Property.DTEND, getProperties());
        }
        catch (ValidationException ve) {
            PropertyValidator.getInstance().assertNone(Property.DURATION, getProperties());
        }

        if (getProperty(Property.DTEND) != null) {
            
            /*
             *  The "VEVENT" is also the calendar component used to specify an
             *  anniversary or daily reminder within a calendar. These events have a
             *  DATE value type for the "DTSTART" property instead of the default
             *  data type of DATE-TIME. If such a "VEVENT" has a "DTEND" property, it
             *  MUST be specified as a DATE value also. The anniversary type of
             *  "VEVENT" can span more than one date (i.e, "DTEND" property value is
             *  set to a calendar date after the "DTSTART" property value).
             */
            DtStart start = (DtStart) getProperty(Property.DTSTART);
            DtEnd end = (DtEnd) getProperty(Property.DTEND);
            if (start != null) {
                Parameter value = start.getParameter(Parameter.VALUE);
                if (value != null && !value.equals(end.getParameter(Parameter.VALUE))) {
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

    /**
     * Returns a list of periods representing the consumed time for this event
     * in the specified range. Note that the returned list may contain a single
     * period for non-recurring components or multiple periods for recurring
     * components. If no time is consumed by this event an empty list is returned.
     * @param rangeStart the start of the range to check for consumed time
     * @param rangeEnd the end of the range to check for consumed time
     * @return a list of periods representing consumed time for this event
     */
    public final PeriodList getConsumedTime(final Date rangeStart, final Date rangeEnd) {
        PeriodList periods = new PeriodList();
        // if component is transparent return empty list..
        if (Transp.TRANSPARENT.equals(getProperty(Property.TRANSP))) {
            return periods;
        }
        DtStart start = (DtStart) getProperty(Property.DTSTART);
        DtEnd end = (DtEnd) getProperty(Property.DTEND);
        Duration duration = (Duration) getProperty(Property.DURATION);
        // if no start date specified return empty list..
        if (start == null) {
            return periods;
        }
        // if an explicit event duration is not specified, derive a value for recurring
        // periods from the end date..
        Dur rDuration;
        if (duration == null) {
            rDuration = new Dur(start.getDate(), end.getDate());
        }
        else {
            rDuration = duration.getDuration();
        }
        // adjust range start back by duration to allow for recurrences that
        // start before the range but finish inside..
//        FIXME: See bug #1325558..
        Date adjustedRangeStart = new DateTime(rangeStart);
        adjustedRangeStart.setTime(rDuration.negate().getTime(rangeStart).getTime());
        // if start/end specified as anniversary-type (i.e. uses DATE values
        // rather than DATE-TIME), return empty list..
        if (Value.DATE.equals(start.getParameter(Parameter.VALUE))) {
            return periods;
        }
        // recurrence dates..
        PropertyList rDates = getProperties(Property.RDATE);
        for (Iterator i = rDates.iterator(); i.hasNext();) {
            RDate rdate = (RDate) i.next();
            // only period-based rdates are applicable..
            // FIXME: ^^^ not true - date-time/date also applicable..
            if (Value.PERIOD.equals(rdate.getParameter(Parameter.VALUE))) {
                for (Iterator j = rdate.getPeriods().iterator(); j.hasNext();) {
                    Period period = (Period) j.next();
                    if (period.getStart().before(rangeEnd) && period.getEnd().after(rangeStart)) {
                        periods.add(period);
                    }
                }
            }
        }
        // recurrence rules..
        PropertyList rRules = getProperties(Property.RRULE);
        for (Iterator i = rRules.iterator(); i.hasNext();) {
            RRule rrule = (RRule) i.next();
            DateList startDates = rrule.getRecur().getDates(start.getDate(), adjustedRangeStart, rangeEnd, (Value) start.getParameter(Parameter.VALUE));
//            DateList startDates = rrule.getRecur().getDates(start.getDate(), rangeStart, rangeEnd, (Value) start.getParameters().getParameter(Parameter.VALUE));
            for (int j = 0; j < startDates.size(); j++) {
                Date startDate = (Date) startDates.get(j);
                periods.add(new Period(new DateTime(startDate), rDuration));
            }
        }
        // exception dates..
        PropertyList exDates = getProperties(Property.EXDATE);
        for (Iterator i = exDates.iterator(); i.hasNext();) {
            ExDate exDate = (ExDate) i.next();
            for (Iterator j = periods.iterator(); j.hasNext();) {
                Period period = (Period) j.next();
                // for DATE-TIME instances check for DATE-based exclusions also..
                if (exDate.getDates().contains(period.getStart())
                        || exDate.getDates().contains(new Date(period.getStart()))) {
                    j.remove();
                }
            }
        }
        // exception rules..
        // FIXME: exception rules should be consistent with exception dates (i.e. not use periods?)..
        PropertyList exRules = getProperties(Property.EXRULE);
        PeriodList exPeriods = new PeriodList();
        for (Iterator i = exRules.iterator(); i.hasNext();) {
            ExRule exrule = (ExRule) i.next();
//            DateList startDates = exrule.getRecur().getDates(start.getDate(), adjustedRangeStart, rangeEnd, (Value) start.getParameters().getParameter(Parameter.VALUE));
            DateList startDates = exrule.getRecur().getDates(start.getDate(), rangeStart, rangeEnd, (Value) start.getParameter(Parameter.VALUE));
            for (Iterator j = startDates.iterator(); j.hasNext();) {
                Date startDate = (Date) j.next();
                exPeriods.add(new Period(new DateTime(startDate), rDuration));
            }
        }
        // apply exceptions..
        if (!exPeriods.isEmpty()) {
            periods = periods.subtract(exPeriods);
        }
        // if periods already specified through recurrence, return..
        // ..also normalise before returning.
        if (!periods.isEmpty()) {
            return periods.normalise();
        }
        // add first instance if included in range..
        if (start.getDate().before(rangeEnd)) {
            if (end != null && end.getDate().after(rangeStart)) {
                periods.add(new Period(new DateTime(start.getDate()), new DateTime(end.getDate())));
            }
            else if (duration != null) {
                Period period = new Period(new DateTime(start.getDate()), duration.getDuration());
                if (period.getEnd().after(rangeStart)) {
                    periods.add(period);
                }
            }
        }
        return periods;
    }

    /**
     * Convenience method to pull the DTSTART out of the property list.
     *
     * @return
     *      The DtStart object representation of the start Date
     */
    public final DtStart getStartDate() {
        return (DtStart) getProperty(Property.DTSTART);
    }

    /**
     * Convenience method to pull the DTEND out of the property list.  If
     * DTEND was not specified, use the DTSTART + DURATION to calculate it.
     *
     * @return
     *       The end for this VEVENT.
     */
    public final DtEnd getEndDate() {
        DtEnd dtEnd = (DtEnd) getProperty(Property.DTEND);
        // No DTEND?  No problem, we'll use the DURATION.
        if (dtEnd == null) {
            DtStart dtStart = getStartDate();
            Duration vEventDuration =
                      (Duration) getProperty(Property.DURATION);
            dtEnd = new DtEnd(Dates.getInstance(vEventDuration.getDuration().getTime(dtStart.getDate()),
                    (Value) dtStart.getParameter(Parameter.VALUE)));
            if (dtStart.isUtc()) {
                dtEnd.setUtc(true);
            }
        }
        return dtEnd;
    }
    
    /**
     * Returns the UID property of this component if available.
     * @return a Uid instance, or null if no UID property exists
     */
    public final Uid getUid() {
        return (Uid) getProperty(Property.UID);
    }
}
