/*
 * $Id$ [Apr 14, 2004]
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
package net.fortuna.ical4j.model;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

import net.fortuna.ical4j.util.DateTimeFormat;
import net.fortuna.ical4j.util.DurationFormat;

/**
 * Defines a period of time.
 *
 * @author benf
 */
public class Period implements Serializable {
    
    private static final long serialVersionUID = 7321090422911676490L;

    private Date start;

    private Date end;

    private long duration;

    /**
     * Constructor.
     * @param aValue a string representation of a period
     * @throws ParseException where the specified string is
     * not a valid representation
     */
    public Period(final String aValue) throws ParseException {

        start = DateTimeFormat.getInstance().parse(aValue.substring(0,
                aValue.indexOf('/') - 1));

        // period may end in either a date-time or a duration..
        try {

            end = DateTimeFormat.getInstance()
                    .parse(aValue.substring(aValue.indexOf('/')));
        }
        catch (ParseException pe) {

            duration = DurationFormat.getInstance().parse(aValue);
        }
    }

    /**
     * @return Returns the duration.
     */
    public final long getDuration() {
        return duration;
    }

    /**
     * @return Returns the end.
     */
    public final Date getEnd() {
        return end;
    }

    /**
     * @return Returns the start.
     */
    public final Date getStart() {
        return start;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public final String toString() {
        StringBuffer b = new StringBuffer();

        b.append(DateTimeFormat.getInstance().format(start));
        b.append('/');

        if (end != null) {
            b.append(DateTimeFormat.getInstance().format(end));
        }
        else {
            b.append(DurationFormat.getInstance().format(duration));
        }

        return b.toString();
    }
}