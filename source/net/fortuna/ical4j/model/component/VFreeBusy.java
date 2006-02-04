/*
 * $Id$ [Apr 5, 2004]
 *
 * Copyright (c) 2005, Ben Fortuna
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
 * A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
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
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Dur;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.PeriodList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.parameter.FbType;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStamp;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Duration;
import net.fortuna.ical4j.model.property.FreeBusy;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.util.PropertyValidator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Defines an iCalendar VFREEBUSY component.
 * 
 * <pre>
 *         4.6.4 Free/Busy Component
 *         
 *            Component Name: VFREEBUSY
 *         
 *            Purpose: Provide a grouping of component properties that describe
 *            either a request for free/busy time, describe a response to a request
 *            for free/busy time or describe a published set of busy time.
 *         
 *            Formal Definition: A &quot;VFREEBUSY&quot; calendar component is defined by the
 *            following notation:
 *         
 *              freebusyc  = &quot;BEGIN&quot; &quot;:&quot; &quot;VFREEBUSY&quot; CRLF
 *                           fbprop
 *                           &quot;END&quot; &quot;:&quot; &quot;VFREEBUSY&quot; CRLF
 *         
 *              fbprop     = *(
 *         
 *                         ; the following are optional,
 *                         ; but MUST NOT occur more than once
 *         
 *                         contact / dtstart / dtend / duration / dtstamp /
 *                         organizer / uid / url /
 *         
 *                         ; the following are optional,
 *                         ; and MAY occur more than once
 *         
 *                         attendee / comment / freebusy / rstatus / x-prop
 *         
 *                         )
 * </pre>
 * 
 * Example 1 - Requesting all busy time slots for a given period:
 * 
 * <pre><code>
 * // request all busy time between today and 1 week from now..
 * java.util.Calendar cal = java.util.Calendar.getInstance();
 * Date start = cal.getTime();
 * cal.add(java.util.Calendar.WEEK_OF_YEAR, 1);
 * Date end = cal.getTime();
 * 
 * VFreeBusy request = new VFreeBusy(start, end);
 * </code></pre>
 * 
 * Example 2 - Publishing all busy time slots for the period requested:
 * 
 * <pre><code>
 * VFreeBusy reply = new VFreeBusy(request, calendar.getComponents());
 * </code></pre>
 * 
 * Example 3 - Requesting all free time slots for a given period of at least the
 * specified duration:
 * 
 * <pre><code>
 * // request all free time between today and 1 week from now of
 * // duration 2 hours or more..
 * java.util.Calendar cal = java.util.Calendar.getInstance();
 * Date start = cal.getTime();
 * cal.add(java.util.Calendar.WEEK_OF_YEAR, 1);
 * Date end = cal.getTime();
 * 
 * VFreeBusy request = new VFreeBusy(start, end, 2 * 60 * 60 * 1000);
 * </code></pre>
 * 
 * @author Ben Fortuna
 */
public class VFreeBusy extends Component {
    
    private static final long serialVersionUID = 1046534053331139832L;
    
    private static Log log = LogFactory.getLog(VFreeBusy.class);

    /**
     * Default constructor.
     */
    public VFreeBusy() {
        super(VFREEBUSY);
    }

    /**
     * Constructor.
     * 
     * @param properties
     *            a list of properties
     */
    public VFreeBusy(final PropertyList properties) {
        super(VFREEBUSY, properties);
    }

    /**
     * Constructs a new VFreeBusy instance with the specified start and end
     * boundaries. This constructor should be used for requesting Free/Busy time
     * for a specified period.
     * 
     * @param startDate
     *            the starting boundary for the VFreeBusy
     * @param endDate
     *            the ending boundary for the VFreeBusy
     */
    public VFreeBusy(final DateTime start, final DateTime end) {
        this();
        // dtstart MUST be specified in UTC..
        getProperties().add(new DtStart(start, true));
        // dtend MUST be specified in UTC..
        getProperties().add(new DtEnd(end, true));
        getProperties().add(new DtStamp(new DateTime()));
    }

