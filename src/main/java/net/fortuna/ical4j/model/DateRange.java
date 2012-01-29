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

import java.io.Serializable;
import java.util.Date;

/**
 * @author fortuna
 *
 */
public class DateRange implements Serializable {

    private static final long serialVersionUID = -7303846680559287286L;

    /**
     * A flag indicating whether to include the start of the period in test functions.
     */
    public static final int INCLUSIVE_START = 1;

    /**
     * A flag indicating whether to include the end of the period in test functions.
     */
    public static final int INCLUSIVE_END = 2;

    private final Date rangeStart;
    
    private final Date rangeEnd;
    
    /**
     * @param start the start of the range
     * @param end the end of the range
     */
    public DateRange(Date start, Date end) {
        if (start == null) {
            throw new IllegalArgumentException("Range start is null");
        }
        if (end == null) {
            throw new IllegalArgumentException("Range end is null");
        }
        if (end.before(start)) {
            throw new IllegalArgumentException("Range start must be before range end");
        }
        this.rangeStart = start;
        this.rangeEnd = end;
    }

    /**
     * @return the rangeStart
     */
    public Date getRangeStart() {
        return rangeStart;
    }

    /**
     * @return the rangeEnd
     */
    public Date getRangeEnd() {
        return rangeEnd;
    }

    /**
     * Determines if the specified date occurs within this period (inclusive of
     * period start and end).
     * @param date a date to test for inclusion
     * @return true if the specified date occurs within the current period
     * 
     */
    public final boolean includes(final Date date) {
        return includes(date, INCLUSIVE_START | INCLUSIVE_END);
    }

    /**
     * Decides whether a date falls within this period.
     * @param date the date to be tested
     * @param inclusiveMask specifies whether period start and end are included
     * in the calculation
     * @return true if the date is in the period, false otherwise
     * @see Period#INCLUSIVE_START
     * @see Period#INCLUSIVE_END
     */
    public final boolean includes(final Date date, final int inclusiveMask) {
        boolean includes = true;
        if ((inclusiveMask & INCLUSIVE_START) > 0) {
            includes = includes && !rangeStart.after(date);
        }
        else {
            includes = includes && rangeStart.before(date);
        }
        if ((inclusiveMask & INCLUSIVE_END) > 0) {
            includes = includes && !rangeEnd.before(date);
        }
        else {
            includes = includes && rangeEnd.after(date);
        }
        return includes;
    }

    /**
     * Decides whether this period is completed before the given period starts.
     * 
     * @param range
     *            a period that may or may not start after this period ends
     * @return true if the specified period starts after this periods ends,
     *         otherwise false
     */
    public final boolean before(final DateRange range) {
        return (rangeEnd.before(range.getRangeStart()));
    }

    /**
     * Decides whether this period starts after the given period ends.
     * 
     * @param range
     *            a period that may or may not end before this period starts
     * @return true if the specified period end before this periods starts,
     *         otherwise false
     */
    public final boolean after(final DateRange range) {
        return (rangeStart.after(range.getRangeEnd()));
    }

    /**
     * Decides whether this period intersects with another one.
     * 
     * @param range
     *            a possible intersecting period
     * @return true if the specified period intersects this one, false
     *         otherwise.
     */
    public final boolean intersects(final DateRange range) {
        boolean intersects = false;
        // Test for our start date in period
        // (Exclude if it is the end date of test range)
        if (range.includes(rangeStart) && !range.getRangeEnd().equals(rangeStart)) {
            intersects = true;
        }
        // Test for test range's start date in our range
        // (Exclude if it is the end date of our range)
        else if (includes(range.getRangeStart())
                && !rangeEnd.equals(range.getRangeStart())) {
            intersects = true;
        }
        return intersects;
    }

    /**
     * Decides whether these periods are serial without a gap.
     * @param range a period to test for adjacency
     * @return true if one period immediately follows the other, false otherwise
     */
    public final boolean adjacent(final DateRange range) {
        boolean adjacent = false;
        if (rangeStart.equals(range.getRangeEnd())) {
            adjacent = true;
        } else if (rangeEnd.equals(range.getRangeStart())) {
            adjacent = true;
        }
        return adjacent;
    }

    /**
     * Decides whether the given period is completely contained within this one.
     * 
     * @param range
     *            the period that may be contained by this one
     * @return true if this period covers all the dates of the specified period,
     *         otherwise false
     */
    public final boolean contains(final DateRange range) {
        // Test for period's start and end dates in our range
        return (includes(range.getRangeStart()) && includes(range.getRangeEnd()));
    }
}
