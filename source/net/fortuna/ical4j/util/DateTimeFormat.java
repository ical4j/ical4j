/*
 * $Id$ [15-May-2004]
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
package net.fortuna.ical4j.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Defines a format for all iCalendar date-times.
 * @author benfortuna
 * @deprecated Use <code>net.fortuna.ical4j.model.DateTime</code> instead.
 */
public final class DateTimeFormat {

    /**
     * FORM #1: DATE WITH LOCAL TIME
     */
    private static final String DEFAULT_PATTERN = "yyyyMMdd'T'HHmmss";
    
    /**
     * FORM #2: DATE WITH UTC TIME
     */
    private static final String UTC_PATTERN = "yyyyMMdd'T'HHmmss'Z'";

    private static Log log = LogFactory.getLog(DateTimeFormat.class);

    private static DateTimeFormat instance = new DateTimeFormat();

    /**
     * Constructor made private to enforce singleton.
     */
    private DateTimeFormat() {
    }

    /**
     * @return Returns the instance.
     */
    public static DateTimeFormat getInstance() {
        return instance;
    }

    /**
     * Returns a string representation of the specified
     * date in UTC format.
     * @param date a date to format
     * @return a string representation of the specified date
     */
    public String format(final Date date) {
        return format(date, true);
    }

    /**
     * Returns a string representation of the specified
     * date.
     * @param date a date to format
     * @param utc indicates whether to format as UTC time
     * @return a string representation of the specified date
     */
    public String format(final Date date, final boolean utc) {
        if (utc) {
            return getUtcFormat().format(date);
        }

        return getDefaultFormat().format(date);
    }

    /**
     * @param string a string representation of a date-time
     * @return a date parsed from the specified string representation
     * @throws java.text.ParseException thrown if the specified string
     * is not a valid representation of a date-time
     */
    public Date parse(final String string) throws ParseException {
        try {
            return getUtcFormat().parse(string);
        }
        catch (ParseException pe) {
            return getDefaultFormat().parse(string);
        }
    }
    
    /**
     * @return a new date format instance
     */
    private DateFormat getDefaultFormat() {
        return new SimpleDateFormat(DEFAULT_PATTERN);
    }
    
    /**
     * @return a new date format instance
     */
    private DateFormat getUtcFormat() {
        DateFormat utcFormat = new SimpleDateFormat(UTC_PATTERN);
        utcFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return utcFormat;
    }
}
