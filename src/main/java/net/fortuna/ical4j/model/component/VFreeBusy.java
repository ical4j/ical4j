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
import java.util.Iterator;
import java.util.Map;

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.DateRange;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Dur;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.PeriodList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.Validator;
import net.fortuna.ical4j.model.parameter.FbType;
import net.fortuna.ical4j.model.property.Contact;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStamp;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Duration;
import net.fortuna.ical4j.model.property.FreeBusy;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Url;
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.util.PropertyValidator;

/**
 * $Id$ [Apr 5, 2004]
 *
 * Defines an iCalendar VFREEBUSY component.
 *
 * <pre>
 *  4.6.4 Free/Busy Component
 *
 *     Component Name: VFREEBUSY
 *
 *     Purpose: Provide a grouping of component properties that describe
 *     either a request for free/busy time, describe a response to a request
 *     for free/busy time or describe a published set of busy time.
 *
 *     Formal Definition: A &quot;VFREEBUSY&quot; calendar component is defined by the
 *     following notation:
 *
 *       freebusyc  = &quot;BEGIN&quot; &quot;:&quot; &quot;VFREEBUSY&quot; CRLF
 *                    fbprop
 *                    &quot;END&quot; &quot;:&quot; &quot;VFREEBUSY&quot; CRLF
 *
 *       fbprop     = *(
 *
 *                  ; the following are optional,
 *                  ; but MUST NOT occur more than once
 *
 *                  contact / dtstart / dtend / duration / dtstamp /
 *                  organizer / uid / url /
 *
 *                  ; the following are optional,
 *                  ; and MAY occur more than once
 *
 *                  attendee / comment / freebusy / rstatus / x-prop
 *
 *                  )
 *
 *     Description: A &quot;VFREEBUSY&quot; calendar component is a grouping of
 *     component properties that represents either a request for, a reply to
 *     a request for free or busy time information or a published set of
 *     busy time information.
 *
 *     When used to request free/busy time information, the &quot;ATTENDEE&quot;
 *     property specifies the calendar users whose free/busy time is being
 *     requested; the &quot;ORGANIZER&quot; property specifies the calendar user who
 *     is requesting the free/busy time; the &quot;DTSTART&quot; and &quot;DTEND&quot;
 *     properties specify the window of time for which the free/busy time is
 *     being requested; the &quot;UID&quot; and &quot;DTSTAMP&quot; properties are specified to
 *     assist in proper sequencing of multiple free/busy time requests.
 *
 *     When used to reply to a request for free/busy time, the &quot;ATTENDEE&quot;
 *     property specifies the calendar user responding to the free/busy time
 *     request; the &quot;ORGANIZER&quot; property specifies the calendar user that
 *     originally requested the free/busy time; the &quot;FREEBUSY&quot; property
 *     specifies the free/busy time information (if it exists); and the
 *     &quot;UID&quot; and &quot;DTSTAMP&quot; properties are specified to assist in proper
 *     sequencing of multiple free/busy time replies.
 *
 *     When used to publish busy time, the &quot;ORGANIZER&quot; property specifies
 *     the calendar user associated with the published busy time; the
 *     &quot;DTSTART&quot; and &quot;DTEND&quot; properties specify an inclusive time window
 *     that surrounds the busy time information; the &quot;FREEBUSY&quot; property
 *     specifies the published busy time information; and the &quot;DTSTAMP&quot;
 *     property specifies the date/time that iCalendar object was created.
 *
 *     The &quot;VFREEBUSY&quot; calendar component cannot be nested within another
 *     calendar component. Multiple &quot;VFREEBUSY&quot; calendar components can be
 *     specified within an iCalendar object. This permits the grouping of
 *     Free/Busy information into logical collections, such as monthly
 *     groups of busy time information.
 *
 *     The &quot;VFREEBUSY&quot; calendar component is intended for use in iCalendar
 *     object methods involving requests for free time, requests for busy
 *     time, requests for both free and busy, and the associated replies.
 *
 *     Free/Busy information is represented with the &quot;FREEBUSY&quot; property.
 *     This property provides a terse representation of time periods. One or
 *     more &quot;FREEBUSY&quot; properties can be specified in the &quot;VFREEBUSY&quot;
 *     calendar component.
 *
 *     When present in a &quot;VFREEBUSY&quot; calendar component, the &quot;DTSTART&quot; and
 *     &quot;DTEND&quot; properties SHOULD be specified prior to any &quot;FREEBUSY&quot;
 *     properties. In a free time request, these properties can be used in
 *     combination with the &quot;DURATION&quot; property to represent a request for a
 *     duration of free time within a specified window of time.
 *
 *     The recurrence properties (&quot;RRULE&quot;, &quot;EXRULE&quot;, &quot;RDATE&quot;, &quot;EXDATE&quot;) are
 *     not permitted within a &quot;VFREEBUSY&quot; calendar component. Any recurring
 *     events are resolved into their individual busy time periods using the
 *     &quot;FREEBUSY&quot; property.
 *
 *     Example: The following is an example of a &quot;VFREEBUSY&quot; calendar
 *     component used to request free or busy time information:
 *
 *       BEGIN:VFREEBUSY
 *       ORGANIZER:MAILTO:jane_doe@host1.com
 *       ATTENDEE:MAILTO:john_public@host2.com
 *       DTSTART:19971015T050000Z
 *       DTEND:19971016T050000Z
 *       DTSTAMP:19970901T083000Z
 *       END:VFREEBUSY
 *
 *     The following is an example of a &quot;VFREEBUSY&quot; calendar component used
 *     to reply to the request with busy time information:
 *
 *       BEGIN:VFREEBUSY
 *       ORGANIZER:MAILTO:jane_doe@host1.com
 *       ATTENDEE:MAILTO:john_public@host2.com
 *       DTSTAMP:19970901T100000Z
 *       FREEBUSY;VALUE=PERIOD:19971015T050000Z/PT8H30M,
 *        19971015T160000Z/PT5H30M,19971015T223000Z/PT6H30M
 *       URL:http://host2.com/pub/busy/jpublic-01.ifb
 *       COMMENT:This iCalendar file contains busy time information for
 *         the next three months.
 *       END:VFREEBUSY
 *
 *     The following is an example of a &quot;VFREEBUSY&quot; calendar component used
 *     to publish busy time information.
 *
 *       BEGIN:VFREEBUSY
 *       ORGANIZER:jsmith@host.com
 *       DTSTART:19980313T141711Z
 *       DTEND:19980410T141711Z
 *       FREEBUSY:19980314T233000Z/19980315T003000Z
 *       FREEBUSY:19980316T153000Z/19980316T163000Z
 *       FREEBUSY:19980318T030000Z/19980318T040000Z
 *       URL:http://www.host.com/calendar/busytime/jsmith.ifb
 *       END:VFREEBUSY
 * </pre>
 *
 * Example 1 - Requesting all busy time slots for a given period:
 *
 * <pre><code>
 * // request all busy times between today and 1 week from now..
 * DateTime start = new DateTime();
 * DateTime end = new DateTime(start.getTime() + 1000 * 60 * 60 * 24 * 7);
 *
 * VFreeBusy request = new VFreeBusy(start, end);
 *
 * VFreeBusy reply = new VFreeBusy(request, calendar.getComponents());
 * </code></pre>
 *
 * Example 2 - Requesting all free time slots for a given period of at least the specified duration:
 *
 * <pre><code>
 * // request all free time between today and 1 week from now of
 * // duration 2 hours or more..
 * DateTime start = new DateTime();
 * DateTime end = new DateTime(start.getTime() + 1000 * 60 * 60 * 24 * 7);
 *
 * VFreeBusy request = new VFreeBusy(start, end, new Dur(0, 2, 0, 0));
 *
 * VFreeBusy response = new VFreeBusy(request, myCalendar.getComponents());
 * </code></pre>
 *
 * @author Ben Fortuna
 */
