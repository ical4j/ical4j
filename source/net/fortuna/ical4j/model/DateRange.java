/*
 * $Id$
 *
 * Copyright (c) 2005, Dustin Jenkins
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
 * A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.fortuna.ical4j.model;

import java.util.Date;

/**
 * This class represents a time period from a start date to an end date.
 * @author Dustin Jenkins
 */
public class DateRange implements Comparable {

    private Date startDate;
    private Date endDate;

    /**
     * Default Constructor.
     */
    public DateRange() {
    }

    /**
     * Decides whether a date falls within this date range.
     *
     * @param testDate the date to be tested
     * @return true if the date is in the range, false otherwise
     */
    public boolean includes(Date testDate) {
        if (getStartDate().compareTo(testDate) <= 0 &&
                getEndDate().compareTo(testDate) >= 0) {
            return true;
        }
        return false;
    }

    /**
     * Decides whether the date range finishes before the given date.
     *
     * @param testDate the date to be tested
     * @return true if the date is before the range, false otherwise
     */
    public boolean before(Date testDate) {
        if (getEndDate().compareTo(testDate) <= 0) {
            return true;
        }
        return false;
    }

    /**
     * Decides whether this data range is completed before the given range starts
     *
     * @param afterRange
     * @return
     */
    public boolean before(DateRange afterRange) {
        if (before(afterRange.getStartDate())) {
            return true;
        }
        return false;
    }

    /**
     * Decides whether the date range starts before the given date.
     *
     * @param testDate the date to be tested
     * @return true if the date is after the range, false otherwise
     */
    public boolean after(Date testDate) {
        if (getStartDate().compareTo(testDate) >= 0) {
            return true;
        }
        return false;
    }

    /**
     * Decides whether this range starts after the given range ends
     *
     * @param beforeRange
     * @return
     */
    public boolean after(DateRange beforeRange) {
        if (after(beforeRange.getEndDate())) {
            return true;
        }
        return false;
    }

