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

import java.text.ParseException;
import java.util.TimeZone;

import net.fortuna.ical4j.util.Dates;
import net.fortuna.ical4j.util.TimeZones;


/**
 * $Id$
 *
 * Created on 26/06/2005
 *
 * Base class for all representations of time values in RFC2445.
 *
 * <pre>
 * 4.3.4 Date
 * 
 *    Value Name: DATE
 * 
 *    Purpose: This value type is used to identify values that contain a
 *    calendar date.
 * 
 *    Formal Definition: The value type is defined by the following
 *    notation:
 * 
 *      date               = date-value
 * 
 *      date-value         = date-fullyear date-month date-mday
 *      date-fullyear      = 4DIGIT
 *      date-month         = 2DIGIT        ;01-12
 *      date-mday          = 2DIGIT        ;01-28, 01-29, 01-30, 01-31
 *                                         ;based on month/year
 * 
 *    Description: If the property permits, multiple "date" values are
 *    specified as a COMMA character (US-ASCII decimal 44) separated list
 *    of values. The format for the value type is expressed as the [ISO
 *    8601] complete representation, basic format for a calendar date. The
 *    textual format specifies a four-digit year, two-digit month, and
 *    two-digit day of the month. There are no separator characters between
 *    the year, month and day component text.
 * 
 *    No additional content value encoding (i.e., BACKSLASH character
 *    encoding) is defined for this value type.
 * 
 *    Example: The following represents July 14, 1997:
 * 
 *      19970714
 * 
 * </pre>
 * 
 * @author Ben Fortuna
 */
public class Date extends Iso8601 {

    private static final long serialVersionUID = 7136072363141363141L;

    private static final String PATTERN = "yyyyMMdd";

    /**
     * Default constructor.
     */
    public Date() {
        super(PATTERN, Dates.PRECISION_DAY, TimeZones.getDateTimeZone());
    }
    
    /**
     * Creates a new date instance with the specified precision. This
     * constructor is only intended for use by sub-classes.
     * @param precision the date precision
     * @param tz the timezone
     * @see Dates#PRECISION_DAY
     * @see Dates#PRECISION_SECOND
     */
    protected Date(final int precision, TimeZone tz) {
        super(PATTERN, precision, tz);
    }

    /**
     * @param time a date value in milliseconds
     */
    public Date(final long time) {
        super(time, PATTERN, Dates.PRECISION_DAY, TimeZones.getDateTimeZone());
    }
    
    /**
     * Creates a new date instance with the specified precision. This
     * constructor is only intended for use by sub-classes.
     * @param time a date value in milliseconds
     * @param precision the date precision
     * @param tz the timezone
     * @see Dates#PRECISION_DAY
     * @see Dates#PRECISION_SECOND
     */
    protected Date(final long time, final int precision, TimeZone tz) {
        super(time, PATTERN, precision, tz);
    }

    /**
     * @param date a date value
     */
    public Date(final java.util.Date date) {
//        this();
        this(date.getTime(), Dates.PRECISION_DAY, TimeZones.getDateTimeZone());
//        setTime(date.getTime());
    }

    /**
     * @param value a string representation of a date
     * @throws ParseException where the specified string is not a valid date
     */
    public Date(final String value) throws ParseException {
        this();
        setTime(getFormat().parse(value).getTime());
    }
    
    /**
     * @param value a string representation of a date
     * @param pattern a date pattern to apply when parsing
     * @throws ParseException where the specified string is not a valid date
     */
    public Date(String value, String pattern) throws ParseException {
        super(pattern, Dates.PRECISION_DAY, TimeZones.getDateTimeZone());
        setTime(getFormat().parse(value).getTime());
    }
}