public class VFreeBusy extends CalendarComponent {

    private static final long serialVersionUID = 1046534053331139832L;

    private final Map methodValidators = new HashMap();
    {
        methodValidators.put(Method.PUBLISH, new PublishValidator());
        methodValidators.put(Method.REPLY, new ReplyValidator());
        methodValidators.put(Method.REQUEST, new RequestValidator());
    }
    
    /**
     * Default constructor.
     */
    public VFreeBusy() {
        super(VFREEBUSY);
        getProperties().add(new DtStamp());
    }

    /**
     * Constructor.
     * @param properties a list of properties
     */
    public VFreeBusy(final PropertyList properties) {
        super(VFREEBUSY, properties);
    }

    /**
     * Constructs a new VFreeBusy instance with the specified start and end boundaries. This constructor should be used
     * for requesting busy time for a specified period.
     * @param start the starting boundary for the VFreeBusy
     * @param end the ending boundary for the VFreeBusy
     */
    public VFreeBusy(final DateTime start, final DateTime end) {
        this();
        
        // 4.8.2.4 Date/Time Start:
        //
        //    Within the "VFREEBUSY" calendar component, this property defines the
        //    start date and time for the free or busy time information. The time
        //    MUST be specified in UTC time.
        getProperties().add(new DtStart(start, true));
        
        // 4.8.2.2 Date/Time End
        //
        //    Within the "VFREEBUSY" calendar component, this property defines the
        //    end date and time for the free or busy time information. The time
        //    MUST be specified in the UTC time format. The value MUST be later in
        //    time than the value of the "DTSTART" property.
        getProperties().add(new DtEnd(end, true));
    }

