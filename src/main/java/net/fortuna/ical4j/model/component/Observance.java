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
package net.fortuna.ical4j.model.component;

import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.property.*;
import net.fortuna.ical4j.util.TimeZones;
import net.fortuna.ical4j.validate.ComponentValidator;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.ValidationResult;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static net.fortuna.ical4j.model.Property.*;

/**
 * $Id$ [05-Apr-2004]
 * <p/>
 * Defines an iCalendar sub-component representing a timezone observance. Class made abstract such that only Standard
 * and Daylight instances are valid.
 *
 * @author Ben Fortuna
 */
public abstract class Observance extends Component implements TimeZonePropertyAccessor {

    /**
     *
     */
    private static final long serialVersionUID = 2523330383042085994L;

    /**
     * one of 'standardc' or 'daylightc' MUST occur and each MAY occur more than once.
     */
    public static final String STANDARD = "STANDARD";

    /**
     * Token for daylight observance.
     */
    public static final String DAYLIGHT = "DAYLIGHT";

    // TODO: clear cache when observance definition changes (??)
    private long[] onsetsMillisec;
    private OffsetDateTime[] onsetsDates;
    //    private Map onsets = new TreeMap();
    private OffsetDateTime initialOnset = null;

    /**
     * Used for parsing times in a UTC date-time representation.
     */
    private static final String UTC_PATTERN = "yyyyMMdd'T'HHmmss";
    private static final DateFormat UTC_FORMAT = new SimpleDateFormat(UTC_PATTERN);

    static {
        UTC_FORMAT.setTimeZone(TimeZones.getUtcTimeZone());
        UTC_FORMAT.setLenient(false);
    }

    /* If this is set we have rrules. If we get a date after this rebuild onsets */
    private OffsetDateTime onsetLimit;

    /**
     * Constructs a timezone observance with the specified name and no properties.
     *
     * @param name the name of this observance component
     */
    protected Observance(final String name) {
        super(name);
    }

    /**
     * Constructor protected to enforce use of sub-classes from this library.
     *
     * @param name       the name of the time type
     * @param properties a list of properties
     */
    protected Observance(final String name, final PropertyList properties) {
        super(name, properties);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValidationResult validate(final boolean recurse) throws ValidationException {
        var result = ComponentValidator.OBSERVANCE_ITIP.validate(this);
        if (recurse) {
            result = result.merge(validateProperties());
        }
        return result;
    }

    /**
     * Returns the latest applicable onset of this observance for the specified date.
     *
     * @param date the latest date that an observance onset may occur
     * @return the latest applicable observance date or null if there is no applicable observance onset for the
     * specified date
     */
    public final OffsetDateTime getLatestOnset(final Temporal date) {
        if (!TemporalAdapter.isDateTimePrecision(date)) {
            throw new UnsupportedOperationException("Unable to get timezone observance for date-only temporal.");
        }

        TzOffsetTo offsetTo = getRequiredProperty(TZOFFSETTO);

        TzOffsetFrom offsetFrom = getRequiredProperty(TZOFFSETFROM);

        var offsetDate = LocalDateTime.ofInstant(Instant.from(date), ZoneOffset.UTC).atOffset(
                offsetTo.getOffset());

        // get first onset without applying TZFROM offset as this may lead to a day boundary
        // change which would be incompatible with BYDAY RRULES
        // we will have to add the offset to all cacheable onsets

        if (initialOnset == null) {
            try {
                DtStart<?> dtStart = getRequiredProperty(DTSTART);
                if (dtStart.getDate().isSupported(ChronoField.HOUR_OF_DAY)) {
                    initialOnset = LocalDateTime.from(dtStart.getDate()).atOffset(offsetFrom.getOffset());
                } else {
                    initialOnset = LocalDate.from(dtStart.getDate()).atStartOfDay().atOffset(offsetFrom.getOffset());
                }
            } catch (ConstraintViolationException e) {
                var log = LoggerFactory.getLogger(Observance.class);
                log.warn("Unexpected error calculating initial onset - applying default", e);
                initialOnset = LocalDateTime.ofEpochSecond(0,0,
                        offsetFrom.getOffset()).atOffset(offsetFrom.getOffset());
            }
        }

        // observance not applicable if date is before the effective date of this observance..
        if (TemporalAdapter.isBefore(offsetDate, initialOnset)) {
            return null;
        }

        if ((onsetsMillisec != null) &&
                (onsetLimit == null || TemporalAdapter.isBefore(offsetDate, onsetLimit))) {

            return getCachedOnset(offsetDate);
        }

        var onset = initialOnset;

        // collect all onsets for the purposes of caching..
        final List<OffsetDateTime> cacheableOnsets = new ArrayList<>();
        cacheableOnsets.add(initialOnset);

        // check rdates for latest applicable onset..
        final List<RDate<LocalDateTime>> rdates = getProperties(RDATE);
        for (RDate<LocalDateTime> rdate : rdates) {
            List<LocalDateTime> rdateDates = rdate.getDates();
            for (final var rdateDate : rdateDates) {
                final var rdateOnset = rdateDate.atOffset(offsetFrom.getOffset());
                if (!rdateOnset.isAfter(offsetDate) && rdateOnset.isAfter(onset)) {
                    onset = rdateOnset;
                }
                /*
                 * else if (rdateOnset.after(date) && rdateOnset.after(onset) && (nextOnset == null ||
                 * rdateOnset.before(nextOnset))) { nextOnset = rdateOnset; }
                 */
                cacheableOnsets.add(rdateOnset);
            }
        }

        // check recurrence rules for latest applicable onset..
        final List<RRule<OffsetDateTime>> rrules = getProperties(RRULE);
        for (RRule<OffsetDateTime> rrule : rrules) {
            // include future onsets to determine onset period..
            onsetLimit = offsetDate.plusYears(10);
            final List<OffsetDateTime> recurrenceDates = rrule.getRecur().getDates(initialOnset, onsetLimit);
            for (final Temporal recurDate : recurrenceDates) {
                final var rruleOnset = OffsetDateTime.from(recurDate).plusSeconds(offsetFrom.getOffset().getTotalSeconds());
                if (!rruleOnset.isAfter(offsetDate) && rruleOnset.isAfter(onset)) {
                    onset = rruleOnset;
                }
                /*
                 * else if (rruleOnset.after(date) && rruleOnset.after(onset) && (nextOnset == null ||
                 * rruleOnset.before(nextOnset))) { nextOnset = rruleOnset; }
                 */
                cacheableOnsets.add(rruleOnset);
            }
        }

        // cache onsets..
        Collections.sort(cacheableOnsets);
        OffsetDateTime cacheableOnset;
        this.onsetsMillisec = new long[cacheableOnsets.size()];
        this.onsetsDates = new OffsetDateTime[onsetsMillisec.length];

        for (int i = 0; i < onsetsMillisec.length; i++) {
            cacheableOnset = cacheableOnsets.get(i);
            onsetsMillisec[i] = cacheableOnset.toInstant().toEpochMilli();
            onsetsDates[i] = cacheableOnset;
        }

        return onset;
    }

    /**
     * Returns a cached onset for the specified date.
     *
     * @param date
     * @return a cached onset date or null if no cached onset is applicable for the specified date
     */
    private OffsetDateTime getCachedOnset(final Temporal date) {
        int index = Arrays.binarySearch(onsetsMillisec, Instant.from(date).toEpochMilli());
        if (index >= 0) {
            return onsetsDates[index];
        } else {
            int insertionIndex = -index - 1;
            return onsetsDates[insertionIndex - 1];
        }
    }
}
