/*
 * $Id$
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
package net.fortuna.ical4j.util;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Iterator;
import java.util.Date;

import net.fortuna.ical4j.model.DateRange;
import net.fortuna.ical4j.model.DateList;

/**
 * This is a utility class that takes a sorted set of date range objects
 * and puts them together. This could mean adding them together, finding
 * overlaps, and creating one long date range out of two shorter ones.
 * Or it could mean subtracting one from the other, shortening one of the
 * ranges or even creating two ranges from one.
 */
public class DateRangeNormalizer {

    private static DateRangeNormalizer instance = new DateRangeNormalizer();

    /**
     * Constructor made private to enforce singleton.
     */
    private DateRangeNormalizer() {
    }

    /**
     * @return Returns the instance.
     */
    public static DateRangeNormalizer getInstance() {
        return instance;
    }

    /**
     * Takes two DateRanges and coalesces them into one if the ranges overlap.
     * @param initRange the inital range
     * @param addRange the range to be added
     * @return a date range that encompasses both, or null if no coalescing can happen
     */
    private DateRange coalesceDateRanges(DateRange initRange, DateRange addRange) {
        if (initRange != null && addRange != null) {
            if (initRange.overlaps(addRange) || initRange.adjacent(addRange)) {
                DateRange lastRange = initRange.add(addRange);
                return lastRange;
            }
        }
        return null;
    }

    /**
     * Add two date ranges together, coalescing multiple ranges that overlap
     * into a single range.
     * @param currSet the set of date ranges you are starting with
     * @param addSet the set you want to add to the currSet
     * @return a new Set that holds the coalesced date ranges, or null if both
     *       params are null
     */
    public SortedSet addDateRanges(SortedSet currSet, SortedSet addSet) {
        if (currSet == null && addSet == null) {
            return null;
        }
        SortedSet resultSet = new TreeSet();
        SortedSet denormedSet = new TreeSet();
        if (currSet != null) {
            denormedSet.addAll(currSet);
        }
        if (addSet != null) {
            denormedSet.addAll(addSet);
        }
        DateRange lastRange = null;
        for (Iterator rangeIter = denormedSet.iterator();
                                                    rangeIter.hasNext();) {

            DateRange range = (DateRange) rangeIter.next();
            if (lastRange == null) {
                lastRange = range;
            } else {
                DateRange combinedRange = coalesceDateRanges(lastRange, range);
                if (combinedRange == null) {
                    resultSet.add(lastRange);
                    lastRange = range;
                } else {
                    lastRange = combinedRange;
                }
            }
        }
        // Don't miss the last one
        if (lastRange != null) {
            resultSet.add(lastRange);
        }

        return resultSet;
    }

    /**
    * Create a Sorted Set of DateRange objects.
    *
    * @param dateList
    *           An unordered list of Start Dates.
    * @param duration
    *           The duration of the range, used to determine the End Date.
    * @return
    *           Sorted Set of DateRange objects.
    */
    public SortedSet createDateRangeSet(DateList dateList, long duration) {

        if (dateList == null) {
            return null;
        }

        SortedSet dateRangeSet = new TreeSet();

        for (Iterator dateIter = dateList.iterator(); dateIter.hasNext();) {

            Date nextStartDate = (Date) dateIter.next();
            Date nextEndDate = new Date(nextStartDate.getTime() + duration);
            DateRange nextDateRange = new DateRange();

            nextDateRange.setStartDate(nextStartDate);
            nextDateRange.setEndDate(nextEndDate);
            dateRangeSet.add(nextDateRange);
        }

        return dateRangeSet;
    }

    /**
     * Takes the date ranges in one set away from the ranges in another set.
     * @param currSet the original set we are working with
     * @param removeSet the set of date ranges to remove
     * @return a new set with the adjustedranges, or null if currSet is null
     */
    public SortedSet subtractDateRanges(SortedSet currSet, SortedSet removeSet) {
        if (currSet == null) {
            return null;
        }
        if (removeSet == null || removeSet.size() < 1) {
            return currSet;
        }
        SortedSet resultSet = new TreeSet(currSet);
        SortedSet toBeAddedSet = new TreeSet();
        for (Iterator removeIter = removeSet.iterator(); removeIter.hasNext();) {
            DateRange removeRange = (DateRange) removeIter.next();
            for (Iterator partIter = resultSet.iterator(); partIter.hasNext();) {
                DateRange testRange = (DateRange) partIter.next();
                if (testRange.after(removeRange)) {
                    // Gone to far
                    break;
                }
                if (testRange.before(removeRange)) {
                    // Haven't reached the subtraction range yet
                    continue;
                }
                if (removeRange.contains(testRange)) {
                    // Whole range is excluded
                    partIter.remove();
                } else if (testRange.contains(removeRange)) {
                    // Subtracted range breaks this one into two parts
                    DateRange beforeRange = new DateRange();
                    beforeRange.setStartDate(testRange.getStartDate());
                    beforeRange.setEndDate(removeRange.getStartDate());
                    DateRange afterRange = new DateRange();
                    afterRange.setStartDate(removeRange.getEndDate());
                    afterRange.setEndDate(testRange.getEndDate());
                    partIter.remove();
                    toBeAddedSet.add(beforeRange);
                    toBeAddedSet.add(afterRange);
                } else if (removeRange.overlaps(testRange) &&
                        removeRange.compareTo(testRange) <= 0) {
                    // remove from beginning of range
                    partIter.remove();
                    testRange.setStartDate(removeRange.getEndDate());
                    toBeAddedSet.add(testRange);
                } else if (removeRange.overlaps(testRange) &&
                           removeRange.compareTo(testRange) > 0) {
                    // Remove from end of range
                    partIter.remove();
                    testRange.setEndDate(removeRange.getStartDate());
                    toBeAddedSet.add(testRange);

                }
            }

            // Add any remaining stuff at the end due to
            // Concurrent Modification Exceptions.  Flush out the set of
            // Date Ranges to be added afterwards.
            resultSet.addAll(toBeAddedSet);
            toBeAddedSet.clear();
        }
        return resultSet;
    }
}