    /**
     * Constructs a new VFreeBusy instance with the specified start and end boundaries. This constructor should be used
     * for requesting free time for a specified duration in given period defined by the start date and end date.
     * @param start the starting boundary for the VFreeBusy
     * @param end the ending boundary for the VFreeBusy
     * @param duration the length of the period being requested
     */
    public VFreeBusy(final DateTime start, final DateTime end, final Dur duration) {
        this();
        
        // 4.8.2.4 Date/Time Start:
        //
        //    Within the "VFREEBUSY" calendar component, this property defines the
        //    start date and time for the free or busy time information. The time
        //    MUST be specified in UTC time.
        getProperties().add(new DtStart(start, true));
        
        // 4.8.2.2 Date/Time End
        //
        //    Within the "VFREEBUSY" calendar component, this property defines the
        //    end date and time for the free or busy time information. The time
        //    MUST be specified in the UTC time format. The value MUST be later in
        //    time than the value of the "DTSTART" property.
        getProperties().add(new DtEnd(end, true));

        getProperties().add(new Duration(duration));
    }

    /**
     * Constructs a new VFreeBusy instance representing a reply to the specified VFREEBUSY request according to the
     * specified list of components.
     * If the request argument has its duration set, then the result
     * represents a list of <em>free</em> times (that is, parameter FBTYPE
     * is set to FbType.FREE).
     * If the request argument does not have its duration set, then the result
     * represents a list of <em>busy</em> times.
     * @param request a VFREEBUSY request
     * @param components a component list used to initialise busy time
     * @throws ValidationException 
     */
    public VFreeBusy(final VFreeBusy request, final ComponentList components) {
        this();
        
        final DtStart start = (DtStart) request.getProperty(Property.DTSTART);
        
        final DtEnd end = (DtEnd) request.getProperty(Property.DTEND);
        
        final Duration duration = (Duration) request.getProperty(Property.DURATION);
        
        // 4.8.2.4 Date/Time Start:
        //
        //    Within the "VFREEBUSY" calendar component, this property defines the
        //    start date and time for the free or busy time information. The time
        //    MUST be specified in UTC time.
        getProperties().add(new DtStart(start.getDate(), true));
        
        // 4.8.2.2 Date/Time End
        //
        //    Within the "VFREEBUSY" calendar component, this property defines the
        //    end date and time for the free or busy time information. The time
        //    MUST be specified in the UTC time format. The value MUST be later in
        //    time than the value of the "DTSTART" property.
        getProperties().add(new DtEnd(end.getDate(), true));
        
        if (duration != null) {
            getProperties().add(new Duration(duration.getDuration()));
            // Initialise with all free time of at least the specified duration..
            final DateTime freeStart = new DateTime(start.getDate());
            final DateTime freeEnd = new DateTime(end.getDate());
            final FreeBusy fb = new FreeTimeBuilder().start(freeStart)
                .end(freeEnd)
                .duration(duration.getDuration())
                .components(components)
                .build();
            if (fb != null && !fb.getPeriods().isEmpty()) {
                getProperties().add(fb);
            }
        }
        else {
            // initialise with all busy time for the specified period..
            final DateTime busyStart = new DateTime(start.getDate());
            final DateTime busyEnd = new DateTime(end.getDate());
            final FreeBusy fb = new BusyTimeBuilder().start(busyStart)
                .end(busyEnd)
                .components(components)
                .build();
            if (fb != null && !fb.getPeriods().isEmpty()) {
                getProperties().add(fb);
            }
        }
    }

