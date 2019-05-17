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
import net.fortuna.ical4j.util.Strings;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.time.temporal.Temporal;
import java.util.Optional;

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

    private DateList<T> dates;

    private TimeZone timeZone;

    /**
     * @param name the property name
     */
    public DateListProperty(final String name, PropertyFactory factory) {
        this(name, new DateList<>(), factory);
    }

    /**
     * @param name       the property name
     * @param parameters property parameters
     */
    public DateListProperty(final String name, final ParameterList parameters, PropertyFactory factory) {
        super(name, parameters, factory);
    }

    /**
     * @param name  the property name
     * @param dates a list of initial dates for the property
     */
    public DateListProperty(final String name, final DateList<T> dates, PropertyFactory factory) {
        this(name, new ParameterList(), dates, factory);
    }

    /**
     * @param name       the property name
     * @param parameters property parameters
     * @param dates      a list of initial dates for the property
     */
    public DateListProperty(final String name, final ParameterList parameters, final DateList<T> dates,
                            PropertyFactory factory) {
        super(name, parameters, factory);
        this.dates = dates;
    }

    /**
     * @return Returns the dates.
     */
    public final DateList<T> getDates() {
        return dates;
    }

    /**
     * {@inheritDoc}
     */
    public void setValue(final String aValue) throws ParseException {
        dates = DateList.parse(aValue);
    }

    /**
     * {@inheritDoc}
     */
    public String getValue() {
        return Strings.valueOf(dates);
    }

    /**
     * Sets the timezone associated with this property.
     *
     * @param timezone a timezone to associate with this property
     */
    public void setTimeZone(final TimeZone timezone) {
        if (dates == null) {
            throw new UnsupportedOperationException(
                    "TimeZone is not applicable to current value");
        }
        this.timeZone = timezone;
        if (timezone != null) {
            final net.fortuna.ical4j.model.parameter.TzId tzId = new net.fortuna.ical4j.model.parameter.TzId(timezone.getID());
            getParameters().replace(tzId);
        } else {
            // use setUtc() to reset timezone..
            setUtc(false);
        }
    }

    /**
     * @return the timezone
     */
    public final Optional<TimeZone> getTimeZone() {
        return Optional.ofNullable(timeZone);
    }

    /**
     * Resets the timezone associated with the property. If utc is true, any TZID parameters are removed and the Java
     * timezone is updated to UTC time. If utc is false, TZID parameters are removed and the Java timezone is set to the
     * default timezone (i.e. represents a "floating" local time)
     *
     * @param utc the UTC value
     */
    public final void setUtc(final boolean utc) {
        if (dates == null) {
            throw new UnsupportedOperationException("TimeZone is not applicable to current value");
        }
        if (utc) {
            getParameters().remove(getParameter(Parameter.TZID));
        }
    }

    /**
     * {@inheritDoc}
     */
    public final Property copy() throws IOException, URISyntaxException, ParseException {
        final Property copy = super.copy();

        ((DateListProperty) copy).timeZone = timeZone;
        ((DateListProperty) copy).setValue(getValue());

        return copy;
    }
}
