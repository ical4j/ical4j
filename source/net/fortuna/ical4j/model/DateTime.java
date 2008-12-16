/**
 * Copyright (c) 2008, Ben Fortuna
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.util.Dates;
import net.fortuna.ical4j.util.TimeZones;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * $Id$
 *
 * Created on 26/06/2005
 *
 * Represents a time of day on a specific date.
 * @author Ben Fortuna
 */
public class DateTime extends Date {

    private static final long serialVersionUID = -6407231357919440387L;

    private static final String DEFAULT_PATTERN = "yyyyMMdd'T'HHmmss";

    private static final String UTC_PATTERN = "yyyyMMdd'T'HHmmss'Z'";

    private static final String RELAXED_PATTERN = "yyyyMMdd";

    /**
     * Used for parsing times in a UTC date-time representation.
     */
     private static final ThreadLocal utc_format =
             new ThreadLocal () {
                protected Object initialValue() {
                    DateFormat format = new SimpleDateFormat(UTC_PATTERN);
                    format.setTimeZone(TimeZone.getTimeZone(TimeZones.UTC_ID));
                    format.setLenient(false);
                    return (Object)format;
                }
            };

    /**
     * Used for parsing times in a local date-time representation.
     */
     private static final ThreadLocal default_format =
             new ThreadLocal () {
                protected Object initialValue() {
                    DateFormat format = new SimpleDateFormat(DEFAULT_PATTERN);
                    format.setLenient(false);
                    return (Object)format;
                }
            };

     private static final ThreadLocal lenient_default_format =
             new ThreadLocal () {
                protected Object initialValue() {
                    return (Object)new SimpleDateFormat(DEFAULT_PATTERN);
                }
            };

     private static final ThreadLocal relaxed_format =
             new ThreadLocal () {
                protected Object initialValue() {
                    return (Object)new SimpleDateFormat(RELAXED_PATTERN);
                }
            };

    private Time time;

    private TimeZone timezone;

    /**
     * Default constructor.
     */
    public DateTime() {
        super(Dates.PRECISION_SECOND);
        this.time = new Time(System.currentTimeMillis(), getFormat()
                .getTimeZone());
    }

    /**
     * @param utc
     */
    public DateTime(final boolean utc) {
        this();
        setUtc(utc);
    }

    /**
     * @param time
     */
    public DateTime(final long time) {
        super(time, Dates.PRECISION_SECOND);
        this.time = new Time(time, getFormat().getTimeZone());
    }

    /**
     * @param date
     */
    public DateTime(final java.util.Date date) {
        super(date.getTime(), Dates.PRECISION_SECOND);
        this.time = new Time(date.getTime(), getFormat().getTimeZone());
        // copy timezone information if applicable..
        if (date instanceof DateTime) {
            DateTime dateTime = (DateTime) date;
            if (dateTime.isUtc()) {
                setUtc(true);
            }
            else {
                setTimeZone(dateTime.getTimeZone());
            }
        }
    }

    /**
     * Constructs a new DateTime instance from parsing the specified string representation in the default (local)
     * timezone.
     * @param value
     */
    public DateTime(final String value) throws ParseException {
        this(value, null);
        /*
         * long time = 0; try { synchronized (UTC_FORMAT) { time = UTC_FORMAT.parse(value).getTime(); } setUtc(true); }
         * catch (ParseException pe) { synchronized (DEFAULT_FORMAT) {
         * DEFAULT_FORMAT.setTimeZone(getFormat().getTimeZone()); time = DEFAULT_FORMAT.parse(value).getTime(); }
         * this.time = new Time(time, getFormat().getTimeZone()); } setTime(time);
         */
    }

    /**
     * Creates a new date-time instance from the specified value in the given timezone. If a timezone is not specified,
     * the default timezone (as returned by {@link java.util.TimeZone#getDefault()}) is used.
     * @param value
     * @throws ParseException
     */
    public DateTime(final String value, final TimeZone timezone)
            throws ParseException {
        this();
        try {
            setTime(value, (DateFormat)utc_format.get(), null);
            setUtc(true);
        }
        catch (ParseException pe) {
            try {
                if (timezone != null) {
                    setTime(value, (DateFormat)default_format.get(), timezone);
                }
                else {
                    // Use lenient parsing for floating times. This is to overcome
                    // the problem of parsing VTimeZone dates that specify dates
                    // that the strict parser does not accept.
                    setTime(value, (DateFormat)lenient_default_format.get(), getFormat()
                            .getTimeZone());
                }
            }
            catch (ParseException pe2) {
                if (CompatibilityHints
                        .isHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING)) {

                    setTime(value, (DateFormat)relaxed_format.get(), timezone);
                }
                else {
                    throw pe2;
                }
            }
            setTimeZone(timezone);
        }
    }

    /**
     * Internal set of time by parsing value string.
     * @param value
     * @param format a {@code DateFormat}, protected by the use of a ThreadLocal.
     * @param tz
     * @throws ParseException
     */
    private void setTime(final String value, final DateFormat format, final java.util.TimeZone tz)
            throws ParseException {

        if (tz != null) {
            format.setTimeZone(tz);
        }
        setTime(format.parse(value).getTime());
    }

    /*
     * (non-Javadoc)
     * @see java.util.Date#setTime(long)
     */
    public final void setTime(final long time) {
        super.setTime(time);
        this.time.setTime(time);
    }

    /**
     * @return Returns the utc.
     */
    public final boolean isUtc() {
        return time.isUtc();
    }

    /**
     * Updates this date-time to display in UTC time if the argument is true. Otherwise, resets to the default timezone.
     * @param utc The utc to set.
     */
    public final void setUtc(final boolean utc) {
        // reset the timezone associated with this instance..
        this.timezone = null;
        if (utc) {
            getFormat().setTimeZone(TimeZone.getTimeZone(TimeZones.UTC_ID));
        }
        else {
            resetTimeZone();
        }
        time = new Time(time, getFormat().getTimeZone(), utc);
    }

    /**
     * Sets the timezone associated with this date-time instance. If the specified timezone is null, it will reset
     * to the default timezone.  If the date-time instance is utc, it will turn into
     * either a floating (no timezone) date-time, or a date-time with a timezone.
     * @param timezone
     */
    public final void setTimeZone(final TimeZone timezone) {
        this.timezone = timezone;
        if (timezone != null) {
            getFormat().setTimeZone(timezone);    
        }
        else {
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
        getFormat().setTimeZone(TimeZone.getDefault());
//         getFormat().setTimeZone(TimeZone.getTimeZone(TimeZones.GMT_ID));
    }

    /**
     * Returns the current timezone associated with this date-time value.
     * @return a Java timezone
     */
    public final TimeZone getTimeZone() {
        return timezone;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public final String toString() {
        final StringBuffer b = new StringBuffer(super.toString());
        b.append('T');
        b.append(time.toString());
        return b.toString();
    }

    /**
     * Uses {@link EqualsBuilder} to test equality.
     */
    public boolean equals(final Object arg0) {
        // TODO: what about compareTo, before, after, etc.?

        if (arg0 instanceof DateTime) {
            return new EqualsBuilder().append(time, ((DateTime) arg0).time).isEquals();
        }
        return super.equals(arg0);
    }

    /**
     * Uses {@link HashCodeBuilder} to build hashcode.
     */
    public int hashCode() {
        return new HashCodeBuilder().append(time).append(timezone).toHashCode();
    }
}
