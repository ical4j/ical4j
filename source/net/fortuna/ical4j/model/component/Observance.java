/*
 * $Id$ [05-Apr-2004]
 *
 * Copyright (c) 2005, Ben Fortuna
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

import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateList;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Period;
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

/**
 * Defines an iCalendar sub-component representing a timezone observance.
 * Class made abstract such that only Standard and Daylight instances are
 * valid.
 *
 * @author Ben Fortuna
 */
public abstract class Observance extends Component implements Comparable {

    /**
     * one of 'standardc' or 'daylightc' MUST occur and each MAY occur more than
     * once.
     */
    public static final String STANDARD = "STANDARD";

    public static final String DAYLIGHT = "DAYLIGHT";
    
    private Log log = LogFactory.getLog(Observance.class);
    
    // TODO: clear cache when observance definition changes (??)
    private Map onsets = new TreeMap();
    
    private boolean rdatesCached = false;

    /**
     * Constructs a timezone observance with the specified name
     * and no properties.
     * @param name the name of this observance component
     */
    protected Observance(final String name) {
        super(name);
    }

    /**
     * Constructor protected to enforce use of sub-classes
     * from this library.
     * @param name the name of the time type
     * @param properties a list of properties
     */
    protected Observance(final String name, final PropertyList properties) {
        super(name, properties);
    }

    /**
     * @see net.fortuna.ical4j.model.Component#validate(boolean)
     */
    public final void validate(final boolean recurse) throws ValidationException {

        /*

                ; the following are each REQUIRED,
                ; but MUST NOT occur more than once

                dtstart / tzoffsetto / tzoffsetfrom /
         */
        PropertyValidator.getInstance().assertOne(Property.DTSTART,
                getProperties());
        PropertyValidator.getInstance().assertOne(Property.TZOFFSETTO,
                getProperties());
        PropertyValidator.getInstance().assertOne(Property.TZOFFSETFROM,
                getProperties());

        /*

                ; the following are optional,
                ; and MAY occur more than once

                comment / rdate / rrule / tzname / x-prop
         */

        if (recurse) {
            validateProperties();
        }
    }
    
    /**
     * Returns the latest applicable onset of this observance for the specified date.
     * @param date the latest date that an observance onset may occur
     * @return the latest applicable observance date or null if there is no applicable
     * observance onset for the specified date
     */
    public final Date getLatestOnset(final Date date) {
        Date initialOnset = ((DtStart) getProperty(Property.DTSTART)).getDate();
        
        // observance not applicable if date is before the effective date of this observance..
        if (date.before(initialOnset)) {
            return null;
        }
        
        long start = System.currentTimeMillis();
        
        Date onset = getCachedOnset(date);

        if (log.isDebugEnabled()) {
            log.debug("Cache " + ((onset != null) ? "hit" : "miss") + " - retrieval time: " + (System.currentTimeMillis() - start) + "ms");
        }
        
        if (onset == null) {
            onset = initialOnset;
            // collect all onsets for the purposes of caching..
            DateList cacheableOnsets = new DateList();
//            Date nextOnset = null;
            
            if (!rdatesCached) {
                // check rdates for latest applicable onset..
                PropertyList rdates = getProperties(Property.RDATE);
                for (Iterator i = rdates.iterator(); i.hasNext();) {
                    RDate rdate = (RDate) i.next();
                    for (Iterator j = rdate.getDates().iterator(); j.hasNext();) {
                        Date rdateOnset = (Date) j.next();
                        if (!rdateOnset.after(date) && rdateOnset.after(onset)) {
                            onset = rdateOnset;
                        }
                        /*
                        else if (rdateOnset.after(date) && rdateOnset.after(onset)
                                && (nextOnset == null || rdateOnset.before(nextOnset))) {
                            nextOnset = rdateOnset;
                        }
                        */
                        cacheableOnsets.add(rdateOnset);
                    }
                }
                rdatesCached = true;
            }
            
            // check recurrence rules for latest applicable onset..
            PropertyList rrules = getProperties(Property.RRULE);
            Value dateType;
            if (date instanceof DateTime) {
                dateType = Value.DATE_TIME;
            }
            else {
                dateType = Value.DATE;
            }
            for (Iterator i = rrules.iterator(); i.hasNext();) {
                RRule rrule = (RRule) i.next();
                // include future onsets to determine onset period..
                Calendar cal = Dates.getCalendarInstance(date);
                cal.setTime(date);
                cal.add(Calendar.YEAR, 1);
                Date endRecur = Dates.getInstance(cal.getTime(), dateType);
                DateList recurrenceDates = rrule.getRecur().getDates(onset, endRecur, dateType);
                for (Iterator j = recurrenceDates.iterator(); j.hasNext();) {
                    Date rruleOnset = (Date) j.next();
                    if (!rruleOnset.after(date) && rruleOnset.after(onset)) {
                        onset = rruleOnset;
                    }
                    /*
                    else if (rruleOnset.after(date) && rruleOnset.after(onset)
                            && (nextOnset == null || rruleOnset.before(nextOnset))) {
                        nextOnset = rruleOnset;
                    }
                    */
                    cacheableOnsets.add(rruleOnset);
                }
            }
            
            // cache onsets..
            Collections.sort(cacheableOnsets);
            Date cacheableOnset = null;
            Date nextOnset = null;
            for (Iterator i = cacheableOnsets.iterator(); i.hasNext();) {
                cacheableOnset = nextOnset;
                nextOnset = (Date) i.next();
                if (cacheableOnset != null) {
                    onsets.put(new Period(new DateTime(cacheableOnset), new DateTime(nextOnset)), cacheableOnset);
                }
            }
            /*
            Period onsetPeriod = null;
            if (nextOnset != null) {
                onsetPeriod = new Period(new DateTime(onset), new DateTime(nextOnset));
            }
            else {
                onsetPeriod = new Period(new DateTime(onset), new DateTime(date));
            }
            onsets.put(onsetPeriod, onset);
            */
        }
        return onset;
    }
    
    /**
     * Returns a cached onset for the specified date.
     * @param date
     * @return a cached onset date or null if no cached onset is applicable
     * for the specified date
     */
    private Date getCachedOnset(final Date date) {
        for (Iterator i = onsets.keySet().iterator(); i.hasNext();) {
            Period onsetPeriod = (Period) i.next();
            if (onsetPeriod.includes(date)) {
                return (Date) onsets.get(onsetPeriod);
            }
        }
        return null;
    }
    
    /**
     * Returns the mandatory dtstart property.
     * @return
     */
    public final DtStart getStartDate() {
        return (DtStart) getProperty(Property.DTSTART);
    }
    
    /**
     * Returns the mandatory tzoffsetfrom property.
     * @return
     */
    public final TzOffsetFrom getOffsetFrom() {
        return (TzOffsetFrom) getProperty(Property.TZOFFSETFROM);
    }
    
    /**
     * Returns the mandatory tzoffsetto property.
     * @return
     */
    public final TzOffsetTo getOffsetTo() {
        return (TzOffsetTo) getProperty(Property.TZOFFSETTO);
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public final int compareTo(final Object arg0) {
        return compareTo((Observance) arg0);
    }
    
    /**
     * @param arg0
     * @return
     */
    public final int compareTo(final Observance arg0) {
        // TODO: sort by RDATE??
        DtStart dtStart = (DtStart) getProperty(Property.DTSTART);
        DtStart dtStart0 = (DtStart) arg0.getProperty(Property.DTSTART);
        return dtStart.getDate().compareTo(dtStart0.getDate());
    }
}
