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
import java.text.ParseException;
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
import net.fortuna.ical4j.util.PropertyValidator;
import net.fortuna.ical4j.util.TimeZoneUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Defines an iCalendar VTIMEZONE component.
 *
 * <pre>
 *   4.6.5 Time Zone Component
 *   
 *      Component Name: VTIMEZONE
 *   
 *      Purpose: Provide a grouping of component properties that defines a
 *      time zone.
 *   
 *      Formal Definition: A "VTIMEZONE" calendar component is defined by the
 *      following notation:
 *   
 *        timezonec  = "BEGIN" ":" "VTIMEZONE" CRLF
 *   
 *                     2*(
 *   
 *                     ; 'tzid' is required, but MUST NOT occur more
 *                     ; than once
 *   
 *                   tzid /
 *   
 *                     ; 'last-mod' and 'tzurl' are optional,
 *                   but MUST NOT occur more than once
 *   
 *                   last-mod / tzurl /
 *   
 *                     ; one of 'standardc' or 'daylightc' MUST occur
 *                   ..; and each MAY occur more than once.
 *   
 *                   standardc / daylightc /
 *   
 *                   ; the following is optional,
 *                   ; and MAY occur more than once
 *   
 *                     x-prop
 *   
 *                     )
 *   
 *                     "END" ":" "VTIMEZONE" CRLF
 *   
 *        standardc  = "BEGIN" ":" "STANDARD" CRLF
 *   
 *                     tzprop
 *   
 *                     "END" ":" "STANDARD" CRLF
 *   
 *        daylightc  = "BEGIN" ":" "DAYLIGHT" CRLF
 *   
 *                     tzprop
 *   
 *                     "END" ":" "DAYLIGHT" CRLF
 *   
 *        tzprop     = 3*(
 *   
 *                   ; the following are each REQUIRED,
 *                   ; but MUST NOT occur more than once
 *   
 *                   dtstart / tzoffsetto / tzoffsetfrom /
 *   
 *                   ; the following are optional,
 *                   ; and MAY occur more than once
 *   
 *                   comment / rdate / rrule / tzname / x-prop
 *   
 *                   )
 * </pre>
 * 
 * @author Ben Fortuna
 */
public class VTimeZone extends Component {
    
    private static final long serialVersionUID = 5629679741050917815L;
	
	//FIXED (CR4): Create a cache map tzid to the java TimeZone 
	private static final Map tzMap = new HashMap(100) ;
	
	//FIXED (CR4): time constants
	private static final int MINUTE_IN_MILLIS = 60 * 1000;
	private static final int HOUR_IN_MILLIS = 60 * MINUTE_IN_MILLIS ;

    private static Log log = LogFactory.getLog(VTimeZone.class);
    
    //FIXED (CR4): create map of Ical days to Calendar days
	private static Map DAY_MAP = new HashMap(7) ;
	static {
		DAY_MAP.put(WeekDay.SU.getDay(), new Integer(java.util.Calendar.SUNDAY)) ;
		DAY_MAP.put(WeekDay.MO.getDay(), new Integer(java.util.Calendar.MONDAY)) ;
		DAY_MAP.put(WeekDay.TU.getDay(), new Integer(java.util.Calendar.TUESDAY)) ;
		DAY_MAP.put(WeekDay.WE.getDay(), new Integer(java.util.Calendar.WEDNESDAY)) ;
		DAY_MAP.put(WeekDay.TH.getDay(), new Integer(java.util.Calendar.THURSDAY)) ;
		DAY_MAP.put(WeekDay.FR.getDay(), new Integer(java.util.Calendar.FRIDAY)) ;
		DAY_MAP.put(WeekDay.SA.getDay(), new Integer(java.util.Calendar.SATURDAY)) ;
	}

    private ComponentList types;

    /**
     * Constructs a new instance containing the specified properties.
     * @param properties a list of properties
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
    public final void validate(final boolean recurse) throws ValidationException {

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
            throw new ValidationException(
                "Sub-components [" + SeasonalTime.STANDARD + "," + SeasonalTime.DAYLIGHT
                        + "] must be specified at least once"); }

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
        return getVTimeZone(TimeZone.getDefault());
    }

    /**
     * Returns an instance of VTimeZone representing the specified Java
     * timezone.
     *
     * @return a VTimeZone
     */
    public static VTimeZone getVTimeZone(final TimeZone timezone) {
        try {
            VTimeZone vTimezone = loadVTimeZone(timezone);

            if (vTimezone != null) { return vTimezone; }
        }
        catch (Exception e) {
            log.debug("Error loading VTimeZone", e);
        }

        return createVTimeZone(timezone);
    }

    /**
     * Loads an existing VTimeZone from the classpath corresponding to the
     * specified Java timezone.
     */
    private static VTimeZone loadVTimeZone(final TimeZone timezone)
            throws IOException, ParserException {
        String resource = "/" + timezone.getID() + ".ics";

        CalendarBuilder builder = new CalendarBuilder();

        Calendar calendar = builder.build(VTimeZone.class
                .getResourceAsStream(resource));

        return (VTimeZone) calendar.getComponents().getComponent(
                Component.VTIMEZONE);
    }

