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

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateList;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.RDate;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.model.property.TzOffsetFrom;
import net.fortuna.ical4j.model.property.TzOffsetTo;
import net.fortuna.ical4j.util.Dates;
import net.fortuna.ical4j.util.PropertyValidator;
import net.fortuna.ical4j.util.TimeZones;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * $Id$ [05-Apr-2004]
 *
 * Defines an iCalendar sub-component representing a timezone observance. Class made abstract such that only Standard
 * and Daylight instances are valid.
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
    private Map onsets = new TreeMap();
    private Date initialOnset = null;
    
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
     * @param name the name of this observance component
     */
    protected Observance(final String name) {
        super(name);
    }

    /**
     * Constructor protected to enforce use of sub-classes from this library.
     * @param name the name of the time type
     * @param properties a list of properties
     */
    protected Observance(final String name, final PropertyList properties) {
        super(name, properties);
    }

    /**
     * {@inheritDoc}
     */
    public final void validate(final boolean recurse) throws ValidationException {

        // From "4.8.3.3 Time Zone Offset From":
        // Conformance: This property MUST be specified in a "VTIMEZONE"
        // calendar component.
        PropertyValidator.getInstance().assertOne(Property.TZOFFSETFROM,
                getProperties());

        // From "4.8.3.4 Time Zone Offset To":
        // Conformance: This property MUST be specified in a "VTIMEZONE"
        // calendar component.
        PropertyValidator.getInstance().assertOne(Property.TZOFFSETTO,
                getProperties());

        /*
         * ; the following are each REQUIRED, ; but MUST NOT occur more than once dtstart / tzoffsetto / tzoffsetfrom /
         */
        PropertyValidator.getInstance().assertOne(Property.DTSTART,
                getProperties());

        /*
         * ; the following are optional, ; and MAY occur more than once comment / rdate / rrule / tzname / x-prop
         */

        if (recurse) {
            validateProperties();
        }
    }

    /**
     * Returns the latest applicable onset of this observance for the specified date.
     * @param date the latest date that an observance onset may occur
     * @return the latest applicable observance date or null if there is no applicable observance onset for the
     * specified date
     */
    public final Date getLatestOnset(final Date date) {
        
        if (initialOnset == null) {
            try {
                initialOnset = applyOffsetFrom(calculateOnset(((DtStart) getProperty(Property.DTSTART)).getDate()));
            } catch (ParseException e) {
                Log log = LogFactory.getLog(Observance.class);
                log.error("Unexpected error calculating initial onset", e);
                // XXX: is this correct?
                return null;
            }
        }
        
        // observance not applicable if date is before the effective date of this observance..
        if (date.before(initialOnset)) {
            return null;
        }

        if ((onsetsMillisec != null) && (onsetLimit == null || date.before(onsetLimit))) {
            return getCachedOnset(date);
        }

        Date onset = initialOnset;
        Date initialOnsetUTC;
        // get first onset without adding TZFROM as this may lead to a day boundary
        // change which would be incompatible with BYDAY RRULES
        // we will have to add the offset to all cacheable onsets
        try {
            initialOnsetUTC = calculateOnset(((DtStart) getProperty(Property.DTSTART)).getDate());
        } catch (ParseException e) {
            Log log = LogFactory.getLog(Observance.class);
            log.error("Unexpected error calculating initial onset", e);
            // XXX: is this correct?
            return null;
        }
        // collect all onsets for the purposes of caching..
        final DateList cacheableOnsets = new DateList();
        cacheableOnsets.setUtc(true);
        cacheableOnsets.add(initialOnset);

        // check rdates for latest applicable onset..
        final PropertyList rdates = getProperties(Property.RDATE);
        for (final Iterator i = rdates.iterator(); i.hasNext();) {
            final RDate rdate = (RDate) i.next();
            for (final Iterator j = rdate.getDates().iterator(); j.hasNext();) {
                try {
                    final DateTime rdateOnset = applyOffsetFrom(calculateOnset((Date) j.next()));
                    if (!rdateOnset.after(date) && rdateOnset.after(onset)) {
                        onset = rdateOnset;
                    }
                    /*
                     * else if (rdateOnset.after(date) && rdateOnset.after(onset) && (nextOnset == null ||
                     * rdateOnset.before(nextOnset))) { nextOnset = rdateOnset; }
                     */
                    cacheableOnsets.add(rdateOnset);
                } catch (ParseException e) {
                    Log log = LogFactory.getLog(Observance.class);
                    log.error("Unexpected error calculating onset", e);
                }
            }
        }

        // check recurrence rules for latest applicable onset..
        final PropertyList rrules = getProperties(Property.RRULE);
        for (final Iterator i = rrules.iterator(); i.hasNext();) {
            final RRule rrule = (RRule) i.next();
            // include future onsets to determine onset period..
            final Calendar cal = Dates.getCalendarInstance(date);
            cal.setTime(date);
            cal.add(Calendar.YEAR, 10);
            onsetLimit = Dates.getInstance(cal.getTime(), Value.DATE_TIME);
            final DateList recurrenceDates = rrule.getRecur().getDates(initialOnsetUTC,
                    onsetLimit, Value.DATE_TIME);
            for (final Iterator j = recurrenceDates.iterator(); j.hasNext();) {
                final DateTime rruleOnset = applyOffsetFrom((DateTime) j.next());
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
        DateTime cacheableOnset = null;
        this.onsetsMillisec = new long[cacheableOnsets.size()];
        this.onsetsDates = new DateTime[onsetsMillisec.length];

        for (int i = 0; i < onsetsMillisec.length; i++) {
            cacheableOnset = (DateTime)cacheableOnsets.get(i);
            onsetsMillisec[i] = cacheableOnset.getTime();
            onsetsDates[i] = cacheableOnset;
        }

        return onset;
    }

    /**
     * Returns a cached onset for the specified date.
     * @param date
     * @return a cached onset date or null if no cached onset is applicable for the specified date
     */
    private DateTime getCachedOnset(final Date date) {
        int index = Arrays.binarySearch(onsetsMillisec, date.getTime());
        if (index >= 0) {
            return onsetsDates[index];
        } else {
            int insertionIndex = -index -1;
            return onsetsDates[insertionIndex -1];
        }
    }

    /**
     * Returns the mandatory dtstart property.
     * @return the DTSTART property or null if not specified
     */
    public final DtStart getStartDate() {
        return (DtStart) getProperty(Property.DTSTART);
    }

    /**
     * Returns the mandatory tzoffsetfrom property.
     * @return the TZOFFSETFROM property or null if not specified
     */
    public final TzOffsetFrom getOffsetFrom() {
        return (TzOffsetFrom) getProperty(Property.TZOFFSETFROM);
    }

    /**
     * Returns the mandatory tzoffsetto property.
     * @return the TZOFFSETTO property or null if not specified
     */
    public final TzOffsetTo getOffsetTo() {
        return (TzOffsetTo) getProperty(Property.TZOFFSETTO);
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
        withOffset.setTime(orig.getTime() - getOffsetFrom().getOffset().getOffset());
        return withOffset;
    }
}