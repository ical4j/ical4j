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
 * 	o Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * 	o Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 	o Neither the name of Ben Fortuna nor the names of any other contributors
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

import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Defines the format used for all iCalendar durations.
 *
 * @author benfortuna
 */
public final class DurationFormat {

    private static final long MILLIS_PER_SECOND = 1000;

    private static final long MILLIS_PER_MINUTE = 60000;

    private static final long MILLIS_PER_HOUR = 3600000;

    private static final long MILLIS_PER_DAY = 86400000;

    private static final long MILLIS_PER_WEEK = 604800000;

    private static Log log = LogFactory.getLog(DurationFormat.class);

    private static DurationFormat instance = new DurationFormat();

    /**
     * Constructor made private to enforce singleton.
     */
    private DurationFormat() {
    }

    /**
     * @return Returns the instance.
     */
    public static DurationFormat getInstance() {
        return instance;
    }

    /**
     * Parse an iCalendar duration (dur-val) string.
     *
     * @param aString
     *            a string representation of a duration
     * @return a duration in milliseconds
     */
    public long parse(final String aString) {

        long duration = 0;

        boolean negative = false;

        String token = null;
        String prevToken = null;

        for (StringTokenizer t = new StringTokenizer(aString, "+-PWDTHMS", true);
            t.hasMoreTokens();) {

            prevToken = token;

            token = t.nextToken();

            if ("+".equals(token)) {
                negative = false;
            }
            else if ("-".equals(token)) {
                negative = true;
            }
            else if ("P".equals(token)) {
                // does nothing..
                if (log.isDebugEnabled()) {
                    log.debug("Redundant [P] token ignored.");
                }
            }
            else if ("W".equals(token)) {

                int weeks = Integer.parseInt(prevToken);

                duration += weeks * MILLIS_PER_WEEK;
            }
            else if ("D".equals(token)) {

                int days = Integer.parseInt(prevToken);

                duration += days * MILLIS_PER_DAY;
            }
            else if ("T".equals(token)) {
                // does nothing..
                if (log.isDebugEnabled()) {
                    log.debug("Redundant [T] token ignored.");
                }
            }
            else if ("H".equals(token)) {

                int hours = Integer.parseInt(prevToken);

                duration += hours * MILLIS_PER_HOUR;
            }
            else if ("M".equals(token)) {

                int minutes = Integer.parseInt(prevToken);

                duration += minutes * MILLIS_PER_MINUTE;
            }
            else if ("S".equals(token)) {

                int seconds = Integer.parseInt(prevToken);

                duration += seconds * MILLIS_PER_SECOND;
            }
        }

        if (negative) { return -duration; }

        return duration;
    }

    /**
     * Returns a string representation of a duration.
     *
     * @param duration
     *            a duration in milliseconds
     * @return a string
     */
    public String format(final long duration) {

        StringBuffer b = new StringBuffer();

        long remainder = Math.abs(duration);

        if (duration < 0) {
            b.append('-');
        }

        b.append('P');

        if (remainder >= MILLIS_PER_WEEK) {
            b.append(remainder / MILLIS_PER_WEEK);
            b.append('W');
        }
        else {

            if (remainder >= MILLIS_PER_DAY) {
                b.append(remainder / MILLIS_PER_DAY);
                b.append('D');

                remainder = remainder % MILLIS_PER_DAY;
            }

            if (remainder > 0) {
                b.append('T');
            }

            if (remainder >= MILLIS_PER_HOUR) {
                b.append(remainder / MILLIS_PER_HOUR);
                b.append('H');

                remainder = remainder % MILLIS_PER_HOUR;
            }

            if (remainder >= MILLIS_PER_MINUTE) {
                b.append(remainder / MILLIS_PER_MINUTE);
                b.append('M');

                remainder = remainder % MILLIS_PER_MINUTE;
            }

            if (remainder >= MILLIS_PER_SECOND) {
                b.append(remainder / MILLIS_PER_SECOND);
                b.append('S');
            }
        }

        return b.toString();
    }
}