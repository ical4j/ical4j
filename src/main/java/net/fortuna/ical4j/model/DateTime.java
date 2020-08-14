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
package net.fortuna.ical4j.model;

import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.util.Dates;
import net.fortuna.ical4j.util.TimeZones;
import org.apache.commons.lang3.builder.EqualsBuilder;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * A representation of the DATE-TIME object defined in RFC5445.
 * <p/>
 * <em>Extract from RFC5545:</em>
 * <p/>
 * <pre>
 * 3.3.5.  Date-Time
 *
 * Value Name:  DATE-TIME
 *
 * Purpose:  This value type is used to identify values that specify a
 * precise calendar date and time of day.
 *
 * Format Definition:  This value type is defined by the following
 * notation:
 *
 * date-time  = date "T" time ;As specified in the DATE and TIME
 * ;value definitions
 *
 * Description:  If the property permits, multiple "DATE-TIME" values
 * are specified as a COMMA-separated list of values.  No additional
 * content value encoding (i.e., BACKSLASH character encoding, see
 * Section 3.3.11) is defined for this value type.
 *
 * The "DATE-TIME" value type is used to identify values that contain
 * a precise calendar date and time of day.  The format is based on
 * the [ISO.8601.2004] complete representation, basic format for a
 * calendar date and time of day.  The text format is a concatenation
 * of the "date", followed by the LATIN CAPITAL LETTER T character,
 * the time designator, followed by the "time" format.
 *
 * The "DATE-TIME" value type expresses time values in three forms:
 *
 * The form of date and time with UTC offset MUST NOT be used.  For
 * example, the following is not valid for a DATE-TIME value:
 *
 * 19980119T230000-0800       ;Invalid time format
 *
 * FORM #1: DATE WITH LOCAL TIME
 *
 * The date with local time form is simply a DATE-TIME value that
 * does not contain the UTC designator nor does it reference a time
 * zone.  For example, the following represents January 18, 1998, at
 * 11 PM:
 *
 * 19980118T230000
 *
 * DATE-TIME values of this type are said to be "floating" and are
 * not bound to any time zone in particular.  They are used to
 * represent the same hour, minute, and second value regardless of
 * which time zone is currently being observed.  For example, an
 * event can be defined that indicates that an individual will be
 * busy from 11:00 AM to 1:00 PM every day, no matter which time zone
 * the person is in.  In these cases, a local time can be specified.
 * The recipient of an iCalendar object with a property value
 * consisting of a local time, without any relative time zone
 * information, SHOULD interpret the value as being fixed to whatever
 * time zone the "ATTENDEE" is in at any given moment.  This means
 * that two "Attendees", in different time zones, receiving the same
 * event definition as a floating time, may be participating in the
 * event at different actual times.  Floating time SHOULD only be
 * used where that is the reasonable behavior.
 *
 * In most cases, a fixed time is desired.  To properly communicate a
 * fixed time in a property value, either UTC time or local time with
 * time zone reference MUST be specified.
 *
 * The use of local time in a DATE-TIME value without the "TZID"
 * property parameter is to be interpreted as floating time,
 * regardless of the existence of "VTIMEZONE" calendar components in
 * the iCalendar object.
 *
 * FORM #2: DATE WITH UTC TIME
 *
 * The date with UTC time, or absolute time, is identified by a LATIN
 * CAPITAL LETTER Z suffix character, the UTC designator, appended to
 * the time value.  For example, the following represents January 19,
 * 1998, at 0700 UTC:
 *
 * 19980119T070000Z
 *
 * The "TZID" property parameter MUST NOT be applied to DATE-TIME
 * properties whose time values are specified in UTC.
 *
 * FORM #3: DATE WITH LOCAL TIME AND TIME ZONE REFERENCE
 *
 * The date and local time with reference to time zone information is
 * identified by the use the "TZID" property parameter to reference
 * the appropriate time zone definition.  "TZID" is discussed in
 * detail in Section 3.2.19.  For example, the following represents
 * 2:00 A.M. in New York on January 19, 1998:
 *
 * TZID=America/New_York:19980119T020000
 *
 * If, based on the definition of the referenced time zone, the local
 * time described occurs more than once (when changing from daylight
 * to standard time), the DATE-TIME value refers to the first
 * occurrence of the referenced time.  Thus, TZID=America/
 * New_York:20071104T013000 indicates November 4, 2007 at 1:30 A.M.
 * EDT (UTC-04:00).  If the local time described does not occur (when
 * changing from standard to daylight time), the DATE-TIME value is
 * interpreted using the UTC offset before the gap in local times.
 * Thus, TZID=America/New_York:20070311T023000 indicates March 11,
 * 2007 at 3:30 A.M. EDT (UTC-04:00), one hour after 1:30 A.M. EST
 * (UTC-05:00).
 *
 * A time value MUST only specify the second 60 when specifying a
 * positive leap second.  For example:
 *
 * 19970630T235960Z
 *
 * Implementations that do not support leap seconds SHOULD interpret
 * the second 60 as equivalent to the second 59.
 *
 * Example:  The following represents July 14, 1997, at 1:30 PM in New
 * York City in each of the three time formats, using the "DTSTART"
 * property.
 *
 * DTSTART:19970714T133000                   ; Local time
 * DTSTART:19970714T173000Z                  ; UTC time
 * DTSTART;TZID=America/New_York:19970714T133000
 * ; Local time and time
 * ; zone reference
 * </pre>
 * 
 * @author Ben Fortuna
 * @version 2.0
 */
