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
import net.fortuna.ical4j.util.Strings;
import net.fortuna.ical4j.validate.PropertyValidator;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.ValidationRule;
import net.fortuna.ical4j.validate.Validator;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.time.temporal.Temporal;
import java.util.Optional;
import java.util.function.Predicate;

import static net.fortuna.ical4j.model.Parameter.VALUE;
import static net.fortuna.ical4j.validate.ValidationRule.ValidationType.*;

/**
 * $Id$
 * <p/>
 * Created on 9/07/2005
 * <p/>
 * Base class for properties with a DATE or DATE-TIME value. Note that some sub-classes may only allow either a DATE or
 * a DATE-TIME value, for which additional rules/validation should be specified.
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
public abstract class DateProperty<T extends Temporal> extends Property {

    private static final long serialVersionUID = 3160883132732961321L;

    private final CalendarDateFormat parseFormat;

    private final Value defaultValueParam;

    private TemporalAdapter<T> date;

    private transient TimeZoneRegistry timeZoneRegistry;

    private final Validator<DateProperty<T>> validator = new PropertyValidator<>(
            new ValidationRule<>(OneOrLess, VALUE),
            new ValidationRule<>(One, (Predicate<DateProperty<T>> & Serializable) p -> p.getDate() instanceof LocalDate, VALUE),
            new ValidationRule<>(None, (Predicate<DateProperty<T>> & Serializable) DateProperty::isUtc, TZID),
            new ValidationRule<>(OneOrLess, (Predicate<DateProperty<T>> & Serializable) p -> !p.isUtc(), TZID)
    );

    /**
     * @param name       the property name
     * @param parameters a list of initial parameters
     */
    public DateProperty(final String name, final ParameterList parameters) {

        this(name, parameters, CalendarDateFormat.DEFAULT_PARSE_FORMAT, Value.DATE_TIME);
    }

    public DateProperty(final String name, final ParameterList parameters, CalendarDateFormat parseFormat,
                        Value defaultValueParam) {

        super(name, parameters);
        this.parseFormat = parseFormat;
        this.defaultValueParam = defaultValueParam;
    }

    /**
     * @param name the property name
     */
    public DateProperty(final String name) {
        this(name, CalendarDateFormat.DEFAULT_PARSE_FORMAT, Value.DATE_TIME);
    }

    public DateProperty(final String name, CalendarDateFormat parseFormat, Value defaultValueParam) {
        super(name);
        this.parseFormat = parseFormat;
        this.defaultValueParam = defaultValueParam;
    }

    /**
     * This method will attempt to dynamically cast the internal {@link Temporal} value to the
     * required return value.
     *
     * e.g. LocalDate localDate = dateProperty.getDate();
     *
     * @return Returns the date.
     */
    @SuppressWarnings("unchecked")
    public T getDate() {
        if (date != null) {
            Optional<TzId> tzId = getParameters().getFirst(Parameter.TZID);
            if (tzId.isPresent() && shouldApplyTimezone()) {
                return (T) date.toLocalTime(tzId.get().toZoneId(timeZoneRegistry));
            } else {
                return date.getTemporal();
            }
        } else {
            return null;
        }
    }

    /**
     * Sets the date value of this property. The timezone and value of this
     * instance will also be updated accordingly.
     *
     * @param date The date to set.
     */
    public void setDate(T date) {
        if (date != null) {
            this.date = new TemporalAdapter<>(date, timeZoneRegistry);
        } else {
            this.date = null;
        }
    }

    /**
     * Default setValue() implementation. Allows for either DATE or DATE-TIME values.
     *
     * Note that this method will use the system default zone rules to parse the string value. For parsing string
     * values in a different timezone use {@link TemporalAdapter#parse(String, ZoneId)} and
     * {@link DateProperty#setDate(Temporal)}.
     *
     * @param value a string representation of a DATE or DATE-TIME value
     */
    @Override
    @SuppressWarnings("unchecked")
    public void setValue(final String value) throws DateTimeParseException {
        // value can be either a date-time or a date..
        if (value != null && !value.isEmpty()) {
            Optional<TzId> tzId = getParameters().getFirst(Parameter.TZID);
            try {
                this.date = tzId.map(id -> (TemporalAdapter<T>) TemporalAdapter.parse(value, id, timeZoneRegistry))
                        .orElseGet(() -> TemporalAdapter.parse(value, parseFormat));
            } catch (DateTimeParseException dtpe) {
                if (CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING)) {
                    LoggerFactory.getLogger(DateProperty.class).warn("Invalid DATE-TIME format", dtpe);

                    // parse with relaxed format..
                    this.date = tzId.map(id -> (TemporalAdapter<T>) TemporalAdapter.parse(value, id, timeZoneRegistry))
                            .orElseGet(() -> TemporalAdapter.parse(value, CalendarDateFormat.DEFAULT_PARSE_FORMAT));
                } else {
                    throw dtpe;
                }
            }
        } else {
            this.date = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getValue() {
        Optional<TzId> tzId = getParameters().getFirst(Parameter.TZID);
        if (tzId.isPresent() && shouldApplyTimezone()) {
            return date.toString(tzId.get().toZoneId(timeZoneRegistry));
        } else {
            return Strings.valueOf(date);
        }
    }

    public void setTimeZoneRegistry(TimeZoneRegistry timeZoneRegistry) {
        this.timeZoneRegistry = timeZoneRegistry;
    }

    private boolean shouldApplyTimezone() {
        Optional<Value> value = getParameters().getFirst(VALUE);
        return !Optional.of(Value.DATE).equals(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return getDate() != null ? getDate().hashCode() : 0;
    }

    /**
     * Indicates whether the current date value is specified in UTC time.
     *
     * @return true if the property is in UTC time, otherwise false
     */
    public final boolean isUtc() {
        return date != null && TemporalAdapter.isUtc(date.getTemporal());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate() throws ValidationException {
        validator.validate(this);
        /*
         * ; the following are optional, ; but MUST NOT occur more than once (";" "VALUE" "=" ("DATE-TIME" / "DATE")) /
         * (";" tzidparam) /
         */

        /*
         * ; the following is optional, ; and MAY occur more than once (";" xparam)
         */

        final Value value = (Value) getParameters().getFirst(VALUE).orElse(defaultValueParam);

        if (date != null) {
            if (date.getTemporal() instanceof LocalDate) {
                if (!Value.DATE.equals(value)) {
                    throw new ValidationException("VALUE parameter [" + value + "] is invalid for DATE instance");
                }
            } else {
                if (!Value.DATE_TIME.equals(value)) {
                    throw new ValidationException("VALUE parameter [" + value + "] is invalid for DATE-TIME instance");
                }
            }
        }
    }

    @Override
    public int compareTo(Property o) {
        if (o instanceof DateProperty) {
            return new TemporalComparator().compare(getDate(), ((DateProperty) o).getDate());
        }
        return super.compareTo(o);
    }
}
