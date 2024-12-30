/*
 *  Copyright (c) 2023, Ben Fortuna
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *   o Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 *
 *   o Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *
 *   o Neither the name of Ben Fortuna nor the names of any other contributors
 *  may be used to endorse or promote products derived from this software
 *  without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 *  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 *  A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *  EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package net.fortuna.ical4j.model;

import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.property.RecurrenceId;

import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public interface RecurrenceSupport<T extends CalendarComponent> extends PropertyContainer, Prototype<T> {

    /**
     * Calculates the recurrence set for this component using the specified period.
     * The recurrence set is derived from a combination of the component start date,
     * recurrence rules and dates, and exception rules and dates. Note that component
     * transparency and anniversary-style dates do not affect the resulting
     * intersection.
     * <p>If an explicit DURATION is not specified, the effective duration of each
     * returned period is derived from the DTSTART and DTEND or DUE properties.
     * If the component has no DURATION, DTEND or DUE, the effective duration is set
     * to PT0S</p>
     *
     * @param period a range to calculate recurrences for
     * @return a set of periods
     */
    <R extends Temporal> Set<Period<R>> calculateRecurrenceSet(final Period<? extends Temporal> period);

    default List<T> getOccurrences(Period<Temporal> period) {
        List<T> occurrences = new ArrayList<>();

        Set<Period<Temporal>> periods = RecurrenceSupport.this.calculateRecurrenceSet(period);
        for (Period<Temporal> p : periods) {
            final T occurrence = this.copy();
            occurrence.add(new RecurrenceId<>(p.getStart()));
            occurrences.add(occurrence);
        }

        return occurrences;
    }
}
