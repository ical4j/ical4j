/**
 * Copyright (c) 2009, Ben Fortuna
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
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateList;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.TimeZone;
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
public abstract class Observance extends Component implements Comparable {

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

    private transient Log log = LogFactory.getLog(Observance.class);

    // TODO: clear cache when observance definition changes (??)
    private Map onsets = new TreeMap();
    private Date initialOnset = null;
    
    /**
     * Used for parsing times in a UTC date-time representation.
     */
    private static final String UTC_PATTERN = "yyyyMMdd'T'HHmmss";
    private static final DateFormat UTC_FORMAT = new SimpleDateFormat(
            UTC_PATTERN);
    
    static {
        UTC_FORMAT.setTimeZone(TimeZone.getTimeZone(TimeZones.UTC_ID));
        UTC_FORMAT.setLenient(false);
    }

    /* If this is set we have rrules. If we get a date after this rebuild onsets */
    private Date onsetLimit;

    private boolean rdatesCached = false;

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
                initialOnset = calculateOnset(((DtStart) getProperty(Property.DTSTART)).getDate());
            } catch (ParseException e) {
                log.error("Unexpected error calculating initial onset", e);
                // XXX: is this correct?
                return null;
            }
        }
        
        // observance not applicable if date is before the effective date of this observance..
        if (date.before(initialOnset)) {
            return null;
        }

        final long start = System.currentTimeMillis();

        if ((onsetLimit != null) && (date.after(onsetLimit))) {
            onsets.clear();
            rdatesCached = false;
        }

        Date onset = getCachedOnset(date);

        final boolean cacheHit = onset != null;
        
        if (onset == null) {
            onset = initialOnset;
            // collect all onsets for the purposes of caching..
            final DateList cacheableOnsets = new DateList();
            // Date nextOnset = null;

            if (!rdatesCached) {
                // check rdates for latest applicable onset..
                final PropertyList rdates = getProperties(Property.RDATE);
                for (final Iterator i = rdates.iterator(); i.hasNext();) {
                    final RDate rdate = (RDate) i.next();
                    for (final Iterator j = rdate.getDates().iterator(); j.hasNext();) {
                        try {
                            final Date rdateOnset = calculateOnset((Date) j.next());
                            if (!rdateOnset.after(date) && rdateOnset.after(onset)) {
                                onset = rdateOnset;
                            }
                            /*
                             * else if (rdateOnset.after(date) && rdateOnset.after(onset) && (nextOnset == null ||
                             * rdateOnset.before(nextOnset))) { nextOnset = rdateOnset; }
                             */
                            cacheableOnsets.add(rdateOnset);
                        } catch (ParseException e) {
                            log.error("Unexpected error calculating onset", e);
                        }
                    }
                }
                rdatesCached = true;
            }

            // check recurrence rules for latest applicable onset..
            final PropertyList rrules = getProperties(Property.RRULE);
            Value dateType;
            if (date instanceof DateTime) {
                dateType = Value.DATE_TIME;
            }
            else {
                dateType = Value.DATE;
            }
            for (final Iterator i = rrules.iterator(); i.hasNext();) {
                final RRule rrule = (RRule) i.next();
                // include future onsets to determine onset period..
                final Calendar cal = Dates.getCalendarInstance(date);
                cal.setTime(date);
                cal.add(Calendar.YEAR, 10);
                onsetLimit = Dates.getInstance(cal.getTime(), dateType);
                final DateList recurrenceDates = rrule.getRecur().getDates(onset,
                         onsetLimit, dateType);
                for (final Iterator j = recurrenceDates.iterator(); j.hasNext();) {
                    final Date rruleOnset = (Date) j.next();
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
            Date cacheableOnset = null;
            Date nextOnset = null;
            for (final Iterator i = cacheableOnsets.iterator(); i.hasNext();) {
                cacheableOnset = nextOnset;
                nextOnset = (Date) i.next();
                if (cacheableOnset != null) {
                    onsets.put(new Period(new DateTime(cacheableOnset),
                            new DateTime(nextOnset)), cacheableOnset);
                }
            }

            // as we don't have an onset following the final onset, we must
            // cache it with an arbitrary period length..
            if (nextOnset != null) {
                final Calendar finalOnsetPeriodEnd = Calendar.getInstance();
                finalOnsetPeriodEnd.setTime(nextOnset);
                finalOnsetPeriodEnd.add(Calendar.YEAR, 100);
                onsets.put(new Period(new DateTime(nextOnset), new DateTime(
                        finalOnsetPeriodEnd.getTime())), nextOnset);
            }

            /*
             * Period onsetPeriod = null; if (nextOnset != null) { onsetPeriod = new Period(new DateTime(onset), new
             * DateTime(nextOnset)); } else { onsetPeriod = new Period(new DateTime(onset), new DateTime(date)); }
             * onsets.put(onsetPeriod, onset);
             */
        }
        
        if (log.isTraceEnabled()) {
            log.trace("Cache " + (cacheHit ? "hit" : "miss")
                    + " - retrieval time: "
                    + (System.currentTimeMillis() - start) + "ms");
        }

        return onset;
    }

    /**
     * Returns a cached onset for the specified date.
     * @param date
     * @return a cached onset date or null if no cached onset is applicable for the specified date
     */
    private Date getCachedOnset(final Date date) {
        for (final Iterator i = onsets.keySet().iterator(); i.hasNext();) {
            final Period onsetPeriod = (Period) i.next();
            if (onsetPeriod.includes(date, Period.INCLUSIVE_START)) {
                return (Date) onsets.get(onsetPeriod);
            }
        }
        return null;
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

    /**
     * {@inheritDoc}
     */
    public final int compareTo(final Object arg0) {
        return compareTo((Observance) arg0);
    }

    /**
     * @param arg0 another observance instance
     * @return a positve value if this observance starts earlier than the other,
     * a negative value if it occurs later than the other, or zero if they start
     * at the same time
     */
    public final int compareTo(final Observance arg0) {
        // TODO: sort by RDATE??
        final DtStart dtStart = (DtStart) getProperty(Property.DTSTART);
        final DtStart dtStart0 = (DtStart) arg0.getProperty(Property.DTSTART);
        return dtStart.getDate().compareTo(dtStart0.getDate());
    }

    /**
     * @param stream
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(java.io.ObjectInputStream stream)
        throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        log = LogFactory.getLog(Observance.class);
    }
    
//    private Date calculateOnset(DateProperty dateProperty) {
//        return calculateOnset(dateProperty.getValue());
//    }
//    
    private Date calculateOnset(Date date) throws ParseException {
        return calculateOnset(date.toString());
    }
    
    private Date calculateOnset(String dateStr) throws ParseException {
        
        // Translate local onset into UTC time by parsing local time 
        // as GMT and adjusting by TZOFFSETFROM
//        try {
            java.util.Date utcOnset = null;
       
            synchronized(UTC_FORMAT) {
                utcOnset = UTC_FORMAT.parse(dateStr);
            }
            
            final long offset = getOffsetFrom().getOffset().getOffset();
            return new DateTime(utcOnset.getTime() - offset);
//        } catch (ParseException e) {
//            throw new RuntimeException(e);
//        }
    }
}