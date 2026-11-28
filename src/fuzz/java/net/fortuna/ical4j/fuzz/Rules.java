/**
 * Copyright (c) 2026, Ben Fortuna
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

package net.fortuna.ical4j.fuzz;

import com.code_intelligence.jazzer.api.FuzzedDataProvider;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;

/** RRULE texts and temporal values drawn from a FuzzedDataProvider (scenarios 03, 04, 14). */
public final class Rules {

    public static final String[] FREQS = {"SECONDLY", "MINUTELY", "HOURLY", "DAILY", "WEEKLY", "MONTHLY", "YEARLY",
            "DAILY", "WEEKLY", "MONTHLY", "YEARLY", "daily", "X-FOO", ""};
    public static final String[] DAYS = {"MO", "TU", "WE", "TH", "FR", "SA", "SU", "mo", "XX", ""};
    public static final String[] ZONES = {"Europe/Paris", "America/Sao_Paulo", "Australia/Lord_Howe",
            "America/New_York", "Asia/Tokyo", "Pacific/Apia", "UTC"};
    static final int[] BIG = {1, 2, 3, 4, 7, 10, 52, 100, 1000, 5000, 65536, Integer.MAX_VALUE, Integer.MAX_VALUE - 1,
            0, -1};

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("uuuuMMdd");
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("uuuuMMdd'T'HHmmss");

    private Rules() {
    }

    public static int big(FuzzedDataProvider d) {
        return d.consumeBoolean() ? d.pickValue(BIG) : d.consumeInt(1, 400);
    }

    private static String numberList(FuzzedDataProvider d, int min, int max, int extraMin, int extraMax) {
        int n = d.consumeInt(0, 4);
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < n; i++) {
            if (i > 0) {
                b.append(',');
            }
            int v = d.consumeInt(0, 9) == 0 ? d.consumeInt(extraMin, extraMax) : d.consumeInt(min, max);
            if (v > 0 && d.consumeInt(0, 9) == 0) {
                b.append('+');
            }
            b.append(v);
        }
        if (n == 0 && d.consumeBoolean()) {
            b.append(d.consumeInt(min, max));
        }
        return b.toString();
    }

    private static String dayList(FuzzedDataProvider d) {
        int n = d.consumeInt(1, 4);
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < n; i++) {
            if (i > 0) {
                b.append(',');
            }
            switch (d.consumeInt(0, 5)) {
                case 0:
                    b.append(d.consumeInt(-53, 53));
                    break;
                case 1:
                    b.append(d.consumeBoolean() ? "+" : "-").append(d.consumeInt(0, 5));
                    break;
                default:
                    break;
            }
            b.append(d.pickValue(DAYS));
        }
        return b.toString();
    }

    public static String until(FuzzedDataProvider d) {
        LocalDateTime t = dateTime(d);
        switch (d.consumeInt(0, 3)) {
            case 0:
                return t.toLocalDate().format(DATE);
            case 1:
                return t.format(DATE_TIME);
            case 2:
                return t.format(DATE_TIME) + "Z";
            default:
                return d.consumeString(20);
        }
    }

    /** A rule assembled from parts (or, sometimes, raw text). */
    public static String rule(FuzzedDataProvider d) {
        if (d.consumeInt(0, 15) == 0) {
            return d.consumeString(120);
        }
        return rule(d, d.pickValue(FREQS));
    }

    /** A rule of that FREQ assembled from parts. */
    public static String rule(FuzzedDataProvider d, String freq) {
        StringBuilder b = new StringBuilder();
        b.append("FREQ=").append(freq);
        // Known DoS, found by scenario 04 (timeout-612e35a0…, timeout-0b198a42…): BYWEEKNO (YEARLY only, RFC 5545)
        // with FREQ=SECONDLY/MINUTELY maps every second back into the week, and RecurDateSpliterator walks second by
        // second to the next year. Not generated any more, so that fuzzing goes past it.
        boolean noWeekNo = "SECONDLY".equals(freq) || "MINUTELY".equals(freq);
        int parts = d.consumeInt(0, 6);
        for (int i = 0; i < parts; i++) {
            switch (d.consumeInt(0, 15)) {
                case 0:
                    b.append(";INTERVAL=").append(big(d));
                    break;
                case 1:
                    b.append(";COUNT=").append(big(d));
                    break;
                case 2:
                    b.append(";UNTIL=").append(until(d));
                    break;
                case 3:
                    b.append(";BYSECOND=").append(numberList(d, 0, 59, -1, 61));
                    break;
                case 4:
                    b.append(";BYMINUTE=").append(numberList(d, 0, 59, -1, 60));
                    break;
                case 5:
                    b.append(";BYHOUR=").append(numberList(d, 0, 23, -1, 25));
                    break;
                case 6:
                case 7:
                    b.append(";BYDAY=").append(dayList(d));
                    break;
                case 8:
                    b.append(";BYMONTHDAY=").append(numberList(d, -31, 31, -40, 40));
                    break;
                case 9:
                    b.append(";BYYEARDAY=").append(numberList(d, -366, 366, -400, 400));
                    break;
                case 10:
                    if (!noWeekNo) {
                        b.append(";BYWEEKNO=").append(numberList(d, -53, 53, -60, 60));
                    }
                    break;
                case 11:
                    b.append(";BYMONTH=").append(d.consumeInt(0, 7) == 0 ? d.consumeInt(1, 12) + "L"
                            : numberList(d, 1, 12, 0, 14));
                    break;
                case 12:
                    b.append(";BYSETPOS=").append(numberList(d, -10, 10, -400, 400));
                    break;
                case 13:
                    b.append(";WKST=").append(d.pickValue(DAYS));
                    break;
                case 14:
                    b.append(d.pickValue(new String[]{";RSCALE=GREGORIAN", ";RSCALE=HEBREW", ";RSCALE=CHINESE",
                            ";RSCALE=ISLAMIC-CIVIL", ";SKIP=OMIT", ";SKIP=BACKWARD", ";SKIP=FORWARD"}));
                    break;
                default:
                    b.append(";X-NAME=").append(d.consumeString(6));
                    break;
            }
        }
        return b.toString();
    }

    /** A local date-time over years 1–9999, mostly 1970–2040. */
    public static LocalDateTime dateTime(FuzzedDataProvider d) {
        int year;
        switch (d.consumeInt(0, 9)) {
            case 0:
                year = d.consumeInt(1, 9999);
                break;
            case 1:
                year = d.pickValue(new int[]{1, 1582, 1601, 1900, 2038, 9999});
                break;
            default:
                year = d.consumeInt(1970, 2040);
                break;
        }
        LocalDate date = LocalDate.of(year, 1, 1).plusDays(d.consumeInt(0, 365));
        if (date.getYear() != year) {
            date = date.minusDays(1);
        }
        LocalTime time = d.consumeBoolean() ? LocalTime.of(d.consumeInt(0, 23), d.pickValue(new int[]{0, 30, 59}))
                : LocalTime.ofSecondOfDay(d.consumeInt(0, 86399));
        return date.atTime(time);
    }

    /** type 0: LocalDate, 1: LocalDateTime (floating), 2: ZonedDateTime, 3: OffsetDateTime (UTC). */
    public static Temporal of(LocalDateTime t, int type, ZoneId zone) {
        switch (type) {
            case 0:
                return t.toLocalDate();
            case 1:
                return t;
            case 2:
                return t.atZone(zone);
            default:
                return OffsetDateTime.of(t, ZoneOffset.UTC);
        }
    }
}