public class DateTime extends Date {

	private static final long serialVersionUID = -6407231357919440387L;

	private static final String DEFAULT_PATTERN = "yyyyMMdd'T'HHmmss";

	private static final String UTC_PATTERN = "yyyyMMdd'T'HHmmss'Z'";
	
	private static final String VCARD_PATTERN = "yyyy'-'MM'-'dd'T'HH':'mm':'ss'Z'";

	private static final String RELAXED_PATTERN = "yyyyMMdd";

	/**
	 * Used for parsing times in a UTC date-time representation.
	 */
	private static final DateFormatCache UTC_FORMAT;
	static {
		final DateFormat format = new SimpleDateFormat(UTC_PATTERN);
		format.setTimeZone(TimeZones.getUtcTimeZone());
		format.setLenient(false);

		UTC_FORMAT = new DateFormatCache(format);
	}

	/**
	 * Used for parsing times in a local date-time representation.
	 */
	private static final DateFormatCache DEFAULT_FORMAT;
	static {
		final DateFormat format = new SimpleDateFormat(DEFAULT_PATTERN);
		format.setLenient(false);
		DEFAULT_FORMAT = new DateFormatCache(format);
	}

	private static final DateFormatCache LENIENT_DEFAULT_FORMAT;
	static {
		final DateFormat format = new SimpleDateFormat(DEFAULT_PATTERN);
		LENIENT_DEFAULT_FORMAT = new DateFormatCache(format);
	}

	private static final DateFormatCache RELAXED_FORMAT;
	static {
		final DateFormat format = new SimpleDateFormat(RELAXED_PATTERN);
        format.setLenient(true);
		RELAXED_FORMAT = new DateFormatCache(format);
	}

	private static final DateFormatCache VCARD_FORMAT;
	static {
		final DateFormat format = new SimpleDateFormat(VCARD_PATTERN);
        VCARD_FORMAT = new DateFormatCache(format);
	}

	private Time time;

	private TimeZone timezone;

	/**
	 * Default constructor.
	 */
	public DateTime() {
		super(Dates.PRECISION_SECOND, java.util.TimeZone.getDefault());
		this.time = new Time(getTime(), getFormat().getTimeZone());
	}

	/**
	 * @param utc
	 *            indicates if the date is in UTC time
	 */
	public DateTime(final boolean utc) {
		this();
		setUtc(utc);
	}

	/**
	 * @param time
	 *            a date-time value in milliseconds
	 */
	public DateTime(final long time) {
		super(time, Dates.PRECISION_SECOND, java.util.TimeZone.getDefault());
		this.time = new Time(time, getFormat().getTimeZone());
	}

	/**
	 * @param date
	 *            a date-time value
	 */
	public DateTime(final java.util.Date date) {
		super(date.getTime(), Dates.PRECISION_SECOND, java.util.TimeZone.getDefault());
		this.time = new Time(date.getTime(), getFormat().getTimeZone());
		// copy timezone information if applicable..
		if (date instanceof DateTime) {
			final DateTime dateTime = (DateTime) date;
			if (dateTime.isUtc()) {
				setUtc(true);
			} else {
				setTimeZone(dateTime.getTimeZone());
			}
		}
	}

