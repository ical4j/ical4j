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

import java.text.DateFormat;
import java.util.Date;

import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.util.Dates;
import net.fortuna.ical4j.util.TimeZones;

/**
 * $Id$
 *
 * Created on 30/06/2005
 *
 * Base class for date and time representations as defined
 * by the ISO 8601 standard. Sub-classes must ensure that either the correct
 * precision is used in constructor arguments, or that <code>Object.equals()</code>
 * is overridden to ensure equality checking is consistent with the type.
 * @author Ben Fortuna
 */
public abstract class Iso8601 extends Date {
    
    /**
     * 
     */
    private static final long serialVersionUID = -4290728005713946811L;

    private DateFormat format;
    
    private DateFormat gmtFormat;
    
    private int precision;

    /**
     * @param time a time value in milliseconds
     * @param pattern the formatting pattern to apply
     * @param precision the precision to apply
     * @param tz the timezone for the instance
     * @see Dates#PRECISION_DAY
     * @see Dates#PRECISION_SECOND
     */
    public Iso8601(final long time, final String pattern, final int precision, java.util.TimeZone tz) {
        super(Dates.round(time, precision, tz)); //, TimeZone.getTimeZone(TimeZones.GMT_ID)));
//        format = new SimpleDateFormat(pattern);
        format = CalendarDateFormatFactory.getInstance(pattern);
        format.setTimeZone(tz);
        format.setLenient(CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING));
        // use GMT timezone to avoid daylight savings rules affecting floating
        // time values..
//        gmtFormat = new SimpleDateFormat(pattern);
//        gmtFormat.setTimeZone(TimeZone.getTimeZone(TimeZones.GMT_ID));
        this.precision = precision;
    }
    
    /**
     * @param pattern the formatting pattern to apply
     * @param precision the precision to apply
     * @param tz the timezone for the instance
     * @see Dates#PRECISION_DAY
     * @see Dates#PRECISION_SECOND
     */
    public Iso8601(final String pattern, final int precision, java.util.TimeZone tz) {
        this(Dates.getCurrentTimeRounded(), pattern, precision, tz);
    }

    /**
     * @param time a time value as a date
     * @param pattern the formatting pattern to apply
     * @param precision the precision to apply
     * @param tz the timezone for the instance
     * @see Dates#PRECISION_DAY
     * @see Dates#PRECISION_SECOND
     */
    public Iso8601(final Date time, final String pattern, final int precision, java.util.TimeZone tz) {
        this(time.getTime(), pattern, precision, tz);
    }
    
    /**
     * {@inheritDoc}
     */
    public String toString() {
        // if time is floating avoid daylight saving rules when generating
        // string representation of date..
        final java.util.TimeZone timeZone = format.getTimeZone();
        if (!(timeZone instanceof TimeZone)) {
            if (gmtFormat == null) {
                gmtFormat = (DateFormat) format.clone();
                gmtFormat.setTimeZone(TimeZone.getTimeZone(TimeZones.GMT_ID));
            }
            if (timeZone.inDaylightTime(this)
                    && timeZone.inDaylightTime(new Date(getTime() - 1))) {

                return gmtFormat.format(new Date(getTime()
                        + timeZone.getRawOffset()
                        + timeZone.getDSTSavings()));
//                return format.format(new Date(getTime() - format.getTimeZone().getDSTSavings()));
            }
//            return gmtFormat.format(new Date(getTime() + format.getTimeZone().getOffset(getTime())));
            return gmtFormat.format(new Date(getTime() + timeZone.getRawOffset()));
        }
        return format.format(this);
    }

    /**
     * @return Returns the format.
     */
    protected final DateFormat getFormat() {
        return format;
    }
    
    /**
     * {@inheritDoc}
     */
    public void setTime(final long time) {
        // need to check for null format due to Android java.util.Date(long) constructor
        // calling this method..
        if (format != null) {
            super.setTime(Dates.round(time, precision, format.getTimeZone()));
        }
        else {
            // XXX: what do we do here??
            super.setTime(time);
        }
    }
}
