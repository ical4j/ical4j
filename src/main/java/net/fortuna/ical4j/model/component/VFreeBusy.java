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
import net.fortuna.ical4j.model.parameter.FbType;
import net.fortuna.ical4j.model.property.*;
import net.fortuna.ical4j.validate.ComponentValidator;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.ValidationRule;
import net.fortuna.ical4j.validate.Validator;
import net.fortuna.ical4j.validate.component.VFreeBusyValidator;

import java.time.temporal.TemporalAmount;
import java.util.HashMap;
import java.util.Map;

import static net.fortuna.ical4j.model.Property.*;
import static net.fortuna.ical4j.validate.ValidationRule.ValidationType.*;

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
public class VFreeBusy extends CalendarComponent implements ComponentContainer<Component> {

    private static final long serialVersionUID = 1046534053331139832L;

    private static final Map<Method, Validator> methodValidators = new HashMap<Method, Validator>();
    static {
        methodValidators.put(Method.PUBLISH, new ComponentValidator<VFreeBusy>(new ValidationRule(OneOrMore, FREEBUSY),
                new ValidationRule(One, DTSTAMP, DTSTART, DTEND, ORGANIZER, UID),
                new ValidationRule(OneOrLess, URL),
                new ValidationRule(None, ATTENDEE, DURATION, REQUEST_STATUS)));
        methodValidators.put(Method.REPLY, new ComponentValidator(new ValidationRule(One, ATTENDEE, DTSTAMP, DTEND, DTSTART, ORGANIZER, UID),
                new ValidationRule(OneOrLess, URL),
                new ValidationRule(None, DURATION, SEQUENCE)));
        methodValidators.put(Method.REQUEST, new ComponentValidator(new ValidationRule(OneOrMore, ATTENDEE),
                new ValidationRule(One, DTEND, DTSTAMP, DTSTART, ORGANIZER, UID),
                new ValidationRule(None, FREEBUSY, DURATION, REQUEST_STATUS, URL)));
    }

    /**
     * Default constructor.
     */
    public VFreeBusy() {
        this(true);
    }

    public VFreeBusy(boolean initialise) {
        super(VFREEBUSY);
        if (initialise) {
            getProperties().add(new DtStamp());
        }
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
    public VFreeBusy(final DateTime start, final DateTime end, final TemporalAmount duration) {
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
    public VFreeBusy(final VFreeBusy request, final ComponentList<CalendarComponent> components) {
        this();

        final DtStart start = request.getProperty(Property.DTSTART);

        final DtEnd end = request.getProperty(Property.DTEND);

        final Duration duration = request.getProperty(Property.DURATION);

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

        private ComponentList<CalendarComponent> components;

        public BusyTimeBuilder start(DateTime start) {
            this.start = start;
            return this;
        }

        public BusyTimeBuilder end(DateTime end) {
            this.end = end;
            return this;
        }

        public BusyTimeBuilder components(ComponentList<CalendarComponent> components) {
            this.components = components;
            return this;
        }

        public FreeBusy build() {
            final PeriodList periods = getConsumedTime(components, start, end);
            final DateRange range = new DateRange(start, end);
            // periods must be in UTC time for freebusy..
            periods.setUtc(true);
            periods.removeIf(period -> {
                // check if period outside bounds..
                return !range.intersects(period);
            });
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

        private TemporalAmount duration;

        private ComponentList<CalendarComponent> components;

        public FreeTimeBuilder start(DateTime start) {
            this.start = start;
            return this;
        }

        public FreeTimeBuilder end(DateTime end) {
            this.end = end;
            return this;
        }

        private FreeTimeBuilder duration(TemporalAmount duration) {
            this.duration = duration;
            return this;
        }

        public FreeTimeBuilder components(ComponentList<CalendarComponent> components) {
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
            for (final Period period : periods) {
                // check if period outside bounds.. or period intersects with the end of the range..
                if (range.contains(period) ||
                		(range.intersects(period) && period.getStart().after(range.getRangeStart()))) {

                    // calculate duration between this period start and last period end..
                    final Duration freeDuration = new Duration(lastPeriodEnd, period.getStart());
                    if (new TemporalAmountComparator().compare(freeDuration.getDuration(), duration) >= 0) {
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
    private PeriodList getConsumedTime(final ComponentList<CalendarComponent> components, final DateTime rangeStart,
            final DateTime rangeEnd) {

        final PeriodList periods = new PeriodList();
        // only events consume time..
        for (final Component event : components.getComponents(Component.VEVENT)) {
            periods.addAll(((VEvent) event).getConsumedTime(rangeStart, rangeEnd, false));
        }
        return periods.normalise();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void validate(final boolean recurse) throws ValidationException {
        new VFreeBusyValidator().validate(this);
        if (recurse) {
            validateProperties();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Validator getValidator(Method method) {
        return methodValidators.get(method);
    }

    @Override
    public ComponentList<Component> getComponents() {
        return (ComponentList<Component>) components;
    }

    /**
     * @return the CONTACT property or null if not specified
     */
    public final Contact getContact() {
        return getProperty(Property.CONTACT);
    }

    /**
     * @return the DTSTART propery or null if not specified
     */
    public final DtStart getStartDate() {
        return getProperty(Property.DTSTART);
    }

    /**
     * @return the DTEND property or null if not specified
     */
    public final DtEnd getEndDate() {
        return getProperty(Property.DTEND);
    }

    /**
     * @return the DURATION property or null if not specified
     */
    public final Duration getDuration() {
        return getProperty(Property.DURATION);
    }

    /**
     * @return the DTSTAMP property or null if not specified
     */
    public final DtStamp getDateStamp() {
        return getProperty(Property.DTSTAMP);
    }

    /**
     * @return the ORGANIZER property or null if not specified
     */
    public final Organizer getOrganizer() {
        return getProperty(Property.ORGANIZER);
    }

    /**
     * @return the URL property or null if not specified
     */
    public final Url getUrl() {
        return getProperty(Property.URL);
    }

    /**
     * Returns the UID property of this component if available.
     * @return a Uid instance, or null if no UID property exists
     */
    public final Uid getUid() {
        return getProperty(Property.UID);
    }

    public static class Factory extends Content.Factory implements ComponentFactory<VFreeBusy> {

        public Factory() {
            super(VFREEBUSY);
        }

        @Override
        public VFreeBusy createComponent() {
            return new VFreeBusy(false);
        }

        @Override
        public VFreeBusy createComponent(PropertyList properties) {
            return new VFreeBusy(properties);
        }
    }
}
