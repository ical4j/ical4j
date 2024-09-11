/*
 *  Copyright (c) 2022, Ben Fortuna
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

package net.fortuna.ical4j.validate;

import net.fortuna.ical4j.model.Recur;

/**
 * This validator provides semantic recurrence rule validation according to the RFC5545
 * specification excerpt listed below.
 *
 * <pre>
 *
 *       The table below summarizes the dependency of BYxxx rule part
 *       expand or limit behavior on the FREQ rule part value.
 *
 *       The term "N/A" means that the corresponding BYxxx rule part MUST
 *       NOT be used with the corresponding FREQ value.
 *
 *       BYDAY has some special behavior depending on the FREQ value and
 *       this is described in separate notes below the table.
 *
 *    +----------+--------+--------+-------+-------+------+-------+------+
 *    |          |SECONDLY|MINUTELY|HOURLY |DAILY  |WEEKLY|MONTHLY|YEARLY|
 *    +----------+--------+--------+-------+-------+------+-------+------+
 *    |BYMONTH   |Limit   |Limit   |Limit  |Limit  |Limit |Limit  |Expand|
 *    +----------+--------+--------+-------+-------+------+-------+------+
 *    |BYWEEKNO  |N/A     |N/A     |N/A    |N/A    |N/A   |N/A    |Expand|
 *    +----------+--------+--------+-------+-------+------+-------+------+
 *    |BYYEARDAY |Limit   |Limit   |Limit  |N/A    |N/A   |N/A    |Expand|
 *    +----------+--------+--------+-------+-------+------+-------+------+
 *    |BYMONTHDAY|Limit   |Limit   |Limit  |Limit  |N/A   |Expand |Expand|
 *    +----------+--------+--------+-------+-------+------+-------+------+
 *    |BYDAY     |Limit   |Limit   |Limit  |Limit  |Expand|Note 1 |Note 2|
 *    +----------+--------+--------+-------+-------+------+-------+------+
 *    |BYHOUR    |Limit   |Limit   |Limit  |Expand |Expand|Expand |Expand|
 *    +----------+--------+--------+-------+-------+------+-------+------+
 *    |BYMINUTE  |Limit   |Limit   |Expand |Expand |Expand|Expand |Expand|
 *    +----------+--------+--------+-------+-------+------+-------+------+
 *    |BYSECOND  |Limit   |Expand  |Expand |Expand |Expand|Expand |Expand|
 *    +----------+--------+--------+-------+-------+------+-------+------+
 *    |BYSETPOS  |Limit   |Limit   |Limit  |Limit  |Limit |Limit  |Limit |
 *    +----------+--------+--------+-------+-------+------+-------+------+
 *
 *       Note 1:  Limit if BYMONTHDAY is present; otherwise, special expand
 *                for MONTHLY.
 *
 *       Note 2:  Limit if BYYEARDAY or BYMONTHDAY is present; otherwise,
 *                special expand for WEEKLY if BYWEEKNO present; otherwise,
 *                special expand for MONTHLY if BYMONTH present; otherwise,
 *                special expand for YEARLY.
 * </pre>
 */
public class RecurValidator implements Validator<Recur> {

    @Override
    public ValidationResult validate(Recur target) throws ValidationException {
        var result = new ValidationResult();
        switch (target.getFrequency()) {
            case SECONDLY:
                if (!target.getWeekNoList().isEmpty()) {
                    result.getEntries().add(new ValidationEntry("BYWEEKNO not applicable for FREQ=SECONDLY",
                            ValidationEntry.Severity.ERROR, "RECUR"));
                }
                break;
            case MINUTELY:
                if (!target.getWeekNoList().isEmpty()) {
                    result.getEntries().add(new ValidationEntry("BYWEEKNO not applicable for FREQ=MINUTELY",
                            ValidationEntry.Severity.ERROR, "RECUR"));
                }
                break;
            case HOURLY:
                if (!target.getWeekNoList().isEmpty()) {
                    result.getEntries().add(new ValidationEntry("BYWEEKNO not applicable for FREQ=HOURLY",
                            ValidationEntry.Severity.ERROR, "RECUR"));
                }
                break;
            case DAILY:
                if (!target.getWeekNoList().isEmpty()) {
                    result.getEntries().add(new ValidationEntry("BYWEEKNO not applicable for FREQ=DAILY",
                            ValidationEntry.Severity.ERROR, "RECUR"));
                }
                if (!target.getYearDayList().isEmpty()) {
                    result.getEntries().add(new ValidationEntry("BYYEARDAY not applicable for FREQ=DAILY",
                            ValidationEntry.Severity.ERROR, "RECUR"));
                }
                break;
            case WEEKLY:
                if (!target.getWeekNoList().isEmpty()) {
                    result.getEntries().add(new ValidationEntry("BYWEEKNO not applicable for FREQ=WEEKLY",
                            ValidationEntry.Severity.ERROR, "RECUR"));
                }
                if (!target.getYearDayList().isEmpty()) {
                    result.getEntries().add(new ValidationEntry("BYYEARDAY not applicable for FREQ=WEEKLY",
                            ValidationEntry.Severity.ERROR, "RECUR"));
                }
                if (!target.getMonthDayList().isEmpty()) {
                    result.getEntries().add(new ValidationEntry("BYMONTHDAY not applicable for FREQ=WEEKLY",
                            ValidationEntry.Severity.ERROR, "RECUR"));
                }
                break;
            case MONTHLY:
                if (!target.getWeekNoList().isEmpty()) {
                    result.getEntries().add(new ValidationEntry("BYWEEKNO not applicable for FREQ=MONTHLY",
                            ValidationEntry.Severity.ERROR, "RECUR"));
                }
                if (!target.getYearDayList().isEmpty()) {
                    result.getEntries().add(new ValidationEntry("BYYEARDAY not applicable for FREQ=MONTHLY",
                            ValidationEntry.Severity.ERROR, "RECUR"));
                }
                break;
        }
        return result;
    }
}
