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

import net.fortuna.ical4j.model.parameter.TzId;

import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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

    @SuppressWarnings("rawtypes")
    public static final DateList EMPTY_LIST = new DateList();

    private final List<TemporalAdapter<T>> dates;

    /**
     * Default constructor.
     */
    public DateList() {
        this(Collections.emptyList());
    }

    /**
     * Constructs a new date list of the specified type containing
     * the dates in the specified list.
     * @param dates a list of dates to include in the new list
     */
    public DateList(final T...dates) {
        this.dates = Arrays.stream(dates).map(TemporalAdapter::new).collect(Collectors.toList());
    }

    public DateList(TimeZoneRegistry timeZoneRegistry, T...dates) {
        this.dates = Arrays.stream(dates).map(date -> new TemporalAdapter<>(date, timeZoneRegistry))
                .collect(Collectors.toList());
    }

    private DateList(List<TemporalAdapter<T>> dates) {
        this.dates = dates;
    }
    
    /**
     * Parse a string representation of a date/time list.
     *
     * @param value
     * @return
     * @throws DateTimeParseException
     */
    public static DateList<? extends Temporal> parse(String value) {
        if (value == null || value.isEmpty()) {
            return emptyList();
        }

        List<TemporalAdapter<Temporal>> dates = Arrays.stream(value.split(","))
                .parallel().map(TemporalAdapter::parse)
                .collect(Collectors.toList());

        return new DateList<>(dates);
    }

    public static DateList<? extends Temporal> parse(String value, ZoneId zoneId) {
        if (value == null || value.isEmpty()) {
            return emptyList();
        }

        List<TemporalAdapter<ZonedDateTime>> dates = Arrays.stream(value.split(","))
                .parallel().map(s -> TemporalAdapter.parse(s, zoneId))
                .collect(Collectors.toList());
        return new DateList<>(dates);
    }

    public static DateList<ZonedDateTime> parse(String value, TzId tzId, TimeZoneRegistry timeZoneRegistry) {
        if (value == null || value.isEmpty()) {
            return emptyList();
        }

        List<TemporalAdapter<ZonedDateTime>> dates = Arrays.stream(value.split(","))
                .parallel().map(s -> TemporalAdapter.parse(s, tzId, timeZoneRegistry))
                .collect(Collectors.toList());
        return new DateList<>(dates);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Temporal> DateList<T> emptyList() {
        return (DateList<T>) EMPTY_LIST;
    }

    @Override
    public String toString() {
        if (dates.isEmpty()) {
            return "";
        }
        return dates.stream().map(TemporalAdapter::toString).collect(Collectors.joining(","));
    }

    public String toString(ZoneId zoneId) {
        if (dates.isEmpty()) {
            return "";
        }
        return dates.stream().map(date -> date.toString(zoneId)).collect(Collectors.joining(","));
    }

    /**
     * Add a date to the list. The date will be updated to reflect the timezone of this list.
     * 
     * @param date
     *            the date to add
     * @return true
     * @see List#add(java.lang.Object)
     */
    public final DateList<T> add(final T date) {
        List<TemporalAdapter<T>> copy = new ArrayList<>(dates);
        copy.add(new TemporalAdapter<T>(date));
        return new DateList<>(copy);
    }

    public final DateList<T> addAll(Collection<? extends T> arg0) {
        List<TemporalAdapter<T>> copy = new ArrayList<>(dates);
        copy.addAll(arg0.stream().map(TemporalAdapter<T>::new).collect(Collectors.toList()));
        return new DateList<>(copy);
    }

    public List<T> getDates() {
        return dates.stream().map(TemporalAdapter::getTemporal).collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DateList<?> dateList = (DateList<?>) o;
        return Objects.equals(dates, dateList.dates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dates);
    }
}