    /**
     * Constructs a new VFreeBusy instance with the specified start and end
     * boundaries. This constructor should be used for requesting Free/Busy time
     * for a specified duration in given period defined by the start date and
     * end date.
     * 
     * @param startDate
     *            the starting boundary for the VFreeBusy
     * @param endDate
     *            the ending boundary for the VFreeBusy
     * @param duration
     *            the length of the period being requested
     */
    public VFreeBusy(final DateTime start, final DateTime end, final Dur duration) {
        this();
        // dtstart MUST be specified in UTC..
        getProperties().add(new DtStart(start, true));
        // dtend MUST be specified in UTC..
        getProperties().add(new DtEnd(end, true));
        getProperties().add(new Duration(duration));
        getProperties().add(new DtStamp(new DateTime()));
    }

    /**
     * Constructs a new VFreeBusy instance represeting a reply to the specified
     * VFREEBUSY request according to the specified list of components.
     * 
     * @param request
     *            a VFREEBUSY request
     * @param components
     *            a component list used to initialise busy time
     */
    public VFreeBusy(final VFreeBusy request, final ComponentList components) {
        this();
        DtStart start = (DtStart) request.getProperty(Property.DTSTART);
        DtEnd end = (DtEnd) request.getProperty(Property.DTEND);
        Duration duration = (Duration) request.getProperty(Property.DURATION);
        // dtstart MUST be specified in UTC..
        getProperties().add(new DtStart(start.getDate(), true));
        // dtend MUST be specified in UTC..
        getProperties().add(new DtEnd(end.getDate(), true));
        getProperties().add(new DtStamp(new DateTime()));
        if (duration != null) {
            getProperties().add(new Duration(duration.getDuration()));
            // Initialise with all free time of at least the specified
            // duration..
            DateTime freeStart = new DateTime(start.getDate());
            DateTime freeEnd = new DateTime(end.getDate());
            FreeBusy fb = createFreeTime(freeStart, freeEnd, duration.getDuration(), components);
            if (fb != null && !fb.getPeriods().isEmpty()) {
                getProperties().add(fb);
            }
        } else {
            // initialise with all busy time for the specified period..
            DateTime busyStart = new DateTime(start.getDate());
            DateTime busyEnd = new DateTime(end.getDate());
            FreeBusy fb = createBusyTime(busyStart, busyEnd, components);
            if (fb != null && !fb.getPeriods().isEmpty()) {
                getProperties().add(fb);
            }
        }
    }

    /**
     * Create a FREEBUSY property representing the busy time for the specified
     * component list. If the component is not applicable to FREEBUSY time, or if the
     * component is outside the bounds of the start and end dates, null is
     * returned. If no valid busy periods are identified in the component an
     * empty FREEBUSY property is returned (i.e. empty period list).
     * 
     * @param component
     *            a component to base the FREEBUSY property on
     * @return a FreeBusy instance or null if the component is not applicable
     */
    private FreeBusy createBusyTime(final DateTime start, final DateTime end, final ComponentList components) {
        PeriodList periods = getConsumedTime(components, start, end);
        for (Iterator i = periods.iterator(); i.hasNext();) {
            Period period = (Period) i.next();
            // check if period outside bounds..
            if (period.getStart().after(end) || period.getEnd().before(start)) {
                periods.remove(period);
            }
        }
        return new FreeBusy(periods);
    }

