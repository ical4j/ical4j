/**
 * Copyright (c) 2009, Ben Fortuna
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

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * $Id$ [06-Apr-2004]
 * 
 * Creates DateFormat objects optimized for common iCalendar date patterns.
 * 
 * @author Dave Nault dnault@laszlosystems.com
 * @see #getInstance(String)
 */
public final class CalendarDateFormatFactory {
    private static final Log LOG = LogFactory.getLog(CalendarDateFormatFactory.class);

    private static final String DATETIME_PATTERN = "yyyyMMdd'T'HHmmss";
    private static final String DATETIME_UTC_PATTERN = "yyyyMMdd'T'HHmmss'Z'";
    private static final String DATE_PATTERN = "yyyyMMdd";
    private static final String TIME_PATTERN = "HHmmss";
    private static final String TIME_UTC_PATTERN = "HHmmss'Z'";

    /**
     * Constructor made private to enforce static nature.
     */
    private CalendarDateFormatFactory() {
    }

    /**
     * Returns DateFormat objects optimized for common iCalendar date patterns. The DateFormats are *not* thread safe.
     * Attempts to get or set the Calendar or NumberFormat of an optimized DateFormat will result in an
     * UnsupportedOperation exception being thrown.
     * 
     * @param pattern
     *            a SimpleDateFormat-compatible pattern
     * @return an optimized DateFormat instance if possible, otherwise a normal SimpleDateFormat instance
     * @throws NullPointerException
     *             if the given pattern is null
     * @throws IllegalArgumentException
     *             if the given pattern is invalid
     */
    public static java.text.DateFormat getInstance(String pattern) {

        // if (true) {
        // return new SimpleDateFormat(pattern);
        // }

        if (pattern.equals(DATETIME_PATTERN) || pattern.equals(DATETIME_UTC_PATTERN)) {
            return new DateTimeFormat(pattern);
        }

        if (pattern.equals(DATE_PATTERN)) {
            return new DateFormat(pattern);
        }

        if (pattern.equals(TIME_PATTERN) || pattern.equals(TIME_UTC_PATTERN)) {
            return new TimeFormat(pattern);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("unexpected date format pattern: " + pattern);
        }

        return new SimpleDateFormat(pattern);
    }

    private static abstract class CalendarDateFormat extends java.text.DateFormat {
        /**
		 * 
		 */
        private static final long serialVersionUID = -4191402739860280205L;

        static private final java.util.TimeZone DEFAULT_TIME_ZONE = TimeZone.getDefault();

        final private String pattern;

        private boolean lenient = true;

        private java.util.TimeZone timeZone = DEFAULT_TIME_ZONE;

        public CalendarDateFormat(String pattern) {
            this.pattern = pattern;
        }

        public java.util.TimeZone getTimeZone() {
            return this.timeZone;
        }

        public void setTimeZone(java.util.TimeZone tz) {
            this.timeZone = tz;
        }

        public void setLenient(boolean lenient) {
            this.lenient = lenient;
        }

        public boolean isLenient() {
            return lenient;
        }

        public java.util.Calendar getCalendar() {
            throw new UnsupportedOperationException();
        }

        public void setCalendar(java.util.Calendar c) {
            throw new UnsupportedOperationException();
        }

        public NumberFormat getNumberFormat() {
            throw new UnsupportedOperationException();
        }

        public void setNumberFormat(NumberFormat n) {
            throw new UnsupportedOperationException();
        }

        public Object clone() {
            // don't call super.clone()
            CalendarDateFormat f = (CalendarDateFormat) CalendarDateFormatFactory.getInstance(pattern);
            f.setTimeZone(getTimeZone());
            f.setLenient(isLenient());
            return f;
        }

        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            if (!super.equals(o))
                return false;

            CalendarDateFormat that = (CalendarDateFormat) o;

            if (lenient != that.lenient)
                return false;
            if (!pattern.equals(that.pattern))
                return false;
            if (!timeZone.equals(that.timeZone))
                return false;