    /**
     * Creates a new VTimeZone based on the specified Java timezone.
     */
    private static VTimeZone createVTimeZone(final TimeZone timezone) {
        TzId tzId = new TzId(timezone.getID());

        PropertyList tzProps = new PropertyList();
        tzProps.add(tzId);

        ComponentList tzComponents = new ComponentList();

        TzName standardTzName = new TzName(new ParameterList(), timezone
                .getDisplayName());
        DtStart standardTzStart = new DtStart(new ParameterList(),
                new Date(TimeZoneUtils.getDaylightEnd(timezone).getTime()));
        TzOffsetTo standardTzOffsetTo = new TzOffsetTo(new ParameterList(),
                new UtcOffset(timezone.getRawOffset()));
        TzOffsetFrom standardTzOffsetFrom = new TzOffsetFrom(new UtcOffset(timezone
                .getRawOffset()
                + timezone.getDSTSavings()));

        PropertyList standardTzProps = new PropertyList();
        standardTzProps.add(standardTzName);
        standardTzProps.add(standardTzStart);
        standardTzProps.add(standardTzOffsetTo);
        standardTzProps.add(standardTzOffsetFrom);

        tzComponents.add(new Standard(standardTzProps));

        if (timezone.useDaylightTime()) {
            TzName daylightTzName = new TzName(new ParameterList(), timezone
                    .getDisplayName()
                    + " (DST)");
            DtStart daylightTzStart = new DtStart(new ParameterList(),
                    new Date(TimeZoneUtils.getDaylightStart(timezone).getTime()));
            TzOffsetTo daylightTzOffsetTo = new TzOffsetTo(new ParameterList(),
                    new UtcOffset(timezone.getRawOffset() + timezone.getDSTSavings()));
            TzOffsetFrom daylightTzOffsetFrom = new TzOffsetFrom(new UtcOffset(timezone
                    .getRawOffset()));

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
    
    //FIXED (CR4): add the getTimeZone() method and helpers
    
	/** Create a Java TimeZone (instance of SimpleTimeZone) representing this VTimeZone
	 * <b>Note:</b> The time zone returned will <b>NOT</b> be a predefined java TimeZone (e.g. America/Denver)
	 * Rather a new TimeZone will be created, with an ID equal to the value of TZID.
	 * 
	 * @throws ParseException if the time zone canot be created due to spec a vioalation
	 * @return An instance of SimpleTimeZone representing the this VTimeZone
	 */
	public TimeZone getTimeZone() throws ParseException {
		final String tzid = getProperties().getProperty(Property.TZID).getValue() ;
		TimeZone tz = (TimeZone) tzMap.get(tzid) ;
		
		if (tz == null) {
			synchronized (tzMap) {
				if (!tzMap.containsKey(tzid)) {
					final ComponentList types = getTypes();
					final Component std = types.getComponent(SeasonalTime.STANDARD) ;
					final Component dl = types.getComponent(SeasonalTime.DAYLIGHT) ;
					
					final TzInfo stdInfo = getTzInfo(std);
					final TzInfo dlInfo = getTzInfo(dl);
					
					if (stdInfo != null && dlInfo == null) {
						tz = new SimpleTimeZone(stdInfo.getOffset(), tzid) ;
					} else if (stdInfo == null && dlInfo != null) {
						tz = new SimpleTimeZone(dlInfo.getOffset(), tzid) ;
					} else if (stdInfo != null && dlInfo != null) {
						tz = new SimpleTimeZone(stdInfo.getOffset(), tzid, 
								dlInfo.getMonth(), dlInfo.getWeekOffset(), dlInfo.getDay(), dlInfo.getTime(),
								stdInfo.getMonth(), stdInfo.getWeekOffset(), stdInfo.getDay(), stdInfo.getTime(),
								dlInfo.getOffset() - stdInfo.getOffset()) ;
					} else {
						throw new ParseException("Time Zone must contain at least 1 STANDARD or DAYLIGHT section.", -1) ;
					}
					
					tzMap.put(tzid, tz) ;
				}
			}
		}
		return tz ;
	}

	private TzInfo getTzInfo(final Component tzComp) throws ParseException {
		if (tzComp == null) {
			return null ;
		}
		final PropertyList properties = tzComp.getProperties();
		final String offsetStr = properties.getProperty(Property.TZOFFSETTO).getValue() ;
		final RRule rruleProp = (RRule) properties.getProperty(Property.RRULE);

		final int weekOffset ;
		final int day ;
		final int month ;

		if (rruleProp != null) {
			final Recur recur = rruleProp.getRecur() ;
			final WeekDay weekDay = (WeekDay) recur.getDayList().get(0);
			weekOffset = weekDay.getOffset() ;
			day = ((Integer) DAY_MAP.get(weekDay.getDay())).intValue() ;
			month = ((Integer) recur.getMonthList().get(0)).intValue() - 1;
		} else {
			weekOffset = 0 ;
			day = 0 ;
			month = 0 ;
		}
		
		final Date date = ((DtStart) properties.getProperty(Property.DTSTART)).getTime() ;
		final java.util.Calendar cal = java.util.Calendar.getInstance() ;
		cal.setTime(date) ;
		final int time = HOUR_IN_MILLIS * cal.get(java.util.Calendar.HOUR_OF_DAY) + 
			MINUTE_IN_MILLIS * cal.get(java.util.Calendar.MINUTE) ;
		final int offset = convertOffset(offsetStr);
		
		return new TzInfo(month, day, weekOffset, time, offset) ;
	}

	private int convertOffset(final String strOffset) {
		final int minStart = strOffset.length() - 2;
		final String hours = strOffset.substring(0, minStart) ;
		final String mins = strOffset.substring(minStart) ;
	
		int offset = HOUR_IN_MILLIS * Integer.parseInt(hours) + MINUTE_IN_MILLIS * Integer.parseInt(mins) ;
		return offset;
	}

	private static class TzInfo {
		private final int weekOffset ;
		private final int day ;
		private final int time ;
		private final int offset ;
		private final int month ;
		
		public TzInfo(int month, int day, int weekOffset, int time, int offset) {
			this.month = month ;
			this.day = day;
			this.offset = offset;
			this.time = time;
			this.weekOffset = weekOffset;
		}
		public int getMonth() {
			return month ;
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