    /**
     * Create a FREEBUSY property representing the free time available of the specified
     * duration for the given list of components.
     * component. If the component is not applicable to FREEBUSY time, or if the
     * component is outside the bounds of the start and end dates, null is
     * returned. If no valid busy periods are identified in the component an
     * empty FREEBUSY property is returned (i.e. empty period list).
     * @param start
     * @param end
     * @param duration
     * @param components
     * @return
     */
    private FreeBusy createFreeTime(final DateTime start, final DateTime end, final Dur duration, final ComponentList components) {
        FreeBusy fb = new FreeBusy();
        fb.getParameters().add(FbType.FREE);
        PeriodList periods = getConsumedTime(components, start, end);
        // debugging..
        if (log.isDebugEnabled()) {
            log.debug("Busy periods: " + periods);
        }
        DateTime lastPeriodEnd = null;
        // where no time is consumed set the last period end as the range start..
        if (periods.isEmpty()) {
            lastPeriodEnd = new DateTime(start);
        }
        for (Iterator i = periods.iterator(); i.hasNext();) {
            Period period = (Period) i.next();
            // check if period outside bounds..
            if (period.getStart().after(end) || period.getEnd().before(start)) {
                continue;
            }
            // create a dummy last period end if first period starts after the start date
            // (i.e. there is a free time gap between the start and the first period).
            if (lastPeriodEnd == null && period.getStart().after(start)) {
                lastPeriodEnd = new DateTime(start);
            }
            // calculate duration between this period start and last period end..
            if (lastPeriodEnd != null) {
                Duration freeDuration = new Duration(lastPeriodEnd, period.getStart());
                if (freeDuration.getDuration().compareTo(duration) >= 0) {
                    fb.getPeriods().add(new Period(lastPeriodEnd, freeDuration.getDuration()));
                }
            }
            lastPeriodEnd = period.getEnd();
        }
        // calculate duration between last period end and end ..
        if (lastPeriodEnd != null) {
            Duration freeDuration = new Duration(lastPeriodEnd, end);
            if (freeDuration.getDuration().compareTo(duration) >= 0) {
                fb.getPeriods().add(new Period(lastPeriodEnd, freeDuration.getDuration()));
            }
        }
        return fb;
    }

    /**
     * Creates a list of periods representing the time consumed by the specified
     * list of components.
     * @param components
     * @return
     */
    private PeriodList getConsumedTime(final ComponentList components, final DateTime rangeStart, final DateTime rangeEnd) {
        PeriodList periods = new PeriodList();
        for (Iterator i = components.iterator(); i.hasNext();) {
            Component component = (Component) i.next();
            // only events consume time..
            if (component instanceof VEvent) {
                periods.addAll(((VEvent) component).getConsumedTime(rangeStart, rangeEnd));
            }
        }
        return periods.normalise();
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.fortuna.ical4j.model.Component#validate(boolean)
     */
    public final void validate(final boolean recurse) throws ValidationException {
        PropertyValidator validator = PropertyValidator.getInstance();

        /*
         * ; the following are optional, ; but MUST NOT occur more than once
         * 
         * contact / dtstart / dtend / duration / dtstamp / organizer / uid /
         * url /
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
         * ; the following are optional, ; and MAY occur more than once
         * 
         * attendee / comment / freebusy / rstatus / x-prop
         */

        /*
         * The recurrence properties ("RRULE", "EXRULE", "RDATE", "EXDATE") are
         * not permitted within a "VFREEBUSY" calendar component. Any recurring
         * events are resolved into their individual busy time periods using the
         * "FREEBUSY" property.
         */
        validator.assertNone(Property.RRULE, getProperties());
        validator.assertNone(Property.EXRULE, getProperties());
        validator.assertNone(Property.RDATE, getProperties());
        validator.assertNone(Property.EXDATE, getProperties());

        // DtEnd value must be later in time that DtStart..
        DtStart dtStart = (DtStart) getProperty(Property.DTSTART);
        DtEnd dtEnd = (DtEnd) getProperty(Property.DTEND);
        if (dtStart != null && dtEnd != null && !dtStart.getDate().before(dtEnd.getDate())) {
            throw new ValidationException("Property [" + Property.DTEND + "] must be later in time than ["
                    + Property.DTSTART + "]");
        }

        if (recurse) {
            validateProperties();
        }
    }
    
    /**
     * Returns the UID property of this component if available.
     * @return a Uid instance, or null if no UID property exists
     */
    public final Uid getUid() {
        return (Uid) getProperty(Property.UID);
    }
}
