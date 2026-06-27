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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Spliterators;
import java.util.function.Consumer;

final class RecurDateSpliterator<T extends Temporal> extends Spliterators.AbstractSpliterator<T> {

    private final Recur<T> recur;
    private final T seed;
    private final Temporal periodStart;
    private final Temporal periodEnd;
    private final int maxCount;

    private int generatedCount;

    private T candidateSeed;
    private int incrementMultiplier = 1;

    private T lastCandidate = null;

    private Iterator<T> candidates = null;

    private final HashSet<T> invalidCandidates = new HashSet<>();

    private int noCandidateIncrementCount = 0;

    RecurDateSpliterator(Recur<T> recur, T seed, Temporal periodStart, Temporal periodEnd, int maxCount) {
        super(maxCount > 0 ? maxCount : Long.MAX_VALUE, ORDERED | DISTINCT | NONNULL);
        this.recur = recur;
        this.seed = seed;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.maxCount = maxCount;

        generatedCount = 0;

        candidateSeed = seed;

        // optimize the start time for selecting candidates
        // (only applicable where a COUNT is not specified)
        if (recur.getCountValue() == null) {
            // Use and exponential approach to increment the candidate seed until it's after the period start.
            T incremented = recur.increment(seed, incrementMultiplier);
            while (TemporalAdapter.isBefore(incremented, periodStart.minus(Math.max(recur.getInterval(), 1), recur.getCalIncField()))) {
                candidateSeed = incremented;
                incrementMultiplier *= 2;
                if (candidateSeed == null) {
                    break;
                }
                incremented = recur.increment(seed, incrementMultiplier);
            }
            // Now let's make a binary search between the last candidate seed and the current one to find the optimal candidate seed to start with.
            int low = Math.max(1, incrementMultiplier / 2); // last before
            int high = incrementMultiplier; // first after
            while (low < high) {
                int mid = (low + high) / 2;
                incremented = recur.increment(seed, mid);
                if (TemporalAdapter.isBefore(incremented, periodStart.minus(Math.max(recur.getInterval(), 1), recur.getCalIncField()))) {
                    candidateSeed = incremented;
                    low = mid + 1;
                } else {
                    high = mid;
                }
            }
            incrementMultiplier = low;
        }
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
        boolean advance = maxCount < 0 || generatedCount < maxCount;
        if (advance) {
            advance = isWithinEndBoundaries(lastCandidate);
        }

        if (advance) {
            // generate new candidate list..
            while (candidates == null || !candidates.hasNext()) {
                if (Thread.currentThread().isInterrupted()) {
                    throw new RuntimeException("Thread was interrupted during recurrence generation");
                }

                // rootSeed = date used for the seed for the RRule at the
                //            start of the first period.
                // candidateSeed = date used for the start of
                //                 the current period.
                candidates = recur.getCandidates(seed, candidateSeed).iterator();

                if (!candidates.hasNext()) {
                    noCandidateIncrementCount++;
                    if (((recur.getMaxIncrementCount() > 0) && (noCandidateIncrementCount > recur.getMaxIncrementCount())) || !isWithinEndBoundaries(candidateSeed)) {
                        advance = false;
                        break;
                    }
                } else {
                    noCandidateIncrementCount = 0;
                }
                candidateSeed = recur.increment(seed, incrementMultiplier++);
            }
        }

        if (advance) {
            // iterate current candidate list..
            lastCandidate = candidates.next();
            // don't count candidates that occur before the seed date..
            if (!TemporalAdapter.isBefore(lastCandidate, seed)) {
                // candidates exclusive of periodEnd..
                if (TemporalAdapter.isBefore(lastCandidate, periodStart) || TemporalAdapter.isAfter(lastCandidate, periodEnd)) {
                    invalidCandidates.add(lastCandidate);
                } else if (!TemporalAdapter.isBefore(lastCandidate, periodStart) && !TemporalAdapter.isAfter(lastCandidate, periodEnd)
                        && (recur.getUntil() == null || !TemporalAdapter.isAfter(lastCandidate, recur.getUntil()))) {

                    generatedCount++;
                    action.accept(lastCandidate);
                }
            }
        }
        return advance;
    }

    private boolean isWithinEndBoundaries(T candidate) {
        boolean advance = true;
        if (recur.getUntil() != null && candidate != null && TemporalAdapter.isAfter(candidate, recur.getUntil())) {
            advance = false;
        } else if (periodEnd != null && candidate != null && TemporalAdapter.isAfter(candidate, periodEnd)) {
            advance = false;
        } else if (recur.getCount() >= 1 && (generatedCount + invalidCandidates.size()) >= recur.getCount()) {
            advance = false;
        }
        return advance;
    }
}