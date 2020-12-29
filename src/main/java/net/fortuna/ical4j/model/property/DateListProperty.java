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
package net.fortuna.ical4j.model.property;

import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.parameter.TzId;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.validate.ValidationException;

import java.time.LocalDate;
import java.time.temporal.Temporal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * $Id$
 * <p/>
 * Created on 11/08/2005
 * <p/>
 * Base class for properties with a list of dates as a value.
 *
 * Note that generics have been introduced as part of the migration to the new Java Date/Time API.
 * Date properties should now indicate the applicable {@link Temporal} type for the property.
 *
 * For example:
 *
 * <ul>
 *     <li>UTC-based properties should use {@link java.time.Instant} to represent UTC time</li>
 *     <li>Date-only properties should use {@link java.time.LocalDate} to represent a date value</li>
 *     <li>Date-time properties should use {@link java.time.ZonedDateTime} to represent a date-time value influenced by timezone rules</li>
 * </ul>
 *
 * @author Ben Fortuna
 */
public abstract class DateListProperty<T extends Temporal> extends Property {

    /**
     *
     */
    private static final long serialVersionUID = 5233773091972759919L;

    private final Value defaultValueParam;

    private DateList<T> dates;

    private transient TimeZoneRegistry timeZoneRegistry;

    /**
     * @param name the property name
     */
    public DateListProperty(final String name) {
        this(name, new DateList<>());
    }

    /**
     * @param name       the property name
     * @param parameters property parameters
     */
    public DateListProperty(final String name, final ParameterList parameters, Value defaultValueParam) {
        super(name, parameters);
        this.defaultValueParam = defaultValueParam;
    }

    /**
     * @param name  the property name
     * @param dates a list of initial dates for the property
     */
    public DateListProperty(final String name, final DateList<T> dates) {
        this(name, new ParameterList(), dates, Value.DATE_TIME);
    }

    /**
     * @param name       the property name
     * @param parameters property parameters
     * @param dates      a list of initial dates for the property
     */
    public DateListProperty(final String name, final ParameterList parameters, final DateList<T> dates,
                            Value defaultValueParam) {
        super(name, parameters);
        this.dates = dates;
        this.defaultValueParam = defaultValueParam;
    }

    /**
     * @return Returns the dates.
     */
    @SuppressWarnings("unchecked")
    public final List<T> getDates() {
        Optional<TzId> tzId = getParameters().getFirst(Parameter.TZID);
        if (tzId.isPresent()) {
            return dates.getDates().stream().map(date -> (T) TemporalAdapter.toLocalTime(
                    date, tzId.get().toZoneId(timeZoneRegistry))).collect(Collectors.toList());
        } else {
            return dates.getDates();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValue(final String aValue) {
        Optional<TzId> tzId = getParameters().getFirst(Parameter.TZID);
        if (tzId.isPresent()) {
            dates = DateList.parse(aValue, tzId.get(), timeZoneRegistry);
        } else {
            dates = DateList.parse(aValue);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getValue() {
        Optional<TzId> tzId = getParameters().getFirst(Parameter.TZID);
        if (tzId.isPresent()) {
            return dates.toString(tzId.get().toZoneId(timeZoneRegistry));
        } else {
            return dates.toString();
        }
    }

    public void setTimeZoneRegistry(TimeZoneRegistry timeZoneRegistry) {
        this.timeZoneRegistry = timeZoneRegistry;
    }

    @Override
    public void validate() throws ValidationException {
        if (!CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION)) {
            // Ensure date list is consistent with VALUE param..
            if (Value.DATE.equals(getParameters().getFirst(Parameter.VALUE).orElse(defaultValueParam))) {
                for (T t : dates.getDates()) {
                    if (!(t instanceof LocalDate)) {
                        throw new ValidationException("Mismatch between VALUE param and dates");
                    }
                }
            } else {
                for (T t : dates.getDates()) {
                    if (t instanceof LocalDate) {
                        throw new ValidationException("Mismatch between VALUE param and dates");
                    }
                }
            }
        }
    }
}