	public DateTime(final java.util.Date date, TimeZone timeZone) {
		this(date);
		setTimeZone(timeZone);
	}

	/**
	 * Constructs a new DateTime instance from parsing the specified string
	 * representation in the default (local) timezone.
	 * 
	 * @param value
	 *            a string representation of a date-time
	 * @throws ParseException
	 *             where the specified string is not a valid date-time
	 */
	public DateTime(final String value) throws ParseException {
		this(value, null);
		/*
		 * long time = 0; try { synchronized (UTC_FORMAT) { time =
		 * UTC_FORMAT.parse(value).getTime(); } setUtc(true); } catch
		 * (ParseException pe) { synchronized (DEFAULT_FORMAT) {
		 * DEFAULT_FORMAT.setTimeZone(getFormat().getTimeZone()); time =
		 * DEFAULT_FORMAT.parse(value).getTime(); } this.time = new Time(time,
		 * getFormat().getTimeZone()); } setTime(time);
		 */
	}

	/**
	 * Creates a new date-time instance from the specified value in the given
	 * timezone. If a timezone is not specified, the default timezone (as
	 * returned by {@link TimeZones#getDefault()}) is used.
	 * 
	 * @param value
	 *            a string representation of a date-time
	 * @param timezone
	 *            the timezone for the date-time instance
	 * @throws ParseException
	 *             where the specified string is not a valid date-time
	 */
	public DateTime(final String value, final TimeZone timezone)
			throws ParseException {
		// setting the time to 0 since we are going to reset it anyway
		super(0, Dates.PRECISION_SECOND, timezone != null ? timezone : TimeZones.getDefault());
		this.time = new Time(getTime(), getFormat().getTimeZone());

        try {
            if (value.endsWith("Z")) {
                setTime(value, UTC_FORMAT.get(), null);
                setUtc(true);
            } else {
                if (timezone != null) {
                    setTime(value, DEFAULT_FORMAT.get(), timezone);
                } else {
                    // Use lenient parsing for floating times. This is to
                    // overcome
                    // the problem of parsing VTimeZone dates that specify dates
                    // that the strict parser does not accept.
                    setTime(value, LENIENT_DEFAULT_FORMAT.get(),
                            getFormat().getTimeZone());
                }
                setTimeZone(timezone);
            }
        } catch (ParseException pe) {
            if (CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_VCARD_COMPATIBILITY)) {

            	try {
	                setTime(value, VCARD_FORMAT.get(), timezone);
	                setTimeZone(timezone);
            	} catch (ParseException pe2) {
                    if (CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING)) {
    	                setTime(value, RELAXED_FORMAT.get(), timezone);
    	                setTimeZone(timezone);
                    }            		
            	}
            } else if (CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING)) {
                setTime(value, RELAXED_FORMAT.get(), timezone);
                setTimeZone(timezone);
            } else {
                throw pe;
            }
        }
	}

	/**
	 * @param value
	 *            a string representation of a date-time
	 * @param pattern
	 *            a pattern to apply when parsing the date-time value
	 * @param timezone
	 *            the timezone for the date-time instance
	 * @throws ParseException
	 *             where the specified string is not a valid date-time
	 */
	public DateTime(String value, String pattern, TimeZone timezone)
			throws ParseException {
		// setting the time to 0 since we are going to reset it anyway
		super(0, Dates.PRECISION_SECOND, timezone != null ? timezone : TimeZones.getDefault());
		this.time = new Time(getTime(), getFormat().getTimeZone());

		final DateFormat format = CalendarDateFormatFactory
				.getInstance(pattern);
		setTime(value, format, timezone);
	}

	/**
	 * @param value
	 *            a string representation of a date-time
	 * @param pattern
	 *            a pattern to apply when parsing the date-time value
	 * @param utc
	 *            indicates whether the date-time is in UTC time
	 * @throws ParseException
	 *             where the specified string is not a valid date-time
	 */
	public DateTime(String value, String pattern, boolean utc)
			throws ParseException {
                // setting the time to 0 since we are going to reset it anyway
		this(0);
		final DateFormat format = CalendarDateFormatFactory
				.getInstance(pattern);
		if (utc) {
			setTime(value, format,
					UTC_FORMAT.get().getTimeZone());
		} else {
			setTime(value, format, null);
		}
		setUtc(utc);
	}

	/**
	 * Internal set of time by parsing value string.
	 * 
	 * @param value
	 * @param format
	 *            a {@code DateFormat}, protected by the use of a ThreadLocal.
	 * @param tz
	 * @throws ParseException
	 */
	private void setTime(final String value, final DateFormat format,
			final java.util.TimeZone tz) throws ParseException {

		if (tz != null) {
			format.setTimeZone(tz);
		}
		setTime(format.parse(value).getTime());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setTime(final long time) {
		super.setTime(time);
		// need to check for null time due to Android java.util.Date(long)
		// constructor
		// calling this method..
		if (this.time != null) {
			this.time.setTime(time);
		}
	}

	/**
	 * @return Returns the utc.
	 */
	public final boolean isUtc() {
		return time.isUtc();
	}

	/**
	 * Updates this date-time to display in UTC time if the argument is true.
	 * Otherwise, resets to the default timezone.
	 * 
	 * @param utc
	 *            The utc to set.
	 */
	public final void setUtc(final boolean utc) {
		// reset the timezone associated with this instance..
		this.timezone = null;
		if (utc) {
			getFormat().setTimeZone(TimeZones.getUtcTimeZone());
		} else {
			resetTimeZone();
		}
		time = new Time(time, getFormat().getTimeZone(), utc);
	}

	/**
	 * Sets the timezone associated with this date-time instance. If the
	 * specified timezone is null, it will reset to the default timezone. If the
	 * date-time instance is utc, it will turn into either a floating (no
	 * timezone) date-time, or a date-time with a timezone.
	 * 
	 * @param timezone
	 *            a timezone to apply to the instance
	 */
	public final void setTimeZone(final TimeZone timezone) {
		this.timezone = timezone;
		if (timezone != null) {
			getFormat().setTimeZone(timezone);
		} else {
			resetTimeZone();
		}
		time = new Time(time, getFormat().getTimeZone(), false);
	}

	/**
	 * Reset the timezone to default.
	 */
	private void resetTimeZone() {
		// use GMT timezone to avoid daylight savings rules affecting floating
		// time values..
		getFormat().setTimeZone(TimeZones.getDefault());
		// getFormat().setTimeZone(TimeZone.getTimeZone(TimeZones.GMT_ID));
	}

	/**
	 * Returns the current timezone associated with this date-time value.
	 * 
	 * @return a Java timezone
	 */
	public final TimeZone getTimeZone() {
		return timezone;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
		return super.toString() + 'T' +
				time.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object arg0) {
		// TODO: what about compareTo, before, after, etc.?

		if (arg0 instanceof DateTime) {
			return new EqualsBuilder().append(time, ((DateTime) arg0).time)
					.isEquals();
		}
		return super.equals(arg0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
	}

	/**
	 * This cache class is a workaround for DateFormat not being threadsafe.
	 * We maintain map from Thread to DateFormat instance so that the instances
	 * are not shared between threads (effectively a ThreadLocal).
	 * TODO: once the project targets Java 8+, the new date utilities are
	 * thread-safe and we should remove this code.
	 */
	private static class DateFormatCache {

		/**
		 * This map needs to keep weak references (to avoid memory leaks - see r1.37)
		 * and be thread-safe (since it may be concurrently modified in get() below).
		 */
		private final Map<Thread, DateFormat> threadMap = Collections.synchronizedMap(new WeakHashMap<Thread, DateFormat>());

		private final DateFormat templateFormat;

		private DateFormatCache(DateFormat dateFormat) {
			this.templateFormat = dateFormat;
		}

		public DateFormat get() {
			DateFormat dateFormat = threadMap.get(Thread.currentThread());
			if (dateFormat == null) {
				dateFormat = (DateFormat) templateFormat.clone();
				threadMap.put(Thread.currentThread(), dateFormat);
			}
			return dateFormat;
		}
	}
}
