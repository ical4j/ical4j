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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import net.fortuna.ical4j.util.Dates;
import net.fortuna.ical4j.util.TimeZones;

/**
 * $Id$
 *
 * Created on 30/06/2005
 *
 * A type used to represent iCalendar time values.
 * @author Ben Fortuna
 */
public class Time extends Iso8601 {
    
    private static final long serialVersionUID = -8401010870773304348L;
    
    private boolean utc = false;
    
    /**
     * FORM #1: LOCAL TIME.
     */
    private static final String DEFAULT_PATTERN = "HHmmss";
    
    /**
     * FORM #2: UTC TIME.
     */
    private static final String UTC_PATTERN = "HHmmss'Z'";

    /**
     * @param timezone a timezone for the instance
     */
    public Time(final TimeZone timezone) {
        this(timezone, TimeZones.isUtc(timezone));
    }
    
    /**
     * @param timezone a timezone for the instance
     * @param utc indicates if the time is in UTC
     */
    public Time(final TimeZone timezone, boolean utc) {
        super(utc ? UTC_PATTERN : DEFAULT_PATTERN, Dates.PRECISION_SECOND, timezone);
        getFormat().setTimeZone(timezone);
        this.utc = utc;
    }

    /**
     * @param time a time value in milliseconds from the epoch
     * @param timezone a timezone for the instance
     */
    public Time(final long time, final TimeZone timezone) {
        this(time, timezone, TimeZones.isUtc(timezone));
    }
    
    /**
     * @param time a time value in milliseconds from the epoch
     * @param timezone a timezone for the instance
     * @param utc indicates if the time is in UTC
     */
    public Time(final long time, final TimeZone timezone, boolean utc) {
        super(time, (utc ? UTC_PATTERN : DEFAULT_PATTERN), Dates.PRECISION_SECOND, timezone);
        getFormat().setTimeZone(timezone);
        this.utc = utc;
    }

    /**
     * @param time a time value in milliseconds from the epoch
     * @param timezone a timezone for the instance
     */
    public Time(final java.util.Date time, final TimeZone timezone) {
        this(time, timezone, TimeZones.isUtc(timezone));
    }
    
    /**
     * @param time a time value as a Java date instance
     * @param timezone a timezone for the instance
     * @param utc indicates if the time is in UTC
     */
    public Time(final java.util.Date time, final TimeZone timezone, boolean utc) {
        super(time.getTime(), (utc ? UTC_PATTERN : DEFAULT_PATTERN), Dates.PRECISION_SECOND, timezone);
        getFormat().setTimeZone(timezone);
        this.utc = utc;
    }
    
    /**
     * @param value
     * @param timezone
     * @throws ParseException where the specified value is not a valid time string
     */
    public Time(String value, TimeZone timezone) throws ParseException {
        this(value, timezone, TimeZones.isUtc(timezone));
    }
    
    /**
     * @param value
     * @param timezone
     * @param utc
     * @throws ParseException where the specified value is not a valid time string
     */
    public Time(String value, TimeZone timezone, boolean utc) throws ParseException {
        this(parseDate(value, timezone), timezone, utc);
    }
    
    private static java.util.Date parseDate(String value, TimeZone timezone) throws ParseException {
        DateFormat df = new SimpleDateFormat(DEFAULT_PATTERN);
        df.setTimeZone(timezone);
        try {
            return df.parse(value);
        }
        catch (ParseException e) {
            df = new SimpleDateFormat(UTC_PATTERN);
            df.setTimeZone(timezone);
        }
        return df.parse(value);
    }
    
    /**
     * @return true if time is utc
     */
    public final boolean isUtc() {
        return utc;
    }
}
