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

import java.util.Date;

import net.fortuna.ical4j.model.Dur;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactoryImpl;
import net.fortuna.ical4j.model.ValidationException;

/**
 * $Id$
 * 
 * Created: [Apr 6, 2004]
 *
 * Defines a DURATION iCalendar component property.
 * 
 * <pre>
 *     4.3.6   Duration
 *     
 *        Value Name: DURATION
 *     
 *        Purpose: This value type is used to identify properties that contain
 *        a duration of time.
 *     
 *        Formal Definition: The value type is defined by the following
 *        notation:
 *     
 *          dur-value  = ([&quot;+&quot;] / &quot;-&quot;) &quot;P&quot; (dur-date / dur-time / dur-week)
 *     
 *          dur-date   = dur-day [dur-time]
 *          dur-time   = &quot;T&quot; (dur-hour / dur-minute / dur-second)
 *          dur-week   = 1*DIGIT &quot;W&quot;
 *          dur-hour   = 1*DIGIT &quot;H&quot; [dur-minute]
 *          dur-minute = 1*DIGIT &quot;M&quot; [dur-second]
 *          dur-second = 1*DIGIT &quot;S&quot;
 *          dur-day    = 1*DIGIT &quot;D&quot;
 *     
 *        Description: If the property permits, multiple &quot;duration&quot; values are
 *        specified by a COMMA character (US-ASCII decimal 44) separated list
 *        of values. The format is expressed as the [ISO 8601] basic format for
 *        the duration of time. The format can represent durations in terms of
 *        weeks, days, hours, minutes, and seconds.
 *     
 *        No additional content value encoding (i.e., BACKSLASH character
 *        encoding) are defined for this value type.
 *     
 *        Example: A duration of 15 days, 5 hours and 20 seconds would be:
 *     
 *          P15DT5H0M20S
 *     
 *        A duration of 7 weeks would be:
 *     
 *          P7W
 * </pre>
 * 
 * @author Ben Fortuna
 */
public class Duration extends Property {

    private static final long serialVersionUID = 9144969653829796798L;

    private Dur duration;

    /**
     * Default constructor.
     */
    public Duration() {
        super(DURATION, PropertyFactoryImpl.getInstance());
    }

    /**
     * @param aList a list of parameters for this component
     * @param aValue a value string for this component
     */
    public Duration(final ParameterList aList, final String aValue) {
        super(DURATION, aList, PropertyFactoryImpl.getInstance());
        setValue(aValue);
    }

    /**
     * @param duration a duration  value
     */
    public Duration(final Dur duration) {
        super(DURATION, PropertyFactoryImpl.getInstance());
        this.duration = duration;
    }

    /**
     * @param aList a list of parameters for this component
     * @param duration a duration value
     */
    public Duration(final ParameterList aList, final Dur duration) {
        super(DURATION, aList, PropertyFactoryImpl.getInstance());
        setDuration(duration);
    }

    /**
     * Constructs a new duration representing the time between the specified start date and end date.
     * @param start the starting time for the duration
     * @param end the end time for the duration
     */
    public Duration(final Date start, final Date end) {
        super(DURATION, PropertyFactoryImpl.getInstance());
        setDuration(new Dur(start, end));
    }

    /**
     * @return Returns the duration.
     */
    public final Dur getDuration() {
        return duration;
    }

    /**
     * {@inheritDoc}
     */
    public final void setValue(final String aValue) {
        // duration = DurationFormat.getInstance().parse(aValue);
        duration = new Dur(aValue);
    }

    /**
     * {@inheritDoc}
     */
    public final String getValue() {
        // return DurationFormat.getInstance().format(getDuration());
        return duration.toString();
    }

    /**
     * @param duration The duration to set.
     */
    public final void setDuration(final Dur duration) {
        this.duration = duration;
    }

    /**
     * {@inheritDoc}
     */
    public final void validate() throws ValidationException {
        // TODO: Auto-generated method stub
    }
}