    /**
     * Create a FREEBUSY property representing the busy time for the specified component list. If the component is not
     * applicable to FREEBUSY time, or if the component is outside the bounds of the start and end dates, null is
     * returned. If no valid busy periods are identified in the component an empty FREEBUSY property is returned (i.e.
     * empty period list).
     */
    private class BusyTimeBuilder {
        
        private DateTime start;
        
        private DateTime end;
        
        private ComponentList components;
        
        public BusyTimeBuilder start(DateTime start) {
            this.start = start;
            return this;
        }
        
        public BusyTimeBuilder end(DateTime end) {
            this.end = end;
            return this;
        }
        
        public BusyTimeBuilder components(ComponentList components) {
            this.components = components;
            return this;
        }
        
        public FreeBusy build() {
            final PeriodList periods = getConsumedTime(components, start, end);
            final DateRange range = new DateRange(start, end);
            // periods must be in UTC time for freebusy..
            periods.setUtc(true);
            for (final Iterator i = periods.iterator(); i.hasNext();) {
                final Period period = (Period) i.next();
                // check if period outside bounds..
                if (!range.intersects(period)) {
                    periods.remove(period);
                }
            }
            return new FreeBusy(periods);
        }
    }

    /**
     * Create a FREEBUSY property representing the free time available of the specified duration for the given list of
     * components. component. If the component is not applicable to FREEBUSY time, or if the component is outside the
     * bounds of the start and end dates, null is returned. If no valid busy periods are identified in the component an
     * empty FREEBUSY property is returned (i.e. empty period list).
     */
    private class FreeTimeBuilder {
        
        private DateTime start;
        
        private DateTime end;
        
        private Dur duration;
        
        private ComponentList components;
        
        public FreeTimeBuilder start(DateTime start) {
            this.start = start;
            return this;
        }
        
        public FreeTimeBuilder end(DateTime end) {
            this.end = end;
            return this;
        }
        
        private FreeTimeBuilder duration(Dur duration) {
            this.duration = duration;
            return this;
        }
        
        public FreeTimeBuilder components(ComponentList components) {
            this.components = components;
            return this;
        }
        
