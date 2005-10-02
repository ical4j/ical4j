/*
 * $Id$
 *
 * Created on 30/06/2005
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
import java.text.SimpleDateFormat;
import java.util.Date;

import net.fortuna.ical4j.util.Dates;
import net.fortuna.ical4j.util.TimeZones;

/**
 * Base class for date and time representations as defined
 * by the ISO 8601 standard. Sub-classes must ensure that either the correct
 * precision is used in constructor arguments, or that <code>Object.equals()</code>
 * is overridden to ensure equality checking is consistent with the type.
 * @author Ben Fortuna
 */
public abstract class Iso8601 extends Date {

    protected static final int PRECISION_SECOND = 0;

    protected static final int PRECISION_DAY = 1;
    
    private DateFormat format;
    
    private int precision;

    /**
     * @param time
     * @param pattern
     */
    public Iso8601(final long time, final String pattern, final int precision) {
        super(round(time, precision));
        format = new SimpleDateFormat(pattern);
        // use GMT timezone to avoid daylight savings rules affecting floating
        // time values..
        format.setTimeZone(TimeZone.getTimeZone(TimeZones.GMT_ID));
        this.precision = precision;
    }
    
    /**
     * @param pattern
     */
    public Iso8601(final String pattern, final int precision) {
        this(System.currentTimeMillis(), pattern, precision);
    }

    /**
     * @param time
     * @param pattern
     */
    public Iso8601(final Date time, final String pattern, final int precision) {
        this(time.getTime(), pattern, precision);
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return format.format(this);
    }

    /**
     * @return Returns the format.
     */
    protected final DateFormat getFormat() {
        return format;
    }
    
    /**
     * Rounds a time value to remove any precision smaller than specified.
     * @param time the time value to round
     * @return a round time value
     */
    protected static final long round(final long time, final int precision) {
        if (precision == PRECISION_DAY) {
            return (long) Math.floor(time / (double) Dates.MILLIS_PER_DAY) * Dates.MILLIS_PER_DAY;
        }
        else if (precision == PRECISION_SECOND) {
            return (long) Math.floor(time / (double) Dates.MILLIS_PER_SECOND) * Dates.MILLIS_PER_SECOND;
        }
        // unrecognised precision..
        return time;
    }
    
    /* (non-Javadoc)
     * @see java.util.Date#setTime(long)
     */
    public void setTime(final long time) {
        super.setTime(round(time, precision));
    }
}
