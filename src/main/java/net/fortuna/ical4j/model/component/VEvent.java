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
import net.fortuna.ical4j.model.parameter.TzId;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.*;
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.util.Strings;
import net.fortuna.ical4j.validate.PropertyValidator;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.ValidationRule;
import net.fortuna.ical4j.validate.Validator;
import net.fortuna.ical4j.validate.component.VEventValidator;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.util.*;

import static net.fortuna.ical4j.model.Property.*;
import static net.fortuna.ical4j.validate.ValidationRule.ValidationType.*;

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

    private final Map<Method, Validator<VEvent>> methodValidators = new HashMap<>();
    {
        methodValidators.put(Method.ADD, new VEventValidator(new ValidationRule(One, DTSTAMP, DTSTART, ORGANIZER, SEQUENCE, SUMMARY, UID),
                new ValidationRule(OneOrLess, CATEGORIES, CLASS, CREATED, DESCRIPTION, DTEND, DURATION, GEO,
                        LAST_MODIFIED, LOCATION, PRIORITY, RESOURCES, STATUS, TRANSP, URL),
                new ValidationRule(None, RECURRENCE_ID, REQUEST_STATUS)));
        methodValidators.put(Method.CANCEL, new VEventValidator(false, new ValidationRule(One, DTSTAMP, DTSTART, ORGANIZER, SEQUENCE, UID),
                new ValidationRule(OneOrLess, CATEGORIES, CLASS, CREATED, DESCRIPTION, DTEND, DTSTART, DURATION, GEO,
                        LAST_MODIFIED, LOCATION, PRIORITY, RECURRENCE_ID, RESOURCES, STATUS, SUMMARY, TRANSP, URL),
                new ValidationRule(None, REQUEST_STATUS)));
        methodValidators.put(Method.COUNTER, new VEventValidator(new ValidationRule(One, DTSTAMP, DTSTART, SEQUENCE, SUMMARY, UID),
                new ValidationRule(One, true, ORGANIZER),
                new ValidationRule(OneOrLess, CATEGORIES, CLASS, CREATED, DESCRIPTION, DTEND, DURATION, GEO,
                        LAST_MODIFIED, LOCATION, PRIORITY, RECURRENCE_ID, RESOURCES, STATUS, TRANSP, URL)));
        methodValidators.put(Method.DECLINE_COUNTER, new VEventValidator(false, new ValidationRule(One, DTSTAMP, ORGANIZER, UID),
                new ValidationRule(OneOrLess, RECURRENCE_ID, SEQUENCE),
                new ValidationRule(None, ATTACH, ATTENDEE, CATEGORIES, CLASS, CONTACT, CREATED, DESCRIPTION, DTEND,
                        DTSTART, DURATION, EXDATE, EXRULE, GEO, LAST_MODIFIED, LOCATION, PRIORITY, RDATE, RELATED_TO,
                        RESOURCES, RRULE, STATUS, SUMMARY, TRANSP, URL)));
        methodValidators.put(Method.PUBLISH, new VEventValidator(new ValidationRule(One, DTSTART, UID),
                new ValidationRule(One, true, DTSTAMP, ORGANIZER, SUMMARY),
                new ValidationRule(OneOrLess, RECURRENCE_ID, SEQUENCE, CATEGORIES, CLASS, CREATED, DESCRIPTION, DTEND,
                        DURATION, GEO, LAST_MODIFIED, LOCATION, PRIORITY, RESOURCES, STATUS, TRANSP, URL),
                new ValidationRule(None, true, ATTENDEE),
                new ValidationRule(None, REQUEST_STATUS)));
        methodValidators.put(Method.REFRESH, new VEventValidator(false, new ValidationRule(One, ATTENDEE, DTSTAMP, ORGANIZER, UID),
                new ValidationRule(OneOrLess, RECURRENCE_ID),
                new ValidationRule(None, ATTACH, CATEGORIES, CLASS, CONTACT, CREATED, DESCRIPTION, DTEND, DTSTART,
                        DURATION, EXDATE, EXRULE, GEO, LAST_MODIFIED, LOCATION, PRIORITY, RDATE, RELATED_TO,
                        REQUEST_STATUS, RESOURCES, RRULE, SEQUENCE, STATUS, SUMMARY, TRANSP, URL)));
        methodValidators.put(Method.REPLY, new VEventValidator(CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION),
                new ValidationRule(One, ATTENDEE, DTSTAMP, ORGANIZER, UID),
                new ValidationRule(OneOrLess, RECURRENCE_ID, SEQUENCE, CATEGORIES, CLASS, CREATED, DESCRIPTION, DTEND,
                        DTSTART, DURATION, GEO, LAST_MODIFIED, LOCATION, PRIORITY, RESOURCES, STATUS, SUMMARY, TRANSP,
                        URL)));
        methodValidators.put(Method.REQUEST, new VEventValidator(new ValidationRule(OneOrMore, true, ATTENDEE),
                new ValidationRule(One, DTSTAMP, DTSTART, ORGANIZER, SUMMARY, UID),
                new ValidationRule(OneOrLess, SEQUENCE, CATEGORIES, CLASS, CREATED, DESCRIPTION, DTEND, DURATION, GEO,
                        LAST_MODIFIED, LOCATION, PRIORITY, RECURRENCE_ID, RESOURCES, STATUS, TRANSP, URL)));
    }
    
    private ComponentList<VAlarm> alarms;

    /**
     * Default constructor.
     */
    public VEvent() {
        this(true);
    }

    public VEvent(boolean initialise) {
        super(VEVENT);
        this.alarms = new ComponentList<VAlarm>();
        if (initialise) {
            getProperties().add(new DtStamp());
        }
    }

    /**
     * Constructor.
     * @param properties a list of properties
     */
    public VEvent(final PropertyList properties) {
        super(VEVENT, properties);
        this.alarms = new ComponentList<VAlarm>();
    }

    /**
     * Constructor.
     * @param properties a list of properties
     * @param alarms a list of alarms
     */
    public VEvent(final PropertyList properties, final ComponentList<VAlarm> alarms) {
        super(VEVENT, properties);
        this.alarms = alarms;
    }

    /**
     * Constructs a new VEVENT instance starting at the specified time with the specified summary.
     * @param start the start date of the new event
     * @param summary the event summary
     */
    public VEvent(final Temporal start, final String summary) {
        this();
        getProperties().add(new DtStart<>(start));
        getProperties().add(new Summary(summary));
    }

    /**
     * Constructs a new VEVENT instance starting and ending at the specified times with the specified summary.
     * @param start the start date of the new event
     * @param end the end date of the new event
     * @param summary the event summary
     */
    public VEvent(final Temporal start, final Temporal end, final String summary) {
        this();
        getProperties().add(new DtStart<>(start));
        getProperties().add(new DtEnd<>(end));
        getProperties().add(new Summary(summary));
    }

    /**
     * Constructs a new VEVENT instance starting at the specified times, for the specified duration, with the specified
     * summary.
     * @param start the start date of the new event
     * @param duration the duration of the new event
     * @param summary the event summary
     */
    public VEvent(final Temporal start, final TemporalAmount duration, final String summary) {
        this();
        getProperties().add(new DtStart<>(start));
        getProperties().add(new Duration(duration));
        getProperties().add(new Summary(summary));
    }

    /**
     * Returns the list of alarms for this event.
     * @return a component list
     */
    public final ComponentList<VAlarm> getAlarms() {
        return alarms;
    }

    /**
     * {@inheritDoc}
     */
    public final String toString() {
        return BEGIN +
                ':' +
                getName() +
                Strings.LINE_SEPARATOR +
                getProperties() +
                getAlarms() +
                END +
                ':' +
                getName() +
                Strings.LINE_SEPARATOR;
    }

    /**
     * {@inheritDoc}
     */
    public final void validate(final boolean recurse) throws ValidationException {

        // validate that getAlarms() only contains VAlarm components
//        final Iterator iterator = getAlarms().iterator();
//        while (iterator.hasNext()) {
//            final Component component = (Component) iterator.next();
//
//            if (!(component instanceof VAlarm)) {
//                throw new ValidationException("Component ["
//                        + component.getName() + "] may not occur in VEVENT");
//            }
//            
//            ((VAlarm) component).validate(recurse);
//        }

        if (!CompatibilityHints
                .isHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION)) {

            // From "4.8.4.7 Unique Identifier":
            // Conformance: The property MUST be specified in the "VEVENT", "VTODO",
            // "VJOURNAL" or "VFREEBUSY" calendar components.
            PropertyValidator.assertOne(Property.UID,
                    getProperties());

            // From "4.8.7.2 Date/Time Stamp":
            // Conformance: This property MUST be included in the "VEVENT", "VTODO",
            // "VJOURNAL" or "VFREEBUSY" calendar components.
            PropertyValidator.assertOne(Property.DTSTAMP,
                    getProperties());
        }

        /*
         * ; the following are optional, ; but MUST NOT occur more than once class / created / description / dtstart /
         * geo / last-mod / location / organizer / priority / dtstamp / seq / status / summary / transp / uid / url /
         * recurid /
         */
        Arrays.asList(Property.CLASS, Property.CREATED, Property.DESCRIPTION,
                Property.DTSTART, Property.GEO, Property.LAST_MODIFIED, Property.LOCATION, Property.ORGANIZER,
                Property.PRIORITY, Property.DTSTAMP, Property.SEQUENCE, Property.STATUS, Property.SUMMARY,
                Property.TRANSP, Property.UID, Property.URL, Property.RECURRENCE_ID).forEach(property -> PropertyValidator.assertOneOrLess(property, getProperties()));

        final Optional<Status> status = getProperty(Property.STATUS);
        if (status.isPresent() && !Status.VEVENT_TENTATIVE.getValue().equals(status.get().getValue())
                && !Status.VEVENT_CONFIRMED.getValue().equals(status.get().getValue())
                && !Status.VEVENT_CANCELLED.getValue().equals(status.get().getValue())) {
            throw new ValidationException("Status property ["
                    + status.toString() + "] is not applicable for VEVENT");
        }

        /*
         * ; either 'dtend' or 'duration' may appear in ; a 'eventprop', but 'dtend' and 'duration' ; MUST NOT occur in
         * the same 'eventprop' dtend / duration /
         */
        try {
            PropertyValidator.assertNone(Property.DTEND,
                    getProperties());
        }
        catch (ValidationException ve) {
            PropertyValidator.assertNone(Property.DURATION,
                    getProperties());
        }

        final Optional<DtEnd<?>> end = getProperty(Property.DTEND);
        if (end.isPresent()) {

            /*
             * The "VEVENT" is also the calendar component used to specify an anniversary or daily reminder within a
             * calendar. These events have a DATE value type for the "DTSTART" property instead of the default data type
             * of DATE-TIME. If such a "VEVENT" has a "DTEND" property, it MUST be specified as a DATE value also. The
             * anniversary type of "VEVENT" can span more than one date (i.e, "DTEND" property value is set to a
             * calendar date after the "DTSTART" property value).
             */
            final Optional<DtStart<Temporal>> start = getProperty(Property.DTSTART);

            if (start.isPresent()) {
                final Optional<Parameter> startValue = start.get().getParameter(Parameter.VALUE);
                final Optional<Parameter> endValue = end.get().getParameter(Parameter.VALUE);
                
                if (!startValue.equals(endValue)) {
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
     * Performs method-specific ITIP validation.
     * @param method the applicable method
     * @throws ValidationException where the component does not comply with RFC2446
     */
    public void validate(Method method) throws ValidationException {
        final Validator<VEvent> validator = methodValidators.get(method);
        if (validator != null) {
            validator.validate(this);
        }
        else {
            super.validate(method);
        }
    }

    /**
     * Returns a normalised list of periods representing the consumed time for this event.
     * @param range the range to check for consumed time
     * @return a normalised list of periods representing consumed time for this event
     */
    public final <T extends Temporal> List<Period<T>> getConsumedTime(final Period<T> range) {
        return getConsumedTime(range, true);
    }

    /**
     * Returns a list of periods representing the consumed time for this event in the specified range. Note that the
     * returned list may contain a single period for non-recurring components or multiple periods for recurring
     * components. If no time is consumed by this event an empty list is returned.
     * @param range the range to check for consumed time
     * @param normalise indicate whether the returned list of periods should be normalised
     * @return a list of periods representing consumed time for this event
     */
    public final <T extends Temporal> List<Period<T>> getConsumedTime(final Period<T> range, final boolean normalise) {
        PeriodList<T> periods;
        // if component is transparent return empty list..
        Optional<Transp> transp = getProperty(Property.TRANSP);
        if (!transp.isPresent() || !Transp.TRANSPARENT.equals(transp.get())) {

//          try {
            periods = new PeriodList<>(calculateRecurrenceSet(range));
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
        } else {
            periods = new PeriodList<>();
        }
        return new ArrayList<>(periods.getPeriods());
    }

    /**
     * Returns a single occurrence of a recurring event.
     * @param date a date on which the occurence should occur
     * @return a single non-recurring event instance for the specified date, or null if the event doesn't
     * occur on the specified date
     */
    public final <T extends Temporal> VEvent getOccurrence(final T date) {
        
        final List<Period<T>> consumedTime = getConsumedTime(new Period<>(date, date));
        for (final Period<T> p : consumedTime) {
            if (p.getStart().equals(date)) {
                final VEvent occurrence = this.copy();
                occurrence.getProperties().add(new RecurrenceId<>(date));
                return occurrence;
            }
        }
        return null;
    }
    
    /**
     * @return the optional access classification property for an event
     * @deprecated use {@link VEvent#getProperty(String)}
     */
    @Deprecated
    public final Optional<Clazz> getClassification() {
        return getProperty(Property.CLASS);
    }

    /**
     * @return the optional creation-time property for an event
     * @deprecated use {@link VEvent#getProperty(String)}
     */
    @Deprecated
    public final Optional<Created> getCreated() {
        return getProperty(Property.CREATED);
    }

    /**
     * @return the optional description property for an event
     * @deprecated use {@link VEvent#getProperty(String)}
     */
    @Deprecated
    public final Optional<Description> getDescription() {
        return getProperty(Property.DESCRIPTION);
    }

    /**
     * Convenience method to pull the DTSTART out of the property list.
     * @return The DtStart object representation of the start Date
     * @deprecated use {@link VEvent#getProperty(String)}
     */
    @Deprecated
    public final Optional<DtStart<?>> getStartDate() {
        return getProperty(Property.DTSTART);
    }

    /**
     * @return the optional geographic position property for an event
     * @deprecated use {@link VEvent#getProperty(String)}
     */
    @Deprecated
    public final Optional<Geo> getGeographicPos() {
        return getProperty(Property.GEO);
    }

    /**
     * @return the optional last-modified property for an event
     * @deprecated use {@link VEvent#getProperty(String)}
     */
    @Deprecated
    public final Optional<LastModified> getLastModified() {
        return getProperty(Property.LAST_MODIFIED);
    }

    /**
     * @return the optional location property for an event
     * @deprecated use {@link VEvent#getProperty(String)}
     */
    @Deprecated
    public final Optional<Location> getLocation() {
        return getProperty(Property.LOCATION);
    }

    /**
     * @return the optional organizer property for an event
     * @deprecated use {@link VEvent#getProperty(String)}
     */
    @Deprecated
    public final Optional<Organizer> getOrganizer() {
        return getProperty(Property.ORGANIZER);
    }

    /**
     * @return the optional priority property for an event
     * @deprecated use {@link VEvent#getProperty(String)}
     */
    @Deprecated
    public final Optional<Priority> getPriority() {
        return getProperty(Property.PRIORITY);
    }

    /**
     * @return the optional date-stamp property
     * @deprecated use {@link VEvent#getProperty(String)}
     */
    @Deprecated
    public final Optional<DtStamp> getDateStamp() {
        return getProperty(Property.DTSTAMP);
    }

    /**
     * @return the optional sequence number property for an event
     * @deprecated use {@link VEvent#getProperty(String)}
     */
    @Deprecated
    public final Optional<Sequence> getSequence() {
        return getProperty(Property.SEQUENCE);
    }

    /**
     * @return the optional status property for an event
     * @deprecated use {@link VEvent#getProperty(String)}
     */
    @Deprecated
    public final Optional<Status> getStatus() {
        return getProperty(Property.STATUS);
    }

    /**
     * @return the optional summary property for an event
     * @deprecated use {@link VEvent#getProperty(String)}
     */
    @Deprecated
    public final Optional<Summary> getSummary() {
        return getProperty(Property.SUMMARY);
    }

    /**
     * @return the optional time transparency property for an event
     * @deprecated use {@link VEvent#getProperty(String)}
     */
    @Deprecated
    public final Optional<Transp> getTransparency() {
        return getProperty(Property.TRANSP);
    }

    /**
     * @return the optional URL property for an event
     * @deprecated use {@link VEvent#getProperty(String)}
     */
    @Deprecated
    public final Optional<Url> getUrl() {
        return getProperty(Property.URL);
    }

    /**
     * @return the optional recurrence identifier property for an event
     * @deprecated use {@link VEvent#getProperty(String)}
     */
    @Deprecated
    public final Optional<RecurrenceId<?>> getRecurrenceId() {
        return getProperty(Property.RECURRENCE_ID);
    }

    /**
     * Returns the end date of this event. Where an end date is not available it will be derived from the event
     * duration.
     * @return a DtEnd instance, or null if one cannot be derived
     */
    public final Optional<DtEnd<?>> getEndDate() {
        return getEndDate(true);
    }

    /**
     * Convenience method to pull the DTEND out of the property list. If DTEND was not specified, use the DTSTART +
     * DURATION to calculate it.
     * @param deriveFromDuration specifies whether to derive an end date from the event duration where an end date is
     * not found
     * @return The end for this VEVENT.
     */
    public final Optional<DtEnd<?>> getEndDate(final boolean deriveFromDuration) {
        Optional<DtEnd<?>> dtEnd = getProperty(Property.DTEND);
        // No DTEND? No problem, we'll use the DURATION.
        if (!dtEnd.isPresent() && deriveFromDuration) {
            Optional<DtStart<?>> dtStart = getProperty(DTSTART);
            if (dtStart.isPresent()) {
                final Duration vEventDuration;
                Optional<Duration> duration = getProperty(DURATION);
                if (duration.isPresent()) {
                    vEventDuration = getDuration().get();
                } else if (dtStart.get().getParameter(Parameter.VALUE).equals(Optional.of(Value.DATE_TIME))) {
                    // If "DTSTART" is a DATE-TIME, then the event's duration is zero (see: RFC 5545, 3.6.1 Event Component)
                    vEventDuration = new Duration(java.time.Duration.ZERO);
                } else {
                    // If "DTSTART" is a DATE, then the event's duration is one day (see: RFC 5545, 3.6.1 Event Component)
                    vEventDuration = new Duration(java.time.Duration.ofDays(1));
                }

                DtEnd<?> newdtEnd = new DtEnd<>(dtStart.get().getDate().plus(vEventDuration.getDuration()));
                Optional<TzId> tzId = dtStart.get().getParameter(Parameter.TZID);
                if (tzId.isPresent()) {
                    newdtEnd.getParameters().add(tzId.get());
                } else {
                    newdtEnd.getParameters().removeIf(p -> p.getName().equals(Parameter.TZID));
                }

                return Optional.of(newdtEnd);
            }
        }
        return dtEnd;
    }

    /**
     * @return the optional Duration property
     * @deprecated use {@link VEvent#getProperty(String)}
     */
    @Deprecated
    public final Optional<Duration> getDuration() {
        return getProperty(Property.DURATION);
    }

    /**
     * Returns the UID property of this component if available.
     * @return a Uid instance, or null if no UID property exists
     * @deprecated use {@link VEvent#getProperty(String)}
     */
    @Deprecated
    public final Optional<Uid> getUid() {
        return getProperty(Property.UID);
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(final Object arg0) {
        if (arg0 instanceof VEvent) {
            return super.equals(arg0)
                    && Objects.equals(alarms, ((VEvent) arg0).getAlarms());
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
     * @see net.fortuna.ical4j.model.Component#copy()
     */
    public VEvent copy() {
        return new Factory().createComponent(getProperties(), getAlarms());
    }

    public static class Factory extends Content.Factory implements ComponentFactory<VEvent> {

        public Factory() {
            super(VEVENT);
        }

        @Override
        public VEvent createComponent() {
            return new VEvent(false);
        }

        @Override
        public VEvent createComponent(PropertyList properties) {
            return new VEvent(properties);
        }

        @Override
        public VEvent createComponent(PropertyList properties, ComponentList subComponents) {
            return new VEvent(properties, subComponents);
        }
    }
}