        public FreeBusy build() {
            final FreeBusy fb = new FreeBusy();
            fb.getParameters().add(FbType.FREE);
            final PeriodList periods = getConsumedTime(components, start, end);
            final DateRange range = new DateRange(start, end);
            // Add final consumed time to avoid special-case end-of-list processing
            periods.add(new Period(end, end));
            DateTime lastPeriodEnd = new DateTime(start);
            // where no time is consumed set the last period end as the range start..
            for (final Iterator i = periods.iterator(); i.hasNext();) {
                final Period period = (Period) i.next();
                
                // check if period outside bounds.. or period intersects with the end of the range..
                if (range.contains(period) || 
                		(range.intersects(period) && period.getStart().after(range.getRangeStart()))) {
                    
                    // calculate duration between this period start and last period end..
                    final Duration freeDuration = new Duration(lastPeriodEnd, period.getStart());
                    if (freeDuration.getDuration().compareTo(duration) >= 0) {
                        fb.getPeriods().add(new Period(lastPeriodEnd, freeDuration.getDuration()));
                    }
                }
                
                if (period.getEnd().after(lastPeriodEnd)) {
                    lastPeriodEnd = period.getEnd();
                }
            }
            return fb;
        }
    }

    /**
     * Creates a list of periods representing the time consumed by the specified list of components.
     * @param components
     * @return
     */
    private PeriodList getConsumedTime(final ComponentList components, final DateTime rangeStart,
            final DateTime rangeEnd) {
        
        final PeriodList periods = new PeriodList();
        // only events consume time..
        for (final Iterator i = components.getComponents(Component.VEVENT).iterator(); i.hasNext();) {
            final Component component = (Component) i.next();
            periods.addAll(((VEvent) component).getConsumedTime(rangeStart, rangeEnd, false));
        }
        return periods.normalise();
    }