    /**
     * Decides whether this date range overlaps with another one
     *
     * @param testRange the range to test for
     * @return true if ranges overlap, false otherwise.
     */
    public boolean overlaps(DateRange testRange) {
        // Test for our start date in test range
        if (testRange.includes(getStartDate())) {
            // Exclude if it is the end date of test range
            if (!getStartDate().equals(testRange.getEndDate())) {
                return true;
            }
        }
        // Test for test range's start date in our range
        if (includes(testRange.getStartDate())) {
            // Exclude if it is the end date of our range
            if (!getEndDate().equals(testRange.getStartDate())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Decides whether these date ranges are serial without a gap.
     * @return true if one range immediately follows the other, false otherwise
     */
    public boolean adjacent(DateRange testRange) {
        if (getStartDate().equals(testRange.getEndDate())) {
            return true;
        }
        if (getEndDate().equals(testRange.getStartDate())) {
            return true;
        }
        return false;
    }

    /**
     * Decides whether the given date range is completely contained within this one
     *
     * @param innerRange the range to be tested for being contained
     * @return true if this date range covers all the dates of the inner range
     */
    public boolean contains(DateRange innerRange) {
        // Test for inner range's start date in our range
        if (includes(innerRange.getStartDate())) {
            // Test for inner range's end date in our range
            if (includes(innerRange.getEndDate())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Creates a date range that encompasses both this range and another one.
     * If the other range is null, return a copy of this range.
     *
     * @param otherRange the other range that needs to begin the resulting range
     * @return a date range that is just large enough to hold both ranges
     */
    public DateRange add(DateRange otherRange) {

        DateRange newRange = new DateRange();

        if (otherRange == null) {
            newRange.setStartDate(getStartDate());
            newRange.setEndDate(getEndDate());
            return newRange;
        }
        int compareStarts = getStartDate().compareTo(otherRange.getStartDate());
        if (compareStarts > 0) {
            newRange.setStartDate(otherRange.getStartDate());
        } else {
            newRange.setStartDate(getStartDate());
        }
        int compareEnds = getEndDate().compareTo(otherRange.getEndDate());
        if (compareEnds < 0) {
            newRange.setEndDate(otherRange.getEndDate());
        } else {
            newRange.setEndDate(getEndDate());
        }
        return newRange;
    }

    /**
     * Gets the starting date of the date range, inclusive
     *
     * @return the startDate set on this range, or the current time if it is null
     */
    public Date getStartDate() {
        if (startDate == null) {
            setStartDate(new Date());
        }
        if (endDate != null && startDate.compareTo(endDate) > 0) {
            return endDate;
        }
        return startDate;
    }

    /**
     * Sets the starting date of the date range, inclusive
     *
     * @param startDate the first date this range should cover
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * Gets the ending date of the date range, inclusive.
     * Note that this guarantees that it will return the earlier of the
     * two dates stored in this object.
     *
     * @return the endDate set on this range, or the current time if it is null
     */
    public Date getEndDate() {
        if (endDate == null) {
            setEndDate(new Date());
        }
        if (startDate != null && endDate.compareTo(startDate) < 0) {
            return startDate;
        }
        return endDate;
    }

    /**
     * Sets the ending date of the date range, inclusive
     *
     * @param endDate the last date this range should cover
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }


    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.<p>
     * <p/>
     * In the foregoing description, the notation
     * <tt>sgn(</tt><i>expression</i><tt>)</tt> designates the mathematical
     * <i>signum</i> function, which is defined to return one of <tt>-1</tt>,
     * <tt>0</tt>, or <tt>1</tt> according to whether the value of <i>expression</i>
     * is negative, zero or positive.
     * <p/>
     * The implementor must ensure <tt>sgn(x.compareTo(y)) ==
     * -sgn(y.compareTo(x))</tt> for all <tt>x</tt> and <tt>y</tt>.  (This
     * implies that <tt>x.compareTo(y)</tt> must throw an exception iff
     * <tt>y.compareTo(x)</tt> throws an exception.)<p>
     * <p/>
     * The implementor must also ensure that the relation is transitive:
     * <tt>(x.compareTo(y)&gt;0 &amp;&amp; y.compareTo(z)&gt;0)</tt> implies
     * <tt>x.compareTo(z)&gt;0</tt>.<p>
     * <p/>
     * Finally, the implementer must ensure that <tt>x.compareTo(y)==0</tt>
     * implies that <tt>sgn(x.compareTo(z)) == sgn(y.compareTo(z))</tt>, for
     * all <tt>z</tt>.<p>
     * <p/>
     * It is strongly recommended, but <i>not</i> strictly required that
     * <tt>(x.compareTo(y)==0) == (x.equals(y))</tt>.  Generally speaking, any
     * class that implements the <tt>Comparable</tt> interface and violates
     * this condition should clearly indicate this fact.  The recommended
     * language is "Note: this class has a natural ordering that is
     * inconsistent with equals."
     *
     * @param o the Object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     *         is less than, equal to, or greater than the specified object.
     * @throws ClassCastException if the specified object's type prevents it
                          from being compared to this Object, or it is null.
     */
    public int compareTo(Object o) {
        // Throws documented exception if type is wrong or parameter is null
        if (o == null) {
            throw new ClassCastException("Cannot compare this object to null");
        }
        DateRange testRange = (DateRange) o;
        int startCompare = getStartDate().compareTo(testRange.getStartDate());
        if (startCompare < 0 || startCompare > 0) {
            return startCompare;
        }
        // Start dates are the same, compare end dates
        int endCompare = getEndDate().compareTo(testRange.getEndDate());
        if (endCompare < 0 || endCompare > 0) {
            return endCompare;
        }
        // Both start and end dates match. These must be equal.
        return 0;
    }

    /**
     * Overrides the equality test, compares fields of this  objects for
     * equality.
     *
     * @param o object being compared for equality
     * @return true if the objects are equal, false otherwise
     */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DateRange)) return false;

        final DateRange dateRange = (DateRange) o;
        final Date start = getStartDate();
        final Date end = getEndDate();

        if (!start.equals(dateRange.getStartDate())) return false;
        if (!end.equals(dateRange.getEndDate())) return false;

        return true;
    }

    /**
     * Override hashCode() with code that checks fields in this object.
     *
     * @return hascode for this object
     */
    public int hashCode() {
        int result;
        result = getStartDate().hashCode();
        result = 29 * result + getEndDate().hashCode();
        return result;
    }

    /**
     * Convert this object to a displayable string
     *
     * @return string version of this object
     */
    public String toString() {
        return startDate.toString() + " - " + endDate.toString();
    }
}
