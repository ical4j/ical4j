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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.UtcOffset;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.WeekDay;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.model.property.TzId;
import net.fortuna.ical4j.model.property.TzName;
import net.fortuna.ical4j.model.property.TzOffsetFrom;
import net.fortuna.ical4j.model.property.TzOffsetTo;
import net.fortuna.ical4j.util.Dates;
import net.fortuna.ical4j.util.PropertyValidator;
import net.fortuna.ical4j.util.TimeZoneUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Defines an iCalendar VTIMEZONE component.
 * 
 * <pre>
 *    4.6.5 Time Zone Component
 *    
 *       Component Name: VTIMEZONE
 *    
 *       Purpose: Provide a grouping of component properties that defines a
 *       time zone.
 *    
 *       Formal Definition: A &quot;VTIMEZONE&quot; calendar component is defined by the
 *       following notation:
 *    
 *         timezonec  = &quot;BEGIN&quot; &quot;:&quot; &quot;VTIMEZONE&quot; CRLF
 *    
 *                      2*(
 *    
 *                      ; 'tzid' is required, but MUST NOT occur more
 *                      ; than once
 *    
 *                    tzid /
 *    
 *                      ; 'last-mod' and 'tzurl' are optional,
 *                    but MUST NOT occur more than once
 *    
 *                    last-mod / tzurl /
 *    
 *                      ; one of 'standardc' or 'daylightc' MUST occur
 *                    ..; and each MAY occur more than once.
 *    
 *                    standardc / daylightc /
 *    
 *                    ; the following is optional,
 *                    ; and MAY occur more than once
 *    
 *                      x-prop
 *    
 *                      )
 *    
 *                      &quot;END&quot; &quot;:&quot; &quot;VTIMEZONE&quot; CRLF
 *    
 *         standardc  = &quot;BEGIN&quot; &quot;:&quot; &quot;STANDARD&quot; CRLF
 *    
 *                      tzprop
 *    
 *                      &quot;END&quot; &quot;:&quot; &quot;STANDARD&quot; CRLF
 *    
 *         daylightc  = &quot;BEGIN&quot; &quot;:&quot; &quot;DAYLIGHT&quot; CRLF
 *    
 *                      tzprop
 *    
 *                      &quot;END&quot; &quot;:&quot; &quot;DAYLIGHT&quot; CRLF
 *    
 *         tzprop     = 3*(
 *    
 *                    ; the following are each REQUIRED,
 *                    ; but MUST NOT occur more than once
 *    
 *                    dtstart / tzoffsetto / tzoffsetfrom /
 *    
 *                    ; the following are optional,
 *                    ; and MAY occur more than once
 *    
 *                    comment / rdate / rrule / tzname / x-prop
 *    
 *                    )
 * </pre>
 * 
 * @author Ben Fortuna
 */
public class VTimeZone extends Component {

    private static final long serialVersionUID = 5629679741050917815L;

    private static Log log = LogFactory.getLog(VTimeZone.class);

    private static Map cache = new HashMap();

    private ComponentList types;

    private net.fortuna.ical4j.model.parameter.TzId tzIdParam;

    /**
     * A Java timezone representation of this VTimeZone.
     */
    private TimeZone timeZone;

    /**
     * Constructs a new instance containing the specified properties.
     * 
     * @param properties
     *            a list of properties
     */
    public VTimeZone(final PropertyList properties) {
        super(VTIMEZONE, properties);
        this.types = new ComponentList();
    }

    /**
     * Constructs a new vtimezone component with no properties and the specified
     * list of type components.
     * 
     * @param types
     *            a list of type components
     */
    public VTimeZone(final ComponentList types) {
        super(VTIMEZONE);
        this.types = types;
    }