    /**
     * {@inheritDoc}
     */
    public final void validate(final boolean recurse) throws ValidationException {

        if (!CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION)) {

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

        final PropertyValidator validator = PropertyValidator.getInstance();

        /*
         * ; the following are optional, ; but MUST NOT occur more than once contact / dtstart / dtend / duration /
         * dtstamp / organizer / uid / url /
         */
        validator.assertOneOrLess(Property.CONTACT, getProperties());
        validator.assertOneOrLess(Property.DTSTART, getProperties());
        validator.assertOneOrLess(Property.DTEND, getProperties());
        validator.assertOneOrLess(Property.DURATION, getProperties());
        validator.assertOneOrLess(Property.DTSTAMP, getProperties());
        validator.assertOneOrLess(Property.ORGANIZER, getProperties());
        validator.assertOneOrLess(Property.UID, getProperties());
        validator.assertOneOrLess(Property.URL, getProperties());

        /*
         * ; the following are optional, ; and MAY occur more than once attendee / comment / freebusy / rstatus / x-prop
         */

        /*
         * The recurrence properties ("RRULE", "EXRULE", "RDATE", "EXDATE") are not permitted within a "VFREEBUSY"
         * calendar component. Any recurring events are resolved into their individual busy time periods using the
         * "FREEBUSY" property.
         */
        validator.assertNone(Property.RRULE, getProperties());
        validator.assertNone(Property.EXRULE, getProperties());
        validator.assertNone(Property.RDATE, getProperties());
        validator.assertNone(Property.EXDATE, getProperties());

        // DtEnd value must be later in time that DtStart..
        final DtStart dtStart = (DtStart) getProperty(Property.DTSTART);
        
        // 4.8.2.4 Date/Time Start:
        //
        //    Within the "VFREEBUSY" calendar component, this property defines the
        //    start date and time for the free or busy time information. The time
        //    MUST be specified in UTC time.
        if (dtStart != null && !dtStart.isUtc()) {
            throw new ValidationException("DTSTART must be specified in UTC time");
        }
        
        final DtEnd dtEnd = (DtEnd) getProperty(Property.DTEND);
        
        // 4.8.2.2 Date/Time End
        //
        //    Within the "VFREEBUSY" calendar component, this property defines the
        //    end date and time for the free or busy time information. The time
        //    MUST be specified in the UTC time format. The value MUST be later in
        //    time than the value of the "DTSTART" property.
        if (dtEnd != null && !dtEnd.isUtc()) {
            throw new ValidationException("DTEND must be specified in UTC time");
        }
        
        if (dtStart != null && dtEnd != null
                && !dtStart.getDate().before(dtEnd.getDate())) {
            throw new ValidationException("Property [" + Property.DTEND
                    + "] must be later in time than [" + Property.DTSTART + "]");
        }

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
     * METHOD              1       MUST be "PUBLISH"
     * 
     * VFREEBUSY           1+
     *     DTSTAMP         1
     *     DTSTART         1       DateTime values must be in UTC
     *     DTEND           1       DateTime values must be in UTC
     *     FREEBUSY        1+      MUST be BUSYTIME. Multiple instances are
     *                             allowed. Multiple instances must be sorted
     *                             in ascending order
     *     ORGANIZER       1       MUST contain the address of originator of
     *                             busy time data.
     *     UID             1
     *     COMMENT         0 or 1
     *     CONTACT         0+
     *     X-PROPERTY      0+
     *     URL             0 or 1  Specifies busy time URL
     * 
     *     ATTENDEE        0
     *     DURATION        0
     *     REQUEST-STATUS  0
     * 
     * X-COMPONENT         0+
     * 
     * VEVENT              0
     * VTODO               0
     * VJOURNAL            0
     * VTIMEZONE           0
     * VALARM              0
     * </pre>
     * 
     */
    private class PublishValidator implements Validator {
        
		private static final long serialVersionUID = 1L;
 
        public void validate() throws ValidationException {
            PropertyValidator.getInstance().assertOneOrMore(Property.FREEBUSY, getProperties());
            
            PropertyValidator.getInstance().assertOne(Property.DTSTAMP, getProperties());
            PropertyValidator.getInstance().assertOne(Property.DTSTART, getProperties());
            PropertyValidator.getInstance().assertOne(Property.DTEND, getProperties());
            PropertyValidator.getInstance().assertOne(Property.ORGANIZER, getProperties());
            PropertyValidator.getInstance().assertOne(Property.UID, getProperties());
            
            PropertyValidator.getInstance().assertOneOrLess(Property.COMMENT, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.URL, getProperties());
            
            PropertyValidator.getInstance().assertNone(Property.ATTENDEE, getProperties());
            PropertyValidator.getInstance().assertNone(Property.DURATION, getProperties());
            PropertyValidator.getInstance().assertNone(Property.REQUEST_STATUS, getProperties());
        }
    }
    
    /**
     * <pre>
     * Component/Property  Presence
     * ------------------- ----------------------------------------------
     * METHOD              1      MUST be "REPLY"
     * 
     * VFREEBUSY           1
     *     ATTENDEE        1      (address of recipient replying)
     *     DTSTAMP         1
     *     DTEND           1      DateTime values must be in UTC
     *     DTSTART         1      DateTime values must be in UTC
     *     FREEBUSY        0+      (values MUST all be of the same data
     *                             type. Multiple instances are allowed.
     *                             Multiple instances MUST be sorted in
     *                             ascending order. Values MAY NOT overlap)
     *     ORGANIZER       1       MUST be the request originator's address
     *     UID             1
     * 
     *     COMMENT         0 or 1
     *     CONTACT         0+
     *     REQUEST-STATUS  0+
     *     URL             0 or 1  (specifies busy time URL)
     *     X-PROPERTY      0+
     *     DURATION        0
     *     SEQUENCE        0
     * 
     * X-COMPONENT         0+
     * VALARM              0
     * VEVENT              0
     * VTODO               0
     * VJOURNAL            0
     * VTIMEZONE           0
     * </pre>
     * 
     */
    private class ReplyValidator implements Validator {
        
		private static final long serialVersionUID = 1L;

        public void validate() throws ValidationException {

            // FREEBUSY is 1+ in RFC2446 but 0+ in Calsify
            
            PropertyValidator.getInstance().assertOne(Property.ATTENDEE, getProperties());
            PropertyValidator.getInstance().assertOne(Property.DTSTAMP, getProperties());
            PropertyValidator.getInstance().assertOne(Property.DTEND, getProperties());
            PropertyValidator.getInstance().assertOne(Property.DTSTART, getProperties());
            PropertyValidator.getInstance().assertOne(Property.ORGANIZER, getProperties());
            PropertyValidator.getInstance().assertOne(Property.UID, getProperties());
            
            PropertyValidator.getInstance().assertOneOrLess(Property.COMMENT, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.URL, getProperties());
            
            PropertyValidator.getInstance().assertNone(Property.DURATION, getProperties());
            PropertyValidator.getInstance().assertNone(Property.SEQUENCE, getProperties());
        }
    }
    
    /**
     * METHOD:REQUEST Validator.
     * 
     * <pre>
     * Component/Property  Presence
     * ------------------- ----------------------------------------------
     * METHOD              1      MUST be "REQUEST"
     * 
     * VFREEBUSY           1
     *     ATTENDEE        1+     contain the address of the calendar store
     *     DTEND           1      DateTime values must be in UTC
     *     DTSTAMP         1
     *     DTSTART         1      DateTime values must be in UTC
     *     ORGANIZER       1      MUST be the request originator's address
     *     UID             1
     *     COMMENT         0 or 1
     *     CONTACT         0+
     *     X-PROPERTY      0+
     * 
     *     FREEBUSY        0
     *     DURATION        0
     *     REQUEST-STATUS  0
     *     URL             0
     * 
     * X-COMPONENT         0+
     * VALARM              0
     * VEVENT              0
     * VTODO               0
     * VJOURNAL            0
     * VTIMEZONE           0
     * </pre>
     * 
     */
    private class RequestValidator implements Validator {
        
		private static final long serialVersionUID = 1L;

        public void validate() throws ValidationException {
            PropertyValidator.getInstance().assertOneOrMore(Property.ATTENDEE, getProperties());
            
            PropertyValidator.getInstance().assertOne(Property.DTEND, getProperties());
            PropertyValidator.getInstance().assertOne(Property.DTSTAMP, getProperties());
            PropertyValidator.getInstance().assertOne(Property.DTSTART, getProperties());
            PropertyValidator.getInstance().assertOne(Property.ORGANIZER, getProperties());
            PropertyValidator.getInstance().assertOne(Property.UID, getProperties());
            
            PropertyValidator.getInstance().assertOneOrLess(Property.COMMENT, getProperties());
            
            PropertyValidator.getInstance().assertNone(Property.FREEBUSY, getProperties());
            PropertyValidator.getInstance().assertNone(Property.DURATION, getProperties());
            PropertyValidator.getInstance().assertNone(Property.REQUEST_STATUS, getProperties());
            PropertyValidator.getInstance().assertNone(Property.URL, getProperties());
        }
    }
    
    /**
     * @return the CONTACT property or null if not specified
     */
    public final Contact getContact() {
        return (Contact) getProperty(Property.CONTACT);
    }

    /**
     * @return the DTSTART propery or null if not specified
     */
    public final DtStart getStartDate() {
        return (DtStart) getProperty(Property.DTSTART);
    }

    /**
     * @return the DTEND property or null if not specified
     */
    public final DtEnd getEndDate() {
        return (DtEnd) getProperty(Property.DTEND);
    }

    /**
     * @return the DURATION property or null if not specified
     */
    public final Duration getDuration() {
        return (Duration) getProperty(Property.DURATION);
    }

    /**
     * @return the DTSTAMP property or null if not specified
     */
    public final DtStamp getDateStamp() {
        return (DtStamp) getProperty(Property.DTSTAMP);
    }

    /**
     * @return the ORGANIZER property or null if not specified
     */
    public final Organizer getOrganizer() {
        return (Organizer) getProperty(Property.ORGANIZER);
    }

    /**
     * @return the URL property or null if not specified
     */
    public final Url getUrl() {
        return (Url) getProperty(Property.URL);
    }

    /**
     * Returns the UID property of this component if available.
     * @return a Uid instance, or null if no UID property exists
     */
    public final Uid getUid() {
        return (Uid) getProperty(Property.UID);
    }
}
