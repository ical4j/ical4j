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
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.*;
import net.fortuna.ical4j.util.Dates;
import net.fortuna.ical4j.util.TimeZones;
import net.fortuna.ical4j.validate.ComponentValidator;
import net.fortuna.ical4j.validate.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * $Id$ [05-Apr-2004]
 * <p/>
 * Defines an iCalendar sub-component representing a timezone observance. Class made abstract such that only Standard
 * and Daylight instances are valid.
 *
 * @author Ben Fortuna
 */
public abstract class Observance extends Component {

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
    private DateTime[] onsetsDates;
    //    private Map onsets = new TreeMap();
    private Date initialOnset = null;
    private DateTime initialOnsetUTC = null;

    /**
     * Used for parsing times in a UTC date-time representation.
     */
    private static final String UTC_PATTERN = "yyyyMMdd'T'HHmmss";
    private static final DateFormat UTC_FORMAT = new SimpleDateFormat(
            UTC_PATTERN);

    static {
        UTC_FORMAT.setTimeZone(TimeZones.getUtcTimeZone());
        UTC_FORMAT.setLenient(false);
    }

    /* If this is set we have rrules. If we get a date after this rebuild onsets */
    private Date onsetLimit;

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
    protected Observance(final String name, final PropertyList<Property> properties) {
        super(name, properties);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void validate(final boolean recurse) throws ValidationException {
        ComponentValidator.OBSERVANCE_ITIP.validate(this);
        if (recurse) {
            validateProperties();
        }
    }

    /**
     * Returns the latest applicable onset of this observance for the specified date.
     *
     * @param date the latest date that an observance onset may occur
     * @return the latest applicable observance date or null if there is no applicable observance onset for the
     * specified date
     */
    public final Date getLatestOnset(final Date date) {

        // get first onset without adding TZFROM as this may lead to a day boundary
        // change which would be incompatible with BYDAY RRULES
        // we will have to add the offset to all cacheable onsets
        if (initialOnsetUTC == null) {
            try {
                DtStart dtStart = (DtStart) getRequiredProperty(Property.DTSTART);
                initialOnsetUTC = calculateOnset(dtStart.getDate());
            } catch (ParseException | ConstraintViolationException e) {
                Logger log = LoggerFactory.getLogger(Observance.class);
                log.error("Unexpected error calculating initial onset", e);
                // XXX: is this correct?
    //            return null;
                initialOnsetUTC = new DateTime(new java.util.Date(0));
            }
        }

        if (initialOnset == null) {
            initialOnset = applyOffsetFrom(initialOnsetUTC);
        }

        // observance not applicable if date is before the effective date of this observance..
        if (date.before(initialOnset)) {
            return null;
        }

        if ((onsetsMillisec != null) && (onsetLimit == null || date.before(onsetLimit))) {
            return getCachedOnset(date);
        }

        Date onset = initialOnset;
        // collect all onsets for the purposes of caching..
        final DateList cacheableOnsets = new DateList();
        cacheableOnsets.setUtc(true);
        cacheableOnsets.add(initialOnset);

        // check rdates for latest applicable onset..
        final List<RDate> rdates = getProperties(Property.RDATE);
        for (RDate rdate : rdates) {            
            for (final Date rdateDate : rdate.getDates()) {
                try {
                    final DateTime rdateOnset = applyOffsetFrom(calculateOnset(rdateDate));
                    if (!rdateOnset.after(date) && rdateOnset.after(onset)) {
                        onset = rdateOnset;
                    }
                    /*
                     * else if (rdateOnset.after(date) && rdateOnset.after(onset) && (nextOnset == null ||
                     * rdateOnset.before(nextOnset))) { nextOnset = rdateOnset; }
                     */
                    cacheableOnsets.add(rdateOnset);
                } catch (ParseException e) {
                    Logger log = LoggerFactory.getLogger(Observance.class);
                    log.error("Unexpected error calculating onset", e);
                }
            }
        }

        // check recurrence rules for latest applicable onset..
        final List<RRule> rrules = getProperties(Property.RRULE);
        for (RRule rrule : rrules) {            
            // include future onsets to determine onset period..
            final Calendar cal = Dates.getCalendarInstance(date);
            cal.setTime(date);
            cal.add(Calendar.YEAR, 10);
            onsetLimit = Dates.getInstance(cal.getTime(), Value.DATE_TIME);
            final DateList recurrenceDates;
            if (rrule.getRecur().getDayList().isEmpty()) {
                recurrenceDates = rrule.getRecur().getDates(initialOnset, onsetLimit, Value.DATE_TIME);
            } else {
                recurrenceDates = rrule.getRecur().getDates(initialOnsetUTC, onsetLimit, Value.DATE_TIME);
            }
            for (final Date recurDate : recurrenceDates) {
                final DateTime rruleOnset = applyOffsetFrom((DateTime) recurDate);
                if (!rruleOnset.after(date) && rruleOnset.after(onset)) {
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
        DateTime cacheableOnset;
        this.onsetsMillisec = new long[cacheableOnsets.size()];
        this.onsetsDates = new DateTime[onsetsMillisec.length];

        for (int i = 0; i < onsetsMillisec.length; i++) {
            cacheableOnset = (DateTime) cacheableOnsets.get(i);
            onsetsMillisec[i] = cacheableOnset.getTime();
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
    private DateTime getCachedOnset(final Date date) {
        int index = Arrays.binarySearch(onsetsMillisec, date.getTime());
        if (index >= 0) {
            return onsetsDates[index];
        } else {
            int insertionIndex = -index - 1;
            return onsetsDates[insertionIndex - 1];
        }
    }

    /**
     * Returns the mandatory dtstart property.
     *
     * @return the DTSTART property or null if not specified
     */
    public final DtStart getStartDate() {
        return getProperty(Property.DTSTART);
    }

    /**
     * Returns the mandatory tzoffsetfrom property.
     *
     * @return the TZOFFSETFROM property or null if not specified
     */
    public final TzOffsetFrom getOffsetFrom() {
        return getProperty(Property.TZOFFSETFROM);
    }

    /**
     * Returns the mandatory tzoffsetto property.
     *
     * @return the TZOFFSETTO property or null if not specified
     */
    public final TzOffsetTo getOffsetTo() {
        return getProperty(Property.TZOFFSETTO);
    }

    //    private Date calculateOnset(DateProperty dateProperty) {
//        return calculateOnset(dateProperty.getValue());
//    }
//    
    private DateTime calculateOnset(Date date) throws ParseException {
        return calculateOnset(date.toString());
    }

    private DateTime calculateOnset(String dateStr) throws ParseException {

        // Translate local onset into UTC time by parsing local time 
        // as GMT and adjusting by TZOFFSETFROM if required
        long utcOnset;

        synchronized (UTC_FORMAT) {
            utcOnset = UTC_FORMAT.parse(dateStr).getTime();
        }

        // return a UTC
        DateTime onset = new DateTime(true);
        onset.setTime(utcOnset);
        return onset;
    }

    private DateTime applyOffsetFrom(DateTime orig) {
        DateTime withOffset = new DateTime(true);
        withOffset.setTime(orig.getTime() - (getOffsetFrom().getOffset().getTotalSeconds() * 1000L));
        return withOffset;
    }
}
