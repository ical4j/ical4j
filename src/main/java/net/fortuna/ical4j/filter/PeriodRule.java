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
package net.fortuna.ical4j.filter;

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.PeriodList;

/**
 * $Id$
 *
 * Created on 2/02/2006
 *
 * A rule that matches any component that occurs in the specified time period.
 * @author Ben Fortuna
 */
public class PeriodRule extends ComponentRule {

    private Period period;

    /**
     * Constructs a new instance using the specified period.
     * @param period a period instance to match on
     */
    public PeriodRule(final Period period) {
        this.period = period;
    }

    /**
     * {@inheritDoc}
     */
    public final boolean match(final Component component) {

        /*
        DtStart start = (DtStart) component.getProperty(Property.DTSTART);
        DtEnd end = (DtEnd) component.getProperty(Property.DTEND);
        Duration duration = (Duration) component.getProperty(Property.DURATION);
        
        if (start == null) {
            return false;
        }
        
        // detect events that consume no time..
        if (end == null && duration == null) {
            if (period.includes(start.getDate(), Period.INCLUSIVE_START)) {
                return true;
            }
        }
        */
        
//        try {
        final PeriodList recurrenceSet = component.calculateRecurrenceSet(period);
        return (!recurrenceSet.isEmpty());
//        }
//        catch (ValidationException ve) {
//            log.error("Invalid component data", ve);
//            return false;
//        }
    }
}
