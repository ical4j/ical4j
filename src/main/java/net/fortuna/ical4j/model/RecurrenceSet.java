package net.fortuna.ical4j.model;

import org.jspecify.annotations.Nullable;

import java.time.Duration;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.util.*;
import java.util.stream.Collectors;

/*
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
public class RecurrenceSet<T extends Temporal> extends TreeSet<Period<T>> {

    public static class Builder<T extends Temporal> {

        private @Nullable T start;

        private @Nullable T end;

        private @Nullable TemporalAmount duration;

        private List<? extends T> recurrenceDates = new ArrayList<>();

        private Set<Period<T>> recurrencePeriods = new HashSet<>();

        private List<Recur<T>> recurrenceRules = new ArrayList<>();

        private List<? extends T> exceptionDates = new ArrayList<>();

        private List<Recur<T>> exceptionRules = new ArrayList<>();

        private @Nullable Period<? extends Temporal> period;

        public Builder<T> start(@Nullable T start) {
            this.start = start;
            return this;
        }

        public  Builder<T> end(@Nullable T end) {
            this.end = end;
            return this;
        }

        public Builder<T> duration(@Nullable TemporalAmount duration) {
            this.duration = duration;
            return this;
        }

        public Builder<T> recurrenceDates(List<? extends T> recurrenceDates) {
            this.recurrenceDates = recurrenceDates;
            return this;
        }

        public Builder<T> recurrencePeriods(Set<Period<T>> recurrencePeriods) {
            this.recurrencePeriods = recurrencePeriods;
            return this;
        }

        public Builder<T> recurrenceRules(List<Recur<T>> recurrenceRules) {
            this.recurrenceRules = recurrenceRules;
            return this;
        }

        public Builder<T> exceptionDates(List<? extends T> exceptionDates) {
            this.exceptionDates = exceptionDates;
            return this;
        }

        public Builder<T> exceptionRules(List<Recur<T>> exceptionRules) {
            this.exceptionRules = exceptionRules;
            return this;
        }

        /**
         * Bounds the recurrence set. The period start is inclusive and the period end is exclusive: occurrences
         * that start before the period but overlap it are included, occurrences starting exactly at the period
         * end are not. A zero-length period is a point query (see {@link #build()}).
         * @param period the query period
         * @return this builder
         */
        public Builder<T> period(Period<? extends Temporal> period) {
            this.period = period;
            return this;
        }

        public RecurrenceSet<T> build() {
            final T startDate = Objects.requireNonNull(start, "start");
            final Period<? extends Temporal> bounds = Objects.requireNonNull(period, "period");
            RecurrenceSet<T> recurrenceSet = new RecurrenceSet<>();

            // if no end or duration specified, end date equals start date..
            // unless the start date represents a date value, in which case
            // the effective duration is 1 day..
            final TemporalAmount effectiveDuration;
            if (duration != null) {
                effectiveDuration = duration;
            } else if (end == null) {
                if (startDate.isSupported(ChronoField.SECOND_OF_DAY)) {
                    effectiveDuration = Duration.ZERO;
                } else {
                    effectiveDuration = java.time.Period.ofDays(1);
                }
            }
            // if an explicit event duration is not specified, derive a value for recurring
            // periods from the end date..
            else {
                effectiveDuration = TemporalAmountAdapter.between(startDate, end).getDuration();
            }

            // add recurrence dates..
            recurrenceSet.addAll(recurrenceDates.stream()
                    .map(date -> new Period<T>(date, effectiveDuration))
                    .filter(p -> withinBounds(bounds, p))
                    .collect(Collectors.toList()));

            // add recurrence periods..
            recurrenceSet.addAll(recurrencePeriods.stream().filter(p -> withinBounds(bounds, p))
                    .collect(Collectors.toList()));

            // allow for recurrence rules that start prior to the specified period
            // but still intersect with it..
            final Temporal startMinusDuration;
            if (effectiveDuration instanceof Duration && !bounds.getStart().isSupported(ChronoUnit.SECONDS)) {
                // a date-only period cannot subtract a time-based amount; widen by whole days instead (#736)..
                startMinusDuration = bounds.getStart().minus(((Duration) effectiveDuration).toDays() + 1, ChronoUnit.DAYS);
            } else {
                startMinusDuration = bounds.getStart().minus(effectiveDuration);
            }

            // add recurrence rules..
            if (!recurrenceRules.isEmpty()) {
                recurrenceSet.addAll(recurrenceRules.stream().map(rrule ->
                                rrule.getDates(startDate, startMinusDuration, bounds.getEnd()))
                        .flatMap(List<T>::stream).map(date -> new Period<>(date, effectiveDuration))
                        .filter(p -> withinBounds(bounds, p))
                        .collect(Collectors.toList()));
            } else {
                // add initial instance if intersection with the specified period..
                Period<T> initialPeriod = new Period<>(startDate, effectiveDuration);
                if (withinBounds(bounds, initialPeriod)) {
                    recurrenceSet.add(initialPeriod);
                }
            }

            // subtract exception dates..
            recurrenceSet.removeIf(r -> exceptionDates.contains(r.getStart()));

            // subtract exception rules..
            List<Period<T>> exceptionRuleDates = exceptionRules.stream().map(rrule ->
                            rrule.getDates(startDate, startMinusDuration, bounds.getEnd()))
                    .flatMap(List<T>::stream).map(date -> new Period<>(date, effectiveDuration))
                    .collect(Collectors.toList());
            recurrenceSet.removeIf(exceptionRuleDates::contains);

            return recurrenceSet;
        }

        /**
         * Decides whether an occurrence belongs to the recurrence set bounded by the query period.
         *
         * <p>The query period is half-open: an occurrence is included when it overlaps
         * {@code [bounds.getStart(), bounds.getEnd())}. So an occurrence starting exactly at the period end,
         * or ending exactly at the period start, is not included. The one exception is a zero-length
         * query period, which is a point-in-time query and matches an occurrence that starts at, or is in
         * progress at, that instant (relied on by {@code VEvent.getOccurrence}).</p>
         *
         * <p>A zero-length occurrence (no {@code DTEND}/{@code DURATION} on a date-time value, or
         * {@code DTEND} equal to {@code DTSTART}) has no extent to overlap with, so it is included when its
         * instant lies in {@code [bounds.getStart(), bounds.getEnd())}, consistent with
         * {@link Period#intersects(Period)} (issue #82).</p>
         *
         * <p>Comparisons use {@link TemporalComparator} so the period and the occurrence may be of different
         * temporal types.</p>
         */
        private static boolean withinBounds(Period<? extends Temporal> bounds, Period<?> occurrence) {
            final TemporalComparator cmp = TemporalComparator.INSTANCE;
            if (cmp.compare(bounds.getStart(), bounds.getEnd()) == 0) {
                // point query..
                int startCmp = cmp.compare(occurrence.getStart(), bounds.getStart());
                return startCmp == 0 || (startCmp < 0 && cmp.compare(bounds.getStart(), occurrence.getEnd()) < 0);
            }
            if (cmp.compare(occurrence.getStart(), occurrence.getEnd()) == 0) {
                // zero-length occurrence: included when its instant is within the half-open window..
                return cmp.compare(bounds.getStart(), occurrence.getStart()) <= 0
                        && cmp.compare(occurrence.getStart(), bounds.getEnd()) < 0;
            }
            return cmp.compare(bounds.getStart(), occurrence.getEnd()) < 0
                    && cmp.compare(occurrence.getStart(), bounds.getEnd()) < 0;
        }
    }
}
