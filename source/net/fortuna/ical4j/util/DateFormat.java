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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Defines a format for all iCalendar dates.
 * @author benfortuna
 * @deprecated Use <code>net.fortuna.ical4j.model.Date</code> instead.
 */
public final class DateFormat {

    private static final String DATE_FORMAT = "yyyyMMdd";

    private static DateFormat instance = new DateFormat();

    /**
     * Constructor made private to enforce singleton.
     */
    private DateFormat() {
    }

    /**
     * @return Returns the instance.
     */
    public static DateFormat getInstance() {
        return instance;
    }

    /**
     * @param date a date to format
     * @return a string representation of the specified date
     */
    public String format(final Date date) {
        return getDateFormat().format(date);
    }

    /**
     * @param string a string representation of a date
     * @return a date parsed from the specified string representation
     * @throws java.text.ParseException thrown when the specified string
     * is not a valid representation of a date
     */
    public Date parse(final String string) throws ParseException {
        return getDateFormat().parse(string);
    }
    
    /**
     * @return
     */
    private java.text.DateFormat getDateFormat() {
        return new SimpleDateFormat(DATE_FORMAT);
    }
}
