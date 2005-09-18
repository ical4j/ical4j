/*
 * $Id$ [5/07/2004]
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
 * A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.fortuna.ical4j.util;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Utility methods relevant to Java timezones.
 *
 * @author Ben Fortuna
 */
public final class TimeZones {
    
    public static final String UTC_ID = "UTC";

    /**
     * Constructor made private to enforce static nature.
     */
    private TimeZones() {
    }

    /**
     * Determines the first start date of daylight savings for the specified
     * timezone since January 1, 1970.
     *
     * @param timezone
     *            a timezone to determine the start of daylight savings for
     * @return a date
     */
    public static Date getDaylightStart(final TimeZone timezone) {
        Calendar calendar = Calendar.getInstance(timezone);
        calendar.setTime(new Date(0));

        if (timezone.useDaylightTime()) {
            // first find the start of standard time..
            while (timezone.inDaylightTime(calendar.getTime())) {
                calendar.set(Calendar.DAY_OF_YEAR, calendar
                        .get(Calendar.DAY_OF_YEAR) + 1);
            }

            // then find the first daylight time after that..
            while (!timezone.inDaylightTime(calendar.getTime())) {
                calendar.set(Calendar.DAY_OF_YEAR, calendar
                        .get(Calendar.DAY_OF_YEAR) + 1);
            }
        }

        return calendar.getTime();
    }

    /**
     * Determines the first end date of daylight savings for the specified
     * timezone since January 1, 1970.
     *
     * @param timezone
     *            a timezone to determine the end of daylight savings for
     * @return a date
     */
    public static Date getDaylightEnd(final TimeZone timezone) {
        Calendar calendar = Calendar.getInstance(timezone);
        calendar.setTime(new Date(0));

        if (timezone.useDaylightTime()) {
            // first find the start of daylight time..
            while (!timezone.inDaylightTime(calendar.getTime())) {
                calendar.set(Calendar.DAY_OF_YEAR, calendar
                        .get(Calendar.DAY_OF_YEAR) + 1);
            }

            // then find the first standard time after that..
            while (timezone.inDaylightTime(calendar.getTime())) {
                calendar.set(Calendar.DAY_OF_YEAR, calendar
                        .get(Calendar.DAY_OF_YEAR) + 1);
            }
        }

        return calendar.getTime();
    }
    
    /**
     * Indicates whether the specified timezone is equivalent to
     * UTC time.
     * @param timezone
     * @return true if the timezone is UTC time, otherwise false
     */
    public static boolean isUtc(final TimeZone timezone) {
//        return timezone.hasSameRules(TimeZone.getTimeZone(UTC_ID));
//        return timezone.getRawOffset() == 0;
        return UTC_ID.equals(timezone.getID());
    }
}
