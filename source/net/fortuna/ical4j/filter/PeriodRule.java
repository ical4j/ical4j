/*
 * $Id$
 *
 * Created on 2/02/2006
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
package net.fortuna.ical4j.filter;

import java.util.Date;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.DateList;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Duration;
import net.fortuna.ical4j.model.property.RDate;
import net.fortuna.ical4j.model.property.RRule;

/**
 * A rule that matches any component that occurs in the specified time period.
 * @author Ben Fortuna
 */
public class PeriodRule extends ComponentRule {

    private Log log = LogFactory.getLog(PeriodRule.class);

    private Period period;

    /**
     * Constructs a new instance using the specified period.
     * @param period
     */
    public PeriodRule(final Period period) {
        this.period = period;
    }

    /*
     * (non-Javadoc)
     * @see net.fortuna.ical4j.filter.ComponentRule#match(net.fortuna.ical4j.model.Component)
     */
    public final boolean match(final Component component) {
        DtStart start = (DtStart) component.getProperty(Property.DTSTART);
        if (start != null && period.includes(start.getDate())) {
            debug(start.getDate(), "start date");
            return true;
        }
        DtEnd end = (DtEnd) component.getProperty(Property.DTEND);
        if (end != null && period.includes(end.getDate(), false)) {
            debug(end.getDate(), "end date");
            return true;
        }
        Duration duration = (Duration) component.getProperty(Property.DURATION);
        if (start != null && duration != null) {
            Date startPlusDuration = duration.getDuration().getTime(
                    start.getDate());
            if (period.includes(startPlusDuration)) {
                debug(startPlusDuration, "duration");
                return true;
            }
        }
        // recurrence dates..
        for (Iterator i = component.getProperties(Property.RDATE).iterator(); i
                .hasNext();) {
            RDate rdate = (RDate) i.next();
            if (Value.PERIOD.equals(rdate.getParameter(Parameter.VALUE))) {
                for (Iterator j = rdate.getPeriods().iterator(); j.hasNext();) {
                    Period rdatePeriod = (Period) j.next();
                    if (period.intersects(rdatePeriod)) {
                        debug(rdatePeriod.getStart(), "recurrence date");
                        debug(rdatePeriod.getEnd(), "recurrence date");
                        return true;
                    }
                }
            }
        }
        // recurrence rules..
        for (Iterator i = component.getProperties(Property.RRULE).iterator(); i
                .hasNext();) {
            RRule rrule = (RRule) i.next();
            DateList startDates = rrule.getRecur().getDates(start.getDate(),
                    period, (Value) start.getParameter(Parameter.VALUE));
            for (Iterator j = startDates.iterator(); j.hasNext();) {
                Date recurDate = (Date) j.next();
                if (period.includes(recurDate)) {
                    debug(recurDate, "recurrence rule");
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Log the matching date instance.
     * @param date
     */
    private void debug(final Date date, final String type) {
        if (log.isDebugEnabled()) {
            log.debug("Matching date: " + date + " (" + type + ")");
        }
    }
}
