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

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.time.temporal.WeekFields;

final class WeekBasedYears {

    private WeekBasedYears() {
    }

    static TemporalUnit of(DayOfWeek weekStartDay) {
        final WeekFields weekFields = weekStartDay == null
                ? WeekFields.of(DayOfWeek.MONDAY, 4)
                : WeekFields.of(weekStartDay, 4);
        return new TemporalUnit() {
            @Override
            public long between(Temporal one, Temporal other) {
                throw new UnsupportedOperationException();
            }
            @Override
            public <R extends Temporal> R addTo(R one, long other) {
                TemporalField field = weekFields.weekBasedYear();
                long newValue = one.get(field) + other;
                // 'one.with(field, newValue)' would be neater here, but 'with' does not work for
                // ZonedDateTime as WeekFields' field.adjustInto returns a LocalDate,
                // so we need to manually 'adjustInto' and use the result as TemporalAdjuster:
                Temporal result = field.adjustInto(one, newValue);
                if (TemporalAdjuster.class.isAssignableFrom(result.getClass())) {
                    return (R) one.with((TemporalAdjuster) result);
                } else {
                    return one;
                }
            }
            @Override
            public boolean isTimeBased() {
                return false;
            }
            @Override
            public boolean isDateBased() {
                return true;
            }
            @Override
            public boolean isDurationEstimated() {
                return true;
            }
            @Override
            public Duration getDuration() {
                return WeekFields.WEEK_BASED_YEARS.getDuration();
            }
        };
    }
}