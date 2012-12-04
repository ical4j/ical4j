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

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import net.fortuna.ical4j.model.component.Daylight;
import net.fortuna.ical4j.model.component.Observance;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.TzId;
import net.fortuna.ical4j.model.property.TzOffsetTo;

/**
 * $Id$
 *
 * Created on 13/09/2005
 *
 * A Java timezone implementation based on an underlying VTimeZone
 * definition.
 * @author Ben Fortuna
 */
public class TimeZone extends java.util.TimeZone {
    
    private static final long serialVersionUID = -5620979316746547234L;
    
    private final VTimeZone vTimeZone;
    private final int rawOffset;
    
    /**
     * Constructs a new instance based on the specified VTimeZone.
     * @param vTimeZone a VTIMEZONE object instance
     */
    public TimeZone(final VTimeZone vTimeZone) {
        this.vTimeZone = vTimeZone;
        final TzId tzId = (TzId) vTimeZone.getProperty(Property.TZID);
        setID(tzId.getValue());
        this.rawOffset = getRawOffset(vTimeZone);
    }

    /**
     * {@inheritDoc}
     */
    public final int getOffset(final int era, final int year, final int month, final int day,
            final int dayOfWeek, final int milliseconds) {
        
        final Calendar cal = Calendar.getInstance();
        cal.set(Calendar.ERA, era);
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_YEAR, day);
        cal.set(Calendar.DAY_OF_WEEK, dayOfWeek);
        cal.set(Calendar.MILLISECOND, milliseconds);
        final Observance observance = vTimeZone.getApplicableObservance(new DateTime(cal.getTime()));
        if (observance != null) {
            final TzOffsetTo offset = (TzOffsetTo) observance.getProperty(Property.TZOFFSETTO);
            return (int) offset.getOffset().getOffset();
        }
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public int getOffset(long date) {
        final Observance observance = vTimeZone.getApplicableObservance(new DateTime(date));
        if (observance != null) {
            final TzOffsetTo offset = (TzOffsetTo) observance.getProperty(Property.TZOFFSETTO);
            return (int) offset.getOffset().getOffset();
        }
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public final int getRawOffset() {
        return rawOffset;
    }

    /**
     * Determines if the specified date is in daylight time according to
     * this timezone. This is done by finding the latest supporting
     * observance for the specified date and identifying whether it is
     * daylight time.
     * @param date a date instance
     * @return true if the specified date is in daylight time, otherwise false
     */
    public final boolean inDaylightTime(final Date date) {
        final Observance observance = vTimeZone.getApplicableObservance(new DateTime(date));
        return (observance != null && observance instanceof Daylight);
    }

    /**
     * {@inheritDoc}
     */
    public final void setRawOffset(final int offsetMillis) {
        throw new UnsupportedOperationException("Updates to the VTIMEZONE object must be performed directly");
    }

    /**
     * {@inheritDoc}
     */
    public final boolean useDaylightTime() {
        final ComponentList daylights = vTimeZone.getObservances().getComponents(Observance.DAYLIGHT);
        return (!daylights.isEmpty());
    }

    /**
     * @return Returns the VTimeZone backing this instance.
     */
    public final VTimeZone getVTimeZone() {
        return vTimeZone;
    }

    private static final int getRawOffset(VTimeZone vt) {
        // per spec, rawoffset is the raw offset at the current date
        final DateTime now = new DateTime();
        
        List seasonalTimes = vt.getObservances().getComponents(Observance.STANDARD);
        // if no standard time use daylight time..
        if (seasonalTimes.size() == 0) {
            seasonalTimes = vt.getObservances().getComponents(Observance.DAYLIGHT);
        }
        Observance latestSeasonalTime = null;
        Date latestOnset = null;
        for (int i = 0; i < seasonalTimes.size(); i++) {
            Observance seasonalTime = (Observance) seasonalTimes.get(i);
            Date onset = seasonalTime.getLatestOnset(now);
            if (onset == null) {
                continue;
            }
            if (latestOnset == null || onset.after(latestOnset)) {
                latestOnset = onset;
                latestSeasonalTime = seasonalTime;
            }
        }
        if (latestSeasonalTime != null) {
            final TzOffsetTo offsetTo = (TzOffsetTo) latestSeasonalTime.getProperty(Property.TZOFFSETTO);
            if (offsetTo != null) {
                return (int) offsetTo.getOffset().getOffset();
            }
        }
        return 0;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TimeZone timeZone = (TimeZone) o;

        if (rawOffset != timeZone.rawOffset) return false;
        if (vTimeZone != null ? !vTimeZone.equals(timeZone.vTimeZone) : timeZone.vTimeZone != null) return false;

        return true;
    }

    public int hashCode() {
        int result = vTimeZone != null ? vTimeZone.hashCode() : 0;
        result = 31 * result + rawOffset;
        return result;
    }
}