    /**
     * Constructor.
     * 
     * @param properties
     *            a list of properties
     * @param types
     *            a list of timezone types
     */
    public VTimeZone(final PropertyList properties, final ComponentList types) {
        super(VTIMEZONE, properties);
        this.types = types;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public final String toString() {
        return BEGIN + ":" + getName() + "\r\n" + getProperties() + types + END
                + ":" + getName() + "\r\n";
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.fortuna.ical4j.model.Component#validate(boolean)
     */
    public final void validate(final boolean recurse)
            throws ValidationException {

        /*
         * ; 'tzid' is required, but MUST NOT occur more ; than once
         * 
         * tzid /
         */
        PropertyValidator.getInstance().validateOne(Property.TZID,
                getProperties());

        /*
         * ; 'last-mod' and 'tzurl' are optional, but MUST NOT occur more than
         * once last-mod / tzurl /
         */
        PropertyValidator.getInstance().validateOneOrLess(
                Property.LAST_MODIFIED, getProperties());
        PropertyValidator.getInstance().validateOneOrLess(Property.TZURL,
                getProperties());

        /*
         * ; one of 'standardc' or 'daylightc' MUST occur ..; and each MAY occur
         * more than once.
         * 
         * standardc / daylightc /
         */
        if (getTypes().getComponent(SeasonalTime.STANDARD) == null
                && getTypes().getComponent(SeasonalTime.DAYLIGHT) == null) {
            throw new ValidationException("Sub-components ["
                    + SeasonalTime.STANDARD + "," + SeasonalTime.DAYLIGHT
                    + "] must be specified at least once");
        }

        /*
         * ; the following is optional, ; and MAY occur more than once
         * 
         * x-prop
         */

        if (recurse) {
            validateProperties();
        }
    }

    /**
     * Returns an instance of VTimeZone representing the user's default
     * timezone.
     * 
     * @return a VTimeZone
     */
    public static VTimeZone getDefault() {
        return getVTimeZone(TimeZone.getDefault().getID());
    }

    /**
     * Returns an instance of VTimeZone corresponding to the specified timezone
     * id.
     * 
     * @param id
     *            a timezone identifier
     * @return
     */
    public static VTimeZone getVTimeZone(final String id) {
        VTimeZone vTimeZone = (VTimeZone) cache.get(id);
        if (vTimeZone == null) {
            try {
                vTimeZone = loadVTimeZone(id);
            } catch (Exception e) {
                log.debug("Error loading VTimeZone", e);
            }
            if (vTimeZone == null) {
                vTimeZone = createVTimeZone(id);
            }
            if (vTimeZone != null) {
                cache.put(id, vTimeZone);
            }
        }
        return vTimeZone;
    }

    /**
     * Loads an existing VTimeZone from the classpath corresponding to the
     * specified Java timezone.
     */
    private static VTimeZone loadVTimeZone(final String id) throws IOException,
            ParserException {
        String resource = "/" + id + ".ics";

        CalendarBuilder builder = new CalendarBuilder();

        Calendar calendar = builder.build(VTimeZone.class
                .getResourceAsStream(resource));

        return (VTimeZone) calendar.getComponents().getComponent(
                Component.VTIMEZONE);
    }

    /**
     * Creates a new VTimeZone based on the specified Java timezone.
     */
    private static VTimeZone createVTimeZone(final String id) {
        TimeZone timezone = TimeZone.getTimeZone(id);

        TzId tzId = new TzId(timezone.getID());
        PropertyList tzProps = new PropertyList();
        tzProps.add(tzId);

        TzName standardTzName = new TzName(new ParameterList(), timezone
                .getDisplayName());
        DtStart standardTzStart = new DtStart(new ParameterList(), new Date(
                TimeZoneUtils.getDaylightEnd(timezone).getTime()));
        TzOffsetTo standardTzOffsetTo = new TzOffsetTo(new ParameterList(),
                new UtcOffset(timezone.getRawOffset()));
        TzOffsetFrom standardTzOffsetFrom = new TzOffsetFrom(new UtcOffset(
                timezone.getRawOffset() + timezone.getDSTSavings()));
        PropertyList standardTzProps = new PropertyList();
        standardTzProps.add(standardTzName);
        standardTzProps.add(standardTzStart);
        standardTzProps.add(standardTzOffsetTo);
        standardTzProps.add(standardTzOffsetFrom);

        ComponentList tzComponents = new ComponentList();
        tzComponents.add(new Standard(standardTzProps));

        if (timezone.useDaylightTime()) {
            TzName daylightTzName = new TzName(new ParameterList(), timezone
                    .getDisplayName()
                    + " (DST)");
            DtStart daylightTzStart = new DtStart(
                    new ParameterList(),
                    new Date(TimeZoneUtils.getDaylightStart(timezone).getTime()));
            TzOffsetTo daylightTzOffsetTo = new TzOffsetTo(new ParameterList(),
                    new UtcOffset(timezone.getRawOffset()
                            + timezone.getDSTSavings()));
            TzOffsetFrom daylightTzOffsetFrom = new TzOffsetFrom(new UtcOffset(
                    timezone.getRawOffset()));

            PropertyList daylightTzProps = new PropertyList();
            daylightTzProps.add(daylightTzName);
            daylightTzProps.add(daylightTzStart);
            daylightTzProps.add(daylightTzOffsetTo);
            daylightTzProps.add(daylightTzOffsetFrom);

            tzComponents.add(new Daylight(daylightTzProps));
        }

        return new VTimeZone(tzProps, tzComponents);
    }

    /**
     * @return Returns the types.
     */
    public final ComponentList getTypes() {
        return types;
    }

    /**
     * @return
     */
    public final net.fortuna.ical4j.model.parameter.TzId getTzIdParam() {
        if (tzIdParam == null) {
            tzIdParam = new net.fortuna.ical4j.model.parameter.TzId(
                    getProperties().getProperty(Property.TZID).getValue());
        }
        return tzIdParam;
    }

    // FIXED (CR4): add the getTimeZone() method and helpers

    /**
     * Create a Java TimeZone (instance of SimpleTimeZone) representing this
     * VTimeZone <b>Note:</b> The time zone returned will <b>NOT</b> be a
     * predefined java TimeZone (e.g. America/Denver) Rather a new TimeZone will
     * be created, with an ID equal to the value of TZID.
     * 
     * @throws ParseException
     *             if the time zone canot be created due to spec a vioalation
     * @return A <code>java.util.TimeZone</code> instance representing this VTimeZone
     */
    public final TimeZone getTimeZone() {
        if (timeZone == null) {
            String tzid = getProperties().getProperty(Property.TZID).getValue();
            final ComponentList types = getTypes();
            final Component std = types.getComponent(SeasonalTime.STANDARD);
            final Component dl = types.getComponent(SeasonalTime.DAYLIGHT);

            final TzInfo stdInfo = getTzInfo(std);
            final TzInfo dlInfo = getTzInfo(dl);

            if (stdInfo != null && dlInfo == null) {
                timeZone = new SimpleTimeZone(stdInfo.getOffset(), tzid);
            } else if (stdInfo == null && dlInfo != null) {
                timeZone = new SimpleTimeZone(dlInfo.getOffset(), tzid);
            } else if (stdInfo != null && dlInfo != null) {
                timeZone = new SimpleTimeZone(stdInfo.getOffset(), tzid, dlInfo
                        .getMonth(), dlInfo.getWeekOffset(), dlInfo.getDay(),
                        dlInfo.getTime(), stdInfo.getMonth(), stdInfo
                                .getWeekOffset(), stdInfo.getDay(), stdInfo
                                .getTime(), dlInfo.getOffset()
                                - stdInfo.getOffset());
            } else {
                /*
                throw new ParseException(
                        "Time Zone must contain at least 1 STANDARD or DAYLIGHT section.",
                        -1);
                 */
                log.warn("Time Zone must contain at least 1 STANDARD or DAYLIGHT section.");
            }
        }
        return timeZone;
    }

    /**
     * @param tzComp
     * @return
     * @throws ParseException
     */
    private TzInfo getTzInfo(final Component tzComp) {
        if (tzComp == null) {
            return null;
        }
        final PropertyList properties = tzComp.getProperties();
        final String offsetStr = properties.getProperty(Property.TZOFFSETTO)
                .getValue();
        final RRule rruleProp = (RRule) properties.getProperty(Property.RRULE);

        final int weekOffset;
        final int day;
        final int month;

        if (rruleProp != null) {
            final Recur recur = rruleProp.getRecur();
            final WeekDay weekDay = (WeekDay) recur.getDayList().get(0);
            weekOffset = weekDay.getOffset();
            // day = ((Integer) DAY_MAP.get(weekDay.getDay())).intValue() ;
            day = WeekDay.getCalendarDay(weekDay);
            month = ((Integer) recur.getMonthList().get(0)).intValue() - 1;
        } else {
            weekOffset = 0;
            day = 0;
            month = 0;
        }

        final Date date = ((DtStart) properties.getProperty(Property.DTSTART))
                .getTime();
        final java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(date);
        final int time = (int) Dates.MILLIS_PER_HOUR
                * cal.get(java.util.Calendar.HOUR_OF_DAY)
                + (int) Dates.MILLIS_PER_MINUTE
                * cal.get(java.util.Calendar.MINUTE);
        final int offset = convertOffset(offsetStr);

        return new TzInfo(month, day, weekOffset, time, offset);
    }

    /**
     * @param strOffset
     * @return
     */
    private int convertOffset(final String strOffset) {
        final int minStart = strOffset.length() - 2;
        final String hours = strOffset.substring(0, minStart);
        final String mins = strOffset.substring(minStart);

        int offset = (int) Dates.MILLIS_PER_HOUR * Integer.parseInt(hours)
                + (int) Dates.MILLIS_PER_MINUTE * Integer.parseInt(mins);
        return offset;
    }

    /**
     * @author npilke
     *
     */
    private static class TzInfo {
        private final int weekOffset;

        private final int day;

        private final int time;

        private final int offset;

        private final int month;

        public TzInfo(final int month, final int day, final int weekOffset, final int time, final int offset) {
            this.month = month;
            this.day = day;
            this.offset = offset;
            this.time = time;
            this.weekOffset = weekOffset;
        }

        public int getMonth() {
            return month;
        }

        public int getDay() {
            return day;
        }

        public int getOffset() {
            return offset;
        }

        public int getTime() {
            return time;
        }

        public int getWeekOffset() {
            return weekOffset;
        }
    }
}
