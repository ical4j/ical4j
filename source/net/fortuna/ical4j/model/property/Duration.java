/*
 * $Id$
 * 
 * Created: [Apr 6, 2004]
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
package net.fortuna.ical4j.model.property;

import java.util.Date;

import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.util.DurationFormat;

/**
 * Defines a DURATION iCalendar component property.
 *
 * @author benf
 */
public class Duration extends Property {

    private long duration;

    /**
     * Default constructor.
     */
    public Duration() {
        super(DURATION);
    }
    
    /**
     * @param aList
     *            a list of parameters for this component
     * @param aValue
     *            a value string for this component
     */
    public Duration(final ParameterList aList, final String aValue) {
        super(DURATION, aList);
        setValue(aValue);
    }

    /**
     * @param aDuration
     *            a duration specified in milliseconds
     */
    public Duration(final long aDuration) {
        super(DURATION);
        setDuration(aDuration);
    }

    /**
     * @param aList
     *            a list of parameters for this component
     * @param aDuration
     *            a duration specified in milliseconds
     */
    public Duration(final ParameterList aList, final long aDuration) {
        super(DURATION, aList);
        setDuration(aDuration);
    }
    
    /**
     * Constructs a new duration representing the time
     * between the specified start date and end date.
     * @param start the starting time for the duration
     * @param end the end time for the duration
     */
    public Duration(final Date start, final Date end) {
        super(DURATION);
        setDuration(end.getTime() - start.getTime());
    }

    /**
     * @return Returns the duration.
     */
    public final long getDuration() {
        return duration;
    }
    
    
    /* (non-Javadoc)
     * @see net.fortuna.ical4j.model.Property#setValue(java.lang.String)
     */
    public final void setValue(final String aValue) {
        duration = DurationFormat.getInstance().parse(aValue);
    }

    /*
     * (non-Javadoc)
     *
     * @see net.fortuna.ical4j.model.Property#getValue()
     */
    public final String getValue() {
        return DurationFormat.getInstance().format(getDuration());
    }
    
    /**
     * @param duration The duration to set.
     */
    public final void setDuration(final long duration) {
        this.duration = duration;
    }
}