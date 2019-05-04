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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * $Id$ [23-Apr-2004]
 *
 * A DateList is a grouping of date-time instances along with a common format to
 * be applied to all dates in the group.
 *
 * @author Ben Fortuna
 */
public class DateList<T extends Temporal> implements Serializable {

	private static final long serialVersionUID = -3700862452550012357L;

    private final List<T> dates;

    private transient final CalendarDateFormat dateFormat;

    /**
     * Default constructor.
     */
    public DateList() {
        this(CalendarDateFormat.FLOATING_DATE_TIME_FORMAT);
    }

    public DateList(CalendarDateFormat dateFormat) {
    	this(false, dateFormat);
    }

    public DateList(final boolean unmodifiable) {
        this(unmodifiable, CalendarDateFormat.FLOATING_DATE_TIME_FORMAT);
    }

    public DateList(final boolean unmodifiable, CalendarDateFormat dateFormat) {
        if (unmodifiable) {
        	dates = Collections.emptyList();
        } else {
            dates = new ArrayList<>();
        }
        this.dateFormat = dateFormat;
    }

    /**
     * Constructs a new date list of the specified type containing
     * the dates in the specified list.
     * @param list a list of dates to include in the new list
     */
    public DateList(final List<T> list) {
        this(list, CalendarDateFormat.FLOATING_DATE_TIME_FORMAT);
    }

    public DateList(final List<T> list, CalendarDateFormat dateFormat) {
        this.dates = new ArrayList<>(list);
        this.dateFormat = dateFormat;
    }

    /**
     * Parse a string representation of a date/time list.
     *
     * @param value
     * @param <T>
     * @return
     * @throws DateTimeParseException
     */
    public static <T extends Temporal> DateList<T> parse(String value) {
        List<Temporal> dates = Arrays.stream(value.split(",")).map(TemporalAdapter::parse)
                .map(TemporalAdapter::getTemporal).collect(Collectors.toList());
        if (!dates.isEmpty()) {
            return new DateList<>((List<T>) dates, CalendarDateFormat.from(dates.get(0)));
        } else {
            return new DateList<>((List<T>) dates);
        }
    }

    @Override
    public String toString() {
        if (dates.isEmpty()) {
            return "";
        }
        return dates.stream().map(dateFormat::format).collect(Collectors.joining(","));
    }

    public String toString(ZoneId zoneId) {
        if (dates.isEmpty()) {
            return "";
        }
        return dates.stream().map(date -> dateFormat.format(date, zoneId)).collect(Collectors.joining(","));
    }

    /**
     * Add a date to the list. The date will be updated to reflect the timezone of this list.
     * 
     * @param date
     *            the date to add
     * @return true
     * @see List#add(java.lang.Object)
     */
    public final boolean add(final T date) {
        return dates.add(date);
    }

    public final boolean addAll(Collection<? extends T> arg0) {
        return dates.addAll(arg0);
    }

    public List<T> getDates() {
        return dates;
    }

    @Override
	public final boolean equals(Object obj) {
		if (!getClass().isAssignableFrom(obj.getClass())) {
			return false;
		}
		final DateList rhs = (DateList) obj;
		return new EqualsBuilder().append(dates, rhs.dates).isEquals();
	}

    @Override
	public final int hashCode() {
		return new HashCodeBuilder().append(dates).toHashCode();
	}
}
