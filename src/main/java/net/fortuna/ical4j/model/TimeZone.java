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

import net.fortuna.ical4j.model.component.Daylight;
import net.fortuna.ical4j.model.component.Observance;
import net.fortuna.ical4j.model.component.Standard;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.TzId;
import net.fortuna.ical4j.model.property.TzOffsetFrom;
import net.fortuna.ical4j.model.property.TzOffsetTo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * $Id$
 * <p/>
 * Created on 13/09/2005
 * <p/>
 * A Java timezone implementation based on an underlying VTimeZone
 * definition.
 *
 * @author Ben Fortuna
 */
public class TimeZone extends java.util.TimeZone {

    private static final long serialVersionUID = -5620979316746547234L;

    private static final Logger LOG = LoggerFactory.getLogger(TimeZone.class);

    private final VTimeZone vTimeZone;
    private final int rawOffset;

    /**
     * Constructs a new instance based on the specified VTimeZone.
     *
     * @param vTimeZone a VTIMEZONE object instance
     */
    public TimeZone(final VTimeZone vTimeZone) {
        this.vTimeZone = vTimeZone;
        final Optional<TzId> tzId = vTimeZone.getProperties().getFirst(Property.TZID);
        if (tzId.isPresent()) {
            setID(tzId.get().getValue());
        } else {
            throw new IllegalArgumentException("Invalid timezone argument");
        }
        this.rawOffset = getRawOffset(vTimeZone);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int getOffset(final int era, final int year, final int month, final int dayOfMonth,
                               final int dayOfWeek, final int milliseconds) {

        // calculate time of day
        int ms = milliseconds;
        final int hour = ms / 3600000;
        ms -= hour * 3600000;
        final int minute = ms / 60000;
        ms -= minute * 60000;
        final int second = ms / 1000;
        ms -= second * 1000;

        // convert zero-based month of old API to new API by adding 1..
        OffsetDateTime date = OffsetDateTime.of(year, month + 1, dayOfMonth, hour, minute, second, ms * 1000, ZoneOffset.ofTotalSeconds(getRawOffset() / 1000));
        final Observance observance = vTimeZone.getApplicableObservance(date);
        if (observance != null) {
            try {
                final TzOffsetTo offset = observance.getProperties().getRequired(Property.TZOFFSETTO);
                return (int) (offset.getOffset().getTotalSeconds() * 1000L);
            } catch (ConstraintViolationException cve) {
                LOG.error("Invalid observance", cve);
            }
        }
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getOffset(long date) {
        final Observance observance = vTimeZone.getApplicableObservance(Instant.ofEpochMilli(date));
        if (observance != null) {
            try {
                final TzOffsetTo offset = observance.getProperties().getRequired(Property.TZOFFSETTO);
                if ((offset.getOffset().getTotalSeconds() * 1000L) < getRawOffset()) {
                    return getRawOffset();
                } else {
                    return (int) (offset.getOffset().getTotalSeconds() * 1000L);
                }
            } catch (ConstraintViolationException cve) {
                LOG.error("Invalid observance", cve);
            }
        }
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int getRawOffset() {
        return rawOffset;
    }

    /**
     * Determines if the specified date is in daylight time according to
     * this timezone. This is done by finding the latest supporting
     * observance for the specified date and identifying whether it is
     * daylight time.
     *
     * @param date a date instance
     * @return true if the specified date is in daylight time, otherwise false
     */
    @Override
    public final boolean inDaylightTime(final Date date) {
        final Observance observance = vTimeZone.getApplicableObservance(date.toInstant());
        return (observance instanceof Daylight);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setRawOffset(final int offsetMillis) {
        throw new UnsupportedOperationException("Updates to the VTIMEZONE object must be performed directly");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean useDaylightTime() {
        final List<Observance> daylights = vTimeZone.getObservances().get(Observance.DAYLIGHT);
        return (!daylights.isEmpty());
    }

    /**
     * @return Returns the VTimeZone backing this instance.
     */
    public final VTimeZone getVTimeZone() {
        return vTimeZone;
    }

    private static int getRawOffset(VTimeZone vt) {

        List<Observance> seasonalTimes = vt.getObservances().get(Observance.STANDARD);
        // if no standard time use daylight time..
        if (seasonalTimes.isEmpty()) {
            seasonalTimes = vt.getObservances().get(Observance.DAYLIGHT);
            if (seasonalTimes.isEmpty()) {
                return 0;
            }
        }
        Observance latestSeasonalTime = null;
        if (seasonalTimes.size() > 1) {
            // per java spec and when dealing with historical time,
            // rawoffset is the raw offset at the current date
            OffsetDateTime latestOnset = null;
            for (Observance seasonalTime : seasonalTimes) {
                OffsetDateTime onset = seasonalTime.getLatestOnset(Instant.now());
                if (onset == null) {
                    continue;
                }
                if (latestOnset == null || onset.isAfter(latestOnset)) {
                    latestOnset = onset;
                    latestSeasonalTime = seasonalTime;
                }
            }
        } else {
            latestSeasonalTime = seasonalTimes.get(0);
        }
        if (latestSeasonalTime instanceof Daylight) {
            final Optional<TzOffsetFrom> offsetFrom = latestSeasonalTime.getProperties().getFirst(Property.TZOFFSETFROM);
            if (offsetFrom.isPresent()) {
                return (int) (offsetFrom.get().getOffset().getTotalSeconds() * 1000L);
            }
        } else if (latestSeasonalTime instanceof Standard) {
            final Optional<TzOffsetTo> offsetTo = latestSeasonalTime.getProperties().getFirst(Property.TZOFFSETTO);
            if (offsetTo.isPresent()) {
                return (int) (offsetTo.get().getOffset().getTotalSeconds() * 1000L);
            }
        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TimeZone timeZone = (TimeZone) o;

        return rawOffset == timeZone.rawOffset
                && !(vTimeZone != null ? !vTimeZone.equals(timeZone.vTimeZone) : timeZone.vTimeZone != null);
    }

    @Override
    public int hashCode() {
        int result = vTimeZone != null ? vTimeZone.hashCode() : 0;
        result = 31 * result + rawOffset;
        return result;
    }
}
