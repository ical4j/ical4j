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
import net.fortuna.ical4j.validate.ComponentValidator;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.ValidationResult;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    /**
     * Fixed upper bound on the number of onsets expanded from a single observance RRULE, shared by
     * every site that expands an observance over an internally chosen window (this class and
     * {@link net.fortuna.ical4j.model.ZoneRulesBuilder}). A real timezone observance recurs yearly,
     * so even a definition spanning the whole representable range stays far below this; the cap only
     * bites on a crafted observance with a sub-yearly frequency (e.g. {@code FREQ=HOURLY}) and an
     * early DTSTART, which would otherwise expand to millions of onsets from a few hundred bytes of
     * parsed calendar data.
     * <p>
     * This is a hard cap on the number of generated onsets. It is distinct from the configurable
     * {@code net.fortuna.ical4j.recur.maxincrementcount} recurrence setting, which bounds the number
     * of consecutive increment steps that yield no candidate rather than the number of instances
     * produced; the two happen to share the same default value.
     */
    public static final int MAX_RECURRENCE_ONSETS = 1000;

    private static final Logger LOG = LoggerFactory.getLogger(Observance.class);

    // TODO: clear cache when observance definition changes (??)
    private long @Nullable [] onsetsMillisec;
    private OffsetDateTime @Nullable [] onsetsDates;
    //    private Map onsets = new TreeMap();
    private @Nullable OffsetDateTime initialOnset = null;

    /* If this is set we have rrules. If we get a date after this rebuild onsets */
    private @Nullable OffsetDateTime onsetLimit;

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
    public final @Nullable OffsetDateTime getLatestOnset(final Temporal date) {
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
                LOG.warn("Unexpected error calculating initial onset - applying default", e);
                initialOnset = LocalDateTime.ofEpochSecond(0,0,
                        offsetFrom.getOffset()).atOffset(offsetFrom.getOffset());
            }
        }

        // observance not applicable if date is before the effective date of this observance..
        if (TemporalAdapter.isBefore(offsetDate, initialOnset)) {
            return null;
        }

        final long[] cachedOnsetsMillisec = onsetsMillisec;
        final OffsetDateTime[] cachedOnsetsDates = onsetsDates;
        if (cachedOnsetsMillisec != null && cachedOnsetsDates != null &&
                (onsetLimit == null || TemporalAdapter.isBefore(offsetDate, onsetLimit))) {

            return getCachedOnset(offsetDate, cachedOnsetsMillisec, cachedOnsetsDates);
        }

        var onset = initialOnset;

        // collect all onsets for the purposes of caching..
        final List<OffsetDateTime> cacheableOnsets = new ArrayList<>();
        cacheableOnsets.add(initialOnset);

        // check rdates for latest applicable onset..
        final List<RDate<LocalDateTime>> rdates = getProperties(RDATE);
        for (RDate<LocalDateTime> rdate : rdates) {
            if (rdate.getPeriods().isPresent()) {
                for (var rdatePeriod : rdate.getPeriods().get()) {
                    final var rdateOnset = rdatePeriod.getStart().atOffset(offsetFrom.getOffset());
                    if (!rdateOnset.isAfter(offsetDate) && rdateOnset.isAfter(onset)) {
                        onset = rdateOnset;
                    }
                    cacheableOnsets.add(rdateOnset);
                }
            } else {
                for (final var rdateDate : rdate.getDates()) {
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
        }

        // check recurrence rules for latest applicable onset..
        final List<RRule<OffsetDateTime>> rrules = getProperties(RRULE);
        for (RRule<OffsetDateTime> rrule : rrules) {
            // include future onsets to determine onset period..
            onsetLimit = offsetDate.plusYears(10);
            final var recur = rrule.getRecur();
            // A single period's BY* cross-product is materialised in full inside the recurrence
            // engine before the onset cap below can apply, so a rule whose per-period expansion is
            // itself unbounded (e.g. BYYEARDAY x BYHOUR x BYMINUTE x BYSECOND) would exhaust memory
            // regardless of the cap. A real observance has a per-period product of one, so skipping
            // such a rule and keeping only the initial onset affects crafted input only.
            final long perPeriod = recur.getMaxCandidatesPerPeriod();
            if (perPeriod > MAX_RECURRENCE_ONSETS) {
                LOG.warn("Ignoring observance RRULE with an excessive per-period expansion ({} candidates); "
                        + "using the initial onset only", perPeriod);
                continue;
            }
            final List<OffsetDateTime> recurrenceDates = recur.getDates(initialOnset, initialOnset,
                    onsetLimit, MAX_RECURRENCE_ONSETS);
            if (recurrenceDates.size() >= MAX_RECURRENCE_ONSETS) {
                LOG.warn("Observance RRULE reached the onset expansion cap of {}; latest-onset lookups beyond "
                        + "the cap may be inaccurate", MAX_RECURRENCE_ONSETS);
            }
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
     * @param onsetsMillisec cached onsets in epoch milliseconds
     * @param onsetsDates cached onsets corresponding to {@code onsetsMillisec}
     * @return a cached onset date or null if no cached onset is applicable for the specified date
     */
    private static OffsetDateTime getCachedOnset(final Temporal date, final long[] onsetsMillisec,
                                                 final OffsetDateTime[] onsetsDates) {
        int index = Arrays.binarySearch(onsetsMillisec, Instant.from(date).toEpochMilli());
        if (index >= 0) {
            return onsetsDates[index];
        } else {
            int insertionIndex = -index - 1;
            return onsetsDates[insertionIndex - 1];
        }
    }
}
