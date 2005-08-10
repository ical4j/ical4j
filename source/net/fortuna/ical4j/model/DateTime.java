/*
 * $Id$
 *
 * Created on 26/06/2005
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
package net.fortuna.ical4j.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * Represents a time of day on a specific date.
 * 
 * @author Ben Fortuna
 */
public class DateTime extends Date {
    
    private static final long serialVersionUID = -6407231357919440387L;

    private static final String DEFAULT_PATTERN = "yyyyMMdd'T'HHmmss";

    private static final String UTC_PATTERN = "yyyyMMdd'T'HHmmss'Z'";
    
    /**
     * Used for parsing times in a local date-time representation.
     */
    private DateFormat defaultFormat = new SimpleDateFormat(DEFAULT_PATTERN);
    
    /**
     * Used for parsing times in a UTC date-time representation.
     */
    private DateFormat utcFormat = new SimpleDateFormat(UTC_PATTERN);
    {
        utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }
    
    private Time time;
    
    /**
     * Default constructor.
     */
    public DateTime() {
        super();
        this.time = new Time(getTime(), getFormat().getTimeZone());
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
        super(time);
        this.time = new Time(getTime(), getFormat().getTimeZone());
    }
    
    /**
     * @param date
     */
    public DateTime(final java.util.Date date) {
        super(date.getTime());
        this.time = new Time(getTime(), getFormat().getTimeZone());
    }
    
    /**
     * Constructs a new DateTime instance from parsing the specified
     * string representation in the default (local) timezone.
     * @param value
     */
    public DateTime(final String value) throws ParseException {
        this(value, TimeZone.getDefault());
    }
    
    /**
     * @param value
     * @throws ParseException
     */
    public DateTime(final String value, final TimeZone timezone) throws ParseException {
        super();
        this.time = new Time(getTime(), timezone);
        try {
            setTime(utcFormat.parse(value).getTime());
            setUtc(true);
        }
        catch (ParseException pe) {
            defaultFormat.setTimeZone(timezone);
            setTime(defaultFormat.parse(value).getTime());
        }
    }
    
    /* (non-Javadoc)
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
     * Updates this date-time to display in UTC time if the argument
     * is true. Otherwise, resets to the default timezone.
     * @param utc The utc to set.
     */
    public final void setUtc(final boolean utc) {
        if (utc) {
            setTimeZone(TimeZone.getTimeZone("UTC"));
        }
        else {
            setTimeZone(TimeZone.getDefault());
        }
    }

    /**
     * @param timezone
     */
    public final void setTimeZone(final TimeZone timezone) {
        getFormat().setTimeZone(timezone);
        time = new Time(time, timezone);
    }
    
    /**
     * Returns the current timezone associated with this date-time value.
     * @return a Java timezone
     */
    public final TimeZone getTimeZone() {
        return getFormat().getTimeZone();
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public final String toString() {
        StringBuffer b = new StringBuffer(super.toString());
        b.append('T');
        b.append(time.toString());
        return b.toString();
    }
}
