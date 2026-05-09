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

import java.time.temporal.Temporal;

final class RecurFormatter {

    private RecurFormatter() {
    }

    static <T extends Temporal> String toString(Recur<T> recur) {
        final StringBuilder b = new StringBuilder();
        if (recur.getRScale() != null) {
            b.append("RSCALE").append('=').append(recur.getRScale()).append(';');
        }
        b.append("FREQ").append('=').append(recur.getFrequency());
        if (recur.getWeekStartDay() != null) {
            b.append(';').append("WKST").append('=').append(recur.getWeekStartDay());
        }
        if (recur.getUntil() != null) {
            // Note: UNTIL should always be in UTC time.
            b.append(';').append("UNTIL").append('=').append(recur.getUntilAdapter());
        }
        if (recur.getCountValue() != null) {
            b.append(';').append("COUNT").append('=').append(recur.getCountValue());
        }
        if (recur.getIntervalValue() != null) {
            b.append(';').append("INTERVAL").append('=').append(recur.getIntervalValue());
        }
        if (!recur.getMonthList().isEmpty()) {
            b.append(';').append("BYMONTH").append('=').append(recur.getMonthList());
        }
        if (!recur.getWeekNoList().isEmpty()) {
            b.append(';').append("BYWEEKNO").append('=').append(NumberList.toString(recur.getWeekNoList()));
        }
        if (!recur.getYearDayList().isEmpty()) {
            b.append(';').append("BYYEARDAY").append('=').append(NumberList.toString(recur.getYearDayList()));
        }
        if (!recur.getMonthDayList().isEmpty()) {
            b.append(';').append("BYMONTHDAY").append('=').append(NumberList.toString(recur.getMonthDayList()));
        }
        if (!recur.getDayList().isEmpty()) {
            b.append(';').append("BYDAY").append('=').append(WeekDayList.toString(recur.getDayList()));
        }
        if (!recur.getHourList().isEmpty()) {
            b.append(';').append("BYHOUR").append('=').append(NumberList.toString(recur.getHourList()));
        }
        if (!recur.getMinuteList().isEmpty()) {
            b.append(';').append("BYMINUTE").append('=').append(NumberList.toString(recur.getMinuteList()));
        }
        if (!recur.getSecondList().isEmpty()) {
            b.append(';').append("BYSECOND").append('=').append(NumberList.toString(recur.getSecondList()));
        }
        if (!recur.getSetPosList().isEmpty()) {
            b.append(';').append("BYSETPOS").append('=').append(NumberList.toString(recur.getSetPosList()));
        }
        if (recur.getSkip() != null) {
            b.append(';').append("SKIP").append('=').append(recur.getSkip());
        }
        return b.toString();
    }
}