            return true;
        }

        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + pattern.hashCode();
            result = 31 * result + (lenient ? 1 : 0);
            result = 31 * result + timeZone.hashCode();
            return result;
        }
    }

    /**
     * Parses and formats these patterns:
     * 
     * <pre>
     * yyyyMMdd'T'HHmmss
     * yyyyMMdd'T'HHmmss'Z'
     * </pre>
     */
    private static class DateTimeFormat extends CalendarDateFormat {

        /**
		 * 
		 */
        private static final long serialVersionUID = 3005824302269636122L;

        final boolean patternEndsWithZ;

        public DateTimeFormat(String pattern) {
            super(pattern);
            patternEndsWithZ = pattern.endsWith("'Z'");
        }

        public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition) {
            GregorianCalendar cal = new GregorianCalendar(getTimeZone());
            cal.setTimeInMillis(date.getTime());

            appendPadded(toAppendTo, cal.get(GregorianCalendar.YEAR), 4);
            appendPadded(toAppendTo, cal.get(GregorianCalendar.MONTH) + 1, 2);
            appendPadded(toAppendTo, cal.get(GregorianCalendar.DAY_OF_MONTH), 2);
            toAppendTo.append("T");

            appendPadded(toAppendTo, cal.get(GregorianCalendar.HOUR_OF_DAY), 2);
            appendPadded(toAppendTo, cal.get(GregorianCalendar.MINUTE), 2);
            appendPadded(toAppendTo, cal.get(GregorianCalendar.SECOND), 2);

            if (patternEndsWithZ) {
                toAppendTo.append("Z");
            }

            return toAppendTo;
        }

        public Date parse(String source, ParsePosition pos) {
            // if lenient ignore superfluous input..
            if (patternEndsWithZ) {
                if (source.length() > DATETIME_UTC_PATTERN.length() && !isLenient()) {
                    pos.setErrorIndex(DATETIME_UTC_PATTERN.length());
                    return null;
                }
            } else if (source.length() > DATETIME_PATTERN.length() && !isLenient()) {
                pos.setErrorIndex(DATETIME_PATTERN.length());
                return null;
            }

            try {
                if (source.charAt(8) != 'T') {
                    pos.setErrorIndex(8);
                    return null;
                }
                if (patternEndsWithZ && source.charAt(15) != 'Z') {
                    pos.setErrorIndex(15);
                    return null;
                }

                int year = Integer.parseInt(source.substring(0, 4));
                int month = Integer.parseInt(source.substring(4, 6)) - 1;
                int day = Integer.parseInt(source.substring(6, 8));
                int hour = Integer.parseInt(source.substring(9, 11));
                int minute = Integer.parseInt(source.substring(11, 13));
                int second = Integer.parseInt(source.substring(13, 15));

                Date d = makeCalendar(isLenient(), getTimeZone(), year, month, day, hour, minute, second).getTime();
                pos.setIndex(15);
                return d;
            } catch (Exception e) {
                return null;
            }
        }
    }

    /**
     * Parses and formats this pattern:
     * 
     * <pre>
     * yyyyMMdd
     * </pre>
     */
    private static class DateFormat extends CalendarDateFormat {

        /**
		 * 
		 */
        private static final long serialVersionUID = -7626077667268431779L;

        public DateFormat(String pattern) {
            super(pattern);
        }

        public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition) {
            java.util.Calendar cal = java.util.Calendar.getInstance(getTimeZone());
            cal.setTimeInMillis(date.getTime());

            appendPadded(toAppendTo, cal.get(GregorianCalendar.YEAR), 4);
            appendPadded(toAppendTo, cal.get(GregorianCalendar.MONTH) + 1, 2);
            appendPadded(toAppendTo, cal.get(GregorianCalendar.DAY_OF_MONTH), 2);

            return toAppendTo;
        }

        public Date parse(String source, ParsePosition pos) {
            // if lenient ignore superfluous input..
            if (source.length() > DATE_PATTERN.length() && !isLenient()) {
                pos.setErrorIndex(DATE_PATTERN.length());
                return null;
            }

            try {
                int year = Integer.parseInt(source.substring(0, 4));
                int month = Integer.parseInt(source.substring(4, 6)) - 1;
                int day = Integer.parseInt(source.substring(6, 8));

                Date d = makeCalendar(isLenient(), getTimeZone(), year, month, day).getTime();
                pos.setIndex(8);
                return d;
            } catch (Exception e) {
                return null;
            }
        }
    }

    /**
     * Parses and formats these patterns:
     * 
     * <pre>
     * HHmmss
     * HHmmss'Z'
     * </pre>
     */
    private static class TimeFormat extends CalendarDateFormat {

        /**
		 * 
		 */
        private static final long serialVersionUID = -1367114409994225425L;

        final boolean patternEndsWithZ;

        public TimeFormat(String pattern) {
            super(pattern);
            patternEndsWithZ = pattern.endsWith("'Z'");
        }

        public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition) {
            GregorianCalendar cal = new GregorianCalendar(getTimeZone());
            cal.setTimeInMillis(date.getTime());

            appendPadded(toAppendTo, cal.get(GregorianCalendar.HOUR_OF_DAY), 2);
            appendPadded(toAppendTo, cal.get(GregorianCalendar.MINUTE), 2);
            appendPadded(toAppendTo, cal.get(GregorianCalendar.SECOND), 2);

            if (patternEndsWithZ) {
                toAppendTo.append("Z");
            }

            return toAppendTo;
        }

        public Date parse(String source, ParsePosition pos) {
            // if lenient ignore superfluous input..
            if (patternEndsWithZ) {
                if (source.length() > TIME_UTC_PATTERN.length() && !isLenient()) {
                    pos.setErrorIndex(TIME_UTC_PATTERN.length());
                    return null;
                }
            } else if (source.length() > TIME_PATTERN.length() && !isLenient()) {
                pos.setErrorIndex(TIME_PATTERN.length());
                return null;
            }

            try {
                if (patternEndsWithZ && source.charAt(6) != 'Z') {
                    pos.setErrorIndex(6);
                    return null;
                }

                int hour = Integer.parseInt(source.substring(0, 2));
                int minute = Integer.parseInt(source.substring(2, 4));
                int second = Integer.parseInt(source.substring(4, 6));

                Date d = makeCalendar(isLenient(), getTimeZone(), 1970, 0, 1, hour, minute, second).getTime();
                pos.setIndex(6);
                return d;
            } catch (Exception e) {
                return null;
            }
        }
    }

    private static GregorianCalendar makeCalendar(boolean lenient, java.util.TimeZone timeZone, int year,
            int zeroBasedMonth, int day, int hour, int minutes, int seconds) {
        GregorianCalendar cal = new GregorianCalendar(timeZone);
        cal.setLenient(lenient);
        cal.set(year, zeroBasedMonth, day, hour, minutes, seconds);
        cal.set(java.util.Calendar.MILLISECOND, 0);
        return cal;
    }

    private static GregorianCalendar makeCalendar(boolean lenient, TimeZone timeZone, int year, int month, int day) {
        return makeCalendar(lenient, timeZone, year, month, day, 0, 0, 0);
    }

    private static void appendPadded(StringBuffer toAppendTo, int value, int fieldWidth) {
        String s = Integer.toString(value);
        for (int i = 0, max = fieldWidth - s.length(); i < max; i++) {
            toAppendTo.append("0");
        }
        toAppendTo.append(s);
    }

}
