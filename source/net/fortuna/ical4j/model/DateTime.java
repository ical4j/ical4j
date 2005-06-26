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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * Represents a time of day on a specific date.
 * 
 * @author Ben Fortuna
 */
public class DateTime extends Date {
    
    private static final long serialVersionUID = -6400235993874419171L;

    /**
     * FORM #1: DATE WITH LOCAL TIME
     */
    private static final String DEFAULT_PATTERN = "yyyyMMdd'T'HHmmss";
    
    /**
     * FORM #2: DATE WITH UTC TIME
     */
    private static final String UTC_PATTERN = "yyyyMMdd'T'HHmmss'Z'";
    
    private boolean utc;
    
    private boolean floating;
    
    /**
     * Default constructor.
     */
    public DateTime() {
        super();
        format = new SimpleDateFormat(DEFAULT_PATTERN);
    }

    /**
     * @param time
     */
    public DateTime(final long time) {
        super(time);
    }

    /**
     * @param value
     * @throws ParseException
     */
    public DateTime(final String value) throws ParseException {
        super(value);
    }

    /**
     * Constructs a new date-time instance with the same value as the
     * specified date.
     * @param date a date from which to obtain the value for this date-time
     */
    public DateTime(final Date date) {
        super(date);
    }

    /**
     * @return Returns the floating.
     */
    public final boolean isFloating() {
        return floating;
    }

    /**
     * @param floating The floating to set.
     */
    public final void setFloating(final boolean floating) {
        this.floating = floating;
    }

    /**
     * @return Returns the utc.
     */
    public final boolean isUtc() {
        return utc;
    }

    /**
     * @param utc The utc to set.
     */
    public final void setUtc(final boolean utc) {
        format = new SimpleDateFormat(UTC_PATTERN);
        setTimeZone(TimeZone.getTimeZone("UTC"));
        this.utc = utc;
    }

    /**
     * @param timezone
     */
    public final void setTimeZone(final TimeZone timezone) {
        format.setTimeZone(timezone);
    }
    
    /**
     * Returns the current timezone associated with this date-time value.
     * @return a Java timezone
     */
    public final TimeZone getTimeZone() {
        return format.getTimeZone();
    }
}
