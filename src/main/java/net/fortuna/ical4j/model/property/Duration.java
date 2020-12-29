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
package net.fortuna.ical4j.model.property;

import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.validate.ValidationException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.time.temporal.TemporalAmount;
import java.util.Date;

/**
 * $Id$
 * <p/>
 * Created: [Apr 6, 2004]
 * <p/>
 * Defines a DURATION iCalendar component property.
 * <p/>
 * <pre>
 * 3.3.6.  Duration
 *
 * Value Name:  DURATION
 *
 * Purpose:  This value type is used to identify properties that contain
 * a duration of time.
 *
 * Format Definition:  This value type is defined by the following
 * notation:
 *
 * dur-value  = (["+"] / "-") "P" (dur-date / dur-time / dur-week)
 *
 * dur-date   = dur-day [dur-time]
 * dur-time   = "T" (dur-hour / dur-minute / dur-second)
 * dur-week   = 1*DIGIT "W"
 * dur-hour   = 1*DIGIT "H" [dur-minute]
 * dur-minute = 1*DIGIT "M" [dur-second]
 * dur-second = 1*DIGIT "S"
 * dur-day    = 1*DIGIT "D"
 *
 * Description:  If the property permits, multiple "duration" values are
 * specified by a COMMA-separated list of values.  The format is
 * based on the [ISO.8601.2004] complete representation basic format
 * with designators for the duration of time.  The format can
 * represent nominal durations (weeks and days) and accurate
 * durations (hours, minutes, and seconds).  Note that unlike
 * [ISO.8601.2004], this value type doesn't support the "Y" and "M"
 * designators to specify durations in terms of years and months.
 *
 * The duration of a week or a day depends on its position in the
 * calendar.  In the case of discontinuities in the time scale, such
 * as the change from standard time to daylight time and back, the
 * computation of the exact duration requires the subtraction or
 * addition of the change of duration of the discontinuity.  Leap
 * seconds MUST NOT be considered when computing an exact duration.
 * When computing an exact duration, the greatest order time
 * components MUST be added first, that is, the number of days MUST
 * be added first, followed by the number of hours, number of
 * minutes, and number of seconds.
 *
 * Negative durations are typically used to schedule an alarm to
 * trigger before an associated time (see Section 3.8.6.3).
 *
 * No additional content value encoding (i.e., BACKSLASH character
 * encoding, see Section 3.3.11) are defined for this value type.
 *
 * Example:  A duration of 15 days, 5 hours, and 20 seconds would be:
 *
 * P15DT5H0M20S
 *
 * A duration of 7 weeks would be:
 *
 * P7W
 * </pre>
 *
 * @author Ben Fortuna
 */
public class Duration extends Property {

    private static final long serialVersionUID = 9144969653829796798L;

    private TemporalAmountAdapter duration;

    /**
     * Default constructor.
     */
    public Duration() {
        super(DURATION, new Factory());
    }

    /**
     * @param aList  a list of parameters for this component
     * @param aValue a value string for this component
     */
    public Duration(final ParameterList aList, final String aValue) {
        super(DURATION, aList, new Factory());
        setValue(aValue);
    }

    /**
     * @param duration a duration  value
     */
    @Deprecated
    public Duration(final Dur duration) {
        this(TemporalAmountAdapter.from(duration).getDuration());
    }

    /**
     * @param duration a duration  value
     */
    public Duration(final TemporalAmount duration) {
        super(DURATION, new Factory());
        this.duration = new TemporalAmountAdapter(duration);
    }

    /**
     * @param aList    a list of parameters for this component
     * @param duration a duration value
     */
    @Deprecated
    public Duration(final ParameterList aList, final Dur duration) {
        this(aList, TemporalAmountAdapter.from(duration).getDuration());
    }

    /**
     * @param aList    a list of parameters for this component
     * @param duration a duration value
     */
    public Duration(final ParameterList aList, final TemporalAmount duration) {
        super(DURATION, aList, new Factory());
        setDuration(duration);
    }

    /**
     * Constructs a new duration representing the time between the specified start date and end date.
     *
     * @param start the starting time for the duration
     * @param end   the end time for the duration
     */
    public Duration(final Date start, final Date end) {
        super(DURATION, new Factory());
        setDuration(TemporalAmountAdapter.fromDateRange(start, end).getDuration());
    }

    /**
     * @return Returns the duration.
     */
    public final TemporalAmount getDuration() {
        return duration.getDuration();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setValue(final String aValue) {
        duration = TemporalAmountAdapter.parse(aValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getValue() {
        return duration.toString();
    }

    /**
     * @param duration The duration to set.
     */
    public final void setDuration(final TemporalAmount duration) {
        this.duration = new TemporalAmountAdapter(duration);
    }

    @Override
    public void validate() throws ValidationException {

    }

    public static class Factory extends Content.Factory implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super(DURATION);
        }

        @Override
        public Property createProperty(final ParameterList parameters, final String value)
                throws IOException, URISyntaxException, ParseException {
            return new Duration(parameters, value);
        }

        @Override
        public Property createProperty() {
            return new Duration();
        }
    }

}
